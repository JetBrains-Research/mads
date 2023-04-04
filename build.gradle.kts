val tagName = System.getenv("GIT_TAG") ?: "unspecified"
val javaVersion = JavaVersion.VERSION_17 // Adjust this to the desired Java version
val javaLanguageVersion = JavaLanguageVersion.of(javaVersion.toString())
val kotlinVersion = "1.8.0" // Adjust this to the desired Kotlin version

plugins {
    java
    id("maven-publish")
    kotlin("jvm") version "1.8.0"
}

java {
    toolchain {
        languageVersion.set(javaLanguageVersion) // Set the desired JDK version for the toolchain
    }
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "maven-publish")

    version = tagName

    repositories {
        mavenCentral()
    }

    tasks.register("publishAll") {
        group = "publishing"
        description = "Publish all subprojects"

        dependsOn(subprojects.map { it.tasks.named("publish") })
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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