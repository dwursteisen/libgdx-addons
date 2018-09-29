plugins {
    kotlin("jvm")
    `maven-publish`

}


dependencies {

    val ashleyVersion: String by project.extra
    val ktxVersion: String by project.extra

    compile(kotlin("stdlib"))
    compile("com.badlogicgames.ashley:ashley:$ashleyVersion")
    compile("io.github.libktx:ktx-log:$ktxVersion")

    testCompile("org.assertj:assertj-core:3.8.0")
    testCompile("org.mockito:mockito-core:2.8.47")
}
