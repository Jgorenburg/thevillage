package Snowedin

import Base.{Actor, Story, Vibe}

object Father extends Actor {
  lazy val myEvents: Array[Any] = Array(Vibe, Nap, Laundry, NoticeBrokenDoor)

  var noticedBrokenDoor = false
  var tools: Array[Boolean] = Array.fill(Tools.maxId)(false)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState._1 match
      case NoticeBrokenDoor => noticedBrokenDoor = true
      case _: Story         => // tories without relevant beginnings for father
  }
  def tick(tick: Int): Unit = {
    commonState._1 match
      case _: Story => // stories without relevant progress for father
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState._1 match
      case _: Story => // stories without relevant endings for father
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState._1 match
      case _: Story => // stories without relevant interrupts for father
  }

  def reset() = {
    commonState = (Vibe, 0)
    noticedBrokenDoor = false
    tools = Array.fill(Tools.maxId)(false)
  }

  def log() = commonState.toString() +
    " Tools: " + tools.mkString(", ") + s" Aware of Door: ${noticedBrokenDoor}"

}
