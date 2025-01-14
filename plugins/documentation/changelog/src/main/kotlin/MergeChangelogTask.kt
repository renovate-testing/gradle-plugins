import java.text.SimpleDateFormat
import java.util.Date
import org.gradle.api.Project

val Project.isFinal: Boolean
    get() = "${project.version}".all { it.isDigit() || it == '.' }

@OptIn(ExperimentalStdlibApi::class)
fun Project.mergeChangelog() {
    val version: String = this.version.toString()

    if (isFinal.not()) return

    val changelog = changelogFile.readLines()

    val header = "## [$version] - ${SimpleDateFormat("yyyy-MM-dd").format(Date())}\n"

    val firstVersionIndex = changelog.indexOfFirst { it.containVersion(version) }
    val lastVersionIndex = changelog.indexOfLast { it.containVersion(version) }

    if (firstVersionIndex == -1 || lastVersionIndex == -1) {
        logger.lifecycle("This version doesn't need to be merged")
        return
    }

    val firstOldVersion =
        changelog.subList(lastVersionIndex + 1, changelog.count()).firstOrNull {
            it.contains("## [")
        }
    val firstOldVersionIndex =
        if (firstOldVersion != null) changelog.indexOf(firstOldVersion) else changelog.count()

    val versionBlock = changelog.subList(firstVersionIndex, firstOldVersionIndex)

    val mergedVersion = mutableListOf<String>()
    mergedVersion.add(header)
    mergedVersion.addAll(extractAllBlocks(versionBlock))

    val mergedChangelog = buildList {
        addAll(changelog.subList(0, firstVersionIndex - 1).cleanUnreleasedBlock())
        add("")
        addAll(mergedVersion)
        addAll(changelog.subList(firstOldVersionIndex, changelog.count()))
    }

    changelogFile.writeText(mergedChangelog.joinToString("\n"))
}

private fun extractAllBlocks(versionBlock: List<String>): List<String> {

    val allBlocks = mutableListOf<String>()

    extractBlock("Added", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }
    extractBlock("Changed", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }
    extractBlock("Deprecated", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }
    extractBlock("Removed", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }
    extractBlock("Fixed", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }
    extractBlock("Updated", versionBlock).run { if (isNotEmpty()) allBlocks.addAll(this) }

    return allBlocks
}

private fun extractBlock(blockName: String, versionBlock: List<String>): List<String> {
    val block = mutableListOf<String>()
    block.add("### $blockName")
    val indexes =
        versionBlock.mapIndexedNotNull { index, line ->
            if (line.contains("### $blockName")) index else null
        }

    for (i in indexes) {
        val changes = versionBlock.subList(i + 1, versionBlock.count())
        block.addAll(changes.takeWhile { it.replace(" ", "").startsWith("-") })
    }
    block.add("")

    return if (block.any { it.replace(" ", "").startsWith("-") }) block else emptyList()
}

private fun List<String>.cleanUnreleasedBlock(): List<String> = mapNotNull { line ->
    if (line.filterNot(Char::isWhitespace).startsWith("-")) null else line
}
