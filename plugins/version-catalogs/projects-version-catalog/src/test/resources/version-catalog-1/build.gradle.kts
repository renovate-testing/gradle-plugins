import com.javiersc.gradle.plugins.projects.version.catalog.ProjectsVersionCatalogExtension

plugins {
    id("com.javiersc.gradle.plugins.projects.version.catalog")
}

allprojects {
    group = "com.example.foo-bar"
    version = "1.2.3"
}

configure<ProjectsVersionCatalogExtension> {
    removeVersionAliasPrefix.set("com")
}
