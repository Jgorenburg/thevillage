package Snowedin

import Base.Story
import scala.collection.mutable.HashSet
import Base.Importance
import Base.GameManager
import Snowedin.Location.Door
import Base.StoryCommonState
import Base.Importance.Critical
import Snowedin.Location.Kitchen
import Base.Person
import Snowedin.SnowedInPositionConstants.*

object KitchenFire extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(Father, Mother, Son, Daughter, Stove)
  val people: HashSet[Person] = HashSet(Father, Mother, Son, Daughter)
  var conditions: List[() => Boolean] = List(
    () => Stove.unattended,
    () => GameManager.tick - 4 >= Stove.leftAlone
  )
  var importance: Base.Importance.Importance = Critical
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  val startState: Base.StoryCommonState = (false, -1, false, 600)
  var commonState: StoryCommonState = startState.copy()
  def setStartLocations(): Unit = {
    people
      .foreach(
        _.setDestination(Stove.interactLoc)
      )
  }
  def progress(tick: Int): Boolean = {
    return people.exists(_.walk())
  }
  def storySpecificBeginning(tick: Int): Unit = {
    actors.foreach(_.room = Kitchen)
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object Snowcrash extends Story {
  var active: Boolean = false
  val numParticipants = 2
  lazy val actors = HashSet()
  var conditions: List[() => Boolean] = List(
    () => GlobalVars.brokenDoor,
    // a little after dinner cooking can begin
    () => GameManager.tick > GameManager.ending * 7 / 8,
    () => getActors()
  )

  def getActors(): Boolean = {
    val potential = GameManager.characters.filter(person =>
      Importance.shouldInterrupt(person.getCurStoryImportance(), importance)
    )
    if (potential.length < numParticipants) {
      return false
    }
    actors ++= Location.closest(Door, numParticipants, potential)
    return true
  }

  def setStartLocations(): Unit = {
    actors
      .asInstanceOf[HashSet[Person]]
      .foreach(_.setDestination(FrontDoor.interactLoc))
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = true
      actors
        .asInstanceOf[HashSet[Person]]
        .foreach(p => arrived = p.walk() && arrived)
    }
    return false
  }
  var importance: Base.Importance.Importance = Importance.Critical
  def reset(): Unit = {
    active = false
    commonState = startState.copy()
  }
  val startState: Base.StoryCommonState = (false, -1, false, 300)

  var commonState: Base.StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    actors.foreach(_.room = Door)
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}
