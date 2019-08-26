package com.github.dwursteisen.libgdx.ashley.fsm

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Array
import com.github.dwursteisen.libgdx.ashley.EventBus
import com.github.dwursteisen.libgdx.ashley.EventData
import com.github.dwursteisen.libgdx.ashley.EventListener
import com.github.dwursteisen.libgdx.ashley.StateComponent
import com.github.dwursteisen.libgdx.ashley.get
import com.github.dwursteisen.libgdx.ashley.getNullable
import ktx.log.debug

typealias Transition = StateMachine.(entity: Entity, event: EventData) -> Unit

typealias Event = Int

abstract class StateMachineSystem(
    val eventBus: EventBus,
    private val clazz: Class<out StateComponent>
) : IteratingSystem(Family.all(clazz).get()), EventListener, StateMachine {

    private var transitions = emptyMap<EntityState, Map<Event, Transition>>()
    private var defaultTransition = emptyMap<EntityState, Transition>()

    private val state: ComponentMapper<StateComponent> = get()

    private val tmpEntities = Array<Entity>()

    private val events: Set<Event>
        get() {
            val allEvents = transitions.values.flatMap { it.keys }.toSet()
            if (defaultTransition.isNotEmpty()) {
                return allEvents + EVENT_NOP
            }
            return allEvents
        }

    companion object {
        const val EVENT_NOP = -1
        const val EVENT_TOUCHED = -2
        const val EVENT_SLIDE = -3
        const val EVENT_KEY = -4
        const val EVENT_KEY_UP = -5
    }

    /**
     * Describe the current state machine.
     */
    abstract fun describeMachine()

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        describeMachine()
        eventBus.register(this, *events.toIntArray())
        engine.addEntityListener(family, object : EntityListener {
            override fun entityRemoved(entity: Entity) = Unit

            override fun entityAdded(entity: Entity) {
                eventBus.emit(EVENT_NOP, entity)
            }
        })
    }

    /**
     * Will search the current state regarding the entity and the class targeted by this state machine.
     */
    private inline fun usingState(entity: Entity, block: (state: EntityState, time: Float) -> Unit) {
        val state = entity.getNullable(state)
        if (state == null) {
            ktx.log.error {
                "Your entity SHOULD have the StateComponent component, " +
                    "as the entity is managed by a State Machine System. " +
                    "As the current entity doesn't have a state, it will be silently ignored. " +
                    "But you may wants to fix this issue as it is not expected."
            }
        } else {
            val componentState = state.status[clazz] ?: EntityState.STATE_NOP
            val time = state.time - (state.timeReset[clazz] ?: state.time)
            block.invoke(componentState, time)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        usingState(entity) { state, time ->
            state.update(entity, time)
        }
    }

    class OnState(
        private val state: EntityState,
        private val parent: StateMachineSystem
    ) {

        fun on(vararg events: Int, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(events: List<Int>, block: Transition): OnState {
            events.forEach { on(it, block) }
            return this
        }

        fun on(event: Int, block: Transition): OnState {
            var currentTransitions = parent.transitions[state] ?: emptyMap()
            currentTransitions = currentTransitions + (event to block)

            parent.transitions += state to currentTransitions
            return this
        }

        fun default(block: Transition) {
            parent.defaultTransition += state to block
        }
    }

    fun onState(state: EntityState): OnState = OnState(state, this)

    fun startsWith(state: EntityState) = startsWith { entity, data -> go(state, entity, data) }

    fun startsWith(transition: Transition) {
        onState(EntityState.STATE_NOP).default(transition)
    }

    override fun go(newState: EntityState, entity: Entity) = go(newState, entity, eventBus.createEventData())

    override fun go(newState: EntityState, entity: Entity, event: EventData) {
        usingState(entity) { entityState, _ ->
            debug("STATE_MACHINE") { "Exit ${entityState::class.java.simpleName} on event ${event.event}" }
            entityState.exit(entity, event)
        }

        entity[state].status[clazz] = newState
        entity[state].timeReset[clazz] = entity[state].time

        usingState(entity) { entityState, _ ->
            debug("STATE_MACHINE") { "Enter ${entityState::class.java.simpleName} on event ${event.event}" }

            entityState.enter(entity, event)
        }
    }

    override fun emit(event: Event) = emit(event, eventBus.createEventData())

    override fun emit(event: Event, eventData: EventData) {
        tmpEntities.clear()
        entities?.let {
            entities.forEach { tmpEntities.add(it) }
        }
        tmpEntities.forEach { perform(event, it, eventData) }
    }

    private fun perform(event: Event, entity: Entity, eventData: EventData) {
        eventData.event = event
        usingState(entity) { entityState, _ ->
            val transition: Transition? = transitions[entityState]?.get(event) ?: defaultTransition[entityState]
            transition?.invoke(this, entity, eventData)
        }
    }

    override fun onEvent(event: Event, eventData: EventData) {
        if (!checkProcessing()) {
            return
        }
        val target = eventData.target

        if (target == null) {
            emit(event, eventData)
        } else if (family.matches(target)) {
            perform(event, target, eventData)
        }
    }
}
