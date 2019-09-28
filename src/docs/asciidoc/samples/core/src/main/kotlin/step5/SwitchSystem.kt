package step5

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family.all
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.github.dwursteisen.libgdx.ashley.*
import com.github.dwursteisen.libgdx.ashley.fsm.EntityState
import com.github.dwursteisen.libgdx.ashley.fsm.StateMachineSystem
import step4.Player
import step4.Switch

class SwitchSystem(eventBus: EventBus) : StateMachineSystem(eventBus, Switch::class.java) {

    private val animated = get<Animated>()
    private val position = get<Position>()

    override fun describeMachine() {
        val off = object : EntityState() {
            override fun enter(entity: Entity, eventData: EventData) {
                entity[animated].animation.playMode = Animation.PlayMode.REVERSED
                entity[animated].time = 0f
            }
        }

        val on = object : EntityState() {
            override fun enter(entity: Entity, eventData: EventData) {
                entity[animated].animation.playMode = Animation.PlayMode.NORMAL
                entity[animated].time = 0f
            }

            override fun update(entity: Entity, time: Float) {
                val player = engine.getEntitiesFor(all(Player::class.java).get()).first()
                Rectangle.tmp.set(player[position].value.x, player[position].value.y, 16f, 16f)
                Rectangle.tmp2.set(entity[position].value.x, entity[position].value.y, 16f, 16f)

                if (Rectangle.tmp.overlaps(Rectangle.tmp2)) {
                    eventBus.emit(EVENT_SWITCH_ON)
                }
            }
        }

        startsWith(on) { entity ->
            entity[animated].finishAnimation()
        }

        onState(on).on(EVENT_SWITCH_ON) { entity: Entity, event: EventData ->
            go(off, entity, event)
        }

    }
}