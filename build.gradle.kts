plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

group = "io.mikael.ksoup"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
		languageVersion = JavaLanguageVersion.of(17)
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
    implementation("org.jsoup:jsoup:1.16.2")
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
