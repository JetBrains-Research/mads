plugins {
    kotlin("jvm")
}

description = "Core types for MADS"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("me.tongfei:progressbar:0.9.3")

    // Logging dependencies
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("org.slf4j:slf4j-api:1.7.36")
}

//readme {
//}