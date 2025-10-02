rootProject.name = "medusa-root"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://repository.kotzilla.io/repository/Koin-Embedded/")

            content {
                includeModule("io.insert-koin", "embedded-koin-core")
                includeModule("io.insert-koin", "embedded-koin-core-jvm")
                includeModule("io.insert-koin", "embedded-koin-android")
            }
        }
    }
    versionCatalogs {
        create("libs") { from(files("gradle/libs.toml")) }
    }
}

include(":medusa")
include(":app")
