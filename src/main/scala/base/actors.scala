package Base

case class ActorCommonState(
    var _1: Story,
    var _2: Int
) {}

trait Actor extends Subject[Actor] with Listener {
  // common state is for things every actor has
  // common state:
  //    1: current activity
  //    2: activity start time
  var commonState: ActorCommonState

  lazy val myEvents: Array[Any]

  def getCurStory(): Story = commonState._1
  def getCurStoryImportance(): Importance.Importance = commonState._1.importance

  def beginStory(story: Story, tick: Int): Unit = {
    commonState._1 = story
    commonState._2 = tick
    actorSpecificBeginning(tick)
  }
  def actorSpecificBeginning(tick: Int): Unit

  def tick(tick: Int): Unit

  def endStory(tick: Int): Unit = {
    actorSpecificEnding(tick)
    commonState._1 = Vibe
    commonState._2 = tick
  }
  def actorSpecificEnding(tick: Int): Unit

  def interruptStory(tick: Int): Unit = {
    actorSpecificInterrupt(tick)

    // these are probably unneeded
    commonState._1 = Vibe
    commonState._2 = tick
  }

  def actorSpecificInterrupt(tick: Int): Unit

  def reset(): Unit

  implicit def actorcommonState_to_tuple(
      cs: ActorCommonState
  ): (Story, Int) =
    (cs._1, cs._2)

  implicit def tuple_to_actorcommonstate(
      t: (Story, Int)
  ): ActorCommonState = ActorCommonState(t._1, t._2)

}
