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

// Father and Mother
object Chat extends Story with Delay {
  var active: Boolean = false
  lazy val actors = HashSet(Mother, Father)
  val startState = (false, -1, true, 3)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  val delay = 15

  var importance = Importance.Event
  def reset(): Unit = {
    active = false
    commonState = startState
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Father.noticedBrokenDoor |= Mother.noticedBrokenDoor
  }
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

}

// Father or Mother
object NoticeBrokenDoor extends Story {
  lazy val actors = HashSet()
  var conditions: List[() => Boolean] =
    List(() => fatherNotices() | motherNotices())

  def fatherNotices(): Boolean = {
    if (
      !Father.noticedBrokenDoor &&
      GameManager.tick >= GameManager.ending / 3 &&
      Laundry.commonState.startTime > 0
    ) {
      actors.add(Father)
      return true
    } else {
      actors.remove(Father)
      return false
    }
  }

  def motherNotices(): Boolean = {
    if (!Mother.noticedBrokenDoor && Cleaning.commonState.startTime > 0) {
      actors.add(Mother)
      return true
    } else {
      actors.remove(Mother)
      return false
    }
  }
  var active: Boolean = false
  val startState = (false, -1, true, 0)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Instantaneous

  // Instantaneous stories immedietely end
  def storySpecificBeginning(tick: Int): Unit = endStory(tick)
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState
  }
}

object CookLunch extends Story {
  lazy val actors = HashSet(Stove)
  var conditions: List[() => Boolean] =
    List(
      // halfway through the day
      () => GameManager.tick >= GameManager.ending * 3 / 8,
      () => fatherAvailible() || motherAvailible()
    )

  def fatherAvailible(): Boolean = {
    if (Importance.interrupt(Father.getCurStoryImportance(), importance)) {
      actors.add(Father)
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
      Importance.interrupt(Mother.getCurStoryImportance(), importance)
    ) {
      actors.add(Mother)
      return true
    } else {
      actors.remove(Mother)
      return false
    }
  }
  var active: Boolean = false
  val startState = (false, -1, false, 6)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Interrupt

  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    if (actors.contains(Father)) {
      CookDinner.actors.add(Mother)
    } else {
      CookDinner.actors.add(Father)
    }
    Table.readyForLunch = true
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState
    actors.clear()
  }
}

object CookDinner extends Story {
  lazy val actors = HashSet(Stove)
  var conditions: List[() => Boolean] =
    List(
      // 3/4th through the day
      () => GameManager.tick >= GameManager.ending * 3 / 4,
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var active: Boolean = false
  val startState = (false, -1, false, 7)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Interrupt

  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = { Table.readyForDinner = true }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState
    actors.clear()
  }
}

// Father and Son, TODO: Daughter Optional
object Movie extends Story with Occupy with Pausable {
  val size = 2
  var needToSeat = size
  var active: Boolean = false
  lazy val actors = HashSet(Son, Father)
  val startState = (false, -1, false, 23)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
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
      if (Importance.interrupt(seating.getCurStoryImportance(), importance)) {
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

  var importance = Importance.Event
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState
    actors --= livingRoomSeating
    beginAnew()
    needToSeat = size
  }
  def storySpecificBeginning(tick: Int): Unit = { begin() }
  def storySpecificEnding(tick: Int): Unit = {}
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
    () => GameManager.tick - 2 >= Movie.commonState.startTime,
    () => Importance.interrupt(Daughter.getCurStoryImportance(), importance),
    () => livingRoomHasSpace()
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
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState
    active = false
  }
  override def progress(tick: Int): Boolean = {
    return !Movie.active
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

// Son and Daughter
object Gossip extends Story with Delay {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Daughter)
  val startState = (false, -1, true, 6)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  val delay = 20
  repeatsLeft = 2

  var importance = Importance.Event
  def reset(): Unit = {
    active = false
    commonState = startState
    endTime = 0
    repeatsLeft = 2
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    setEndTime(tick)
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

}

object CleanTable extends Story with Pausable {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Daughter, Table, Dishwasher)
  val startState = (false, -1, true, 5)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Dishwasher.dirty,
      () => Table.readyToClear,
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = { begin() }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = false
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }

}

// Everybody
object Lunch extends Story with Pausable with Occupy {
  var size = 3
  var active: Boolean = false
  lazy val actors = HashSet(Father, Mother, Daughter, Table)
  val startState = (false, -1, false, 10)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Table.readyForLunch,
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState
    beginAnew()
    actors.remove(Son)
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    if (Importance.interrupt(Son.getCurStoryImportance(), importance)) {
      actors.add(Son)
      StoryRunner.stories.remove(Son.commonState.curStory)
      Son.beginStory(this, tick)
      size = 4
      Table.occupy(this)
    }
  }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = true
    Son.lastAte = tick
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
    Son.lastAte = tick
  }

}

object Dinner extends Story with Pausable with Occupy {
  val size = 4
  var active: Boolean = false
  lazy val actors = HashSet(Father, Mother, Son, Daughter, Table)
  val startState = (false, -1, false, 10)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Table.readyForDinner,
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  var importance = Importance.Interrupt
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
  }
  def storySpecificEnding(tick: Int): Unit = {
    Table.readyToClear = true
    Son.lastAte = tick
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
    Son.lastAte = tick
  }

}
