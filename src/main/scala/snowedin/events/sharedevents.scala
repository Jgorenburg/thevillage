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
    List(() => motherNotices())

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
      () => Lunch.commonState.completed,
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
      () => !Table.readyToClear,
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

object Singalong extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Mother)
  val potentialSingers = List(Father, Daughter, Son)
  val startState = (false, -1, false, 3)
  var commonState = startState.copy()

  var conditions: List[() => Boolean] =
    List(
      () => Music.active,
      () => Dinner.commonState.completed
    )

  var importance = Importance.Interrupt
  var joinInImportance = Importance.Event
  override def progress(tick: Int): Boolean = {
    potentialSingers
      .filter(person =>
        Importance.interrupt(person.getCurStoryImportance(), joinInImportance)
      )
      .foreach(person => join(person, tick))
    return false
  }
  def reset(): Unit = {
    active = false
    commonState = startState
  }
  def storySpecificBeginning(tick: Int): Unit = {
    commonState.completed = true
    Music.commonState.completed = true
    potentialSingers
      .filter(person =>
        Importance.interrupt(person.getCurStoryImportance(), joinInImportance)
      )
      .foreach(person => join(person, tick))
  }
  def join(person: Actor, tick: Int): Unit = {
    actors.add(person)
    StoryRunner.stories.remove(person.commonState.curStory)
    person.beginStory(this, tick)
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

}

// complex
object Boardgame extends Story with Occupy with Pausable {
  restartTime = 2
  var size = 3
  lazy val actors = HashSet(Son, Daughter, Table)
  var conditions: List[() => Boolean] =
    List(
      () => !Table.readyToClear,
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        ),
      () => fatherAvailible() | motherAvailible()
    )

  def fatherAvailible(): Boolean = {
    actors.remove(Father)
    if (Importance.interrupt(Father.getCurStoryImportance(), importance)) {
      actors.add(Father)
      return true
    } else {
      return false
    }
  }
  def motherAvailible(): Boolean = {
    actors.remove(Mother)
    if (Importance.interrupt(Mother.getCurStoryImportance(), importance)) {
      actors.add(Mother)
      return true
    } else {
      return false
    }
  }
  var active: Boolean = false
  val startState = (false, -1, false, 14)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    if (actors.contains(Father) && actors.contains(Mother)) {
      size = 4
      Table.occupy(this)
    }
  }
  def storySpecificEnding(tick: Int): Unit = {
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }

  def reset(): Unit = {
    active = false
    commonState = startState
    actors.clear()
    beginAnew()
  }
}

object FixSomething extends Story with Occupy with Pausable {
  restartTime = 3
  var size = 1
  lazy val actors = HashSet(Father, Worktable)
  var conditions: List[() => Boolean] =
    List(
      // TODO: come up with how/why something breaks
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        ),
      () => daughterAvailible() || sonAvailible()
    )

  def sonAvailible(): Boolean = {
    actors.remove(Son)
    if (Importance.interrupt(Son.getCurStoryImportance(), importance)) {
      actors.add(Son)
      return true
    } else {
      return false
    }
  }
  def daughterAvailible(): Boolean = {
    actors.remove(Daughter)
    if (Importance.interrupt(Daughter.getCurStoryImportance(), importance)) {
      actors.add(Daughter)
      return true
    } else {
      return false
    }
  }
  var active: Boolean = false
  val startState = (false, -1, false, 7)
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
  }
  def storySpecificEnding(tick: Int): Unit = {
    beginAnew()
  }
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }

  def reset(): Unit = {
    active = false
    commonState = startState
    actors.clear()
    beginAnew()
  }
}

object Breakfast extends Story {
  var active: Boolean = false
  lazy val actors = HashSet()
  val eaters = HashSet(Father, Mother, Daughter, Son)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick <= GameManager.ending / 6,
      () => Table.curCapacity > 0,
      () => Importance.interrupt(Table.getCurStoryImportance(), importance),
      () =>
        eaters.exists(person =>
          Importance.interrupt(person.getCurStoryImportance(), importance)
        )
    )
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState
    active = false
  }
  val startState: Base.StoryCommonState = (false, -1, true, 0)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    val willEat = eaters.filter(person =>
      Importance.interrupt(person.getCurStoryImportance(), importance)
    )
    willEat.foreach(eater => StoryRunner.addStory(new IndivBreakfast(eater)))
    eaters --= willEat
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

class IndivBreakfast(eater: Actor) extends Story with Occupy {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(eater, Table)
  var conditions: List[() => Boolean] =
    List(() => false)
  var importance: Base.Importance.Importance = Breakfast.importance
  def reset(): Unit = {}
  val startState: Base.StoryCommonState = (false, -1, false, 4)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    if (eater == Son) {
      Son.lastAte = tick
    }
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    if (eater == Son) {
      Son.lastAte = tick
    }
  }
}
