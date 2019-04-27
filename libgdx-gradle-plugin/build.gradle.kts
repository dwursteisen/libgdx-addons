repositories {
    google()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    api(project(":packr-gradle-plugin"))
    api(project(":assets-gradle-plugin"))
    api(Plugins.intellij)
    api(Plugins.kotlin_gradle)
    api(Plugins.download)
    api(Plugins.android)
    testImplementation(TestDependencies.junit)
}
