package Snowedin

import Base.Story
import scala.collection.mutable.HashSet
import Base.Importance
import Base.GameManager
import Base.Spaces
import Base.Pausable
import Base.Occupy
import Base.Delay
import Base.StoryRunner
import Base.Actor
import Base.StoryCommonState
import Snowedin.SIRoom.*
import Base.Room.Bedroom
import Base.Room
import Base.Person
import Snowedin.SnowedInPositionConstants.*
import Snowedin.Tools.Tambourine

// Father and Mother
object Chat extends Story with Delay {
  var active: Boolean = false
  lazy val actors = HashSet(Mother, Father)
  val startState = (false, -1, true, 1800)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => SIRoom.areClose(Mother.room, Father.room),
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  def setStartLocations(): Unit = {
    Father.setDestinationNoAdjust(Father.location)
    Mother.setDestination(Father.location)
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Mother.walk()
    }
    return false
  }

  val delay = 10800

  var importance = Importance.Event
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Father.noticedBrokenDoor |= Mother.noticedBrokenDoor
    Father.room = Mother.room
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

}

object CookLunch extends Story {
  lazy val actors = HashSet(Stove)
  var cook: Person = Father
  var conditions: List[() => Boolean] =
    List(
      // halfway through the day
      () => GameManager.tick >= GameManager.ending * 3 / 8,
      () => fatherAvailible() || motherAvailible()
    )

  def fatherAvailible(): Boolean = {
    if (
      Importance.shouldInterrupt(Father.getCurStoryImportance(), importance)
    ) {
      actors.add(Father)
      cook = Father
      actors.remove(Mother)
      return true
    } else {
      actors.remove(Mother)
      return false
    }
  }
  def motherAvailible(): Boolean = {
    if (
      !actors.contains(Father) &&
      Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    ) {
      actors.add(Mother)
      cook = Mother
      return true
    } else {
      actors.remove(Mother)
      return false
    }
  }
  var active: Boolean = false
  val startState = (false, -1, false, 1800)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Interrupt

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = cook.walk()
    }
    return false
  }
  def setStartLocations(): Unit = cook.setDestination(Stove.interactLoc)

  def storySpecificBeginning(tick: Int): Unit = {
    actors.foreach(_.room = Kitchen)
  }
  def storySpecificEnding(tick: Int): Unit = {
    if (actors.contains(Father)) {
      CookDinner.actors.add(Mother)
      CookDinner.cook = Mother
    } else {
      CookDinner.actors.add(Father)
      CookDinner.cook = Father
    }
    Table.readyForLunch = true
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors.clear()
  }
}

object CookDinner extends Story {
  lazy val actors = HashSet(Stove)
  var cook: Person = Father
  var conditions: List[() => Boolean] =
    List(
      // 3/4th through the day
      () => GameManager.tick >= GameManager.ending * 3 / 4,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var active: Boolean = false
  val startState = (false, -1, false, 3600)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Interrupt
  def setStartLocations(): Unit = cook.setDestination(Stove.interactLoc)

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = cook.walk()
    }
    return false
  }
  def storySpecificBeginning(tick: Int): Unit = {
    actors.foreach(_.room = Kitchen)
  }
  def storySpecificEnding(tick: Int): Unit = { Table.readyForDinner = true }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors.clear()
  }
}

// Father and Son, Daughter Optional
object Movie extends Story with Occupy with Pausable {
  val size = 2
  var needToSeat = size
  var active: Boolean = false
  lazy val actors = HashSet(Son, Father)
  val people: HashSet[Person] = HashSet(Son, Father)
  val startState = (false, -1, false, 5400)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => Lunch.commonState.completed,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        ),
      () => livingRoomHasSpace()
    )

  val livingRoomSeating = List(Couch, Sofachair)
  def livingRoomHasSpace(): Boolean = {
    actors --= livingRoomSeating
    val iterator = livingRoomSeating.iterator
    var toFind = size
    while (iterator.hasNext) {
      val seating = iterator.next()
      if (
        Importance.shouldInterrupt(seating.getCurStoryImportance(), importance)
      ) {
        toFind -= seating.curCapacity
        if (seating.curCapacity > 0) {
          actors.add(seating)
        }
        if (toFind <= 0) {
          return true
        }
      }
    }
    actors --= livingRoomSeating
    return false
  }

  def setStartLocations(): Unit = {
    Father.setDestination(Couch.seat2Loc)

    if (actors.contains(Sofachair)) {
      Son.setDestination(Sofachair.getSeatingLoc())
    } else {
      Son.setDestination(Couch.seat1Loc)
    }
  }

  var importance = Importance.Event

  def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Son.walk() & Father.walk()
    }
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors --= livingRoomSeating
    beginAnew()
    needToSeat = size
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    actors.foreach(_.room = LivingRoom)
  }
  def storySpecificEnding(tick: Int): Unit = {}
  restartTime = 300
  def storySpecificInterrupt(tick: Int): Unit = { pause() }

}

