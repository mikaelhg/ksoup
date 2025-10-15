plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.2.20"
    id("org.jetbrains.kotlinx.kover") version "0.9.2"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jsoup:jsoup:1.21.2")
    testImplementation("io.undertow:undertow-core:2.3.20.Final")
    testImplementation("org.junit.platform:junit-platform-suite-engine:6.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
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
