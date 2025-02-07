plugins {
    `java-library`
}

version = "1.0.0"

dependencies {
    api(libs.bundles.sadu)
    compileOnlyApi(libs.jetbrains.annotations)
    api(libs.dbdriver.postgres)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
