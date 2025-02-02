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
    implementation(project(":api"))
}

tasks.test {
    useJUnitPlatform()
}

bukkit {
    name = "paper-coreapi"
    main = "de.sakuramc.paper.coreapi.CorePaperService"

    commands {
        register("language") {
            description = "Change the language"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}