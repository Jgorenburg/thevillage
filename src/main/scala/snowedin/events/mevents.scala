package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story

object Cleaning extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Mother.getCurStoryImportance(), importance))
  var active: Boolean = false
  var commonState = (false, -1, false, 7)
  val importance: Importance.Importance = Importance.Event

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = (false, -1, false, 7)
  }
}
