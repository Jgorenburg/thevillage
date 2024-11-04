package Snowedin

import Base.{Actor, Story}

object Father extends Actor {
  var commonState = (Vibe, 0)

  var myEvents: Array[Any] = Array(Vibe, Nap, Construction, FixDoor)

  var noticedBrokenDoor = false
  var tools: Array[Boolean] = Array.fill(Tools.maxId)(false)
}

object Tools extends Enumeration {
  type Tools = Value
  val Screwdriver = Value
}
