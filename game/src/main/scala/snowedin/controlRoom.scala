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
import Snowedin.ControlRoom.statics

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
  val statics: List[Static] = List(House, Counter, LivingRoomTable)

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

    // Set color (RGBA)
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    // shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight)
    shapeRenderer.setColor(0, 1, 0, 1)

    // bob += 1
    // println(bob)
    // shapeRenderer.circle(200f, 200f, bob, 500)

    statics.foreach(_.render(shapeRenderer))

    // End shape rendering
    shapeRenderer.end()
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
  }
}
