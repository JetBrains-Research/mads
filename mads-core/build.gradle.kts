plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

publishing {
    publications {
        create<MavenPublication>("coreLibrary") {
            groupId = "org.jetbrains.research" // Replace with your group ID
            artifactId = "mads-core" // Replace with your artifact ID
            version = project.rootProject.version.toString() // Use the root project version

            from(components["kotlin"])
        }
    }
}

description = "Core types for MADS"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")
}