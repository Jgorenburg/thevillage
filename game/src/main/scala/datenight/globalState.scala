package DateNight

import Base.PositionConstants
import Base.BoxCoords
import Base.Room
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.Gdx

object DNGlobals {
  val speechFont = new BitmapFont(Gdx.files.internal("bookantiqua.fnt"));

}

object DNRoom {
  import Room.Bedroom

  case object Park extends Room

  val allRooms = List(Bedroom, Park)
}

object DateNightPositionConstants extends PositionConstants {
  def heightToWidth(h: Float) = h
  def widthToHeight(w: Float) = w
  val size: Float = HEIGHT * 7f / 8f
  val boxSize = size / 15f

  val HorizBoxes: Int = (size / boxSize).toInt
  val VertBoxes: Int = (widthToHeight(size) / boxSize).toInt
  // Room will always be centered on the X axis and slightly high on the Y
  val StageBase: (Float, Float) =
    ((WIDTH - size) / 2, (HEIGHT - widthToHeight(size)) / 2)
  def stageX: Float = StageBase._1
  def stageY: Float = StageBase._2

  val StageWidth: Float = size
  val StageHeight: Float = widthToHeight(size)
  val bottomLeft: BoxCoords = new BoxCoords(0, 0)
  val bottomRight: BoxCoords = new BoxCoords(HorizBoxes, 0)
  val topLeft: BoxCoords = new BoxCoords(0, VertBoxes)
  val topRight: BoxCoords = new BoxCoords(HorizBoxes, VertBoxes)
}
