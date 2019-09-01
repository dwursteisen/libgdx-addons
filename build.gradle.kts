import groovy.lang.Closure
import internal.ConfigurationPlugin
import org.asciidoctor.gradle.AsciidoctorTask

plugins {
    kotlin
    id("org.asciidoctor.convert") version "2.3.0"
}

tasks {
    create("asciidoctor-media", Copy::class) {
        from(file("src/docs/asciidoc/media"))
        destinationDir = file("docs/media")
    }

    "asciidoctor"(AsciidoctorTask::class) {
        dependsOn("asciidoctor-media")
        outputDir(file("docs"))
        separateOutputDirs = false
        attributes(mapOf(
                "gitCommit" to Git.commit(),
                "versionAshley" to Version.ashley
        ))
    }
}

allprojects {

    apply { plugin(ConfigurationPlugin::class) }

    group = "com.github.dwursteisen.libgdx-addons"
    version = "2.0"
}
