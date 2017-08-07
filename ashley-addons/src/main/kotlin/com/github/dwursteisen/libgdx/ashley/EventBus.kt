package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Vector2


interface EventListener {
    fun onEvent(event: Event, eventData: EventData)
}

data class EventData(var target: Entity? = null, var body: Any? = null)
data class EventTimer(var timer: Float = 0f, val event: Event, val data: EventData)

class EventBus {

    class EventInputProcessor(val bus: EventBus) : InputAdapter() {

        private val keyEventData = EventData()
        private val screenTouchData = EventData(body = Vector2())

        override fun keyDown(keycode: Int): Boolean {
            keyEventData.body = keycode
            bus.emit(StateMachineSystem.EVENT_KEY, keyEventData)
            return super.keyDown(keycode)
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            (screenTouchData.body as Vector2).set(screenX.toFloat(), screenY.toFloat())
            bus.emit(StateMachineSystem.EVENT_TOUCHED, screenTouchData)
            return super.touchDown(screenX, screenY, pointer, button)
        }
    }

    init {
        val currentProcessor = Gdx.input.inputProcessor
        val processor = EventInputProcessor(this)
        if (currentProcessor == null) {
            Gdx.input.inputProcessor = processor
        } else {
            Gdx.input.inputProcessor = InputMultiplexer(processor, currentProcessor)
        }
    }

    private val NO_DATA = EventData()

    private var listeners: MutableMap<Event, EventListener> = mutableMapOf()

    private var emitter: MutableList<Pair<Event, EventData>> = mutableListOf()

    private var emitterLatter: MutableList<EventTimer> = mutableListOf()

    fun emit(event: Event, entity: Entity, data: EventData = NO_DATA): Unit {
        emit(event, data.copy(target = entity))
    }

    fun emit(event: Event, data: EventData = NO_DATA): Unit {
        emitter.add(event to data)
    }

    fun emitLater(delta: Float, event: Event, entity: Entity, data: EventData = NO_DATA): Unit {
        emitLater(delta, event, data.copy(target = entity))
    }

    fun emitLater(delta: Float, event: Event, data: EventData = NO_DATA): Unit {
        val timer = EventTimer(delta, event, data)
        emitterLatter.add(timer)
    }


    fun register(eventListener: EventListener, vararg events: Event): Unit {
        events.forEach { listeners.put(it, eventListener) }
    }


    fun update(delta: Float) {

        emitterLatter.forEach { it.timer -= delta }
        val toEmitNow = emitterLatter.filter { it.timer < 0 }

        toEmitNow.forEach { listeners[it.event]?.onEvent(it.event, it.data) }
        emitter.forEach { listeners[it.first]?.onEvent(it.first, it.second) }

        emitterLatter.removeAll(toEmitNow)
        emitter.clear()

    }
}