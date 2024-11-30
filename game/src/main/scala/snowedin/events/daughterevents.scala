package Snowedin

import Base.Occupy
import Base.Story
import scala.collection.mutable.HashSet
import Base.Importance
import Base.Delay
import Base.GameManager
import Base.Pausable
import Snowedin.Location.LivingRoom
import Snowedin.Location.Kitchen
import Base.Actor
import Snowedin.SnowedInPositionConstants.*

object Read extends Story with Occupy {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(Daughter)
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () =>
        Importance
          .shouldInterrupt(Daughter.getCurStoryImportance(), importance),
      () => livingRoomHasSpace()
    )

  val livingRoomSeating = List(Sofachair, Couch)
  var location: (Float, Float) = (0, 0)
  def livingRoomHasSpace(): Boolean = {
    actors --= livingRoomSeating
    val iterator = livingRoomSeating.iterator
    while (iterator.hasNext) {
      val seating = iterator.next()
      if (
        Importance.shouldInterrupt(seating.getCurStoryImportance(), importance)
      ) {
        actors.add(seating)
        seat = seating
        return true
      }
    }
    return false
  }

  var seat: Actor = Couch
  def setStartLocations(): Unit = {
    Daughter.setDestination(
      seat match
        case Sofachair => Sofachair.getSeatingLoc()
        case Couch     => Couch.getSeatLoc()
    )
  }

  var importance: Base.Importance.Importance = Importance.Base
  def progress(tick: Int): Boolean = {
    if (tick - commonState.startTime > 2700) {
      importance = Importance.Vibe
    }
    if (!arrived) {
      arrived = Daughter.walk()
    }
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    importance = Importance.Base
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Daughter.room = LivingRoom
  }
  def storySpecificEnding(tick: Int): Unit = { importance = Importance.Base }
  def storySpecificInterrupt(tick: Int): Unit = { importance = Importance.Base }
}

object Watercolor extends Story with Occupy with Delay {
  val size = 1
  var delay = 1800
  repeatsLeft = 3
  lazy val actors = HashSet(Daughter, Easle)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Easle.curCapacity >= size,
      () =>
        Importance.shouldInterrupt(Daughter.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, 1800)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Base

  def storySpecificBeginning(tick: Int): Unit = {
    Daughter.room = LivingRoom
  }
  def storySpecificEnding(tick: Int): Unit = {
    importance = Importance.Base
    setEndTime(tick)
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    importance = Importance.Base
  }

  def setStartLocations(): Unit = Daughter.setDestination(Easle.interactLoc)

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Daughter.walk()
    }
    return false
  }

  def reset() = {
    active = false
    commonState = startState.copy()
    importance = Importance.Base
    endTime = 0
    repeatsLeft = 3
  }
}

object StartFire extends Story {
  lazy val actors = HashSet(Daughter)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick > GameManager.ending * 5 / 6,
      () =>
        Importance.shouldInterrupt(Daughter.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 600)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit = Daughter.setDestination(Fireplace.interactLoc)
  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Daughter.walk()
    }
    return false
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Daughter.room = LivingRoom
  }
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object UnloadDishwasher extends Story with Pausable {
  var active: Boolean = false
  lazy val actors = HashSet(Daughter, Dishwasher)
  val startState: Base.StoryCommonState = (false, -1, false, 1200)

  var commonState: Base.StoryCommonState = startState.copy()
  var conditions: List[() => Boolean] = List(
    () => Dishwasher.clean,
    () =>
      actors.forall(actor =>
        Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
      )
  )
  var importance: Base.Importance.Importance = Importance.Event
  def setStartLocations(): Unit =
    Daughter.setDestination(Dishwasher.interactLoc)

  def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Daughter.walk()
    }
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Daughter.room = Kitchen
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
}
