plugins {
    `kotlin-dsl`
    publish
    `plugin-publish`
    `accessors-generator`
}

pluginBundle {
    tags =
        listOf(
            "publish",
            "maven",
        )
}

gradlePlugin {
    plugins {
        named("com.javiersc.gradle.plugins.publish.version.catalog") {
            id = "com.javiersc.gradle.plugins.publish.version.catalog"
            displayName = "Publish Version Catalog"
            description =
                "A custom plugin for `maven-publish` and `version-catalog` plugin with a basic setup"
        }
    }
}

dependencies {
    implementation(files(pluginLibs.javaClass.protectionDomain.codeSource.location))

    api(projects.accessors)
    api(projects.core)

    implementation(gradleApi())
    api(pluginLibs.jetbrains.dokka.dokkaGradlePlugin)
}