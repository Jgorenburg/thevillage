package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story
import Snowedin.Tools.Tambourine
import Base.Pausable
import Base.Occupy
import Base.Delay
import Snowedin.Location.Room
import scala.collection.mutable.Queue
import Snowedin.Location.DiningRoom
import Snowedin.Location.LivingRoom
import Snowedin.Location.Kitchen
import Snowedin.Location.Door

object Placeholder extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  val startState = (false, -1, false, 7)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object Music extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => Mother.tools.contains(Tambourine),
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 4)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object Art extends Story with Occupy {
  val size = 1
  lazy val actors = HashSet(Mother, Easle)
  var conditions: List[() => Boolean] =
    List(
      () => Easle.curCapacity >= size,
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = { Mother.location = LivingRoom }
  override def progress(tick: Int): Boolean = {
    if (tick - commonState.startTime > 5) {
      importance = Importance.Vibe
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    importance = Importance.Base
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    importance = Importance.Base
  }

  def reset() = {
    active = false
    commonState = startState.copy()
    importance = Importance.Base
  }
}

object Cleaning extends Story with Pausable with Delay {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, 5)
  val delay = 10
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Base
  var rooms: Array[Room] = Array(DiningRoom, LivingRoom, Kitchen, Door)
  var cleansSoFar = 0

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Mother.location = rooms(cleansSoFar % rooms.length)
  }
  override def progress(tick: Int): Boolean = {
    proceed()
    if (
      Mother.location == LivingRoom && amountleft < commonState.duration / 2
    ) {
      Mother.tools.add(Tambourine)
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
    beginAnew()
    cleansSoFar += 1
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    if (amountleft < commonState.duration / 3) {
      cleansSoFar += 1
      beginAnew()
    } else {
      restartTime = 3
      pause()
    }
  }

  def reset() = {
    active = false
    commonState = startState.copy()
    beginAnew()
    endTime = 0
    cleansSoFar = 0
  }
}

object NoticeBrokenDoor extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Mother.noticedBrokenDoor, () => Location.areClose(Mother, Door))

  var active: Boolean = false
  val startState = (false, -1, true, 0)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Instantaneous

  // Instantaneous stories immedietely end
  def storySpecificBeginning(tick: Int): Unit = endStory(tick)
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
  }
}
