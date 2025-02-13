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
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.Texture
import Base.Direction
import java.util.Dictionary
import com.badlogic.gdx.graphics.g2d.Animation
import scala.collection.immutable.HashMap
import com.badlogic.gdx.utils
import Base.Room.Bedroom
import Base.Globals

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
  def reset(): Unit = {
    commonState = (Vibe, 0)
  }
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

  val pixelDimensions = (16, 32)

  def animate(batch: SpriteBatch, realTime: Float, tick: Int) = {
    if (room != Bedroom) {
      var currentFrame: TextureRegion =
        if (movementStack.isEmpty)
          facing match
            case Direction.Up =>
              new TextureRegion(
                Texture("Father/idle_back.png"),
                pixelDimensions._1,
                pixelDimensions._2
              )
            case _ =>
              new TextureRegion(
                Texture("Father/idle.png"),
                pixelDimensions._1,
                pixelDimensions._2
              )
        else
          animationTable(facing).getKeyFrame(
            (tick - commonState.startTime).toFloat,
            true
          )

      val adjLoc = (location - (0.25f, 0f)).toRealLocation()
      batch.draw(
        currentFrame,
        adjLoc._1,
        adjLoc._2,
        boxSize * 1.5f,
        boxSize * 3
      )
    }
  }

  // Animations
  val animationTable: HashMap[Direction.Dir, Animation[TextureRegion]] =
    val walkSpeed = Globals.secsPerTick * 50f
    HashMap(
      Direction.Up -> {
        var texture = new Texture("Father/walk_away.png")
        val frames = new utils.Array[TextureRegion]
        TextureRegion
          .split(texture, Father.pixelDimensions._1, Father.pixelDimensions._2)
          .foreach(_.foreach(frames.add(_)))

        new Animation(walkSpeed, frames)
      },
      Direction.Down -> {
        var texture = new Texture("Father/walk_forward.png")
        val frames = new utils.Array[TextureRegion]
        TextureRegion
          .split(texture, Father.pixelDimensions._1, Father.pixelDimensions._2)
          .foreach(_.foreach(frames.add(_)))

        new Animation(walkSpeed, frames)
      },
      Direction.Left -> {
        var texture = new Texture("Father/walk_away.png")
        val frames = new utils.Array[TextureRegion]
        TextureRegion
          .split(texture, Father.pixelDimensions._1, Father.pixelDimensions._2)
          .foreach(_.foreach(frames.add(_)))

        new Animation(walkSpeed, frames)
      },
      Direction.Right -> {
        var texture = new Texture("Father/walk_away.png")
        val frames = new utils.Array[TextureRegion]
        TextureRegion
          .split(texture, Father.pixelDimensions._1, Father.pixelDimensions._2)
          .foreach(_.foreach(frames.add(_)))

        new Animation(walkSpeed, frames)
      }
    )

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
