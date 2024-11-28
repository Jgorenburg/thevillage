package Snowedin

import Base.Importance
import scala.collection.mutable.HashSet
import Base.Story
import Snowedin.Tools.Tambourine
import Base.Pausable
import Base.Occupy
import Base.Delay
import Snowedin.Location.Room
import scala.collection.mutable.Queue
import Snowedin.Location.DiningRoom
import Snowedin.Location.LivingRoom
import Snowedin.Location.Kitchen
import Snowedin.Location.Door
import Snowedin.SnowedInPositionConstants.*
import scala.collection.mutable.HashMap

object Code extends Story with Occupy with Delay {

  var size = 1
  val delay = 900
  lazy val actors = HashSet(Mother, Table)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () =>
        Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, false, 13200)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Base
  def progress(tick: Int): Boolean = {
    if (!arrived) {
      arrived = Mother.walk()
    }
    return false
  }
  def setStartLocations(): Unit = {
    Mother.setDestination(Table.getLoc3())
  }

  def storySpecificBeginning(tick: Int): Unit = {
    Mother.room = DiningRoom
  }
  def storySpecificEnding(tick: Int): Unit = { setEndTime(tick) }

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object Music extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => Mother.tools.contains(Tambourine),
      () =>
        Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, 2700)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Event
  def progress(tick: Int): Boolean = return false
  def setStartLocations(): Unit = {}
  def storySpecificBeginning(tick: Int): Unit = {
    arrived = true
  }
  def storySpecificEnding(tick: Int): Unit = {
    Mother.tools.remove(Tambourine)
  }

  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset() = {
    active = false
    commonState = startState.copy()
  }
}

object Art extends Story with Occupy with Delay {
  val size = 1
  var delay = 7200
  lazy val actors = HashSet(Mother, Easle)
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () => Easle.curCapacity >= size,
      () =>
        Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, -1)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Base
  def setStartLocations(): Unit = Mother.setDestination(Easle.location)

  def storySpecificBeginning(tick: Int): Unit = { Mother.room = LivingRoom }
  def progress(tick: Int): Boolean = {
    if (tick - commonState.startTime > 5) {
      importance = Importance.Vibe
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    importance = Importance.Base
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    importance = Importance.Base
    setEndTime(tick)
  }

  def reset() = {
    active = false
    commonState = startState.copy()
    importance = Importance.Base
  }
}

object Cleaning extends Story with Pausable with Delay {
  lazy val actors = HashSet(Mother)
  repeatsLeft = 6
  var conditions: List[() => Boolean] =
    List(
      () => readyToRepeat(),
      () =>
        Importance.shouldInterrupt(Mother.getCurStoryImportance(), importance)
    )
  var active: Boolean = false
  val startState = (false, -1, true, 1800)
  val delay = 3600
  var commonState = startState.copy()

  var importance: Importance.Importance = Importance.Event
  var rooms: Array[Room] = Array(DiningRoom, LivingRoom, Kitchen, Door)
  var locs: HashMap[Room, (Float, Float)] = HashMap(
    DiningRoom -> (bottomLeft._1 + 9 * boxSize, bottomLeft._2 + 12 * boxSize),
    LivingRoom -> (topLeft._1 + 3 * boxSize, topLeft._2 - 10 * boxSize),
    Kitchen -> (bottomRight._1 - 4 * boxSize, bottomRight._2 + 6 * boxSize),
    Door -> (topRight._1 - 8 * boxSize, topRight._2 - 1 * boxSize)
  )
  var cleansSoFar = 0

  def storySpecificBeginning(tick: Int): Unit = {
    begin()
    Mother.room = rooms(cleansSoFar % rooms.length)
  }
  def setStartLocations(): Unit = Mother.setDestination(locs(Mother.room))

  override def progress(tick: Int): Boolean = {
    proceed()
    if (!arrived) {
      arrived = Mother.walk()
    }
    return false
  }
  def storySpecificEnding(tick: Int): Unit = {
    if (Mother.room == LivingRoom) {
      Mother.tools.add(Tambourine)
    }
    setEndTime(tick)
    beginAnew()
    cleansSoFar += 1
  }

  def storySpecificInterrupt(tick: Int): Unit = {
    if (amountleft < commonState.duration / 3) {
      cleansSoFar += 1
      beginAnew()
    } else {
      restartTime = 300
      pause()
    }
  }

  def reset() = {
    active = false
    commonState = startState.copy()
    beginAnew()
    endTime = 0
    cleansSoFar = 0
  }
}

object NoticeBrokenDoor extends Story {
  lazy val actors = HashSet(Mother)
  var conditions: List[() => Boolean] =
    List(
      () => !Mother.noticedBrokenDoor,
      () => Mother.room == Location.Door
    )

  var active: Boolean = false
  val startState = (false, -1, true, 0)
  var commonState = startState.copy()
  var importance: Importance.Importance = Importance.Instantaneous
  def setStartLocations(): Unit = {}
  def progress(tick: Int): Boolean = return false

  // Instantaneous stories immedietely end
  def storySpecificBeginning(tick: Int): Unit = endStory(tick)
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = false
    commonState = startState.copy()
  }
}
