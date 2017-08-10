package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Vector2


interface EventListener {
    fun onEvent(event: Event, eventData: EventData)
}

// FIXME: I should be abble to target multiple stuff at once
data class EventData(var event: Int = Int.MIN_VALUE, var target: Entity? = null, var body: Any? = null)
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

    private var listeners: MutableMap<Event, List<EventListener>> = mutableMapOf()

    private var emitter: MutableList<Pair<Event, EventData>> = mutableListOf()
    private var emitterMirror: MutableList<Pair<Event, EventData>> = mutableListOf()

    private var emitterLatter: MutableList<EventTimer> = mutableListOf()
    private var emitterLatterMirror: MutableList<EventTimer> = mutableListOf()

    fun emit(event: Event, entity: Entity, data: EventData = NO_DATA): Unit {
        emit(event, data.copy(target = entity))
    }

    fun emit(event: Event, data: EventData = NO_DATA): Unit {
        emitter.add(event to data.copy(event = event))
    }

    fun emitLater(delta: Float, event: Event, entity: Entity, data: EventData = NO_DATA): Unit {
        emitLater(delta, event, data.copy(target = entity))
    }

    fun emitLater(delta: Float, event: Event, data: EventData = NO_DATA): Unit {
        val timer = EventTimer(delta, event, data.copy(event = event))
        emitterLatter.add(timer)
    }


    fun register(eventListener: EventListener, vararg events: Event): Unit {
        events.forEach {
            listeners.compute(it, { evt, lst ->
                if (lst == null) {
                    listOf(eventListener)
                } else {
                    lst + eventListener
                }
            })
        }
    }


    fun update(delta: Float) {

        emitterLatter.forEach { it.timer -= delta }
        val toEmitNow = emitterLatter.filter { it.timer < 0 }

        emitterLatterMirror.addAll(toEmitNow)
        emitterMirror.addAll(emitter)

        emitterLatterMirror.forEach { listeners[it.event]?.forEach({ lst -> lst.onEvent(it.event, it.data) }) }
        emitterMirror.forEach { listeners[it.first]?.forEach({ lst -> lst.onEvent(it.first, it.second) }) }

        emitterLatter.removeAll(toEmitNow)
        emitter.clear()

        emitterMirror.clear()
        emitterLatterMirror.clear()

    }
}