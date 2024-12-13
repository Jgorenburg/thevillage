package DateNight

import Base.PositionConstants
import Base.BoxCoords

object DateNightPositionConstants extends PositionConstants {
  def heightToWidth(h: Float) = h
  def widthToHeight(w: Float) = w
  val size: Float = HEIGHT * 7f / 8f
  val boxSize = size / 15f

  val HorizBoxes: Int = (size / boxSize).toInt
  val VertBoxes: Int = (widthToHeight(size) / boxSize).toInt
  // Room will always be centered on the X axis and slightly high on the Y
  val RoomBase: (Float, Float) =
    ((WIDTH - size) / 2, (HEIGHT - widthToHeight(size)) / 2)
  def roomX: Float = RoomBase._1
  def roomY: Float = RoomBase._2

  val HouseWidth: Float = size
  val HouseHeight: Float = widthToHeight(size)
  val bottomLeft: BoxCoords = new BoxCoords(0, 0)
  val bottomRight: BoxCoords = new BoxCoords(HorizBoxes, 0)
  val topLeft: BoxCoords = new BoxCoords(0, VertBoxes)
  val topRight: BoxCoords = new BoxCoords(HorizBoxes, VertBoxes)
}
