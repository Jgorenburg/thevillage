package Base

case class ActorCommonState(
    var _1: Story,
    var _2: Int
) {}

trait Actor extends Subject[Actor] with Listener {
  // common state is for things every actor has
  // common state:
  //    0: current activity
  //    1: activity start time
  var commonState: ActorCommonState

  var myEvents: Array[Any]

  def getCurStory(): Story = commonState._1
  def getCurStoryImportance(): Importance.Importance = commonState._1.importance

  implicit def actorcommonState_to_tuple(
      cs: ActorCommonState
  ): (Story, Int) =
    (cs._1, cs._2)

  implicit def tuple_to_actorcommonstate(
      t: (Story, Int)
  ): ActorCommonState = ActorCommonState(t._1, t._2)

}
