plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.10"
}

//group = "org.example"
//version = "unspecified"
description = "Neuroscience domain for MADS"

//repositories {
//    mavenCentral()
//}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation(project(":mads-core"))
    implementation("org.apache.commons:commons-collections4:4.4")
}

//dependencies {
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
//}
//
//tasks.getByName<Test>("test") {
//    useJUnitPlatform()
//}