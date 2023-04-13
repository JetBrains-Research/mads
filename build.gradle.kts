val tagName = System.getenv("GIT_TAG") ?: "unspecified"
val javaVersion = JavaVersion.VERSION_17 // Adjust this to the desired Java version
val javaLanguageVersion = JavaLanguageVersion.of(javaVersion.toString())
val kotlinVersion = "1.8.0" // Adjust this to the desired Kotlin version
val kotlinCoroutineVersion = "1.6.2" // Adjust this to the desired Kotlin version

plugins {
    application
    java
    id("maven-publish")
    kotlin("jvm") version "1.8.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
}

java {
    toolchain {
        languageVersion.set(javaLanguageVersion) // Set the desired JDK version for the toolchain
    }
}

tasks {
    jar {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
//        archiveClassifier.set("all") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.compileClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
}
//
//publishing {
//    publications {
//        create<MavenPublication>("rootLibrary") {
//            groupId = "org.jetbrains.research" // Replace with your group ID
//            artifactId = "mads" // Replace with your artifact ID
//            version = tagName // Replace with your version
//
//            from(components["kotlin"])
//        }
//    }
//}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    version = tagName

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