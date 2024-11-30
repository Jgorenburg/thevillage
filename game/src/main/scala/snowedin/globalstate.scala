package Snowedin

import Base.Actor
import scala.collection.mutable.HashMap
import com.badlogic.gdx.Gdx
import Snowedin.SnowedInPositionConstants.bottomLeft
import Snowedin.SnowedInPositionConstants.boxSize
import Base.PositionConstants
import Base.BoxCoords

// global vars
object GlobalVars {
  var brokenDoor: Boolean = true
  var secsPerTick = 2
  val bedLoc: BoxCoords = bottomLeft + (-1, 4)
}

object Tools extends Enumeration {
  type Tools = Value
  val Screwdriver, Tambourine, Knife = Value
}

object Location extends Enumeration {
  type Room = Value
  val Bedroom, Workroom, LivingRoom, Kitchen, DiningRoom, Door = Value

  def areClose(l1: Room, l2: Room): Boolean = {
    if (l1 == Bedroom || l2 == Bedroom) return false
    if (l1 == l2) return true
    l1 match
      case LivingRoom => List(DiningRoom, Door).contains(l2)
      case Kitchen    => l2 == DiningRoom
      case DiningRoom => List(LivingRoom, Kitchen).contains(l2)
      case Door       => List(LivingRoom, Workroom).contains(l2)
      case Workroom   => l2 == Door
      case _          => false
  }

  def areClose(actor: Actor, l2: Room): Boolean = areClose(actor.room, l2)
  def areClose(actor: Actor, actor2: Actor): Boolean =
    areClose(actor.room, actor2.room)
  def areClose(l1: Room, actor: Actor): Boolean = areClose(l1, actor.room)

  var distances: HashMap[(Room, Room), Double] = HashMap()
  distances += ((Bedroom, Bedroom) -> 0)

  def distanceFrom(actor: Actor, loc: Room): Double = {
    val rooms: (Room, Room) =
      if (loc.id <= actor.room.id) (loc, actor.room)
      else (actor.room, loc)

    if (!distances.contains(rooms)) {

      var dist: Double = 0
      var close = List(rooms._1)
      if (rooms._1 == Bedroom) {
        dist = Double.PositiveInfinity
        close = List(rooms._2)
      }
      while (!close.contains(rooms._2)) {
        close = close.flatMap(l => values.filter(v => areClose(l, v)))
        dist += 1
      }
      distances += rooms -> dist
    }

    return distances(rooms)
  }

  def closest(loc: Room, howMany: Int, actors: List[Actor]): List[Actor] = {
    def distance(actor: Actor): Double = distanceFrom(actor, loc)
    actors.sortWith((a1, a2) => distance(a1) <= distance(a2)).take(howMany)
  }
}

object SnowedInPositionConstants extends PositionConstants {
  def heightToWidth(h: Float) = h * 8.5f / 11
  def widthToHeight(w: Float) = w * 11 / 8.5f
  val size: Float = WIDTH / 2.8f
  val boxSize = size / 17

  val HorizBoxes: Int = (size / boxSize).toInt
  val VertBoxes: Int = (widthToHeight(size) / boxSize).toInt
  // House will always be centered on the X axis and slightly high on the Y
  val HouseBase: (Float, Float) =
    ((WIDTH - size) / 2, (HEIGHT - widthToHeight(size)) / 2)
  def houseX: Float = HouseBase._1
  def houseY: Float = HouseBase._2

  val HouseWidth: Float = size
  val HouseHeight: Float = widthToHeight(size)
  val bottomLeft: BoxCoords = new BoxCoords(0, 0)
  val bottomRight: BoxCoords = new BoxCoords(HorizBoxes, 0)
  val topLeft: BoxCoords = new BoxCoords(0, VertBoxes)
  val topRight: BoxCoords = new BoxCoords(HorizBoxes, VertBoxes)
}
