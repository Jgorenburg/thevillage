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

object Knit extends Story with Pausable with Delay {
  var active: Boolean = false
  var delay = 29
  lazy val actors = HashSet(Son)
  val startState = (false, -1, true, 17)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Importance.interrupt(Son.getCurStoryImportance(), importance)
    )
  var importance: Base.Importance.Importance = Importance.Base
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    commonState = startState
    active = false
    beginAnew()
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = begin()
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
  override def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    commonState = startState
    active = false
    beginAnew()
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Son.tools.add(Knife)
    Worktable.tools.remove(Knife)
  }
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
      return true
    }
    if (
      Sofachair.hasSpace(Snack) && Importance.interrupt(
        Sofachair.getCurStoryImportance(),
        importance
      )
    ) {
      actors.add(Sofachair)
      return true
    }
    if (
      Couch.hasSpace(Snack) && Importance.interrupt(
        Couch.getCurStoryImportance(),
        importance
      )
    ) {
      actors.add(Couch)
      return true
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState
    active = false
    repeatsLeft = 2
    endTime = 0
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = { setEndTime(tick) }
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object GiveScarf extends Story {
  var active: Boolean = false
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
      if (Importance.interrupt(person.getCurStoryImportance(), importance)) {
        actors.add(person)
        return true
      }
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    active = false
    commonState = startState
    actors --= recipients
  }
  val startState: Base.StoryCommonState = (false, -1, false, 2)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {}
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
    commonState = startState
    active = false
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}
