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
import Snowedin.PositionConstants.HouseWidth
import Snowedin.PositionConstants.HouseHeight
import Snowedin.PositionConstants.houseX
import Snowedin.PositionConstants.boxSize
import Snowedin.PositionConstants.topRight
import Snowedin.PositionConstants.houseY
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import Snowedin.PositionConstants.HEIGHT
import Base.GameManager.log
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color

object ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story] =
    List(
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
  val characters: List[Actor] = List(Father, Mother, Son, Daughter)
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
      Closet,
      WashingMachine,
      Fireplace,
      Fridge
    )

  var endTick = 1000
  var isLogging = false

  def setup(
      gameLen: Int = 1000,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  ) = {
    endTick = gameLen
    isLogging = logging
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
    GameManager.setup(gameLen, updaters, stories, characters, objects)
  }

  def runGame(
      gameLen: Int = 120,
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

    font.setColor(0, 0, 0, 1)
    font.getData.setScale(1.0f)

    println(HouseWidth)
    println(HouseHeight)

    // Set up the camera
    camera = new OrthographicCamera()
    camera.setToOrtho(false)
    ControlRoom.setup(300, true, "movement")
    // Laundry.beginStory(0)
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

    font.draw(batch, s"Tick: ${tick}", 50, 50)
    font.draw(
      batch,
      s"Father:\n\tCurrent Story: ${Father.commonState.curStory.getClass.getSimpleName
          .stripSuffix("$")}\n\tLocation: ${Father.location}\n\tDestination: ${Father.destination}\n\tColor: Red",
      50,
      HEIGHT - 100
    )
    font.draw(
      batch,
      s"Mother:\n\tCurrent Story: ${Mother.commonState.curStory.getClass.getSimpleName
          .stripSuffix("$")}\n\tLocation: ${Mother.location}\n\tDestination: ${Mother.destination}:\n\tColor: Blue",
      50,
      HEIGHT - 300
    )
    font.draw(
      batch,
      s"Son:\n\tCurrent Story: ${Son.commonState.curStory.getClass.getSimpleName
          .stripSuffix("$")}\n\tLocation: ${Son.location}\n\tDestination: ${Son.destination}\n\tColor: Purple",
      50,
      HEIGHT - 500
    )

    font.draw(
      batch,
      s"Daughter:\n\tCurrent Story: ${Daughter.commonState.curStory.getClass.getSimpleName
          .stripSuffix("$")}\n\tLocation: ${Daughter.location}\n\tDestination: ${Daughter.destination}\n\tColor: Green",
      50,
      HEIGHT - 700
    )

    batch.end()
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
  }
}
