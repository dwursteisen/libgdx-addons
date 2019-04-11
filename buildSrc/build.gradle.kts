plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.30")
    implementation("com.ofg:uptodate-gradle-plugin:+")
}