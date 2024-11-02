package Base

trait Actor extends Subject[Actor] with Listener {
  // common state is for things every actor has
  // common state:
  //    0: current activity
  //    1: activity start time
  var commonState: (Story, Int)

  var myEvents: Array[Any]

  def getCurStory(): Story = commonState._1
  def getCurStoryImportance(): Importance.Importance = commonState._1.importance
}
