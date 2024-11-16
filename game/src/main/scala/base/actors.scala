package Base

import Base.Importance.shouldInterrupt
import Snowedin.Couch.maxCapacity
import Snowedin.Couch.curCapacity
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedHashMap
import Snowedin.Location
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import Snowedin.PositionConstants.*
import com.badlogic.gdx.Game
import Snowedin.GlobalVars
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import Snowedin.Location.Bedroom

case class curStory(
    var curStory: Story,
    var startTime: Int
) {
  def copy(): curStory = { new curStory(curStory, startTime) }
  override def toString(): String =
    s"Current Story: ${curStory.getClass.getSimpleName.stripSuffix("$")}, Start Time: ${startTime}"
}

// for objects that can only take a certain number of charecters
trait Spaces {
  self: Actor =>

  val maxCapacity: Int
  var curCapacity: Int

  val occupiers: HashMap[Occupy, Int] = HashMap()
  // Story -> starttime
  val activeEvents: LinkedHashMap[Story, Int] = LinkedHashMap()

  def hasSpace(size: Int): Boolean = size <= curCapacity
  def hasSpace(story: Occupy): Boolean = hasSpace(story.size)
  def occupy(size: Int): Unit = curCapacity -= size
  def occupy(story: Story, size: Int = -1): Unit = {
    if (activeEvents.contains(story)) {
      leave(story)
    }
    activeEvents += (story -> GameManager.tick)

    val occ = story.asInstanceOf[Occupy]
    var taking = if (size != -1) size else occ.size
    occupiers += (occ -> taking)
    occupy(taking)
  }
  def leave(size: Int): Unit = curCapacity += size
  def leave(story: Story): Unit = {
    val occ = story.asInstanceOf[Occupy]
    leave(if (occupiers.contains(occ)) occupiers(occ) else occ.size)
    occupiers.remove(occ)
    activeEvents.remove(story)
  }

  def vacate() = {
    curCapacity = maxCapacity
    occupiers.clear()
  }

}

trait Renderable {
  var location: (Float, Float)
  // should only be called within Shaperenderer
  def render(shapeRenderer: ShapeRenderer): Unit
}

// for things that aren't interacted with, but still exist
trait Static extends Renderable

trait Actor extends Subject[Actor] with Listener with Renderable {
  // common state is for things every actor has
  // common state:
  //    1: current activity
  //    2: activity start time
  var commonState: curStory = (Vibe, 0)
  var interrupted: curStory = commonState.copy()

  var room: Location.Room = Location.Bedroom

  lazy val myEvents: Array[Any]

  def getCurStory(): Story = commonState.curStory
  def getCurStoryImportance(): Importance.Importance =
    commonState.curStory.importance

  def beginStory(story: Story, tick: Int): Unit = {
    interrupted = commonState.copy()
    if (shouldInterrupt(story)) {
      interrupted.curStory.interruptStory(tick)
    }

    commonState.curStory = story
    commonState.startTime = tick

    actorSpecificBeginning(tick)
  }
  def actorSpecificBeginning(tick: Int): Unit

  def tick(tick: Int): Unit

  def endStory(tick: Int): Unit = {
    actorSpecificEnding(tick)
    if (commonState.curStory.importance == Importance.Instantaneous) {
      commonState = interrupted.copy()
    } else if (
      this
        .isInstanceOf[Spaces] && !this.asInstanceOf[Spaces].activeEvents.isEmpty
    ) {
      commonState.curStory = this.asInstanceOf[Spaces].activeEvents.head._1
      commonState.startTime = this.asInstanceOf[Spaces].activeEvents.head._2
    } else {
      commonState.curStory = Vibe
      commonState.startTime = tick
    }
  }
  def actorSpecificEnding(tick: Int): Unit

  def shouldInterrupt(newStory: Story): Boolean = {
    if (GameManager.characters.contains(this)) {
      return true
    } else if (this.isInstanceOf[Spaces] && newStory.isInstanceOf[Occupy]) {
      return newStory.asInstanceOf[Occupy].size >
        this.asInstanceOf[Spaces].curCapacity
    }
    return true
  }

  def interruptStory(tick: Int): Unit = {
    actorSpecificInterrupt(tick)

    commonState.curStory = Vibe
    commonState.startTime = tick
  }

  def actorSpecificInterrupt(tick: Int): Unit

  // TODO: improve resets
  def reset(): Unit
  def defaultReset(): Unit = {
    commonState = (Vibe, 0)
    interrupted = (Vibe, 0)
    room = Location.Bedroom
  }

  def log(): String

  implicit def actorcommonState_to_tuple(
      cs: curStory
  ): (Story, Int) =
    (cs.curStory, cs.startTime)

  implicit def tuple_to_actorcommonstate(
      t: (Story, Int)
  ): curStory = curStory(t._1, t._2)

}

trait Person extends Actor {
  var wakeTime: Double = 0
  var bedTime = GameManager.ending
  def timeForBed(): Boolean = {
    return GameManager.tick >= bedTime
  }
  var location: (Float, Float) =
    (topRight._1 - 8 * boxSize, topRight._2 - 8f * boxSize)
  var destination: (Float, Float) = location
  var speed: Float =
    boxSize * GlobalVars.secsPerTick / 8 // (2 * GlobalVars.secsPerTick)
  var traveling: Boolean = false

  // returns true if person has reached their destination
  def walk(): Boolean = {
    if (!traveling) return true
    val dx = destination._1 - location._1
    val dy = destination._2 - location._2
    val total_distance = math.sqrt((math.pow(dx, 2) + math.pow(dy, 2)))

    if (total_distance < speed) {
      location = destination
      traveling = false

    } else {
      val norm = speed / total_distance
      val x = location._1 + (dx * norm)
      val y = location._2 + (dy * norm)
      location = (x.toFloat, y.toFloat)
      traveling = true
    }
    return !traveling
  }
  def setDestination(x: Float, y: Float): Unit = {
    traveling = true
    // Squares draw from bottom left but circles from the center,
    // the 0.5 fixes the disconnect
    destination = (x + 0.5f * boxSize, y + 0.5f * boxSize)
  }
  def setDestination(pos: (Float, Float)): Unit = {
    setDestination(pos._1, pos._2)
  }
  def setDestinationNoAdjust(pos: (Float, Float)): Unit = {
    traveling = true
    destination = pos
  }
  def setSpeed(newSpeed: Float) = speed = newSpeed

  val color: Color

  def render(shapeRenderer: ShapeRenderer) = {
    if (room == Bedroom) return
    shapeRenderer.setColor(color)
    shapeRenderer.circle(location._1, location._2, boxSize / 2)
    shapeRenderer.setColor(0, 0, 0, 1)
  }

  def report(
      font: BitmapFont,
      batch: SpriteBatch,
      loc: (Float, Float),
      indivPortion: String = ""
  ): Unit = {
    def simpleName(obj: Any) = obj.getClass.getSimpleName.stripSuffix("$")
    font.draw(
      batch,
      s"[#${color}]${simpleName(this)}:[BLACK]\n\tCurrent Story: ${simpleName(
          this.commonState.curStory
        )}\n\tLocation: ${this.location}\n\tRoom: ${this.room}" + indivPortion,
      loc._1,
      loc._2
    )
  }
}
