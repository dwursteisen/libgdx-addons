repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}
dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    api(Plugins.intellij)
    testImplementation(TestDependencies.junit)
}
