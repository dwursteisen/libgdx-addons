apply { plugin("java-gradle-plugin") }

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
    jcenter()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    // for custom packr depency.
    maven {
        url = uri("https://jitpack.io")
    }
}
dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(gradleApi())
    api(project(":packr-gradle-plugin"))
    api(project(":assets-gradle-plugin"))
    api(Plugins.intellij)
    api(Plugins.kotlin_gradle)
    api(Plugins.download)
    api(Plugins.android)
    testImplementation(TestDependencies.junit)
    testImplementation(gradleTestKit())
}