object JoinMovie extends Story with Occupy {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(Daughter)
  val startState = (false, -1, true, -1)

  var commonState = startState.copy()
  var conditions: List[() => Boolean] = List(
    () => Movie.active,
    () => Movie.arrived,
    () => GameManager.tick - 600 >= Movie.commonState.startTime,
    () =>
      Importance.shouldInterrupt(Daughter.getCurStoryImportance(), importance),
    () => livingRoomHasSpace(),
    () => SIRoom.areClose(Daughter, LivingRoom)
  )

  val livingRoomSeating = List(Sofachair, Couch)
  def livingRoomHasSpace(): Boolean = {
    actors --= livingRoomSeating
    val iterator = livingRoomSeating.iterator
    while (iterator.hasNext) {
      val seating = iterator.next()
      if (
        Importance.shouldInterrupt(seating.getCurStoryImportance(), importance)
      ) {
        actors.add(seating)
        return true
      }
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def setStartLocations(): Unit = {
    Daughter.setDestination(if (actors.contains(Sofachair)) {
      Sofachair.getSeatingLoc()
    } else {
      Couch.getSeatLoc()
    })
  }

  override def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Daughter.walk()
    }
    return !Movie.active
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Daughter.room = LivingRoom
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

// Son and Daughter
object Gossip extends Story with Delay {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Daughter)
  val startState = (false, -1, true, 2700)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => SIRoom.areClose(Daughter, Son),
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  val delay = 3600
  repeatsLeft = 2

  var importance = Importance.Event
  def setStartLocations(): Unit = {
    Daughter.setDestinationNoAdjust(Daughter.location)
    Son.setDestination(Daughter.location)
  }

  override def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Son.walk()
    }
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    endTime = 0
    repeatsLeft = 2
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Son.room = Daughter.room
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

}

object CleanTable extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Daughter, Table, Dishwasher)
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()
  var trips = 6

  var conditions: List[() => Boolean] =
    List(
      () => Dishwasher.dirty,
      () => Table.readyToClear,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    trips = 6
  }
  val sonCleanLoc = Table.getLoc4()
  val daughtCleanLoc = Table.getLoc1()

  def setStartLocations(): Unit = {
    Son.setDestination(sonCleanLoc)
    Daughter.setDestination(daughtCleanLoc)
    sonsDest = Table
    daughtDest = Table
  }

  var sonsDest: Actor = Table
  var daughtDest: Actor = Table
  def progress(tick: Int): Boolean = {
    if (Son.walk()) {
      arrived = true
      if (sonsDest == Table) {
        sonsDest = Dishwasher
        Son.setDestination(Dishwasher.interactLoc - (0, 1))
      } else {
        sonsDest = Table
        trips -= 1
        Son.setDestination(
          sonCleanLoc
        )
      }
    }
    if (Daughter.walk()) {
      if (daughtDest == Table) {
        daughtDest = Dishwasher
        Daughter.setDestination(Dishwasher.interactLoc)

      } else {
        daughtDest = Table
        trips -= 1
        Daughter.setDestination(
          daughtCleanLoc
        )
      }
    }
    return trips <= 0
  }

  def storySpecificBeginning(tick: Int): Unit = {
    Son.room = DiningRoom
    Daughter.room = DiningRoom
  }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = false
    Son.room = Kitchen
    Daughter.room = Kitchen
    trips = 6
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

}

// Everybody
object Lunch extends Story with Pausable with Occupy {
  var size = 3
  var active: Boolean = false
  lazy val actors = HashSet(Father, Mother, Daughter, Table)
  val startState = (false, -1, false, 1800)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Table.readyForLunch,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt
  def setStartLocations(): Unit = {
    if (actors.contains(Son)) Son.setDestination(Table.getLoc1())
    Daughter.setDestination(Table.getLoc2())
    Mother.setDestination(Table.getLoc3())
    Father.setDestination(Table.getLoc4())
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Daughter.walk() & Father.walk() & Mother.walk()
      if (actors.contains(Son)) arrived = Son.walk() && arrived
    }
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    beginAnew()
    actors.remove(Son)
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    if (
      Importance.shouldInterrupt(Son.getCurStoryImportance(), Importance.Event)
    ) {
      actors.add(Son)
      StoryRunner.stories.remove(Son.commonState.curStory)
      Son.beginStory(this, tick)
      size = 4
      Table.occupy(this)
    }
    actors.foreach(_.room = DiningRoom)
  }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = true
    Table.readyForLunch = false
    Son.lastAte = tick
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
    Son.lastAte = tick
  }
  restartTime = 300

}

object Dinner extends Story with Pausable with Occupy {
  val size = 4
  var active: Boolean = false
  lazy val actors = HashSet(Father, Mother, Son, Daughter, Table)
  val startState = (false, -1, false, 3600)
  restartTime = 600
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => !Table.readyToClear,
      () => Table.readyForDinner,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt
  def setStartLocations(): Unit = {
    Son.setDestination(Table.getLoc1())
    Daughter.setDestination(Table.getLoc2())
    Mother.setDestination(Table.getLoc3())
    Father.setDestination(Table.getLoc4())
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Son.walk() & Daughter.walk() & Father.walk() & Mother.walk()
    }
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    actors.foreach(_.room = DiningRoom)
  }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = true
    Table.readyForDinner = false
    Son.lastAte = tick
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
    Son.lastAte = tick
  }

}

