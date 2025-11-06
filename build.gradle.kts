plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.2.21"
}

group = "io.mikael"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
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
            from(components["java"])
            pom {
                name = "ksoup"
                description = "JSoup DSL for Kotlin"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "mikaelhg"
                        name = "Mikael Gueck"
                        email = "gumi@iki.fi"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mikaelhg/ksoup")
            credentials {
                username = project.findProperty("gpr.user") as String?
                    ?: System.getenv("GPR_USER")
                password = project.findProperty("gpr.key") as String?
                    ?: System.getenv("GPR_TOKEN")
            }
        }
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
