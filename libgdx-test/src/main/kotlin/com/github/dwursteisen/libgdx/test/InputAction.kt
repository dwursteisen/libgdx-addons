package com.github.dwursteisen.libgdx.test

internal sealed class InputAction {
    class Wait(var duration: Float) : InputAction()
    class Push(val key: Int) : InputAction()
    class Release(val key: Int) : InputAction()
    class Type(val char: Char) : InputAction()
    class Touch(val x: Int, val y: Int) : InputAction()
    class Screenshot(val name: String) : InputAction()
    class StartRecord() : InputAction()
    class StopRecord(val filename: String) : InputAction()
    object Quit : InputAction()
}
