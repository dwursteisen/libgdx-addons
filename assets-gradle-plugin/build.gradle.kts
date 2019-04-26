apply { plugin("java-gradle-plugin") }

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation(Dependencies.kotlinpoet)
    testImplementation(TestDependencies.junit)
    testImplementation(gradleTestKit())
}
