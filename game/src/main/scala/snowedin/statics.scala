package Snowedin

import Base.Static
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import SnowedInPositionConstants.*
import Snowedin.GlobalVars.bedLoc

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
  val location = (houseX + 5 * boxSize, houseY)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    val vertices = Array(
      location._1,
      location._2,
      bottomRight._1,
      bottomRight._2,
      bottomRight._1,
      bottomRight._2 + 7 * boxSize,
      bottomRight._1 - 3 * boxSize,
      bottomRight._2 + 7 * boxSize,
      bottomRight._1 - 3 * boxSize,
      bottomRight._2 + 3 * boxSize,
      houseX + 5 * boxSize,
      houseY + 3 * boxSize
    )
    shapeRenderer.polygon(vertices)
  }
}

object CoffeeTable extends Static {
  val location =
    (topLeft._1 + 2 * boxSize, topLeft._2 - 8 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      location._1,
      location._2,
      1 * boxSize,
      3 * boxSize
    )
  }
}

object LivingRoomTable extends Static {
  val location =
    (topLeft._1 + 4 * boxSize, topLeft._2 - 11 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      location._1,
      location._2,
      2 * boxSize,
      2 * boxSize
    )
  }
}

object WorkroomWall extends Static {
  val location = (bottomRight._1, bottomRight._2 + 11 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    val vertices = Array(
      location._1,
      location._2,
      bottomRight._1 - 5 * boxSize,
      location._2,
      bottomRight._1 - 5 * boxSize,
      location._2 + 3 * boxSize
    )
    shapeRenderer.polyline(vertices)
  }
}

object FrontDoor extends Static {
  val location = (topRight._1 - 8 * boxSize, topRight._2 - 0.5f * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(location._1, location._2, 2 * boxSize, 0.5f * boxSize)
  }
}

object BedroomDoor extends Static {
  val location = (bedLoc._1 + 1, bedLoc._2 - 1 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(location._1, location._2, 0.5f * boxSize, 2 * boxSize)
  }
}

object Fireplace extends Static {
  val location = (topLeft._1, topLeft._2 - 8 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(location._1, location._2, 0.5f * boxSize, 3 * boxSize)
  }
}

object Fridge extends Static {
  val location = (bottomLeft._1 + 3 * boxSize, bottomLeft._2)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(location._1, location._2, 2 * boxSize, 3 * boxSize)
  }
}

object WashingMachine extends Static {
  val location = (topRight._1 - 4 * boxSize, topRight._2 - 11 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(location._1, location._2, 2 * boxSize, 2 * boxSize)

    shapeRenderer.rect(
      location._1 + 2 * boxSize,
      location._2,
      2 * boxSize,
      2 * boxSize
    )
  }
}
