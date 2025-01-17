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
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.google.code.gson:gson:2.11.0")

    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
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
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
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