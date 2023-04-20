plugins {
    kotlin("jvm")
}

publishing {
    publications {
        create<MavenPublication>("experimentsLibrary") {
            groupId = "org.jetbrains.research" // Replace with your group ID
            artifactId = "mads-experiments" // Replace with your artifact ID
            version = project.rootProject.version.toString() // Use the root project version

            from(components["kotlin"])
        }
    }
}

description = "Computational neuroscience experiments"

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))

    implementation("org.apache.commons:commons-collections4:4.4")
}