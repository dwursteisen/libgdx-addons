package step5

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Animation
import com.github.dwursteisen.libgdx.ashley.Animated
import com.github.dwursteisen.libgdx.ashley.EventBus
import com.github.dwursteisen.libgdx.ashley.EventData
import com.github.dwursteisen.libgdx.ashley.fsm.EntityState
import com.github.dwursteisen.libgdx.ashley.fsm.StateMachineSystem
import com.github.dwursteisen.libgdx.ashley.get
import step4.Door

class DoorSystem(eventBus: EventBus) : StateMachineSystem(eventBus, Door::class.java) {

    private val animated = get<Animated>()

    override fun describeMachine() {
        val close = object : EntityState() {
            override fun enter(entity: Entity, eventData: EventData) {
                entity[animated].animation.playMode = Animation.PlayMode.REVERSED
                entity[animated].time = 0f
            }
        }

        val open = object : EntityState() {
            override fun enter(entity: Entity, eventData: EventData) {
                entity[animated].animation.playMode = Animation.PlayMode.NORMAL
                entity[animated].time = 0f
            }
        }

        startsWith(close) { entity ->
            entity[animated].finishAnimation()
        }

        onState(close).on(EVENT_SWITCH_ON) { entity: Entity, event: EventData ->
            go(open, entity, event)
        }
    }

}