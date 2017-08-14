package com.github.dwursteisen.libgdx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.log.debug


interface EventListener {
    fun onEvent(event: Event, eventData: EventData)
}

// FIXME: I should be abble to target multiple stuff at once
class EventData(var event: Int = Int.MIN_VALUE, var target: Entity? = null, var body: Any? = null) : Pool.Poolable {
    override fun reset() {
        event = Int.MIN_VALUE
        target = null
        body = null
    }
}

data class EventTimer(var timer: Float = 0f, val event: Event, val data: EventData)


/**
 * Simple Event bus.
 *
 * - register listener through register method
 * - The update method should be called from the main loop
 *
 *
 */
class EventBus(val eventMapper: Map<Int, String> = emptyMap()) {

    private val pool: Pool<EventData> = object : Pool<EventData>() {
        override fun newObject(): EventData = EventData()
    }

    class EventInputProcessor(val bus: EventBus) : InputAdapter() {

        private val touch = Vector2()

        override fun keyDown(keycode: Int): Boolean {
            val keyEventData = bus.createEventData()
            keyEventData.body = keycode
            bus.emit(StateMachineSystem.EVENT_KEY, keyEventData)
            return super.keyDown(keycode)
        }

        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {

            val screenTouchData = bus.createEventData()
            touch.set(screenX.toFloat(), screenY.toFloat())
            screenTouchData.body = touch
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

    private var listeners: MutableMap<Event, List<EventListener>> = mutableMapOf()

    private var emitter: MutableList<Pair<Event, EventData>> = mutableListOf()
    private var emitterMirror: MutableList<Pair<Event, EventData>> = mutableListOf()

    private var emitterLatter: MutableList<EventTimer> = mutableListOf()
    private var emitterLatterMirror: MutableList<EventTimer> = mutableListOf()

    fun createEventData(): EventData = pool.obtain()

    fun emit(event: Event, entity: Entity, data: EventData = createEventData()): Unit {
        data.target = entity
        emit(event, data)
    }

    fun emit(event: Event, data: EventData = createEventData()): Unit {
        data.event = event
        debug { "will emit ${eventMapper[event] ?: "??"} (id : ${event})" }
        emitter.add(event to data)
    }

    fun emitLater(delta: Float, event: Event, entity: Entity, data: EventData = createEventData()): Unit {
        data.target = entity
        emitLater(delta, event, data)
    }

    fun emitLater(delta: Float, event: Event, data: EventData = createEventData()): Unit {
        data.event = event
        val timer = EventTimer(delta, event, data)
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

        emitterMirror.forEach({ debug { "emit ${eventMapper[it.first] ?: "??"} (id : ${it.first})" } })

        emitterLatterMirror.forEach { invoke(it.event, it.data) }
        emitterMirror.forEach { invoke(it.first, it.second) }

        val eventDataToReset = toEmitNow.map { it.data } + emitterMirror.map { it.second }

        emitterLatter.removeAll(toEmitNow)
        emitter.removeAll(emitterMirror)

        eventDataToReset.forEach { pool.free(it) }

        emitterMirror.clear()
        emitterLatterMirror.clear()

    }

    private fun invoke(event: Event, data: EventData) {
        listeners[event]?.forEach({ lst -> lst.onEvent(event, data) })
    }
}