object Singalong extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Mother)
  var potentialSingers = HashSet(Father, Daughter, Son)
  var singingRooms: HashSet[Room] = HashSet()
  val startState = (false, -1, false, 1800)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Music.active,
      () => Dinner.commonState.completed,
      () => potentialSingers.exists(SIRoom.areClose(Mother, _))
    )

  var importance = Importance.Interrupt
  def setStartLocations(): Unit = {
    actors
      .asInstanceOf[HashSet[Person]]
      .foreach(p => p.setDestination(p.location))
  }
  def progress(tick: Int): Boolean = {
    potentialSingers
      .filter(person =>
        Importance.shouldInterrupt(
          person.getCurStoryImportance(),
          importance
        ) && singingRooms.exists(room => SIRoom.areClose(person.room, room))
      )
      .foreach(person => join(person, tick))
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    singingRooms = HashSet(Mother.room)
    potentialSingers
      .filter(person =>
        Importance.shouldInterrupt(
          person.getCurStoryImportance(),
          importance
        ) && singingRooms.exists(room => SIRoom.areClose(person.room, room))
      )
      .foreach(person => join(person, tick))
    arrived = true
  }
  def join(person: Person, tick: Int): Unit = {
    actors.add(person)
    potentialSingers.remove(person)
    singingRooms.add(person.room)
    StoryRunner.interruptStory(person.commonState.curStory)
    person.beginStory(this, tick)
  }
  def storySpecificEnding(tick: Int): Unit = {
    Mother.tools.remove(Tambourine)
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    Mother.tools.remove(Tambourine)
    commonState.completed = true
  }

}

// complex
object Boardgame extends Story with Occupy with Pausable {
  restartTime = 900
  var size = 3
  lazy val actors = HashSet(Son, Daughter, Table)
  var conditions: List[() => Boolean] =
    List(
      () => !Table.readyToClear,
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        ),
      () => fatherAvailible() | motherAvailible()
    )

  def fatherAvailible(): Boolean = {
    actors.remove(Father)
    if (
      Importance.shouldInterrupt(Father.getCurStoryImportance(), importance)
    ) {
      actors.add(Father)
      return true
    } else {
      return false
    }
  }
  def motherAvailible(): Boolean = {
    actors.remove(Mother)
    if (
      Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    ) {
      actors.add(Mother)
      return true
    } else {
      return false
    }
  }

  def setStartLocations(): Unit = {
    Son.setDestination(Table.getLoc1())
    Daughter.setDestination(Table.getLoc2())
    if (actors.contains(Mother)) Mother.setDestination(Table.getLoc3())
    if (actors.contains(Father)) Father.setDestination(Table.getLoc4())
  }

  var active: Boolean = false
  val startState = (false, -1, false, 2700)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    if (actors.contains(Father) && actors.contains(Mother)) {
      size = 4
      Table.occupy(this)
    }
    actors.foreach(_.room = DiningRoom)
  }
  def storySpecificEnding(tick: Int): Unit = {
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Son.walk() & Daughter.walk()
      if (actors.contains(Mother)) arrived = Mother.walk() & arrived
      if (actors.contains(Father)) arrived = Father.walk() & arrived
    }
    proceed()
    return false
  }

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors.clear()
    beginAnew()
  }
}

object FixSomething extends Story with Occupy with Pausable {
  restartTime = 1800
  var size = 1
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] =
    List(
      // TODO: come up with how/why something breaks
      () =>
        actors.forall(actor =>
          Importance.shouldInterrupt(actor.getCurStoryImportance(), importance)
        ),
      () => daughterAvailible() || sonAvailible()
    )

  def sonAvailible(): Boolean = {
    actors.remove(Son)
    if (Importance.shouldInterrupt(Son.getCurStoryImportance(), importance)) {
      actors.add(Son)
      helper = Son
      return true
    } else {
      return false
    }
  }
  def daughterAvailible(): Boolean = {
    actors.remove(Daughter)
    if (
      Importance.shouldInterrupt(Daughter.getCurStoryImportance(), importance)
    ) {
      actors.add(Daughter)
      helper = Daughter
      return true
    } else {
      return false
    }
  }

  var helper: Person = Son
  var active: Boolean = false
  val startState = (false, -1, false, 2700)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  def setStartLocations(): Unit = {
    Father.setDestination(Worktable.interactLoc)
    helper.setDestination(Worktable.interactLoc + (0, 1))
  }

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    actors.foreach(_.room = Workroom)
  }
  def storySpecificEnding(tick: Int): Unit = {
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Father.walk() & helper.walk()
    }
    proceed()
    return false
  }

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
    actors.clear()
    beginAnew()
  }
}
