package Base

trait ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story]
  val characters: List[Person]
  val objects: List[Actor]
  val statics: List[Static]

  var endTick = 0
  var isLogging = false

  def setup(
      gameLen: Int = 720,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  )(
      secsPerTick: Int = 2,
      wakeupTimes: List[Int] = List.fill(characters.length)(0),
      bedTimes: List[Int] = List.fill(characters.length)(0)
  ): Unit

  def runGame(
      gameLen: Int = 720,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  ): Unit

}
