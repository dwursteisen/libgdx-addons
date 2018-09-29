
plugins {
    kotlin("jvm")
    `maven-publish`

}

dependencies {

    val gdxVersion: String by project.extra

    compile(project(":admob-addons:admob-core"))
    compile("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
    compile("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    testCompile("junit:junit:4.12")
}
