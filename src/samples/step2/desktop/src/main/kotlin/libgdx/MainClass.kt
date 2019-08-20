
package libgdx

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration


    object MainClass {
        @JvmStatic
        fun main(args: Array<String>) {
            LwjglApplication(MyGame(), LwjglApplicationConfiguration().apply {
                width = 600
                height = 600
            })
        }
}
