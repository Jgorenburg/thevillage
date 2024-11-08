package Snowedin

import Base.Story
import Base.Importance
import scala.collection.mutable.HashSet
import Base.Pausable
import Snowedin.Tools.Knife
import Base.Occupy
import Base.GameManager
import Base.StoryCommonState

object Knit extends Story with Pausable {
  var active: Boolean = false
  lazy val actors = HashSet(Son)
  val startState = (false, -1, true, 17)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Son.getCurStoryImportance(), importance))
  var importance: Base.Importance.Importance = Importance.Base
  def progress(tick: Int): Unit = proceed()
  def reset(): Unit = {
    commonState = startState
    active = false
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = beginAnew()
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object Woodworking extends Story with Pausable {
  var active: Boolean = false
  lazy val actors = HashSet(Son, Worktable)
  val startState = (false, -1, false, 31)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => Worktable.tools.contains(Knife) || Son.tools.contains(Knife),
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )
  var importance: Base.Importance.Importance = Importance.Base
  def progress(tick: Int): Unit = proceed()
  def reset(): Unit = {
    commonState = startState
    active = false
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    beginAnew()
    Son.tools.add(Knife)
    Worktable.tools.remove(Knife)
  }
  def storySpecificEnding(tick: Int): Unit = {
    Son.tools.remove(Knife)
    Worktable.tools.add(Knife)
  }
  def storySpecificInterrupt(tick: Int): Unit = {
    pause()
  }
}

object Snack extends Story with Occupy {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(Son)
  val startState = (false, -1, true, 3)
  var commonState = startState.copy()
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick - 20 > Son.lastAte,
      () => Importance.interrupt(Son.getCurStoryImportance(), importance),
      () => locationIsFree()
    )

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
  def progress(tick: Int): Unit = {}
  def reset(): Unit = {
    commonState = startState
    active = false
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
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
  def progress(tick: Int): Unit = {}
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
