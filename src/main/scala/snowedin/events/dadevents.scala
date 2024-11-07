package Snowedin

import Base.Story
import Base.Importance
import scala.util.Random
import Base.Actor
import Base.GameManager
import scala.collection.mutable.HashSet
import Snowedin.Tools.Screwdriver

// Father only
object Laundry extends Story {
  lazy val actors = HashSet(Father)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick > 3,
      () => Importance.interrupt(Father.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  var commonState = (false, -1, false, 7)
  var importance: Importance.Importance = Importance.Event

  // Father will collect laundry, then go to laundry machine
  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = (false, -1, false, 7)
  }
}

object Nap extends Story {
  lazy val actors = HashSet(Father, Couch)

  var conditions: List[() => Boolean] = List(
    () => Couch.curCapacity == 2,
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Couch.getCurStoryImportance(), importance)
  )
  var active: Boolean = false

  var commonState = (false, -1, true, 10)

// state:
//     0: how many naps father has taken
//     1: when the nap started
  var state: Array[Any] = Array(0, -1)

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = (false, -1, true, 10)
    state = Array(0, -1)
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
  var commonState = (false, -1, false, 3)

  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = (false, -1, false, 3)
  }

}

object Construction extends Story {
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] = List(
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Worktable.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  var commonState = (false, -1, true, 15)
  var amountleft = commonState.duration
  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = { amountleft -= 1 }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {
    commonState.duration = amountleft
  }

  def reset(): Unit = {
    active = false
    commonState = (false, -1, true, 15)
    amountleft = commonState.duration
  }
}
