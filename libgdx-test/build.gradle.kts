dependencies {
    implementation(kotlin("stdlib"))
    implementation(Dependencies.gdx)
    implementation(Dependencies.gdx_lwjgl)
    implementation(Dependencies.gdx_desktop)
    implementation(Dependencies.gif)

    implementation(TestDependencies.junit)
}

tasks.withType<Test>() {
    exclude("com/github/dwursteisen/libgdx/test/**")
}
