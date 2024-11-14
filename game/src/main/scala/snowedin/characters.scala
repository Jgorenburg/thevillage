package Snowedin

import Base.{Actor, Story, Vibe, Person}
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver
import Snowedin.Tools.Tambourine
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color
import Snowedin.PositionConstants.boxSize

object Daughter extends Person {
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.setColor(Color.GREEN)
    shapeRenderer.circle(location._1, location._2, boxSize / 2)
    shapeRenderer.setColor(0, 0, 0, 1)
  }
  // var tools: HashSet[Tools.Tools] = HashSet()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Location: " + room
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

object Son extends Person {
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.setColor(Color.PURPLE)
    shapeRenderer.circle(location._1, location._2, boxSize / 2)
    shapeRenderer.setColor(0, 0, 0, 1)
  }
  var tools: HashSet[Tools.Tools] = HashSet()
  var lastAte = 0

  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ") +
    s", Last Ate: ${lastAte}" +
    ", Location: " + room
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

object Mother extends Person {
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.setColor(Color.BLUE)
    shapeRenderer.circle(location._1, location._2, boxSize / 2)
    shapeRenderer.setColor(0, 0, 0, 1)
  }
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
    ", Location: " + room
}

object Father extends Person {
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.setColor(Color.RED)
    shapeRenderer.circle(location._1, location._2, boxSize / 2)
    shapeRenderer.setColor(0, 0, 0, 1)
  }
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
    ", Location: " + room

}
