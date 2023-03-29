group "org.jetbrains.research.mads"
version "0.1.0"

plugins {
    java
    idea
    kotlin("jvm") version "1.8.0"
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}

dependencies {

}