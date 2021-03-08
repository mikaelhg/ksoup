import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java`
    `maven-publish`
    kotlin("jvm") version "1.4.31"
}

group = "io.mikael.ksoup"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
		languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jsoup:jsoup:1.13.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
