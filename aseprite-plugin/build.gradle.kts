plugins {
    kotlin("jvm")
    `maven-publish`

}

dependencies {
    compile(kotlin("stdlib"))
    compile(gradleApi())
    testCompile("junit:junit:4.12")
}
