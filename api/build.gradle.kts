plugins {
    id("java")
}

group = "de.sakuramc"

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.chojo.sadu", "sadu-postgresql", "2.3.1")
    implementation("de.chojo.sadu", "sadu-datasource", "2.3.1")
    implementation("de.chojo.sadu", "sadu-queries", "2.3.1")
    implementation("net.kyori:adventure-api:4.18.0")
    implementation("net.kyori:adventure-text-minimessage:4.18.0")
    implementation("org.postgresql:postgresql:42.7.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}