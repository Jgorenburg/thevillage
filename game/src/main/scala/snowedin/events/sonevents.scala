package Snowedin

import Base.Story
import Base.Importance
import scala.collection.mutable.HashSet
import Base.Pausable
import Snowedin.Tools.Knife
import Base.Occupy
import Base.GameManager
import Base.StoryCommonState
import Base.Delay
import Snowedin.Location.Workroom
import Snowedin.Location.Room
import Snowedin.Location.Bedroom
import Snowedin.Location.Kitchen
import Snowedin.PositionConstants.topLeft
import Snowedin.PositionConstants.boxSize
import Snowedin.PositionConstants.topRight
import Snowedin.PositionConstants.bottomLeft
import Base.Person
import Snowedin.PositionConstants.bottomRight
import Snowedin.Location.LivingRoom

object Knit extends Story with Pausable with Delay with Occupy {
  var active: Boolean = false
  var delay = 29
  var size = 1
  lazy val actors = HashSet(Son)
  val startState = (false, -1, true, 17)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => livingRoomHasSpace(),
      () => Importance.interrupt(Son.getCurStoryImportance(), importance)
    )

  val livingRoomSeating = List(Sofachair, Couch)
  def livingRoomHasSpace(): Boolean = {
    actors --= livingRoomSeating
    val iterator = livingRoomSeating.iterator
    while (iterator.hasNext) {
      val seating = iterator.next()
      if (Importance.interrupt(seating.getCurStoryImportance(), importance)) {
        actors.add(seating)
        return true
      }
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Base
  def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Son.walk()
    }
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    beginAnew()
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Son.room = LivingRoom
    begin()
  }
  def setStartLocations(): Unit = {
    Son.setDestination(if (actors.contains(Sofachair)) {
      Sofachair.getSeatingLoc()
    } else {
      Couch.getSeatLoc()
    })
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
}

object Woodworking extends Story with Pausable with Delay {
  var active: Boolean = false
  var delay = 13
  lazy val actors = HashSet(Son, Worktable)
  val startState = (false, -1, true, 31)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Worktable.tools.contains(Knife) || Son.tools.contains(Knife),
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )
  var importance: Base.Importance.Importance = Importance.Base

  def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Son.walk()
    }
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    beginAnew()
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Son.tools.add(Knife)
    Worktable.tools.remove(Knife)
    Son.room = Workroom
  }
  def setStartLocations(): Unit =
    Son.setDestination(topRight._1 - 3 * boxSize, topRight._2 - 3 * boxSize)

  def storySpecificEnding(tick: Int): Unit = {
    Son.tools.remove(Knife)
    Worktable.tools.add(Knife)
    setEndTime(tick)
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
  }
}

object Snack extends Story with Occupy with Delay {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(Son)
  val startState = (false, -1, true, 3)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick - 20 > Son.lastAte,
      () => readyToRepeat(),
      () => Importance.interrupt(Son.getCurStoryImportance(), importance),
      () => locationIsFree()
    )

  var delay = 0
  var location: Room = Bedroom
  repeatsLeft = 2

  // priority order: Table > Sofachair > Couch
  def locationIsFree(): Boolean = {
    actors --= List(Table, Sofachair, Couch)
    if (
      Table.hasSpace(Snack) && Importance.interrupt(
        Table.getCurStoryImportance(),
        importance
      )
    ) {
      actors.add(Table)
      location = Table.room
      return true
    }
    if (
      Sofachair.hasSpace(Snack) && Importance.interrupt(
        Sofachair.getCurStoryImportance(),
        importance
      )
    ) {
      actors.add(Sofachair)
      location = Sofachair.room
      return true
    }
    if (
      Couch.hasSpace(Snack) && Importance.interrupt(
        Couch.getCurStoryImportance(),
        importance
      )
    ) {
      actors.add(Couch)
      location = Couch.room
      return true
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    repeatsLeft = 2
    endTime = 0
  }

  var reachedFridge = false
  var reachedSeating = false
  def progress(tick: Int): Boolean = {
    if (!reachedFridge) {
      if (Son.walk()) {
        Son.setDestination(
          Table.getLoc1()
        )
        reachedFridge = true
        arrived = true
      }
    } else if (!reachedSeating) {
      reachedSeating = Son.walk()
    }
    return false
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Son.room = location
    reachedFridge = false
    reachedSeating = false
  }
  def setStartLocations(): Unit =
    Son.setDestination(bottomLeft._1 + 4 * boxSize, bottomLeft._2 + 3 * boxSize)
  def storySpecificEnding(tick: Int): Unit = { setEndTime(tick) }
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object GiveScarf extends Story {
  var active: Boolean = false
  var location: (Float, Float) = (0, 0)
  lazy val actors = HashSet(Son)
  var conditions: List[() => Boolean] = List(
    () => Knit.commonState.completed,
    () => Importance.interrupt(Son.getCurStoryImportance(), importance),
    () => availibleRecipient()
  )
  // TODO: add daughter
  val recipients = List(Father, Mother)
  def availibleRecipient(): Boolean = {
    actors --= recipients
    val iterator = recipients.iterator
    while (iterator.hasNext) {
      val person = iterator.next()
      if (
        Importance.interrupt(person.getCurStoryImportance(), importance) &&
        Location.areClose(Son, person)
      ) {
        actors.add(person)
        room = person.room
        location = person.location
        return true
      }
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Event
  var room = Bedroom
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors --= recipients
  }
  val startState: Base.StoryCommonState = (false, -1, false, 2)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    Son.room = room
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Son.walk()
    }
    return false
  }
  def setStartLocations(): Unit = {
    actors
      .filter(_.isInstanceOf[Person])
      .asInstanceOf[HashSet[Person]]
      .foreach(_.setDestinationNoAdjust(location))
  }

  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object StartDishwasher extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Dishwasher)
  val startState: Base.StoryCommonState = (false, -1, false, 1)

  var commonState: Base.StoryCommonState = startState.copy()
  var conditions: List[() => Boolean] = List(
    () => Dishwasher.readyToWash,
    () =>
      actors.forall(actor =>
        Importance.interrupt(actor.getCurStoryImportance(), importance)
      )
  )
  var importance: Base.Importance.Importance = Importance.Interrupt
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def setStartLocations(): Unit = Son.setDestination(
    bottomRight._1 - 4 * boxSize,
    bottomRight._2 + 4 * boxSize
  )

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Son.walk()
    }
    return false
  }
  def storySpecificBeginning(tick: Int): Unit = { Son.room = Kitchen }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}
