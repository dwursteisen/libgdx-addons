package step1

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


object MainClass {
    @JvmStatic
    fun main(args: Array<String>) {
        LwjglApplication(TODO("Replace With Your Game Class"), LwjglApplicationConfiguration().apply {
            width = 600
            height = 600
        })
    }
}
