plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(gradleApi())
    testCompile("junit:junit:4.12")
}
