rootProject.name = "mads"

include(
    ":mads-core",
    ":mads-ns",
    ":mads-examples"
)

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}
