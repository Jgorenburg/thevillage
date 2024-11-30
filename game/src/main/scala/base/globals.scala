package Base

import com.badlogic.gdx.Gdx

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

  def boxInBounds(loc: (Int, Int)): Boolean = {
    if (loc._1 < 0 || loc._2 < 0) return false
    else if (loc._1 >= HorizBoxes) return false
    else return loc._2 < VertBoxes
  }
}
