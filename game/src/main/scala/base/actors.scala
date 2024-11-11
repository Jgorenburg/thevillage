package Base

import Base.Importance.interrupt
import Snowedin.Couch.maxCapacity
import Snowedin.Couch.curCapacity
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedHashMap
import Snowedin.Location

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

trait Actor extends Subject[Actor] with Listener {
  // common state is for things every actor has
  // common state:
  //    1: current activity
  //    2: activity start time
  var commonState: curStory = (Vibe, 0)
  var interrupted: curStory = commonState.copy()

  var location: Location.Room = Location.Bedroom

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

  def reset(): Unit
  def defaultReset(): Unit = {
    commonState = (Vibe, 0)
    interrupted = (Vibe, 0)
    location = Location.Bedroom
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
