package Base

object Importance extends Enumeration {
  type Importance = Value
  val Vibe, Base, Event, Interrupt, Critical = Value
  def interrupt(cur: Importance, other: Importance): Boolean = {
    if (cur == Critical) return false
    if (other >= Interrupt) return true
    return other > cur
  }
}

trait Story extends Subject[Story] with Listener {
  var conditions: List[() => Boolean]
  var active: Boolean
  // state contains unique state, common state is for things every story has
  // common state:
  //    0: story done already
  //    1: start time
  //    2: repeatable story
  var commonState: (Boolean, Int, Boolean)
  val importance: Importance.Importance

  def canBegin: Boolean = conditions.forall(f => f())
}
