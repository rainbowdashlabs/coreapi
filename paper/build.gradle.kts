import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("maven-publish")
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
}

group = "de.sakuramc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    shadow(project(":api"))
}

tasks.test {
    useJUnitPlatform()
}

bukkit {
    name = "paper-coreapi"
    main = "de.sakuramc.coreapi.paper.CorePaperService"
    apiVersion = "1.13"

    commands {
        register("language") {
            description = "Change the language"
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<Jar> {
    from(project(":api").sourceSets["main"].output)
}


tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    mergeServiceFiles()
}

publishing {
    repositories {
        maven {
            name = "sakuraRepository"
            url = uri("https://dev.sakuramc.de/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "de.sakuramc.coreapi"
            artifactId = "paper"
            version = "1.0.0"
            from(components["java"])
        }
    }
}