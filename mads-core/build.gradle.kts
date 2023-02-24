plugins {
    kotlin("jvm")
}

description = "Core types for MADS"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")

//    implementation("me.tongfei:progressbar:0.9.5")

    // Logging dependencies
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("ch.qos.logback:logback-core:1.4.5")
    implementation("org.slf4j:slf4j-api:2.0.5")
}

//readme {
//}