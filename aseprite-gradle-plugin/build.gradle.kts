dependencies {
    implementation(project(":commons-gradle-plugin"))
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    testImplementation(TestDependencies.junit)
}
