plugins {
    `java-library`
}

version = "1.0.0"

dependencies {
    api(libs.bundles.sadu)
    api(libs.dbdriver.postgres)
    compileOnlyApi(libs.jetbrains.annotations)

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
