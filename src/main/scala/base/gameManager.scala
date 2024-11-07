package Base

import MyLogger.MyLogger

trait Updater {
  def tick(): Unit
  def reset(): Unit
}

object GameManager {
  // all objects that care about these things should reference these lists
  var updaters: List[Updater] = List()
  var stories: List[Story] = List()
  var characters: List[Actor] = List()
  var objects: List[Actor] = List()

  var tick: Int = 0

  def setup(
      ups: List[Updater],
      stors: List[Story],
      chars: List[Actor],
      objs: List[Actor]
  ) = {
    updaters = ups
    stories = stors
    characters = chars
    objects = objs
  }

  def runGame(endTick: Int, logging: Boolean = false): Unit = {
    while (tick < endTick) {
      step()
      if (logging) { log() }
    }
  }

  def step(): Unit = {
    tick += 1
    updaters.foreach(_.tick())
  }

  def reset(): Unit = {
    tick = 0
    updaters.foreach(_.reset())
    stories.foreach(_.reset())
    characters.foreach(_.reset())
    objects.foreach(_.reset())
  }

  def log(): Unit = {
    MyLogger.addActiveStories(StoryRunner.stories)
    (characters ::: objects).foreach(c => MyLogger.addToLine(c.log()))
    MyLogger.printLine()
  }
}
