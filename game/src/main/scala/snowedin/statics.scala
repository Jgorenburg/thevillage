package Snowedin

import Base.{Draw, DrawLayer, Static, WallSegment, Walls}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import SnowedInPositionConstants.*
import Base.Globals.bedloc

object House extends Static {
  val location = bottomLeft
  var interactLoc = location

  // The house's outer walls. There are no interior walls besides the
  // workroom's (see WorkroomWall); the blocked edges in `Base.stage` mostly
  // come from furniture, so walls are not derived from it.
  val outline: List[WallSegment] = List(
    WallSegment(0, 0, HorizBoxes.toFloat, 0),
    WallSegment(0, VertBoxes.toFloat, HorizBoxes.toFloat, VertBoxes.toFloat),
    WallSegment(0, 0, 0, VertBoxes.toFloat),
    WallSegment(HorizBoxes.toFloat, 0, HorizBoxes.toFloat, VertBoxes.toFloat)
  )

  override def drawLayer: Int = DrawLayer.Floor

  def render(batch: SpriteBatch): Unit = {
    val floor = Base.Assets.region(SnowedInSprites.Floor)
    for {
      x <- 0 until HorizBoxes
      y <- 0 until VertBoxes
    } Draw.tiles(batch, floor, x.toFloat, y.toFloat, 1, 1)
    Walls.render(batch, outline, HouseWidth, HouseHeight)
  }

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {

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

  // Everything else (stove, dishwasher) stands on the counter
  override def drawLayer: Int = DrawLayer.Ground

  // L-shape split into a 9x3 run along the bottom and a 3x7 run on the right
  def render(batch: SpriteBatch): Unit = {
    Draw.named(batch, "counter_bottom", location.x, location.y, 9, 3)
    Draw.named(batch, "counter_side", HorizBoxes - 3f, 0, 3, 7)
  }

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
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
  def render(batch: SpriteBatch): Unit =
    Draw.named(batch, "coffee_table", location.x, location.y, 1, 3)

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
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
  def render(batch: SpriteBatch): Unit =
    Draw.named(batch, "living_room_table", location.x, location.y, 2, 2)

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
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

  // Runs left from the right house wall at y=11, then up to y=14
  val segments: List[WallSegment] = List(
    WallSegment(location.x - 5, location.y, location.x, location.y),
    WallSegment(location.x - 5, location.y, location.x - 5, location.y + 3)
  )

  override def drawLayer: Int = DrawLayer.Walls

  def render(batch: SpriteBatch): Unit =
    Walls.render(batch, segments, HouseWidth, HouseHeight)

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
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

  /** `broken` until FixDoor completes. */
  def stateName: String = SnowedInSprites.frontDoorState(GlobalVars.brokenDoor)

  def render(batch: SpriteBatch): Unit =
    Draw.animated(
      batch,
      s"front_door_$stateName",
      location.x,
      location.y,
      2,
      0.5f
    )

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()
    shapeRenderer.rect(loc._1, loc._2, 2 * boxSize, 0.5f * boxSize)
  }
}

object BedroomDoor extends Static {
  val location = bedloc
  var interactLoc = location + (0, 1)
  def render(batch: SpriteBatch): Unit =
    Draw.named(batch, "bedroom_door", location.x, location.y, 0.5f, 2)

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
    val loc = location.toRealLocation()
    shapeRenderer.rect(loc._1, loc._2, 0.5f * boxSize, 2 * boxSize)
  }
}

object Fireplace extends Static {
  val location = topLeft - (0, 8)
  var interactLoc = location + (0.5f, 1f)

  /** `lit` once StartFire's tending has begun, and it stays lit. */
  def stateName: String = SnowedInSprites.fireplaceState(
    StartFire.active && StartFire.started,
    StartFire.commonState.completed
  )

  def render(batch: SpriteBatch): Unit =
    Draw.animated(
      batch,
      s"fireplace_$stateName",
      location.x,
      location.y,
      0.5f,
      3
    )

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 0.5f * boxSize, 3 * boxSize)
  }
}

object Fridge extends Static {
  val location = bottomLeft + (3, 0)
  var interactLoc = location + (0, 4)
  def render(batch: SpriteBatch): Unit =
    Draw.named(batch, "fridge", location.x, location.y, 2, 3)

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 2 * boxSize, 3 * boxSize)
  }
}

object WashingMachine extends Static {
  val location = topRight - (4, 11)
  var interactLoc = location + (1, 2)

  // Two 2x2 units side by side
  def render(batch: SpriteBatch): Unit = {
    Draw.named(batch, "washing_machine", location.x, location.y, 2, 2)
    Draw.named(batch, "washing_machine", location.x + 2, location.y, 2, 2)
  }

  def renderDebug(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 2 * boxSize, 2 * boxSize)

    shapeRenderer.rect(
      rloc()._1 + 2 * boxSize,
      rloc()._2,
      2 * boxSize,
      2 * boxSize
    )
  }
}
