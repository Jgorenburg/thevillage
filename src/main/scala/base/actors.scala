package Base

import Base.Importance.interrupt

case class curStory(
    var curStory: Story,
    var startTime: Int
) {
  def copy(): curStory = { new curStory(curStory, startTime) }
  override def toString(): String =
    s"Current Story: ${curStory.getClass.getSimpleName.stripSuffix("$")}  Start Time: ${startTime}"
}

trait Actor extends Subject[Actor] with Listener {
  // common state is for things every actor has
  // common state:
  //    1: current activity
  //    2: activity start time
  var commonState: curStory = (Vibe, 0)
  var interrupted: curStory = commonState.copy()

  lazy val myEvents: Array[Any]

  def getCurStory(): Story = commonState.curStory
  def getCurStoryImportance(): Importance.Importance =
    commonState.curStory.importance

  def beginStory(story: Story, tick: Int): Unit = {

    if (story.importance == Importance.Instantaneous) {
      interrupted = commonState.copy()
    }
    commonState.curStory = story
    commonState.startTime = tick
    actorSpecificBeginning(tick)
  }
  def actorSpecificBeginning(tick: Int): Unit

  def tick(tick: Int): Unit

  def endStory(tick: Int): Unit = {
    actorSpecificEnding(tick)
    if (commonState.curStory.importance == Importance.Instantaneous) {
      commonState = interrupted.copy()
    } else {
      commonState.curStory = Vibe
      commonState.startTime = tick
    }
  }
  def actorSpecificEnding(tick: Int): Unit

  def interruptStory(tick: Int): Unit = {
    actorSpecificInterrupt(tick)

    // these are probably unneeded
    commonState.curStory = Vibe
    commonState.startTime = tick
  }

  def actorSpecificInterrupt(tick: Int): Unit

  def reset(): Unit

  def log(): String

  implicit def actorcommonState_to_tuple(
      cs: curStory
  ): (Story, Int) =
    (cs.curStory, cs.startTime)

  implicit def tuple_to_actorcommonstate(
      t: (Story, Int)
  ): curStory = curStory(t._1, t._2)

}
