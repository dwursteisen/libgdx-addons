apply { plugin("java-gradle-plugin") }

dependencies {
    implementation(project(":commons-gradle-plugin"))
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation(Dependencies.kotlinpoet)
    testImplementation(TestDependencies.junit)
    testImplementation(gradleTestKit())
}
