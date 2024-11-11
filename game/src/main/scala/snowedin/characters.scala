package Snowedin

import Base.{Actor, Story, Vibe}
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver
import Snowedin.Tools.Tambourine

object Daughter extends Actor {
  // var tools: HashSet[Tools.Tools] = HashSet()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Location: " + location
  lazy val myEvents: Array[Any] = Array()
  def reset(): Unit = {
    // tools = HashSet()
    commonState = (Vibe, 0)
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
}

object Son extends Actor {
  var tools: HashSet[Tools.Tools] = HashSet()
  var lastAte = 0

  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ") +
    s", Last Ate: ${lastAte}" +
    ", Location: " + location
  lazy val myEvents: Array[Any] = Array()
  def reset(): Unit = {
    tools = HashSet()
    lastAte = 0
    commonState = (Vibe, 0)
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case Snack    => lastAte = tick
      case _: Story =>
  }
}

object Mother extends Actor {
  var noticedBrokenDoor = false

  var tools: HashSet[Tools.Tools] = HashSet()

  lazy val myEvents: Array[Any] = Array(Vibe, Placeholder)
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
    ", Tools: " + tools.mkString(", ") +
    s", Aware of Door: ${noticedBrokenDoor}" +
    ", Location: " + location
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
      case _: Story =>
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
      case _: Story =>
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
    ", Tools: " + tools.mkString(", ") +
    s", Aware of Door: ${noticedBrokenDoor}" +
    ", Location: " + location

}
