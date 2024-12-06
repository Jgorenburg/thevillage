package Base

import MyLogger.MyLogger
import Snowedin.GlobalVars
import scala.compiletime.uninitialized
import Snowedin.SnowedInPositionConstants.HorizBoxes

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

  var pathfinder: AStar = uninitialized

  var tick: Int = 0
  var ending: Int = 0
  // var pathfinder: Pathfinder = uninitialized

  def setup(
      endTick: Int,
      ups: List[Updater],
      stors: List[Story],
      chars: List[Actor],
      objs: List[Actor],
      constants: PositionConstants
  ) = {

    ending = endTick
    updaters = ups
    stories = stors
    characters = chars
    objects = objs
    pathfinder = AStar(constants.HorizBoxes, constants.VertBoxes, stage)
  }

  def runGame(endTick: Int, logging: Boolean = false): Unit = {
    ending = endTick
    while (tick < ending) {
      step(logging)
    }
  }

  def step(logging: Boolean = false): Int = {
    tick += GlobalVars.secsPerTick
    updaters.foreach(_.tick())
    if (logging) { log() }
    return tick
  }

  def reset(): Unit = {
    tick = 0
    updaters.foreach(_.reset())
    stories.foreach(_.reset())
    characters.foreach(_.reset())
    objects.foreach(_.reset())
  }

  def log(): Unit = {
    MyLogger.addToLine(tick.toString)
    MyLogger.addActiveStories(StoryRunner.stories)
    (characters ::: objects).foreach(c => MyLogger.addToLine(c.log()))
    MyLogger.printLine()
  }
}
