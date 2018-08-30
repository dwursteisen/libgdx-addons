package com.github.dwursteisen.libgdx.test

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.input.RemoteInput
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.madgag.gif.fmsware.AnimatedGifEncoder
import java.awt.image.BufferedImage
import java.util.concurrent.CountDownLatch


internal class DelegateGame(var d: ApplicationListener, var beforeLatch: CountDownLatch, var afterLatch: CountDownLatch) : ApplicationListener {

    private var recording = false

    private var screenshots = emptyList<Pixmap>()
    private var timestamps: List<Long> = emptyList()


    internal fun startRecord() {
        recording = true
    }

    internal fun stopRecord(filename: String) {
        Gdx.app.postRunnable {
            val delay = (timestamps.last() - timestamps.first()) / timestamps.size
            val encoder = AnimatedGifEncoder()

            val external = Gdx.files.absolute(filename)

            encoder.start(external.file().absolutePath)
            encoder.setSize(screenshots.last().width, screenshots.last().height)
            encoder.setDelay(delay.toInt())
            encoder.setRepeat(0)
            // encoder.setTransparent(Color.WHITE)


            val frame = BufferedImage(screenshots.last().width, screenshots.last().height, BufferedImage.TYPE_INT_RGB)
            screenshots.forEach {


                for (x in 0..it.width - 1) {
                    for (y in 0..it.height - 1) {
                        // http://stackoverflow.com/questions/27071351/change-the-color-of-each-pixel-in-an-image-java


                        val currentPixel = it.getPixel(x, y)
                        val red = (currentPixel shr 24 and 0xff)
                        val green = (currentPixel shr 16 and 0xff)
                        val blue = (currentPixel shr 8 and 0xff)

                        // don't understand why shl 8 with red...
                        val rgb = red shl 8 or green shl 8 or blue

                        frame.setRGB(x, y, rgb)
                    }
                }
                encoder.addFrame(frame)
            }
            encoder.finish()
            screenshots = emptyList()
            timestamps = emptyList()
            recording = false
            Gdx.app.log("LIBGDX-TEST", "Recording created at ${external.path()}")
        }
    }

    override fun render() {
        d.render()

        if (recording) {
            val pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)
            val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
            BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
            screenshots += pixmap

            timestamps += System.currentTimeMillis()


        }
    }

    override fun pause() {
        d.pause()
    }

    override fun resume() {
        d.resume()
    }

    override fun resize(width: Int, height: Int) {
        d.resize(width, height)
    }

    private var alreadySetup = false

    override fun create() {
        if (!alreadySetup) {
            setupDelegate()
            alreadySetup = true
        }

        beforeLatch.countDown()
        d.create()
        afterLatch.countDown()
    }

    private fun setupDelegate() {
        Gdx.input = RemoteInput()
    }

    override fun dispose() {
        d.dispose()
    }

    fun restart(listener: ApplicationListener, before: CountDownLatch, after: CountDownLatch) {

        Gdx.app.postRunnable {
            d.dispose()
            before.countDown()
            listener.create()
            after.countDown()

            if (recording) {
                TODO("""You are currently recording a game session. But you haven't stopped the recording.
Please stop the recording using the method 'stopRecordAndSave()' in your test""")
            }

            listener.resize(Gdx.graphics.width, Gdx.graphics.height)
            d = listener
        }

    }

}
