plugins {
    `kotlin-dsl`
    `javiersc-publish-gradle-plugin`
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
        named("com.javiersc.gradle.plugins.publish.android.library") {
            id = "com.javiersc.gradle.plugins.publish.android.library"
            displayName = "Publish Android Library"
            description = "A custom plugin for `maven-publish` and `android-library` plugin with a basic setup"
        }
    }
}

dependencies {
    api(projects.pluginAccessors)
    api(projects.core)
    api(projects.publishingCore)

    implementation(gradleApi())
    api(pluginLibs.android.toolsBuild.gradle)
    api(pluginLibs.jetbrains.dokka.dokkaGradlePluginX)
}
