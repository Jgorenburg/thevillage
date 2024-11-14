package Snowedin

import Base.Story
import Base.Importance
import scala.util.Random
import Base.Actor
import Base.GameManager
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver
import Base.Pausable
import Base.Occupy
import Base.Delay
import Snowedin.Location.Workroom
import Snowedin.Location.LivingRoom
import Snowedin.Location.Door
import Snowedin.PositionConstants.topRight
import Snowedin.PositionConstants.boxSize
import Snowedin.PositionConstants.topLeft

// Father only
object Laundry extends Story {
  lazy val actors = HashSet(Father)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick > 3,
      () => Importance.interrupt(Father.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 900)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit =
    Father.setDestination(topRight._1 - 3 * boxSize, topRight._2 - 9 * boxSize)

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Father.walk()
    }
    return false
  }

  // Father will collect laundry, then go to laundry machine
  def storySpecificBeginning(tick: Int): Unit = { Father.room = Workroom }
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object Nap extends Story with Occupy with Delay {
  lazy val actors = HashSet(Father, Couch)
  val size = 2

  var conditions: List[() => Boolean] = List(
    () => readyToRepeat(),
    () => Couch.curCapacity == 2,
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Couch.getCurStoryImportance(), importance)
  )

  val delay = 10800 // 3 hours
  var active: Boolean = false

  val startState = (false, -1, true, 2700)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Base
  def setStartLocations(): Unit =
    Father.setDestination(Couch.seat1Loc)

  def storySpecificBeginning(tick: Int): Unit = { Father.room = LivingRoom }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Father.walk()
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
    endTime = 0
  }
}

object FixDoor extends Story {
  lazy val actors = HashSet(Father)
  var conditions: List[() => Boolean] = List(
    () => Father.noticedBrokenDoor,
    () => Father.tools.contains(Screwdriver),
    () => Importance.interrupt(Father.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  val startState = (false, -1, false, 900)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit = Father.setDestination(FrontDoor.location)

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Father.walk()
    }
    return false
  }

  def storySpecificBeginning(tick: Int): Unit = { Father.room = Door }
  def storySpecificEnding(tick: Int): Unit = { GlobalVars.brokenDoor = false }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
  }

}

object Construction extends Story with Pausable with Delay {
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] = List(
    () => readyToRepeat(),
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Worktable.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()

  val delay = 1200

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Father.room = Workroom
  }
  def setStartLocations(): Unit =
    Father.setDestination(topRight._1 - 3 * boxSize, topLeft._2 - 4 * boxSize)

  def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Father.walk()
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    beginAnew()
    endTime = 0
  }
}
