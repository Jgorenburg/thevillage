package DateNight

import Base.Story
import scala.collection.mutable.HashSet
import Base.StoryCommonState
import Base.Importance
import Base.PlayerBased

object Exploring extends Story with PlayerBased {

  val canMove = true
  lazy val actors = HashSet(Player)

  val startState = (false, -1, true, -1)
  var commonState = startState.copy()

  var conditions = List(() =>
    Importance
      .shouldInterrupt(Player.getCurStoryImportance(), importance)
  )
  var importance = Importance.Base
  def progress(tick: Int): Boolean = { return false }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def setStartLocations(): Unit = Player.location
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}
