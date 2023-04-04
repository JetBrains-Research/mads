plugins {
    kotlin("jvm")
}

publishing {
    publications {
        create<MavenPublication>("nsLibrary") {
            from(components["kotlin"])
        }
    }
}

description = "Neuroscience domain for MADS"

dependencies {
    implementation(project(":mads-core"))
    implementation("org.apache.commons:commons-collections4:4.4")
}