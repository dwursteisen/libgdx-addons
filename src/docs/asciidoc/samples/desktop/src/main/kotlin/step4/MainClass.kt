package step4

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import step5.MyGame

// tag::body[]
object MainClass {
    @JvmStatic
    fun main(args: Array<String>) {
        LwjglApplication(MyGame(), LwjglApplicationConfiguration().apply {
            width = 600
            height = 600
        })
    }
}
// end::body[]
