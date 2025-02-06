plugins {
    id("java")
    id("maven-publish")
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "de.sakuramc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":api"))
    implementation(libs.jetbrains.annotations)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    bukkitLibrary(libs.sadu.postgresql)
    bukkitLibrary(libs.sadu.queries)
    bukkitLibrary(libs.sadu.datasource)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    compileOnly(libs.paper.api)
}

tasks.test {
    useJUnitPlatform()
}

bukkit {
    name = "paper-coreapi"
    main = "de.sakuramc.coreapi.paper.CorePaperService"
    apiVersion = "1.21"

    commands {
        register("language") {
            description = "Change the language"
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveVersion.set("1.0.0")
        archiveBaseName.set("paper-coreapi")
    }
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