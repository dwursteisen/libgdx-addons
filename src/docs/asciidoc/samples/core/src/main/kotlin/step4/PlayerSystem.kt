package step4

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.dwursteisen.libgdx.ashley.Position
import com.github.dwursteisen.libgdx.ashley.get
import step4.Player

// tag::body[]
class PlayerSystem : IteratingSystem(Family.all(Player::class.java).get()) {

    private val position = get<Position>()

    private val speed = 64f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            entity[position].value.add(-speed * deltaTime, 0f)
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            entity[position].value.add(speed * deltaTime, 0f)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            entity[position].value.add(0f, speed * deltaTime)
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            entity[position].value.add(0f, -speed * deltaTime)
        }
    }
}
// end::body[]
