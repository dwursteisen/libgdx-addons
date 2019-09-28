package step4

import com.badlogic.ashley.core.Entity
import com.github.dwursteisen.libgdx.ashley.Position
import com.github.dwursteisen.libgdx.ashley.TexturedStrategy
import com.github.dwursteisen.libgdx.ashley.get

class SpriteStrategy : TexturedStrategy() {

    private val position = get<Position>()

    override fun zLevel(entity: Entity, delta: Float): Float {
        return (entity[position].value.y / -400) + 3 // <-- layer 3
    }

}
