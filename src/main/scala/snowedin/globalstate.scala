package Snowedin

import Base.Actor

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
}
