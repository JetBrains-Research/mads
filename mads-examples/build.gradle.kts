plugins {
    kotlin("jvm")
}

description = "Examples of framework usage"

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))

    implementation("org.apache.commons:commons-collections4:4.4")
}