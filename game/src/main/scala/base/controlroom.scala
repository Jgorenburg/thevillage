package Base

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import scala.compiletime.uninitialized

trait ControlRoom {
  val updaters: List[Updater] = List(StoryRunner)
  val stories: List[Story]
  val playerInitiatedStories: List[Story]
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
}

trait GameTemplate extends ApplicationAdapter {
  var shapeRenderer: ShapeRenderer = uninitialized
  var camera: OrthographicCamera = uninitialized
  var font: BitmapFont = uninitialized
  var batch: SpriteBatch = uninitialized

  var tick = 0

  override def create(): Unit = {
    // Initialize the shape renderer
    shapeRenderer = new ShapeRenderer()
    CurveRenderer.setRenderer(shapeRenderer)
    batch = new SpriteBatch()
    font = new BitmapFont()

    font.getData().markupEnabled = true
    font.setColor(0, 0, 0, 1)
    font.getData.setScale(1.0f)

    // Set up the camera
    camera = new OrthographicCamera()
    camera.setToOrtho(false)
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
  }
}
