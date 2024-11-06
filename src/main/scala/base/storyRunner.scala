package Base

import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet

object StoryRunner extends Updater {
  val stories = HashSet[Story]()

  def addStory(story: Story): Unit = {
    if (stories.contains(story))
      return

    if (story.importance != Importance.Instantaneous) {
      stories.add(story)

      // interrupt every story an actor for this story is currently engaged in
      story.actors
        .filter(_.commonState.curStory != Vibe)
        .foreach(actor => interruptStory(actor.commonState.curStory))
    }

    story.beginStory(GameManager.tick)
  }

  def runStories(): Unit = {
    stories.filter(_.tick(GameManager.tick)).foreach(stories.remove(_))
  }

  def interruptStory(story: Story): Unit = {
    if (!stories.contains(story)) return

    story.interruptStory(GameManager.tick)
    stories.remove(story)
  }

  def tick() = {
    StoryAssigner.assign()
    runStories()
  }

  def reset() = {
    StoryAssigner.reset()
    stories.clear()
  }
}

object WaitingList {
  val queue = Array.fill(Importance.maxId)(new Queue[Story]())
  var mostImportant = 0

  def reset(): Unit = {
    val _ = queue.map(_.dequeueAll(_ => true))
    mostImportant = 0
  }

  def addStory(story: Story): Unit = {
    queue(story.importance.id) += story
    mostImportant = mostImportant.max(story.importance.id)
  }

  def getNextStory(): Story = {
    if (mostImportant == 0) {
      return Vibe
    }
    val story = queue(mostImportant).dequeue()
    while (mostImportant > 0 && queue(mostImportant).isEmpty) {
      mostImportant -= 1
    }
    return story
  }
}

object StoryAssigner {
  // all stories must be in this list!
  def reset() = { WaitingList.reset() }

  def buildWaitingList(): Unit = {
    WaitingList.reset()
    GameManager.stories.filter(_.canBegin).foreach(WaitingList.addStory(_))
  }

  def beginNextStory(): Boolean = {
    var story = WaitingList.getNextStory()
    while (!story.canBegin) {
      story = WaitingList.getNextStory()
    }

    if (story == Vibe) {
      return false
    }

    StoryRunner.addStory(story)
    return true
  }

  def assign(): Unit = {
    buildWaitingList()
    while (beginNextStory()) {}
  }
}
