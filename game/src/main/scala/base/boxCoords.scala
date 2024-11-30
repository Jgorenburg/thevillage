package Base

import scala.math.BigDecimal.RoundingMode

class BoxCoords(var x: Float, var y: Float) {

  def this(w: Float, h: Int) = this(w, h.toFloat)
  def this(w: Int, h: Float) = this(w.toFloat, h)
  def this(w: Int, h: Int) = this(w.toFloat, h.toFloat)

  def setX(w: Float, safe: Boolean = true): Unit = {
    if (!safe || (w >= 0 && w < BoxCoords.horizMax)) x = w
    else throw new RuntimeException
  }

  def setY(h: Float, safe: Boolean = true): Unit = {
    if (!safe || (h >= 0 && h < BoxCoords.vertMax)) y = h
    else throw new RuntimeException
  }

  def setPos(w: Float, h: Float, safe: Boolean = true): Unit = {
    setX(w, safe)
    setY(h, safe)
  }

  def +(pos2: BoxCoords): BoxCoords =
    return new BoxCoords(x + pos2.x, y + pos2.y)

  def -(pos2: BoxCoords): BoxCoords =
    return new BoxCoords(x - pos2.x, y - pos2.y)

  def toRealLocation(): (Float, Float) =
    return (
      BoxCoords.housePos._1 + (x * BoxCoords.boxSize),
      BoxCoords.housePos._2 + (y * BoxCoords.boxSize)
    )

  override def toString(): String =
    return s"(${BigDecimal(x).setScale(1, RoundingMode.HALF_UP)}, ${BigDecimal(y)
        .setScale(1, RoundingMode.HALF_UP)})"
}

object BoxCoords {
  implicit def posToTuple(
      pos: BoxCoords
  ): (Float, Float) = (pos.x, pos.y)

  implicit def tupleIntToPos(pos: (Int, Int)): BoxCoords =
    new BoxCoords(pos._1.toFloat, pos._2.toFloat)

  implicit def tupleFloatToPos(pos: (Float, Float)): BoxCoords =
    new BoxCoords(pos._1, pos._2)

  var horizMax = 1e200 * 1e200
  var vertMax = 1e200 * 1e200
  var housePos = (0f, 0f)
  var boxSize = 1f

  def setup(
      house: (Float, Float),
      boxes: Float,
      horizBoxes: Int,
      vertBoxes: Int
  ): Unit = {
    housePos = house
    boxSize = boxes
    horizMax = horizBoxes
    vertMax = vertBoxes
  }

  def posToRealLocation(pos: BoxCoords): (Float, Float) =
    (
      BoxCoords.housePos._1 + (pos.x * BoxCoords.boxSize),
      BoxCoords.housePos._2 + (pos.y * BoxCoords.boxSize)
    )
}
