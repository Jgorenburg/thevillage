package Base

import com.badlogic.gdx.Gdx

trait Room
object Room {
  case object Bedroom extends Room
}

object Globals {
  var secsPerTick = 1
  var bedloc: BoxCoords = (0, 0)
}

object Direction extends Enumeration {
  type Dir = Value
  val Left, Right, Down, Up = Value
}

trait PositionConstants {
  lazy val WIDTH: Float = Gdx.graphics.getWidth().toFloat
  lazy val HEIGHT: Float = Gdx.graphics.getHeight().toFloat
  def heightToWidth(h: Float): Float
  def widthToHeight(w: Float): Float

  // Use width as a baseline
  val size: Float
  val boxSize: Float

  val HorizBoxes: Int
  val VertBoxes: Int
}

val stage: GameMap = GameMap(
  vertWalls = Array(
    Array(false, false, false, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false),
    Array(true, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false),
    Array(true, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false),
    Array(false, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false),
    Array(false, true, true, true, true, true, true, true, true, true, true,
      true, true, true, false, false, false),
    Array(false, true, true, true, true, true, false, false, false, false,
      false, false, true, true, false, false, false),
    Array(true, true, true, true, true, true, false, false, false, false, false,
      false, true, true, false, false, false),
    Array(true, true, true, true, true, true, false, false, false, false, false,
      false, true, true, false, false, false),
    Array(true, true, true, true, true, true, false, false, false, false, false,
      false, true, true, true, true, true),
    Array(true, true, true, true, true, true, false, false, false, false, false,
      false, true, true, true, true, true),
    Array(true, true, true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true),
    Array(true, true, true, true, false, false, true, true, true, true, true,
      true, false, false, false, false, false),
    Array(true, true, true, true, false, false, true, true, true, true, true,
      true, true, false, false, false, false),
    Array(true, true, true, true, false, false, true, true, true, true, true,
      true, true, false, false, false, false),
    Array(false, true, false, true, false, false, true, true, true, true, true,
      true, true, true, true, true, true),
    Array(false, true, false, true, false, false, true, true, true, true, true,
      true, true, true, true, true, true),
    Array(false, true, false, true, false, false, true, true, true, true, true,
      true, true, true, true, true, true),
    Array(false, true, false, true, false, false, true, true, true, true, true,
      true, true, true, true, false, false),
    Array(true, true, true, true, true, true, true, true, true, true, true,
      true, true, true, true, false, false),
    Array(true, false, true, false, true, true, true, true, true, true, true,
      true, true, true, true, false, false),
    Array(true, false, false, false, true, true, true, true, true, true, true,
      true, true, true, true, false, false),
    Array(true, true, true, true, true, true, true, true, true, true, true,
      true, true, true, true, false, false),
    Array(false, false, false, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false)
  ),
  horizWalls = Array(
    Array(false, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false),
    Array(false, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false),
    Array(false, true, true, false, false, false, false, false, false, false,
      false, false, false, false, false, false, false, false),
    Array(false, true, true, true, true, true, true, true, true, true, true,
      true, true, true, false, false, false, false),
    Array(false, true, true, true, true, true, true, true, true, true, true,
      true, true, true, false, false, false, false),
    Array(false, true, true, true, true, true, false, false, false, false,
      false, false, false, true, false, false, false, false),
    Array(false, true, true, true, true, true, false, false, false, false,
      false, false, false, true, false, false, false, false),
    Array(false, true, true, true, true, true, false, false, false, false,
      false, false, false, true, true, true, true, false),
    Array(false, true, true, true, true, true, false, false, false, false,
      false, false, false, true, true, true, true, false),
    Array(false, true, true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, false),
    Array(false, true, true, true, true, true, true, true, true, true, true,
      true, true, true, true, true, true, false),
    Array(false, true, true, true, false, false, false, true, true, true, true,
      true, false, false, false, false, false, false),
    Array(false, true, true, true, false, false, false, true, true, true, true,
      true, false, false, false, false, false, false),
    Array(false, true, true, true, false, false, false, true, true, true, true,
      true, false, true, true, true, true, false),
    Array(false, true, false, false, true, false, false, true, true, true, true,
      true, true, true, true, true, true, false),
    Array(false, true, false, false, true, false, false, true, true, true, true,
      true, true, true, true, true, true, false),
    Array(false, true, false, false, true, false, false, true, true, true, true,
      true, true, true, true, true, true, false),
    Array(false, true, true, true, false, false, false, true, true, true, true,
      true, true, true, true, false, false, false),
    Array(false, true, false, false, false, true, true, true, true, true, true,
      true, true, true, true, false, false, false),
    Array(false, true, false, false, false, true, true, true, true, true, true,
      true, true, true, true, false, false, false),
    Array(false, true, true, true, true, true, false, false, true, true, true,
      true, true, true, true, false, false, false),
    Array(false, true, true, true, true, true, true, true, true, false, false,
      false, true, true, true, true, true, false)
  )
)
