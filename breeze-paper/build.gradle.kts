plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    mavenCentral()
}

dependencies {
    implementation(project(":breeze-core"))
    implementation(project(":breeze-api"))
    annotationProcessor(project(":breeze-processor"))

    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")
}