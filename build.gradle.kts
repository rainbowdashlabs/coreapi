import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"

    `maven-publish`
}

group = "de.sakuramc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.simplecloud.app/releases")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("org.jetbrains:annotations:26.0.1")
    implementation("de.chojo.sadu", "sadu-mysql", "2.3.1")
    implementation("de.chojo.sadu", "sadu-datasource", "2.3.1")
    implementation("de.chojo.sadu", "sadu-queries", "2.3.1")

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    mergeServiceFiles()
}

publishing {
    repositories {
        maven {
            name = "myDomainRepository"
            url = uri("https://dev.sakuramc.de/releases")
            credentials {
                username = "root"
                password = "WT6V69oVnPHOcTfX8WPg5wGniQpxHYOGg9rttFb/7DYVahBL/XZWDjqJ5rCJQSDI"
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.sakuramc"
            artifactId = "sakuraapi"
            version = "1.0.0"
            from(components["java"])
        }
    }
}