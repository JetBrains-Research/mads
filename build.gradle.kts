import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val tagName = System.getenv("GIT_TAG") ?: "unspecified"
val groupName = "org.jetbrains.research.mads"
val javaVersion = JavaVersion.VERSION_17 // Adjust this to the desired Java version
val javaLanguageVersion = JavaLanguageVersion.of(javaVersion.toString())
val kotlinVersion = "1.9.0" // Adjust this to the desired Kotlin version
val excludedModules = setOf(":mads-examples") // Examples won't be published, but still compiled

plugins {
    java
    id("maven-publish")
    kotlin("jvm") version "1.9.0"
}

java {
    toolchain {
        languageVersion.set(javaLanguageVersion) // Set the desired JDK version for the toolchain
    }
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    version = tagName
    group = groupName

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "maven-publish")

    tasks.register("publishAll") {
        group = "publishing"
        description = "Publish all subprojects"

        dependsOn(subprojects.map { it.tasks.named("publish") })
    }

    // Only disable 'publish' task for excludedModules
    tasks.withType<PublishToMavenRepository> {
        onlyIf { !excludedModules.contains(project.path) }
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = javaVersion.toString()
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = javaVersion.toString()
            targetCompatibility = javaVersion.toString()
        }
    }
}