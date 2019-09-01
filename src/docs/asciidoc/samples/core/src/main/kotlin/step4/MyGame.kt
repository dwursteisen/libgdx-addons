package step4

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.github.dwursteisen.libgdx.get
import com.github.dwursteisen.libgdx.graphics.RefreshableTextureLoader

// tag::body[]
class MyGame : Game() {

    private val assetManager: AssetManager = AssetManager()

    private val engine = Engine()

    private lateinit var batch: SpriteBatch

    override fun create() {
        // <1>
        assetManager.setLoader(Texture::class.java, RefreshableTextureLoader(InternalFileHandleResolver()))
        assetManager.load(Assets.assets_dungeon_sheet_png, Texture::class.java)
        // <2>
        assetManager.finishLoading()
        batch = SpriteBatch()
    }

    override fun render() {

        engine.update(Gdx.graphics.deltaTime)

        // <3>
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // <4>
        val texture: Texture = assetManager[Assets.assets_dungeon_sheet_png]
        batch.begin()
        batch.draw(texture, 0f, 0f)
        batch.end()
    }
}
// end::body[]
