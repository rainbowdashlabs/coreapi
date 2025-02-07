plugins {
    java
    `maven-publish`
}

group = "de.sakuramc.core"
version = "1.0.0"

allprojects {
    group = rootProject.group
    apply {
        plugin<JavaPlugin>()
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withJavadocJar()
        withSourcesJar()
    }
}

subprojects {
    apply {
        plugin<MavenPublishPlugin>()
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
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}
