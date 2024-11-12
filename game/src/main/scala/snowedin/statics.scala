package Snowedin

import Base.Static
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import PositionConstants.*

object House extends Static {
  val location = HouseBase
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      location._1,
      location._2,
      HouseWidth,
      HouseHeight
    )
  }
}

object Counter extends Static {
  val location = (WIDTH / 3, HEIGHT / 10)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    val vertices = Array(
      houseX + HouseWidth / 3,
      houseY,
      bottomRight._1,
      bottomRight._2,
      bottomRight._1,
      bottomRight._2 + HouseHeight / 3,
      bottomRight._1 - HouseWidth / 7,
      bottomRight._2 + HouseHeight / 3,
      bottomRight._1 - HouseWidth / 7,
      bottomRight._2 + HouseHeight / 7,
      houseX + HouseWidth / 3,
      houseY + HouseHeight / 7
    )
    shapeRenderer.polygon(vertices)
  }
}

object LivingRoomTable extends Static {
  val location = HouseBase
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      topLeft._1 + HouseWidth / 10,
      topLeft._2 - HouseHeight * 2.2f / 5.5f,
      HouseWidth / 10,
      HouseHeight / 6.2f
    )
  }
}
