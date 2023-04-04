plugins {
    kotlin("jvm")
}

publishing {
    publications {
        create<MavenPublication>("experimentsLibrary") {
            from(components["kotlin"])
        }
    }
}

description = "Computational neuroscience experiments"

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))
}