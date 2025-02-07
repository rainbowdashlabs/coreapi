plugins {
    `java-library`
    alias(libs.plugins.shadow)
}

dependencies{
    compileOnly(libs.velocity.api)
    implementation(project(":api"))
    compileOnlyApi(project(":api"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            // We still publish the non shaded jar. The api jar is the retrieved via dependency resolution.
            // For running the paper plugin use the "all" classified jar, which is additionally published for convenience
            from(components["java"])
        }
    }
}
