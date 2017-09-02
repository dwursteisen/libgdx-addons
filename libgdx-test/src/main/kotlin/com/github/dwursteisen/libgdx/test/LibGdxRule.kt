package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.input.RemoteInput
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class LibGdxRule(val listener: ApplicationListener, val configuration: LwjglApplicationConfiguration, val output: String = LibGdxRule::class.java.getResource(".").path) : TestRule {


    companion object {
        private var executor: Thread? = null
        private var delegate: DelegateGame? = null
        private var app: LwjglApplication? = null
        private val sender: RemoteSender by lazy { RemoteSender("127.0.0.1", RemoteInput.DEFAULT_PORT) }
        private var queue = emptySequence<InputAction>()
        private var killMe = AtomicBoolean(false)
        private val isRunning = AtomicBoolean(true)
        private var quitLatch: CountDownLatch = CountDownLatch(1)
    }

    /**
     * Start your game.
     *
     * It can be quite long but all commands will be started after the game is fully started.
     */
    fun startGame(): GdxDsl = GdxDsl(this)


    internal fun add(action: InputAction) {
        queue += action
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {

                killMe.set(false)

                val before = CountDownLatch(1)
                val after = CountDownLatch(1)

                if (delegate == null) {
                    delegate = DelegateGame(listener, before, after)

                    app = LwjglApplication(delegate, configuration)
                } else {
                    Gdx.app.postRunnable {
                        delegate!!.restart(listener, before, after)
                    }
                }
                before.await()

                quitLatch = CountDownLatch(1)
                if (executor == null) {
                    val thread = Thread() {
                        while (isRunning.get()) {
                            queue.firstOrNull()?.let { action ->
                                when (action) {
                                    is InputAction.Wait -> {
                                        action.duration -= 0.010f
                                        if (action.duration < 0f) {
                                            queue = queue.drop(1)
                                        }
                                    }
                                    is InputAction.Push -> {
                                        sender.keyDown(action.key)
                                        queue = queue.drop(1)

                                    }
                                    is InputAction.Release -> {
                                        sender.keyUp(action.key)
                                        queue = queue.drop(1)

                                    }
                                    is InputAction.Type -> {
                                        sender.keyTyped(action.char)
                                        queue = queue.drop(1)
                                    }

                                    is InputAction.Touch -> {
                                        sender.touchDown(action.x, action.y, 1, Input.Buttons.LEFT)
                                        queue = queue.drop(1)
                                    }

                                    is InputAction.Screenshot -> {
                                        Gdx.app.postRunnable {
                                            val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)
                                            val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
                                            BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)

                                            Gdx.app.log("LIBGDX-TEST", "Start build screenshot")
                                            val external = Gdx.files.absolute(output + action.name)
                                            PixmapIO.writePNG(external, pixmap)
                                            pixmap.dispose()
                                            Gdx.app.log("LIBGDX-TEST", "Screenshot created at ${external.path()}")
                                        }
                                        queue = queue.drop(1)
                                    }


                                    is InputAction.Quit -> {
                                        quitLatch.countDown()
                                        queue = queue.drop(1)
                                    }
                                }
                            }
                            sender.sendUpdate()

                            Thread.sleep(10)
                        }
                    }
                    executor = thread
                    thread.start()
                }
                after.await()
                base.evaluate()
                add(InputAction.Wait(1f))
                add(InputAction.Quit)
                quitLatch.await()

                killMe.set(true)

                Executors.newSingleThreadScheduledExecutor().schedule({
                    if (killMe.get()) {
                        isRunning.set(false)
                        app?.stop()
                    }
                }, 3, TimeUnit.SECONDS)

            }

        }
    }

}


