pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.21"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Breeze"
include("breeze-core")
include("breeze-processor")
include("breeze-paper")
include("breeze-api")
include("breeze-admin")