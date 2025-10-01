import io.github.klahap.dotenv.DotEnvBuilder

plugins {
    id("java")
    id("io.github.klahap.dotenv") version "1.1.3"
    id("maven-publish")
}

allprojects {
    group = "me.bottdev"
    version = "0.1.8"

    repositories {
        mavenCentral()
    }
}

val envVars = DotEnvBuilder.dotEnv {
    addFile("$rootDir/.env")
}
extra["envVars"] = envVars

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.github.klahap.dotenv")
    apply(plugin = "maven-publish")

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        implementation("org.projectlombok:lombok:1.18.38")
        annotationProcessor("org.projectlombok:lombok:1.18.38")

        implementation("org.slf4j:slf4j-api:2.0.13")
        implementation("ch.qos.logback:logback-classic:1.5.6")
    }

    tasks.test {
        useJUnitPlatform()
    }

    val envVars = rootProject.extra["envVars"] as Map<String, String>

    publishing {
        repositories {
            maven {
                name = project.name
                url = uri("https://repo.the-light.online/releases")
                credentials {
                    username = envVars["REPO_USERNAME"].toString()
                    password = envVars["REPO_PASSWORD"].toString()
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "me.bottdev"
                artifactId = project.name
                version = "${project.version}"
                from(components["java"])
            }
        }
    }
}
