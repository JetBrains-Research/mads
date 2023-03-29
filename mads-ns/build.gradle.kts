plugins {
    kotlin("jvm")
}

description = "Neuroscience domain for MADS"

dependencies {
    implementation(project(":mads-core"))
    implementation("org.apache.commons:commons-collections4:4.4")
}