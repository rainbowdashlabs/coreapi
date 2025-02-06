plugins {
    `java-library`
    alias(libs.plugins.shadow)
    id("java")

    `maven-publish`
}

group = "de.sakuramc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}