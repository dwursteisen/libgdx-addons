repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(project(":commons-gradle-plugin"))
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("com.github.dwursteisen:packr:4680924076")
    testImplementation(TestDependencies.junit)
}
