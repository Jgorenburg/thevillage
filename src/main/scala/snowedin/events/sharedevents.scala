package Snowedin

import Base.Story
import scala.collection.mutable.HashSet
import Base.Importance
import Base.GameManager

// Father and Mother
object Chat extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Mother, Father)
  var commonState = (false, -1, true, 3)
  var conditions: List[() => Boolean] =
    List(
      () => noRecentChats(),
      () =>
        actors.forall(actor =>
          Importance.interrupt(actor.getCurStoryImportance(), importance)
        )
    )

  val chatGap = 15
  var chatEnd = 0 - chatGap
  def noRecentChats(): Boolean = {
    return GameManager.tick - chatGap >= chatEnd
  }
  var importance = Importance.Event
  def progress(tick: Int): Unit = {}
  def reset(): Unit = {
    active = false
    commonState = (false, -1, true, 3)
    chatEnd = 0 - chatGap
  }
  def storySpecificBeginning(tick: Int): Unit = {
    Father.noticedBrokenDoor |= Mother.noticedBrokenDoor
  }
  def storySpecificEnding(tick: Int): Unit = {
    chatEnd = tick
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
  var commonState = (false, -1, true, 0)
  var importance: Importance.Importance = Importance.Instantaneous

  // Instantaneous stories immedietely end
  def storySpecificBeginning(tick: Int): Unit = endStory(tick)
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = (false, -1, false, 0)
  }
}

object CookLunch extends Story {
  lazy val actors = HashSet()
  var conditions: List[() => Boolean] =
    List(
      // halfway through the day
      () => GameManager.tick >= GameManager.ending / 2,
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
  var commonState = (false, -1, false, 6)
  var importance: Importance.Importance = Importance.Interrupt

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    if (actors.contains(Father)) {
      CookDinner.actors.add(Mother)
    } else {
      CookDinner.actors.add(Father)
    }
  }
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = (false, -1, false, 6)
    actors.clear()
  }
}

object CookDinner extends Story {
  lazy val actors = HashSet()
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
  var commonState = (false, -1, false, 7)
  var importance: Importance.Importance = Importance.Interrupt

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = (false, -1, false, 7)
    actors.clear()
  }
}
