import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.61")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.2.61")
    }
}

plugins {
    base
    kotlin("jvm") version "1.2.61" apply false
}

allprojects {

    val gdxVersion by extra { "1.9.6" }
    val jUnitVersion by extra { "4.12" }
    val mockitoVersion by extra { "1.10.19" }
    val ashleyVersion by extra { "1.7.3" }
    val ktxVersion by extra { "1.9.8-b3" }

    group = "com.github.dwursteisen.libgdx-addons"
    version = "1.0"

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
}

// Configure all KotlinCompile tasks on each sub-project
subprojects {

    tasks.withType<KotlinCompile>().forEach {
        println("Configuring $name in project ${project.name}...")
        it.kotlinOptions {
            suppressWarnings = true
            jvmTarget = "1.6"
        }
    }
}
