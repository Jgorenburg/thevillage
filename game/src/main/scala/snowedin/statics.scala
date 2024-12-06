package Snowedin

import Base.Static
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import SnowedInPositionConstants.*
import Snowedin.GlobalVars.bedLoc

object House extends Static {
  val location = bottomLeft
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {

    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      HouseWidth,
      HouseHeight
    )
  }
}

object Counter extends Static {
  val location = bottomLeft + (5, 0)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {
    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    val br = bottomRight.toRealLocation()
    val loc = location.toRealLocation()

    val vertices = Array(
      loc._1,
      loc._2,
      br._1,
      br._2,
      br._1,
      br._2 + 7 * boxSize,
      br._1 - 3 * boxSize,
      br._2 + 7 * boxSize,
      br._1 - 3 * boxSize,
      br._2 + 3 * boxSize,
      houseX + 5 * boxSize,
      houseY + 3 * boxSize
    )
    shapeRenderer.polygon(vertices)
  }
}

object CoffeeTable extends Static {
  val location = (topLeft + (2, -8))
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()

    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      loc._1,
      loc._2,
      1 * boxSize,
      3 * boxSize
    )
  }
}

object LivingRoomTable extends Static {
  val location = (topLeft + (4, -11))
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()

    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      loc._1,
      loc._2,
      2 * boxSize,
      2 * boxSize
    )
  }
}

object WorkroomWall extends Static {
  val location = bottomRight + (0, 11)
  var interactLoc = location
  val BR = bottomRight.toRealLocation()
  def render(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()
    val BR = bottomRight.toRealLocation()

    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    val vertices = Array(
      loc._1,
      loc._2,
      BR._1 - 5 * boxSize,
      loc._2,
      BR._1 - 5 * boxSize,
      loc._2 + 3 * boxSize
    )
    shapeRenderer.polyline(vertices)
  }
}

object FrontDoor extends Static {
  val location = topRight - (8f, 0.5f)
  var interactLoc = location + (0.5f, 0f)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()
    shapeRenderer.rect(loc._1, loc._2, 2 * boxSize, 0.5f * boxSize)
  }
}

object BedroomDoor extends Static {
  val location = bedLoc
  var interactLoc = location + (0, 1)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()
    shapeRenderer.rect(loc._1, loc._2, 0.5f * boxSize, 2 * boxSize)
  }
}

object Fireplace extends Static {
  val location = topLeft - (0, 8)
  var interactLoc = location + (0.5f, 1f)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 0.5f * boxSize, 3 * boxSize)
  }
}

object Fridge extends Static {
  val location = bottomLeft + (3, 0)
  var interactLoc = location + (0, 4)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 2 * boxSize, 3 * boxSize)
  }
}

object WashingMachine extends Static {
  val location = topRight - (4, 11)
  var interactLoc = location + (1, 2)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 2 * boxSize, 2 * boxSize)

    shapeRenderer.rect(
      rloc()._1 + 2 * boxSize,
      rloc()._2,
      2 * boxSize,
      2 * boxSize
    )
  }
}
