plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.2.21"
}

group = "io.mikael.ksoup"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jsoup:jsoup:1.21.2")
    testImplementation("io.undertow:undertow-core:2.3.20.Final")
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.mikaelhg"
            artifactId = "ksoup"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
