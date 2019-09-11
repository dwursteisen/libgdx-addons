package step5

import Assets
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.dwursteisen.libgdx.TextureSplitter
import com.github.dwursteisen.libgdx.ashley.*
import com.github.dwursteisen.libgdx.getValue
import com.github.dwursteisen.libgdx.graphics.RefreshableTextureLoader
import com.github.dwursteisen.libgdx.scanObjects
import com.github.dwursteisen.libgdx.v2
import step4.Player
import step4.PlayerSystem
import step4.SpriteStrategy

const val SPRITE = 1

// tag::body[]
class MyGame : Game() {

    private val assetManager: AssetManager = AssetManager()

    private val engine = Engine()

    private val viewport: Viewport = FitViewport(200f, 200f)

    // <1>
    private lateinit var eventBus: EventBus

    override fun create() {

        eventBus = EventBus()

        assetManager.setLoader(Texture::class.java, RefreshableTextureLoader(InternalFileHandleResolver()))
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))

        assetManager.load(Assets.assets_dungeon_sheet_png, Texture::class.java)
        assetManager.load(Assets.assets_dungeon_tmx, TiledMap::class.java)

        assetManager.finishLoading()

        viewport.camera.position.set(viewport.worldWidth * 0.5f, viewport.worldHeight * 0.5f, 0f)

        val split = TextureSplitter(assetManager).split(
            Assets.assets_dungeon_sheet_png, 16, 16
        )
        val playerSprite = split.get(column = 19, row = 7)

        val map: TiledMap = assetManager[Assets.assets_dungeon_tmx]

        map.layers["objs"].scanObjects { x, y, objs ->
            val type: String by objs.properties
            if (type == "player") {
                val player = engine.createEntity().apply {
                    add(Player())
                    add(Position(x v2 y))
                    add(Size(16 v2 16))
                    add(Textured(texture = playerSprite))
                    add(Render(SPRITE))
                }
                engine.addEntity(player)
            }
        }

        engine.addSystem(PlayerSystem())
        engine.addSystem(
            RenderSystem(
                viewport,
                mapOf(SPRITE to SpriteStrategy())
            )
        )
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        engine.update(Gdx.graphics.deltaTime)
        // <3>
        eventBus.update(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
// end::body[]
