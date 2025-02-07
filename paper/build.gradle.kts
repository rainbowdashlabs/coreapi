plugins {
    `java-library`
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":api")) {
        exclude(group = "*", module = "*")
    }
    compileOnlyApi(project(":api"))
    compileOnly(libs.jetbrains.annotations)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    bukkitLibrary(libs.bundles.sadu)
    bukkitLibrary(libs.dbdriver.postgres)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    compileOnly(libs.paper.api)
}

tasks {
    build {
        // Shadow jar will be run every time we build
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

bukkit {
    name = "paper-coreapi"
    main = "de.sakuramc.coreapi.paper.CorePaperService"
    apiVersion = "1.21"

    commands {
        register("language") {
            description = "Change the language"
        }
    }
}
