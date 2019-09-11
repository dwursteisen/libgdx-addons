package libgdx

import com.badlogic.gdx.Game

class GameClass : Game() {
    override fun create() {
        setScreen(MyFirstScreen())
    }
}
