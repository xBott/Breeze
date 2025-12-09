import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer
import kotlin.jvm.java

plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    mavenCentral()
}

dependencies {

    paperweight.paperDevBundle("1.21.7-R0.1-SNAPSHOT")

    annotationProcessor(project(":breeze-processor"))
    implementation(project(":breeze-core"))
    implementation(project(":breeze-api"))

}

tasks.build {
    configurations.annotationProcessor.let { an ->
        an.configure {

        }
    }
    dependsOn("shadowJar")
}

tasks.shadowJar {
    transform(ServiceFileTransformer::class.java)
    dependencies {
        include(project(":breeze-core"))
        include(project(":breeze-api"))
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}