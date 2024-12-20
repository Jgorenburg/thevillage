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
import Snowedin.SIRoom.*
import Base.Room.Bedroom
import Snowedin.SnowedInPositionConstants.*

// Father only
object Laundry extends Story {
  lazy val actors = HashSet(Father)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick > 3,
      () =>
        Importance.shouldInterrupt(Father.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 900)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit =
    Father.setDestination(WashingMachine.interactLoc)

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
    () =>
      Importance.shouldInterrupt(Father.getCurStoryImportance(), importance),
    () => Importance.shouldInterrupt(Couch.getCurStoryImportance(), importance)
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
    () => Importance.shouldInterrupt(Father.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  val startState = (false, -1, false, 900)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit = Father.setDestination(FrontDoor.interactLoc)

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

object Construction extends Story with Delay {
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] = List(
    () => readyToRepeat(),
    () =>
      Importance.shouldInterrupt(Father.getCurStoryImportance(), importance),
    () =>
      Importance.shouldInterrupt(Worktable.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()

  val delay = 1200

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {
    Father.room = Workroom
  }
  def setStartLocations(): Unit =
    Father.setDestination(Worktable.interactLoc)

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Father.walk()
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = { setEndTime(tick) }

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    endTime = 0
  }
}
