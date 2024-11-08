package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story
import Snowedin.Tools.Tambourine
import Base.Pausable
import Base.Occupy
import Base.Delay

object Cleaning extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  val startState = (false, -1, false, 7)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState
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
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState
  }
}

object Art extends Story with Occupy with Delay {
  val size = 1
  lazy val actors = HashSet(Mother, Easle)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Easle.curCapacity >= size,
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  var delay = 19
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {
    if (tick - commonState.startTime > 5) {
      importance = Importance.Vibe
    }
  }
  def storySpecificEnding(tick: Int): Unit = {
    importance = Importance.Base
    setEndTime(tick)
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    importance = Importance.Base
  }

  def reset() = {
    active = false
    commonState = startState
    importance = Importance.Base
    endTime = 0
  }
}

object RearrangeHousehold extends Story with Pausable with Delay {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, 20)
  var delay = 60
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = beginAnew()
  def progress(tick: Int): Unit = {
    proceed()
    if (amountleft < commonState.duration / 2) {
      Mother.tools.add(Tambourine)
    }
  }
  def storySpecificEnding(tick: Int): Unit = { setEndTime(tick) }

  def storySpecificInterrupt(tick: Int): Unit = {
    restartTime = 3
    pause()
  }

  def reset() = {
    active = false
    commonState = startState
    beginAnew()
    endTime = 0
  }
}
