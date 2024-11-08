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

// Father only
object Laundry extends Story {
  lazy val actors = HashSet(Father)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick > 3,
      () => Importance.interrupt(Father.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 7)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event

  // Father will collect laundry, then go to laundry machine
  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState
  }
}

object Nap extends Story with Occupy {
  lazy val actors = HashSet(Father, Couch)
  val size = 2

  var conditions: List[() => Boolean] = List(
    () => sleepy(),
    () => Couch.curCapacity == 2,
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Couch.getCurStoryImportance(), importance)
  )

  val napGap = 20
  var napEnd = 0
  def sleepy(): Boolean = {
    return GameManager.tick - napGap >= napEnd
  }
  var active: Boolean = false

  val startState = (false, -1, true, 10)
  var commonState = startState.copy()

// state:
//     0: how many naps father has taken
//     1: when the nap started
  var state: Array[Any] = Array(0, -1)

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    napEnd = tick
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState
    state = Array(0, -1)
    napEnd = 0
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
  val startState = (false, -1, false, 3)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState
  }

}

object Construction extends Story with Pausable {
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] = List(
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Worktable.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  val startState = (false, -1, true, 15)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = { beginAnew() }
  def progress(tick: Int): Unit = { proceed() }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = { pause() }

  def reset(): Unit = {
    active = false
    commonState = startState
    beginAnew()
  }
}
