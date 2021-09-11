import com.javiersc.plugins.core.isSignificant
import com.javiersc.plugins.publishing.core.signPublications

plugins {
    `maven-publish`
    id("com.gradle.plugin-publish")
    signing
    id("org.jetbrains.dokka")
}

val docsJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Javadoc jar file from for publishing"
    archiveClassifier.set("javadoc")
}

val sourcesJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Sources jar file for publishing"
    archiveClassifier.set("sources")
    from((project.properties["sourceSets"] as SourceSetContainer)["main"].allSource)
}

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            pom {
                name.set(property("pom.name").toString())
                description.set(property("pom.description").toString())
                url.set(property("pom.url").toString())

                licenses {
                    license {
                        name.set(property("pom.license.name").toString())
                        url.set(property("pom.license.url").toString())
                    }
                }

                developers {
                    developer {
                        id.set(property("pom.developer.id").toString())
                        name.set(property("pom.developer.name").toString())
                        email.set(property("pom.developer.email").toString())
                    }
                }

                scm {
                    url.set(property("pom.smc.url").toString())
                    connection.set(property("pom.smc.connection").toString())
                    developerConnection.set(property("pom.smc.developerConnection").toString())
                }
            }

            artifact(docsJar)

            artifact(sourcesJar)
        }
    }
}

configure(SigningExtension::signPublications)

pluginBundle {
    website = property("pom.smc.url").toString()
    vcsUrl = property("pom.smc.connection").toString()
}

project.tasks { create<Exec>("gitDiff") { commandLine("git", "diff") } }

val checkIsSignificant: Task by project.tasks.creating {
    dependsOn("gitDiff")
    doLast {
        if (!isSignificant) {
            error("Only significant versions can be published (current: $version)")
        }
    }
}

rootProject.tasks {
    getByName("initializeSonatypeStagingRepository") { dependsOn(checkIsSignificant) }
}

tasks {
    getByName("publish") { dependsOn(checkIsSignificant) }

    getByName("publishToSonatype") { dependsOn(checkIsSignificant) }

    getByName("publishToMavenLocal") { dependsOn(checkIsSignificant) }
}