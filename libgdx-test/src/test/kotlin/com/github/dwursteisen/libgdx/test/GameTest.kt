package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.junit.Rule
import org.junit.Test
import java.time.Duration

class GameTest {

    @JvmField
    @Rule
    val gdx = LibGdxRule(GameUnderTest(), LwjglApplicationConfiguration().apply {
        width = 200
        height = 200
    })


    @Test
    fun runThenScreenshot() {
        gdx.startGame()
                .wait(Duration.ofSeconds(1))
                .screenshot("test1.png")
                .push(Input.Keys.UP)
                .wait(Duration.ofSeconds(1))
                .release(Input.Keys.UP)
                .screenshot("test2.png")
        //.wait(Duration.ofSeconds(1))
        //.screenshot("test3.png")
    }

    @Test
    fun runOtherTest() {
        gdx.startGame()
                .wait(Duration.ofSeconds(1))
                .screenshot("test4.png")

    }

}