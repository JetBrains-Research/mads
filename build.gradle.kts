group "org.jetbrains.research.mads"
version "0.1.0"

plugins {
    java
    kotlin("jvm") version "1.7.10"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    // Logging dependencies
    //implementation('ch.qos.logback:logback-classic:1.2.7')
//    implementation('ch.qos.logback:logback-core:1.2.7')
    //implementation 'org.slf4j:slf4j-api:1.7.32'

}