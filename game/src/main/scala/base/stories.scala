package Base

import scala.collection.mutable.HashSet

// common state:
//    1: story done already
//    2: start time (-1 if not active)
//    3: repeatable story
//    4: duration (-1 if not timebound)
case class StoryCommonState(
    var completed: Boolean,
    var startTime: Int,
    var repeatable: Boolean,
    var duration: Int
) {
  def copy(): StoryCommonState =
    new StoryCommonState(completed, startTime, repeatable, duration)

}
object Importance extends Enumeration {
  type Importance = Value
  val Vibe, Base, Event, Interrupt, Critical, Instantaneous, Override = Value
  def shouldInterrupt(cur: Importance, other: Importance): Boolean = {
    if (cur == Override) return false
    if (other == Override || other == Instantaneous) return true
    if (cur == Critical) return false
    if (other >= Interrupt) return true
    return other > cur
  }
}

// For stories that involve the player
trait PlayerBased {
  self: Story =>

  var canMove: Boolean
}

// for stories where progress is not lost when interrupted
trait Pausable {
  self: Story =>

  var amountleft: Int = -1
  var restartTime: Int = 1
  var isPaused: Boolean = false
  def begin() = {
    if (amountleft == -1) { amountleft = commonState.duration }
    isPaused = false
  }
  def proceed() = amountleft -= 1
  def pause() = {
    self.commonState.duration =
      math.min(amountleft + restartTime, self.commonState.duration)
    isPaused = true
  }
  def beginAnew() = {
    isPaused = false
    commonState.duration = startState.duration
    amountleft = commonState.duration
  }
}

// for stories which occupy part or all of an object
trait Occupy {
  self: Story =>

  var size: Int
}

// for repeatable stories that should have some wait before repeating
trait Delay {
  self: Story =>

  var delay: Int
  var endTime: Int = 0
  var repeatsLeft: Double = Double.PositiveInfinity

  def setEndTime(time: Int) = {
    endTime = time
    repeatsLeft -= 1
  }
  def readyToRepeat(): Boolean =
    !commonState.completed || (repeatsLeft > 0 && GameManager.tick - delay >= endTime)
}

trait Story extends Subject[Story] with Listener {
  lazy val actors: HashSet[Actor]
  lazy val universalConditions: List[() => Boolean] =
    List(() => !active && (commonState.repeatable || !commonState.completed))
  var conditions: List[() => Boolean]
  var active: Boolean = false
  // common state:
  //    1: story done already
  //    2: start time (-1 if not active)
  //    3: repeatable story
  //    4: duration (-1 if not timebound)
  val startState: StoryCommonState
  var commonState: StoryCommonState
  var importance: Importance.Importance

  def allArrived: Boolean = false

  def canBegin: Boolean =
    universalConditions.forall(f => f()) && conditions.forall(f => f())

  def beginStory(tick: Int): Unit = {
    active = true
    commonState.startTime = tick
    actors.foreach(_.beginStory(this, tick))
    arrived = false
    started = false
    storySpecificBeginning(tick)
    setStartLocations()
  }
  def storySpecificBeginning(tick: Int): Unit
  def setStartLocations(): Unit
  var arrived = false
  var started = false

  def tick(tick: Int): Boolean = {
    var shouldEnd = progress(tick: Int)
    actors.foreach(_.tick(tick))
    if (arrived && !started) {
      started = true
      commonState.startTime = tick
      actors.foreach(_.commonState.startTime = tick)
    }
    if (
      started && (shouldEnd ||
        commonState.duration != -1 && tick >= commonState.startTime + commonState.duration)
    ) {
      return true
    }
    return false
  }
  def progress(tick: Int): Boolean

  def endStory(tick: Int): Unit = {
    active = false
    commonState.completed = true
    storySpecificEnding(tick)
    actors.foreach(_.endStory(tick))
  }
  def storySpecificEnding(tick: Int): Unit

  def interruptStory(tick: Int): Unit = {
    if (!active) return
    active = false
    storySpecificInterrupt(tick)
    actors.foreach(_.interruptStory(tick))
  }
  def storySpecificInterrupt(tick: Int): Unit

  // TODO: refactor
  def reset(): Unit

  implicit def storycommonState_to_tuple(
      cs: StoryCommonState
  ): (Boolean, Int, Boolean, Int) =
    (cs.completed, cs.startTime, cs.repeatable, cs.duration)

  implicit def tuple_to_storycommonstate(
      t: (Boolean, Int, Boolean, Int)
  ): StoryCommonState = StoryCommonState(t._1, t._2, t._3, t._4)

}

object Vibe extends Story {
  lazy val actors = HashSet()
  var conditions: List[() => Boolean] = List()
  val startState = (false, 0, true, -1)
  var commonState = startState.copy()
  active = true
  val importance = Importance.Vibe
  override def canBegin: Boolean = true
  def progress(tick: Int): Boolean = false

  def storySpecificBeginning(tick: Int): Unit = {}
  def setStartLocations(): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = true
    commonState = startState.copy()
  }
}
