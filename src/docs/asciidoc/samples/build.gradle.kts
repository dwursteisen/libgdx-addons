import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
}

group = "group.id.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

buildscript {
    dependencies {
        classpath("com.github.dwursteisen.libgdx-addons:libgdx-gradle-plugin:b190c3a")
    }

    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://jitpack.io") }
    }
}

apply(plugin = "libgdx")

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://jitpack.io") }
    }
}

project(":core") {
    dependencies {
        implementation("com.github.dwursteisen.libgdx-addons:core-addons:b190c3a")
        implementation("com.github.dwursteisen.libgdx-addons:ashley-addons:b190c3a")
    }
}
