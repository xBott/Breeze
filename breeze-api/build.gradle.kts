plugins {
    id("com.gradleup.shadow") version "8.3.0"
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.19.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
    implementation("org.apache.commons:commons-lang3:3.18.0")
}


tasks.shadowJar {
    archiveClassifier.set("all")
}

tasks.build {
    dependsOn("shadowJar")
}