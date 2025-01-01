package DateNight

import Base.Story
import scala.collection.mutable.HashSet
import Base.StoryCommonState
import Base.Importance
import Base.PlayerBased
import Base.SpeechBubble
import Base.GlobalVars
import Base.ConversationBase
import Base.Snippet
import scala.collection.mutable.Queue
import Base.DialogController

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
  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = true
    }
    return false
  }
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def setStartLocations(): Unit = Player.location
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object DiscoverBench extends Story with PlayerBased {

  val canMove = false
  lazy val actors = HashSet(Player, Bench)

  val startState = (false, -1, true, -1)
  var commonState = startState.copy()

  var conditions = List(
    () =>
      Importance
        .shouldInterrupt(Player.getCurStoryImportance(), importance),
    () => Bench.canInteract()
  )
  var importance = Importance.Event
  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = true
      if (Player.discoveredBench)
        DialogController.setPlayerConversation(alreadyFoundBenchDialog)
      else DialogController.setPlayerConversation(findParkBenchDialog)
    }
    return dialogFinished
  }

  var findParkBenchDialog = new ConversationBase(
    this,
    List(Snippet(Player, "salmon", false), Snippet(Player, "cod", false))
  )

  var alreadyFoundBenchDialog = new ConversationBase(
    this,
    List(Snippet(Player, "I already found this bench", false))
  )

  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def setStartLocations(): Unit = Player.location
  def storySpecificBeginning(tick: Int): Unit = {
    if (Player.discoveredBench) {
      dialogFinished = false
    }
  }
  def storySpecificEnding(tick: Int): Unit = {
    if (!Player.discoveredBench) {
      Player.discoveredBench = true
    }
  }
  def storySpecificInterrupt(tick: Int): Unit = {}
}
