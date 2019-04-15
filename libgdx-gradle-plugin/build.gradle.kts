repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    api(project(":packr-gradle-plugin"))
    api(Plugins.intellij)
    api(Plugins.download)
    testImplementation(TestDependencies.junit)
}
