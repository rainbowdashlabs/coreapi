plugins {
    id("java")
}

group = "de.sakuramc"

repositories {
    mavenCentral()
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}