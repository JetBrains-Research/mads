plugins {
    kotlin("jvm")
}

description = "Computational neuroscience experiments"

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))
}