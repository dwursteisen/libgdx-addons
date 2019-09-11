package libgdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.Vector2

class MyFirstScreen : ScreenAdapter() {

    private val shapeRender = ShapeRenderer()

    private val position = Vector2(200f, 0f)

    private var time = 0f

    override fun render(delta: Float) {

        time += delta

        position.y = Interpolation.bounce.apply(cos(time)) * 200f

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRender.begin(ShapeRenderer.ShapeType.Filled)
        shapeRender.color = Color.RED
        shapeRender.circle(position.x, position.y, 50f)
        shapeRender.end()
    }
}
