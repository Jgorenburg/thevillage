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
      BoxCoords.stagePos._1 + (x * BoxCoords.boxSize),
      BoxCoords.stagePos._2 + (y * BoxCoords.boxSize)
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

  var horizMax: Int = (1e200 * 1e200).toInt
  var vertMax: Int = (1e200 * 1e200).toInt
  var stagePos: (Float, Float) = (0f, 0f)
  var boxSize: Float = 1f

  def setup(
      stage: (Float, Float),
      boxes: Float,
      horizBoxes: Int,
      vertBoxes: Int
  ): Unit = {
    stagePos = stage
    boxSize = boxes
    horizMax = horizBoxes
    vertMax = vertBoxes
  }

  def posToRealLocation(pos: BoxCoords): (Float, Float) =
    (
      stagePos._1 + (pos.x * BoxCoords.boxSize),
      stagePos._2 + (pos.y * BoxCoords.boxSize)
    )

  def boxInBounds(loc: BoxCoords): Boolean = {
    if (loc._1 < 0 || loc._2 < 0) return false
    else if (loc._1 >= horizMax) return false
    else return loc._2 < vertMax
  }
}
