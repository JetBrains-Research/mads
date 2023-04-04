plugins {
    kotlin("jvm")
}

publishing {
    publications {
        create<MavenPublication>("nsLibrary") {
            groupId = "org.jetbrains.research" // Replace with your group ID
            artifactId = "mads-ns" // Replace with your artifact ID
            version = project.rootProject.version.toString() // Use the root project version

            from(components["kotlin"])
        }
    }
}

description = "Neuroscience domain for MADS"

dependencies {
    implementation(project(":mads-core"))
    implementation("org.apache.commons:commons-collections4:4.4")
}