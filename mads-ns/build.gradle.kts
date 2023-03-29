plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.10"
}

description = "Neuroscience domain for MADS"

dependencies {
    implementation(project(":mads-core"))
    implementation("org.apache.commons:commons-collections4:4.4")
}