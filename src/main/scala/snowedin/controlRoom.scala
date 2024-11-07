package Snowedin

import Base.GameManager
import Base.StoryRunner
import Base.Updater
import Base.Story
import Base.Actor
import MyLogger.MyLogger

object ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story] =
    List(Laundry, Nap, NoticeBrokenDoor)
    // List(Laundry, Nap, NoticeBrokenDoor, FixDoor, Construction)
  val characters: List[Actor] = List(Father)
  val objects: List[Actor] =
    List(Couch)
    // List(Couch, Worktable)

  def runGame(
      gameLen: Int = 30,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  ) = {
    GameManager.setup(updaters, stories, characters, objects)
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
    GameManager.runGame(gameLen, logging)
  }
}
