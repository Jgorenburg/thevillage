package Snowedin

import Base.Actor
import scala.collection.mutable.HashMap
import com.badlogic.gdx.Gdx

// global vars

object GlobalVars {
  var brokenDoor: Boolean = true
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
      case DiningRoom => List(LivingRoom, Door, Kitchen).contains(l2)
      case Door       => List(LivingRoom, DiningRoom).contains(l2)
      case _          => false
  }

  def areClose(actor: Actor, l2: Room): Boolean = areClose(actor.location, l2)
  def areClose(actor: Actor, actor2: Actor): Boolean =
    areClose(actor.location, actor2.location)
  def areClose(l1: Room, actor: Actor): Boolean = areClose(l1, actor.location)

  var distances: HashMap[(Room, Room), Double] = HashMap()
  distances += ((Bedroom, Bedroom) -> 0)

  def distanceFrom(actor: Actor, loc: Room): Double = {
    val rooms: (Room, Room) =
      if (loc.id <= actor.location.id) (loc, actor.location)
      else (actor.location, loc)

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

object PositionConstants {
  val WIDTH: Float = Gdx.graphics.getWidth().toFloat
  val HEIGHT: Float = Gdx.graphics.getHeight().toFloat
  val HouseBase: (Float, Float) = (WIDTH / 3, HEIGHT / 10)
  def houseX: Float = HouseBase._1
  def houseY: Float = HouseBase._2
  val HouseWidth: Float = WIDTH / 3
  val HouseHeight: Float = (WIDTH / 3) * 1.3f
  def bottomLeft = HouseBase
  def bottomRight = (houseX + HouseWidth, houseY)
  def topLeft = (houseX, houseY + HouseHeight)
  def topRight = (houseX + HouseWidth, houseY + HouseHeight)
}
