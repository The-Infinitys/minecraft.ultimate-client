pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "meteor-maven-snapshots"
            url = uri("https://maven.meteordev.org/snapshots")
        }
        maven {
            name = "babbaj-repo"
            url = uri("https://babbaj.github.io/maven/")
        }

        mavenCentral()
        gradlePluginPortal()
    }
    val loom_version: String by settings
    plugins {
        id("fabric-loom") version loom_version
        id("java")
        id("maven-publish")
        kotlin("jvm") version "2.3.0-Beta2"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0-Beta2"
        id("com.diffplug.spotless") version "8.0.0"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "infinite"

