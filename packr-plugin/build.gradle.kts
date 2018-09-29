plugins {
    kotlin("jvm")
    `maven-publish`

}

dependencies {
    compile(kotlin("stdlib"))
    compile(gradleApi())
    compile("com.github.libgdx:packr:ef4035e392")
    testCompile("junit:junit:4.12")
}
