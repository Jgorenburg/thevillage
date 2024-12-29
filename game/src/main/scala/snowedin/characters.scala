package Snowedin

import Base.{Actor, Story, Vibe, Person}
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver
import Snowedin.Tools.Tambourine
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.Color
import Snowedin.SnowedInPositionConstants.boxSize
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

object Daughter extends Person {

  override def timeForBed(): Boolean = {
    Dinner.commonState.completed && super.timeForBed()
  }
  val color = Color.ORANGE

  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Location: " + room
  lazy val myEvents: Array[Any] = Array()

  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
}

object Son extends Person {
  override def timeForBed(): Boolean = {
    Dinner.commonState.completed && super.timeForBed()
  }
  val color = Color.PURPLE
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
  override def reset(): Unit = {
    tools = HashSet()
    lastAte = 0
    super.reset()
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case Snack    => lastAte = tick
      case _: Story =>
  }
  override def report(
      font: BitmapFont,
      batch: SpriteBatch,
      loc: (Float, Float),
      indivPortion: String = ""
  ): Unit = {
    super.report(font, batch, loc, s"\n\tLast Ate: ${lastAte}")
  }
}

object Mother extends Person {
  override def timeForBed(): Boolean = {
    Dinner.commonState.completed && super.timeForBed()
  }
  val color = Color.BLUE
  var noticedBrokenDoor = false

  var tools: HashSet[Tools.Tools] = HashSet()

  lazy val myEvents: Array[Any] = Array(Vibe, Code)
  def actorSpecificBeginning(tick: Int): Unit = {
    commonState._1 match
      case NoticeBrokenDoor => noticedBrokenDoor = true
      case _: Story         =>
  }
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificInterrupt(tick: Int): Unit = {}

  override def reset() = {
    noticedBrokenDoor = false
    tools = HashSet()
    super.reset()
  }

  def log() = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ") +
    s", Aware of Door: ${noticedBrokenDoor}" +
    ", Location: " + room

  override def report(
      font: BitmapFont,
      batch: SpriteBatch,
      loc: (Float, Float),
      indivPortion: String = ""
  ): Unit = {
    val toollist = if (tools.isEmpty) "None" else tools.mkString(", ")

    super.report(
      font,
      batch,
      loc,
      s"\n\tTools: ${toollist}\n\tNoticed Door: ${noticedBrokenDoor}"
    )
  }
}

object Father extends Person {
  val color = Color.RED
  override def timeForBed(): Boolean = {
    Dinner.commonState.completed && super.timeForBed()
  }

  lazy val myEvents: Array[Any] = Array(Vibe, Nap, Laundry, NoticeBrokenDoor)

  var noticedBrokenDoor = false
  var tools: HashSet[Tools.Tools] = HashSet()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {
    if (
      noticedBrokenDoor &&
      !tools.contains(Screwdriver) &&
      room == SIRoom.Workroom &&
      Worktable.tools.contains(Screwdriver)
    ) {
      tools.add(Screwdriver)
      Worktable.tools.remove(Screwdriver)
    }
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    if (
      noticedBrokenDoor &&
      !tools.contains(Screwdriver) &&
      room == SIRoom.Workroom &&
      Worktable.tools.contains(Screwdriver)
    ) {
      tools.add(Screwdriver)
      Worktable.tools.remove(Screwdriver)
    }
  }

  override def reset() = {
    noticedBrokenDoor = false
    tools = HashSet()
    super.reset()
  }

  def log() = commonState.toString() +
    ", Importance: " + commonState.curStory.importance +
    ", Tools: " + tools.mkString(", ") +
    s", Aware of Door: ${noticedBrokenDoor}" +
    ", Location: " + room

  override def report(
      font: BitmapFont,
      batch: SpriteBatch,
      loc: (Float, Float),
      indivPortion: String = ""
  ): Unit = {
    val toollist = if (tools.isEmpty) "None" else tools.mkString(", ")

    super.report(
      font,
      batch,
      loc,
      s"\n\tTools: ${toollist}\n\tNoticed Door: ${noticedBrokenDoor}"
    )
  }

}
