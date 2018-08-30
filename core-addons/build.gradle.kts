plugins {
    kotlin("jvm")
}

dependencies {
    val gdxVersion: String by project.extra

    compile(kotlin("stdlib"))
    compile("com.badlogicgames.gdx:gdx:$gdxVersion")
    testCompile("junit:junit:4.12")
}
