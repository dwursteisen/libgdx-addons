package step2

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// tag::body[]
class MyGame : Game() {

    private lateinit var texture: Texture

    private lateinit var batch: SpriteBatch

    override fun create() {
        texture = Texture(Assets.assets_dungeon_sheet_png)
        batch = SpriteBatch()
    }

    override fun render() {
        batch.begin()
        batch.draw(texture, 0f, 0f)
        batch.end()
    }
}
// end::body[]
