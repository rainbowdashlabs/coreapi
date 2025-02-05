plugins {
    id("java")
    //id("com.gradleup.shadow") version "8.3.5" apply false

    `maven-publish`
}

group = "de.sakuramc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")
        implementation("org.jetbrains:annotations:26.0.1")
    }
}