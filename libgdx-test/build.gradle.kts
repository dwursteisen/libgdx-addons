import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`

}

dependencies {
    val gdxVersion: String by project.extra

    compile(kotlin("stdlib"))
    compile("com.badlogicgames.gdx:gdx:$gdxVersion")
    compile("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    compile("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    compile("com.madgag:animated-gif-lib:1.0")

    compile("junit:junit:4.12")
}

tasks.withType<Test>() {
    exclude("com/github/dwursteisen/libgdx/test/**")
}
