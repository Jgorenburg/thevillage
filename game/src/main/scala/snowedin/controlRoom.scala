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

object ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story] =
    List(
      Laundry,
      Nap,
      NoticeBrokenDoor,
      FixDoor,
      Construction,
      Placeholder,
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
      Door,
      Closet,
      WashingMachine,
      Fireplace,
      Fridge
    )

  def runGame(
      gameLen: Int = 120,
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

class SnowedIn extends ApplicationAdapter {
  private var shapeRenderer: ShapeRenderer = uninitialized
  private var camera: OrthographicCamera = uninitialized

  override def create(): Unit = {
    // Initialize the shape renderer
    shapeRenderer = new ShapeRenderer()

    println(HouseWidth)
    println(HouseHeight)

    // Set up the camera
    camera = new OrthographicCamera()
    camera.setToOrtho(false)
  }

  override def render(): Unit = {
    // Clear the screen
    Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update camera
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    // Begin shape rendering
    shapeRenderer.begin(ShapeType.Line)

    // temp grid for object placement
    // shapeRenderer.setColor(.7f, .7f, .7f, 1)
    // (BigDecimal(houseX) to BigDecimal(topRight._1) by BigDecimal(boxSize))
    //   .foreach(x =>
    //     shapeRenderer.line(x.toFloat, houseY, x.toFloat, houseY + HouseHeight)
    //   )
    // (BigDecimal(houseY) to BigDecimal(topRight._2) by BigDecimal(boxSize))
    //   .foreach(y =>
    //     shapeRenderer.line(houseX, y.toFloat, houseX + HouseWidth, y.toFloat)
    //   )

    statics.foreach(_.render(shapeRenderer))
    objects.foreach(_.render(shapeRenderer))

    // End shape rendering
    shapeRenderer.end()
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
  }
}
