plugins {
    `kotlin-dsl`
    `javiersc-publish`
}

pluginBundle {
    tags =
        listOf(
            "massive catalogs",
            "versions catalogs",
        )
}

gradlePlugin {
    plugins {
        named("com.javiersc.gradle.plugins.massive.catalogs.updater") {
            id = "com.javiersc.gradle.plugins.massive.catalogs.updater"
            displayName = "Massive Catalogs Updater"
            description = "A plugin for updating Massive Catalogs"
        }
    }
}

dependencies {
    api(projects.shared.pluginAccessors)

    implementation(libs.jsoup.jsoup)
    implementation(libs.javiersc.semanticVersioning.semanticVersioningCore)

    compileOnly(pluginLibs.jetbrains.kotlin.kotlinGradlePlugin)
}
