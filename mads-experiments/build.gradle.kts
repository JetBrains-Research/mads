plugins {
    kotlin("jvm")
}

//group = "org.example"
//version = "unspecified"
description = "Neuroscience domain for MADS"

//repositories {
//    mavenCentral()
//}

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))
}

//dependencies {
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
//}
//
//tasks.getByName<Test>("test") {
//    useJUnitPlatform()
//}