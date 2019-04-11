repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("com.badlogicgames.packr:packr:2.1-SNAPSHOT")
    testImplementation(TestDependencies.junit)
}
