dependencies {
    implementation(project(":admob-addons:admob-core"))
    implementation(kotlin("stdlib"))
    implementation(Dependencies.gdx_lwjgl)
    implementation(Dependencies.gdx_desktop)
    testImplementation(TestDependencies.junit)
}
