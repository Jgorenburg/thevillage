package Snowedin

import Base.GameManager
import Base.StoryRunner
import Base.Updater
import Base.Story
import Base.Actor
import Base.Static
import MyLogger.MyLogger
import scala.compiletime.uninitialized

import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import Snowedin.ControlRoom.{statics, objects, characters, stories}
import Snowedin.SnowedInPositionConstants.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import Base.GameManager.log
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color
import Base.Person

object ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story] =
    List(
      GoToBed,
      WakingUp,
      Laundry,
      Nap,
      NoticeBrokenDoor,
      FixDoor,
      Construction,
      Code,
      Art,
      Cleaning,
      Music,
      Chat,
      CookLunch,
      CookDinner,
      Movie,
      JoinMovie,
      Knit,
      Woodworking,
      GiveScarf,
      Snack,
      StartFire,
      Watercolor,
      Read,
      Lunch,
      Dinner,
      CleanTable,
      StartDishwasher,
      RunDishwasher,
      UnloadDishwasher,
      Gossip,
      Boardgame,
      FixSomething,
      Singalong,
      Breakfast,
      Snowcrash,
      KitchenFire
    )
  val characters: List[Person] = List(Father, Mother, Son, Daughter)
  val objects: List[Actor] =
    List(Couch, Sofachair, Table, Worktable, Easle, Stove, Dishwasher)
  val statics: List[Static] =
    List(
      House,
      Counter,
      CoffeeTable,
      LivingRoomTable,
      WorkroomWall,
      FrontDoor,
      BedroomDoor,
      WashingMachine,
      Fireplace,
      Fridge
    )

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
  ) = {
    GlobalVars.secsPerTick = secsPerTick
    endTick = gameLen
    isLogging = logging
    wakeupTimes
      .zip(characters)
      .foreach((time, person) => person.wakeTime = time)
    bedTimes
      .zip(characters)
      .foreach((time, person) => person.bedTime = gameLen - time)
    GameManager.setup(gameLen, updaters, stories, characters, objects)
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
  }

  def runGame(
      gameLen: Int = 720,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  ) = {
    GameManager.setup(gameLen, updaters, stories, characters, objects)
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
    GameManager.runGame(gameLen, logging)
  }
}

class SnowedIn extends ApplicationAdapter {
  private var shapeRenderer: ShapeRenderer = uninitialized
  private var camera: OrthographicCamera = uninitialized
  private var font: BitmapFont = uninitialized
  private var batch: SpriteBatch = uninitialized

  var tick = 0

  override def create(): Unit = {
    // Initialize the shape renderer
    shapeRenderer = new ShapeRenderer()
    batch = new SpriteBatch()
    font = new BitmapFont()

    font.getData().markupEnabled = true
    font.setColor(0, 0, 0, 1)
    font.getData.setScale(1.0f)

    println(HouseWidth)
    println(HouseHeight)

    // Set up the camera
    camera = new OrthographicCamera()
    camera.setToOrtho(false)
    ControlRoom.setup(43200, true, "full")(
      4,
      List(60, 120, 1800, 4000),
      List(40000, 30000, 30000, 2)
    )
    // CleanTable.beginStory(0)
    // Cleaning.beginStory(0)
  }

  override def render(): Unit = {
    if (tick < ControlRoom.endTick) {
      tick = GameManager.step(ControlRoom.isLogging)
    }

    // Laundry.tick(0)
    // Clear the screen
    Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update camera
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    // Begin shape rendering
    shapeRenderer.begin(ShapeType.Line)

    // temp grid for object placement
    shapeRenderer.setColor(.7f, .7f, .7f, 1)
    (BigDecimal(houseX) to BigDecimal(topRight._1) by BigDecimal(boxSize))
      .foreach(x =>
        shapeRenderer.line(x.toFloat, houseY, x.toFloat, houseY + HouseHeight)
      )
    (BigDecimal(houseY) to BigDecimal(topRight._2) by BigDecimal(boxSize))
      .foreach(y =>
        shapeRenderer.line(houseX, y.toFloat, houseX + HouseWidth, y.toFloat)
      )
    shapeRenderer.setColor(1, 0, 0, .5f)
    (BigDecimal(houseX) to BigDecimal(topRight._1) by BigDecimal(5 * boxSize))
      .foreach(x =>
        shapeRenderer.line(x.toFloat, houseY, x.toFloat, houseY + HouseHeight)
      )
    (BigDecimal(houseY) to BigDecimal(topRight._2) by BigDecimal(5 * boxSize))
      .foreach(y =>
        shapeRenderer.line(houseX, y.toFloat, houseX + HouseWidth, y.toFloat)
      )
    shapeRenderer.setColor(0, 0, 0, 1)

    statics.foreach(_.render(shapeRenderer))
    objects.foreach(_.render(shapeRenderer))
    characters.foreach(_.render(shapeRenderer))

    // End shape rendering
    shapeRenderer.end()

    batch.begin()

    font.draw(
      batch,
      s"Hour: ${(tick / 3600).toInt} Minute: ${(tick / 60).toInt % 60}\nTicks: ${tick}",
      50,
      50
    )

    characters
      .zip(
        List(
          (50f, HEIGHT - 100),
          (50f, HEIGHT - 300),
          (50f, HEIGHT - 500),
          (50f, HEIGHT - 700)
        )
      )
      .foreach((person, loc) => person.report(font, batch, loc))

    batch.end()
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
  }
}
