package step4

import Assets
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.dwursteisen.libgdx.TextureSplitter
import com.github.dwursteisen.libgdx.ashley.*
import com.github.dwursteisen.libgdx.graphics.RefreshableTextureLoader
import com.github.dwursteisen.libgdx.v2

const val SPRITE = 1

// tag::body[]
class MyGame : Game() {

    private val assetManager: AssetManager = AssetManager()

    // <1>
    private val engine = Engine()

    private val viewport: Viewport = FitViewport(200f, 200f)

    override fun create() {
        assetManager.setLoader(Texture::class.java, RefreshableTextureLoader(InternalFileHandleResolver()))
        assetManager.load(Assets.assets_dungeon_sheet_png, Texture::class.java)
        assetManager.finishLoading()

        // <2>
        engine.addSystem(RenderSystem(viewport,
            mapOf(SPRITE to SpriteStrategy())
        ))
        engine.addSystem(PlayerSystem())

        // <3>
        val split = TextureSplitter(assetManager).split(
            Assets.assets_dungeon_sheet_png, 16, 16
        )
        val playerSprite = split.get(column = 19, row = 7)

        // <4>
        val player = engine.createEntity().apply {
            add(Player())
            add(Position())
            add(Size(16 v2 16))
            add(Textured(texture = playerSprite))
            add(Render(SPRITE))
        }
        engine.addEntity(player)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // <5>
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
// end::body[]
