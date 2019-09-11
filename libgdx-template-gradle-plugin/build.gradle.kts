apply {
    plugin("java-gradle-plugin")
    plugin("java")
}

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    jcenter()
}

configure<SourceSetContainer> {
    val core = create("core")
    create("desktop") {
        compileClasspath += core.output
    }
    create("android") {
        compileClasspath += core.output
    }

    main {
        resources {
            srcDir("src")
            // exclude the source code of the plugin itself
            exclude("main/**")
        }
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(gradleApi())
    implementation(project(":commons-gradle-plugin"))

    // -- only for template compiling -- //
    "coreImplementation"("com.badlogicgames.gdx:gdx:${Version.gdx}")
    "coreImplementation"("org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}")

    "desktopImplementation"("com.badlogicgames.gdx:gdx-backend-lwjgl:${Version.gdx}")
    "desktopImplementation"("org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}")
    "desktopImplementation"("com.badlogicgames.gdx:gdx-platform:${Version.gdx}:natives-desktop")

    testImplementation(TestDependencies.junit)
    testImplementation(gradleTestKit())
}
