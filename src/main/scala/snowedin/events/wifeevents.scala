package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story
import Snowedin.Tools.Tambourine
import Base.Pausable
import Base.Occupy

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

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {
    if (tick - commonState.startTime > 5) {
      importance = Importance.Vibe
    }
  }
  def storySpecificEnding(tick: Int): Unit = {
    importance = Importance.Base
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    importance = Importance.Base
  }

  def reset() = {
    active = false
    commonState = startState
    importance = Importance.Base
  }
}

object RearrangeHousehold extends Story with Pausable {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  val startState = (false, -1, false, 20)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = beginAnew()
  def progress(tick: Int): Unit = {
    proceed()
    if (amountleft < commonState.duration / 2) {
      Mother.tools.add(Tambourine)
    }
  }
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
  }

  def reset() = {
    active = false
    commonState = startState
    beginAnew()
  }
}
