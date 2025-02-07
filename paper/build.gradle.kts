plugins {
    `java-library`
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
}

version = "1.0.0"

dependencies {
    implementation(project(":api")) {
        exclude(group = "*", module = "*")
    }
    compileOnly(libs.jetbrains.annotations)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    bukkitLibrary(libs.bundles.sadu)
    bukkitLibrary(libs.dbdriver.postgres)

    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.api)
    compileOnly(libs.paper.api)
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

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.shadowJar)
            artifact(tasks.javadocJar)
            artifact(tasks.sourcesJar)
        }
    }
}
