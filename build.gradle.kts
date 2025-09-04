plugins {
    id("java")
}

allprojects {
    group = "me.bottdev"
    version = "0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

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
}
