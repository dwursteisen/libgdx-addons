package step5

import Assets
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
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
import step4.*

const val SPRITE = 1
const val MAP = 2

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

        engine.addSystem(PlayerSystem())
        engine.addSystem(SwitchSystem(eventBus))
        engine.addSystem(DoorSystem(eventBus))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        engine.addSystem(
            RenderSystem(
                viewport,
                mapOf(
                    SPRITE to SpriteStrategy(),
                    MAP to MapLayerStrategy(viewport)
                )
            )
        )

        val split = TextureSplitter(assetManager).split(
            Assets.assets_dungeon_sheet_png, 16, 16
        )
        val playerSprite = split.get(column = 19, row = 7)
        val doorAnimation = split.animations(
            0.1f,
            4 to 6,
            3 to 6,
            2 to 6,
            1 to 6,
            0 to 6
        )
        val switchSprite = split.animations(
            0.1f,
            0 to 5,
            1 to 5,
            2 to 5,
            3 to 5,
            4 to 5,
            5 to 5,
            6 to 5
        )

        val map: TiledMap = assetManager[Assets.assets_dungeon_tmx]

        map.layers["objs"].scanObjects { x, y, objs ->
            val type: String by objs.properties
            when (type) {
                "player" -> {
                    val player = engine.createEntity().apply {
                        add(Player())
                        add(Position(x v2 y))
                        add(Size(16 v2 16))
                        add(Textured(texture = playerSprite))
                        add(Render(SPRITE))
                    }
                    engine.addEntity(player)
                }
                "switch" -> {
                    val switch = engine.createEntity().apply {
                        add(Switch())
                        add(Position(x v2 y))
                        add(Size(16 v2 16))
                        add(Textured())
                        add(Animated(animation = switchSprite))
                        add(Render(SPRITE))
                        add(StateComponent())
                    }
                    engine.addEntity(switch)
                }
                "door" -> {
                    val door = engine.createEntity().apply {
                        add(Door())
                        add(Position(x v2 y))
                        add(Size(16 v2 16))
                        add(Textured())
                        add(Animated(animation = doorAnimation))
                        add(Render(SPRITE))
                        add(StateComponent())
                    }
                    engine.addEntity(door)
                }
            }
        }

        val background = engine.createEntity().apply {
            add(Render(MAP))
            add(MapLayerComponent(zLevel = 0, layer = map.layers["background"]))
        }
        engine.addEntity(background)

        val dungeon = engine.createEntity().apply {
            add(Render(MAP))
            add(MapLayerComponent(zLevel = 1, layer = map.layers["dungeon"]))
        }
        engine.addEntity(dungeon)


    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // <3>
        eventBus.update(Gdx.graphics.deltaTime)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}
// end::body[]
