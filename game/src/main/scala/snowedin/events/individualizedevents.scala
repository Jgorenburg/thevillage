package Snowedin

import Base.Story
import scala.collection.mutable.HashSet
import Base.GameManager
import Base.Importance
import Base.StoryCommonState
import Base.StoryRunner
import Base.Person
import Base.Occupy
import Snowedin.Location.DiningRoom
import Snowedin.PositionConstants.bottomLeft
import Snowedin.PositionConstants.boxSize
import Snowedin.Location.Bedroom
import Snowedin.GlobalVars.bedLoc

object Breakfast extends Story {
  var active: Boolean = false
  lazy val actors = HashSet()
  val eaters = HashSet(Father, Mother, Daughter, Son)
  var conditions: List[() => Boolean] =
    List(
      () => GameManager.tick <= GameManager.ending / 6,
      () => Table.curCapacity > 0,
      () =>
        Importance.shouldInterrupt(Table.getCurStoryImportance(), importance),
      () =>
        eaters.exists(person =>
          Importance.shouldInterrupt(person.getCurStoryImportance(), importance)
        )
    )
  var importance: Base.Importance.Importance = Importance.Event
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def progress(tick: Int): Boolean = return true
  def setStartLocations(): Unit = {}
  val startState: StoryCommonState = (false, -1, true, 0)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    arrived = true
    val willEat = eaters.filter(person =>
      Importance.shouldInterrupt(person.getCurStoryImportance(), importance)
    )
    willEat.foreach(eater => StoryRunner.addStory(new IndivBreakfast(eater)))
    eaters --= willEat
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

class IndivBreakfast(eater: Person) extends Story with Occupy {
  val size = 1
  var active: Boolean = false
  lazy val actors = HashSet(eater, Table)
  var conditions: List[() => Boolean] =
    List(() => false)
  var importance: Base.Importance.Importance = Breakfast.importance
  def reset(): Unit = {}
  val startState: Base.StoryCommonState = (false, -1, false, 1200)
  var commonState: StoryCommonState = startState.copy()
  def setStartLocations(): Unit = {
    eater.setDestination(
      eater match
        case Son      => Table.getLoc1()
        case Daughter => Table.getLoc2()
        case Mother   => Table.getLoc3()
        case Father   => Table.getLoc4()
    )
  }

  def progress(tick: Int): Boolean = {
    if (!arrived) arrived = eater.walk()
    return false
  }
  def storySpecificBeginning(tick: Int): Unit = { eater.room = DiningRoom }
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

object WakingUp extends Story {
  var active: Boolean = false
  lazy val actors = HashSet()
  val sleepers = HashSet(Father, Mother, Daughter, Son)
  var conditions: List[() => Boolean] =
    List(() => true)
  var importance: Base.Importance.Importance = Importance.Override
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
  }
  def progress(tick: Int): Boolean = return true
  def setStartLocations(): Unit = {}
  val startState: StoryCommonState = (false, -1, false, 0)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    arrived = true
    sleepers.foreach(person => StoryRunner.addStory(new IndivSleep(person)))
    sleepers.foreach(_.room = Bedroom)
    sleepers.foreach(_.location = bedLoc)
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

object GoToBed extends Story {
  var active: Boolean = false
  lazy val actors = HashSet()
  var potential = HashSet(Father, Mother, Daughter, Son)
  var conditions: List[() => Boolean] =
    List(() => potential.exists(_.timeForBed()))
  var importance: Base.Importance.Importance = Importance.Override
  def reset(): Unit = {
    commonState = startState.copy()
    active = false
    potential = HashSet(Father, Mother, Daughter, Son)
  }
  def progress(tick: Int): Boolean = return true
  def setStartLocations(): Unit = {}
  val startState: StoryCommonState = (false, -1, true, 0)
  var commonState: StoryCommonState = startState.copy()
  def storySpecificBeginning(tick: Int): Unit = {
    arrived = true
    val sleepers = potential.filter(_.timeForBed())
    sleepers.foreach(person => StoryRunner.addStory(new IndivSleep(person)))
    potential --= sleepers
  }
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}
}

class IndivSleep(sleeper: Person) extends Story {
  var active: Boolean = false
  lazy val actors = HashSet(sleeper)
  var conditions: List[() => Boolean] =
    List(() => false)
  var importance: Base.Importance.Importance = Importance.Override
  def reset(): Unit = {}
  val startState: Base.StoryCommonState = (false, -1, false, -1)
  var commonState: StoryCommonState = startState.copy()
  def setStartLocations(): Unit = { sleeper.setDestination(bedLoc) }

  def progress(tick: Int): Boolean = {
    if (!arrived) arrived = sleeper.walk()
    else if (sleeper.room != Bedroom) sleeper.room = Bedroom
    return tick > sleeper.wakeTime
  }
  def storySpecificBeginning(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {
    sleeper.location = bedLoc
    sleeper.wakeTime = Double.PositiveInfinity
  }
  def storySpecificInterrupt(tick: Int): Unit = {}
}
