package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story
import Snowedin.Tools.Tamborine

object Cleaning extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  var commonState = (false, -1, false, 7)
  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = (false, -1, false, 7)
  }
}

object Music extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => Mother.tools.contains(Tamborine),
      () => Importance.interrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  var commonState = (false, -1, false, 4)
  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = (false, -1, false, 4)
  }
}

object Art extends Story {
  lazy val actors = HashSet(Mother, Easle)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  var commonState = (false, -1, true, -1)
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
    commonState = (false, -1, true, -1)
    importance = Importance.Base
  }
}

object RearrangeHousehold extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  var commonState = (false, -1, false, 20)
  var amountleft = commonState.duration

  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {
    amountleft -= 1
    if (amountleft < commonState.duration / 2) {
      Mother.tools.add(Tamborine)
    }
  }
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {
    commonState.duration = amountleft
  }

  def reset() = {
    active = false
    commonState = (false, -1, false, 20)
    amountleft = commonState.duration
  }
}
