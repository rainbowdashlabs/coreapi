plugins {
    id("java")
    id("maven-publish")
}

group = "de.sakuramc"

dependencies {
    implementation(libs.sadu.postgresql)
    implementation(libs.sadu.datasource)
    implementation(libs.sadu.queries)
    implementation(libs.jetbrains.annotations)
    implementation("org.postgresql:postgresql:42.7.5")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set("1.0-SNAPSHOT")
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