package Snowedin

import Base.{Actor, Story, Vibe}

object Worktable extends Actor {
  var commonState = (Vibe, 0)
  lazy val myEvents: Array[Any] = Array(Construction)
  var tools: Array[Boolean] = Array.fill(Tools.maxId)(true)

  def actorSpecificBeginning(tick: Int): Unit = {}
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
      case _: Story => // stories without relevant endings for father
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    tools = Array.fill(Tools.maxId)(true)
  }
}

object Couch extends Actor {
  var commonState = (Vibe, 0)
  val maxCapacity = 2
  var curCapacity = maxCapacity

  lazy val myEvents: Array[Any] = Array(Nap)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState._1 match
      case Nap      => curCapacity = 0
      case _: Story => // tories without relevant beginnings for father
  }
  def tick(tick: Int): Unit = {
    commonState._1 match
      case _: Story => // stories without relevant progress for father
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState._1 match
      case Nap      => curCapacity = maxCapacity
      case _: Story => // stories without relevant endings for father
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState._1 match
      case Nap      => curCapacity = maxCapacity
      case _: Story => // stories without relevant endings for father
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    curCapacity = maxCapacity
  }
}
