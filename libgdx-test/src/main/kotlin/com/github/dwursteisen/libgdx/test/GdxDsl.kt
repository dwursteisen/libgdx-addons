package com.github.dwursteisen.libgdx.test

import java.time.Duration


class GdxDsl(private val rule: LibGdxRule) {

    /**
     * Wait a specific amount of time.
     *
     * <code>
     *       dsl.wait(Duration.ofSeconds(1))
     * </code>
     */
    fun wait(duration: Duration): GdxDsl {
        rule.add(InputAction.Wait(duration.toMillis().toFloat() / 1000f))
        return this
    }

    /**
     * Push the key. Please note that you'll have to release the key by yourself !
     *
     * @key : a Input.Keys code
     */
    fun push(key: Int): GdxDsl {
        rule.add(InputAction.Push(key))
        return this
    }


    /**
     * Push the key and release the key.
     *
     * @key : a Input.Keys code
     */
    fun press(key: Int): GdxDsl {
        rule.add(InputAction.Push(key))
        rule.add(InputAction.Release(key))
        return this
    }

    /**
     * Release a key previously pushed
     *
     * @key : a Input.Keys code
     */
    fun release(key: Int): GdxDsl {
        rule.add(InputAction.Release(key))
        return this
    }

    fun touch(): GdxDsl {
        notYetImplemented()
    }


    /**
     * type characters
     */
    fun type(str: String): GdxDsl {
        str.forEach { char ->
            rule.add(InputAction.Type(char))
        }
        return this
    }

    /**
     * Touch on the screen (x, y) coordinate
     *
     */
    fun touch(x: Int, y: Int): GdxDsl {
        rule.add(InputAction.Touch(x, y))
        return this;
    }
    
    fun startRecord(): GdxDsl {
        rule.add(InputAction.StartRecord())
        return this;
    }

    fun stopAndSaveRecord(filename: String): GdxDsl {
        rule.add(InputAction.StopRecord(filename))
        return this
    }

    /**
     * Take a screenshot and save it using the name
     */
    fun screenshot(name: String): GdxDsl {
        rule.add(InputAction.Screenshot(name))
        return this
    }


    private fun notYetImplemented(): Nothing {
        TODO("""Not yet implemented yet ! Feel free to create a pull request on github for it
                |
                |You can get more info on https://github.com/dwursteisen/libgdx-addons/tree/master/libgdx-test
            """.trimMargin())
    }
}
