plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("net.sf.trove4j:trove4j:3.0.3")
}