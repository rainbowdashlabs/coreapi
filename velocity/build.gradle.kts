plugins {
    id("java")
}

group = "de.sakuramc"

tasks.test {
    useJUnitPlatform()
}