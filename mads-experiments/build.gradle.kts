plugins {
    kotlin("jvm")
}

publishing {
    publications {
        create<MavenPublication>("experimentsLibrary") {
            groupId = "org.jetbrains.research" // Replace with your group ID
            artifactId = "mads-experiments" // Replace with your artifact ID
            version = project.rootProject.version.toString() // Use the root project version

            from(components["kotlin"])
        }
    }
}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
    }
}

description = "Computational neuroscience experiments"

dependencies {
    implementation(project(":mads-core"))
    implementation(project(":mads-ns"))
}

tasks.register("const_current_OneNeuron", JavaExec::class) {
    classpath = sourceSets.getAt("main").runtimeClasspath
    mainClass.set("org.jetbrains.research.mads.experiments.const_current.OneNeuronKt")
}