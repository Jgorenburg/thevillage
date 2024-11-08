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
  val Vibe, Base, Event, Interrupt, Critical, Instantaneous = Value
  def interrupt(cur: Importance, other: Importance): Boolean = {
    if (other == Instantaneous) return true
    if (cur == Critical) return false
    if (other >= Interrupt) return true
    return other > cur
  }
}

// for stories where progress is not lost when interrupted
trait Pausable {
  self: Story =>

  var amountleft: Int = -1
  def proceed() = amountleft -= 1
  def pause() = self.commonState.duration = amountleft
  def beginAnew() = amountleft = commonState.duration
}

// for stories which occupy part or all of an object
trait Occupy {
  self: Story =>

  val size: Int
}

trait Story extends Subject[Story] with Listener {
  lazy val actors: HashSet[Actor]
  lazy val universalConditions: List[() => Boolean] =
    List(() => !active && (commonState.repeatable || !commonState.completed))
  var conditions: List[() => Boolean]
  var active: Boolean
  // common state:
  //    1: story done already
  //    2: start time (-1 if not active)
  //    3: repeatable story
  //    4: duration (-1 if not timebound)
  val startState: StoryCommonState
  var commonState: StoryCommonState
  var importance: Importance.Importance

  def canBegin: Boolean =
    universalConditions.forall(f => f()) && conditions.forall(f => f())

  def beginStory(tick: Int): Unit = {
    active = true
    commonState.startTime = tick
    actors.foreach(_.beginStory(this, tick))
    storySpecificBeginning(tick)
  }
  def storySpecificBeginning(tick: Int): Unit

  def tick(tick: Int): Boolean = {
    progress(tick: Int)
    actors.foreach(_.tick(tick))

    if (
      commonState.duration != -1 && tick >= commonState.startTime + commonState.duration
    ) {
      endStory(tick)
      return true
    }
    return false
  }
  def progress(tick: Int): Unit

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
  var active: Boolean = true
  val importance = Importance.Vibe
  override def canBegin: Boolean = true

  def storySpecificBeginning(tick: Int): Unit = {}
  def progress(tick: Int): Unit = {}
  def storySpecificEnding(tick: Int): Unit = {}
  def storySpecificInterrupt(tick: Int): Unit = {}

  def reset(): Unit = {
    active = true
    commonState = startState
  }
}
