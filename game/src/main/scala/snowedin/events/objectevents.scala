package Snowedin

import Base.Story
import Base.Pausable
import scala.collection.mutable.HashSet
import Base.Importance

object RunDishwasher extends Story with Pausable {
  var active: Boolean = false
  lazy val actors = HashSet(Dishwasher)
  val startState: Base.StoryCommonState = (false, -1, false, 5400)

  var commonState: Base.StoryCommonState = startState.copy()
  var conditions: List[() => Boolean] = List(
    () => Dishwasher.running,
    () =>
      Importance.shouldInterrupt(Dishwasher.getCurStoryImportance(), importance)
  )

  def setStartLocations(): Unit = {}
  var importance: Base.Importance.Importance = Importance.Event
  def progress(tick: Int): Boolean = {
    proceed()
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    beginAnew()
  }
  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    arrived = true
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = { pause() }
}
