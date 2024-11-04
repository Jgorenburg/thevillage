package Base

case class StoryCommonState(
    var _1: Boolean,
    var _2: Int,
    var _3: Boolean,
    var _4: Int
) {}
implicit def storycommonState_to_tuple(
    cs: StoryCommonState
): (Boolean, Int, Boolean, Int) =
  (cs._1, cs._2, cs._3, cs._4)

implicit def tuple_to_storycommonstate(
    t: (Boolean, Int, Boolean, Int)
): StoryCommonState = StoryCommonState(t._1, t._2, t._3, t._4)

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

trait Story extends Subject[Story] with Listener {
  var universalConditions: List[() => Boolean] =
    List(() => !active && (commonState._3 || !commonState._1))
  var conditions: List[() => Boolean]
  var active: Boolean
  // state contains unique state, common state is for things every story has
  // common state:
  //    0: story done already
  //    1: start time (-1 if not active)
  //    2: repeatable story
  //    3: duration (-1 if not timebound)
  var commonState: StoryCommonState
  val importance: Importance.Importance

  def canBegin: Boolean =
    universalConditions.forall(f => f()) && conditions.forall(f => f())
}
