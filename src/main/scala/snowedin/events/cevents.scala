package Snowedin

import Base.Story
import scala.collection.mutable.HashSet
import Base.Importance

// Father or Mother
object NoticeBrokenDoor extends Story {
  lazy val actors = HashSet()
  var conditions: List[() => Boolean] =
    List(() => fatherNotices() | motherNotices())

  def fatherNotices(): Boolean = {
    if (!Father.noticedBrokenDoor && Laundry.commonState.startTime > 0) {
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
  val importance: Importance.Importance = Importance.Instantaneous

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
