package Snowedin

import Base.{Actor, Story, Vibe}
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver
import Snowedin.Tools.Tamborine

object Mother extends Actor {
  var noticedBrokenDoor = false

  var tools: HashSet[Tools.Tools] = HashSet()

  lazy val myEvents: Array[Any] = Array(Vibe, Cleaning)
  def actorSpecificBeginning(tick: Int): Unit = {
    commonState._1 match
      case NoticeBrokenDoor => noticedBrokenDoor = true
      case _: Story         => // stories without relevant beginnings for mother
  }
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    commonState = (Vibe, 0)
    noticedBrokenDoor = false
    tools = HashSet()
  }

  def log() = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ")
    + s", Aware of Door: ${noticedBrokenDoor}"
}

object Father extends Actor {
  lazy val myEvents: Array[Any] = Array(Vibe, Nap, Laundry, NoticeBrokenDoor)

  var noticedBrokenDoor = false
  var tools: HashSet[Tools.Tools] = HashSet()

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState._1 match
      case NoticeBrokenDoor => noticedBrokenDoor = true
      case _: Story         => // stories without relevant beginnings for father
  }
  def tick(tick: Int): Unit = {
    commonState._1 match
      case _: Story => // stories without relevant progress for father
  }
  def actorSpecificEnding(tick: Int): Unit = {
    if (
      noticedBrokenDoor &&
      !tools.contains(Screwdriver) &&
      commonState.curStory.actors.contains(Worktable) &&
      Worktable.tools.contains(Screwdriver)
    ) {
      tools.add(Screwdriver)
      Worktable.tools.remove(Screwdriver)
    }
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
    tools = HashSet()
  }

  def log() = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ")
    + s", Aware of Door: ${noticedBrokenDoor}"

}
