package Snowedin

import Base.GameManager
import Base.StoryRunner
import Base.Updater
import Base.Story
import Base.Actor
import Base.Static
import MyLogger.MyLogger
import scala.compiletime.uninitialized

import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Pixmap, PixmapIO}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import Base.{Assets, DebugDraw, Draw, DrawOrder, Renderable, ScreenFit}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import Snowedin.SnowedInControls.{statics, objects, characters, stories}
import Snowedin.SnowedInPositionConstants.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import Base.GameManager.log
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color
import Base.Person
import Base.GameMap
import Base.BoxCoords
import Base.AStar
import Base.Position
import Base.Direction.*
import Base.ControlRoom
import Base.Globals

object SnowedInControls extends ControlRoom {
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

  def setup(
      gameLen: Int = 720,
      logging: Boolean = false,
      loggerFile: String = "unnamed"
  )(
      secsPerTick: Int = 2,
      wakeupTimes: List[Int] = List.fill(characters.length)(0),
      bedTimes: List[Int] = List.fill(characters.length)(0)
  ) = {

    Globals.secsPerTick = secsPerTick
    Globals.bedloc = bottomLeft + (0, 3)
    endTick = gameLen
    isLogging = logging
    wakeupTimes
      .zip(characters)
      .foreach((time, person) => person.wakeTime = time)
    bedTimes
      .zip(characters)
      .foreach((time, person) => person.bedTime = gameLen - time)
    GameManager.setup(
      gameLen,
      updaters,
      stories,
      characters,
      objects,
      SnowedInPositionConstants
    )
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
    GameManager.setup(
      gameLen,
      updaters,
      stories,
      characters,
      objects,
      SnowedInPositionConstants
    )
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
    GameManager.runGame(gameLen, logging)
  }
}

class SnowedIn extends ApplicationAdapter {
  private var shapeRenderer: ShapeRenderer = uninitialized
  private var font: BitmapFont = uninitialized
  // Draws the world into the framebuffer, in virtual pixels
  private var worldBatch: SpriteBatch = uninitialized
  // Draws the scaled framebuffer and the UI, in screen pixels
  private var uiBatch: SpriteBatch = uninitialized
  private var worldCamera: OrthographicCamera = uninitialized
  private var screenCamera: OrthographicCamera = uninitialized
  private var worldBuffer: FrameBuffer = uninitialized
  private var worldRegion: TextureRegion = uninitialized
  private var fit: ScreenFit = ScreenFit(1, 0, 0, VirtualWidth, VirtualHeight)
  private val debugTransform = new Matrix4()

  // Width kept free on the left for the character report panels
  private val UiPanelWidth = 340

  // F1 toggles the debug outlines and grid on top of the sprites
  var showDebug = false

  // Dev aid: SNOWEDIN_SCREENSHOT=<file.png> saves the screen after
  // SNOWEDIN_SCREENSHOT_FRAME frames (default 600) and exits.
  private val screenshotPath = Option(System.getenv("SNOWEDIN_SCREENSHOT"))
  private val screenshotFrame =
    Option(System.getenv("SNOWEDIN_SCREENSHOT_FRAME"))
      .flatMap(_.toIntOption)
      .getOrElse(600)
  private var frame = 0

  var tick = 0

  override def create(): Unit = {
    shapeRenderer = new ShapeRenderer()
    worldBatch = new SpriteBatch()
    uiBatch = new SpriteBatch()
    font = new BitmapFont()

    font.getData().markupEnabled = true
    font.setColor(0, 0, 0, 1)
    font.getData.setScale(1.0f)

    Assets.load()
    showDebug = System.getenv("SNOWEDIN_DEBUG") != null

    // The world is a fixed 272x352 image; it is scaled up by whole numbers
    worldBuffer = new FrameBuffer(
      Pixmap.Format.RGBA8888,
      VirtualWidth,
      VirtualHeight,
      false
    )
    val worldTexture = worldBuffer.getColorBufferTexture
    worldTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
    worldRegion = new TextureRegion(worldTexture)
    // Framebuffer textures are upside down
    worldRegion.flip(false, true)

    worldCamera = new OrthographicCamera()
    worldCamera.setToOrtho(false, VirtualWidth.toFloat, VirtualHeight.toFloat)
    screenCamera = new OrthographicCamera()
    resize(Gdx.graphics.getWidth, Gdx.graphics.getHeight)

    BoxCoords.setup(HouseBase, boxSize, HorizBoxes, VertBoxes)

    SnowedInControls.setup(43200, true, "full")(
      2,
      List(60, 120, 1800, 4000),
      List(40000, 30000, 30000, 2)
    )
  }

  override def resize(width: Int, height: Int): Unit = {
    if (width <= 0 || height <= 0) return // minimised
    screenCamera.setToOrtho(false, width.toFloat, height.toFloat)
    fit = ScreenFit.compute(
      width,
      height,
      VirtualWidth,
      VirtualHeight,
      UiPanelWidth
    )
  }

  override def render(): Unit = {
    if (tick < SnowedInControls.endTick) {
      tick = GameManager.step(SnowedInControls.isLogging)
    }

    val delta = Gdx.graphics.getDeltaTime
    Draw.elapsed += delta
    characters.foreach(_.updateSprite(delta))
    if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) showDebug = !showDebug

    renderWorld()

    // Clear the screen
    Gdx.gl.glClearColor(1f, 1f, 1f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    screenCamera.update()
    uiBatch.setProjectionMatrix(screenCamera.combined)
    uiBatch.begin()
    uiBatch.draw(
      worldRegion,
      fit.x.toFloat,
      fit.y.toFloat,
      fit.width.toFloat,
      fit.height.toFloat
    )
    uiBatch.end()

    if (showDebug) renderDebugOverlay()

    renderUi()

    frame += 1
    screenshotPath.foreach(path =>
      if (frame == screenshotFrame) {
        saveScreenshot(path)
        Gdx.app.exit()
      }
    )
  }

  /** Floor -> walls -> ground -> y-sorted furniture and characters, drawn into
    * the fixed-resolution framebuffer.
    */
  private def renderWorld(): Unit = {
    worldBuffer.begin()
    Gdx.gl.glClearColor(0f, 0f, 0f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    worldCamera.update()
    worldBatch.setProjectionMatrix(worldCamera.combined)
    worldBatch.begin()
    val drawables: Seq[Renderable] = statics ++ objects ++ characters
    DrawOrder.sortRenderables(drawables).foreach(_.render(worldBatch))
    worldBatch.end()
    worldBuffer.end()
  }

  /** The original shape rendering plus the grid, drawn on screen over the
    * scaled world so the lines stay crisp.
    */
  private def renderDebugOverlay(): Unit = {
    debugTransform
      .idt()
      .translate(fit.x.toFloat, fit.y.toFloat, 0)
      .scale(fit.scale.toFloat, fit.scale.toFloat, 1)
    shapeRenderer.setProjectionMatrix(screenCamera.combined)
    shapeRenderer.setTransformMatrix(debugTransform)
    shapeRenderer.begin(ShapeType.Line)
    DebugDraw.grid(shapeRenderer, HorizBoxes, VertBoxes)
    shapeRenderer.setColor(0, 0, 0, 1)
    statics.foreach(_.renderDebug(shapeRenderer))
    objects.foreach(_.renderDebug(shapeRenderer))
    characters.foreach(_.renderDebug(shapeRenderer))
    shapeRenderer.end()
  }

  private def renderUi(): Unit = {
    uiBatch.begin()

    font.draw(
      uiBatch,
      s"Hour: ${(tick / 3600).toInt} Minute: ${(tick / 60).toInt % 60}\nTicks: ${tick}" +
        (if (showDebug) "\n[F1] debug on" else ""),
      50,
      50
    )

    // Four report panels down the left side, squeezed on short windows
    val spacing = math.min(200f, (HEIGHT - 150f) / characters.length)
    characters.zipWithIndex
      .foreach((person, i) =>
        person.report(font, uiBatch, (50f, HEIGHT - 100 - i * spacing))
      )

    uiBatch.end()
  }

  private def saveScreenshot(path: String): Unit = {
    val w = Gdx.graphics.getBackBufferWidth
    val h = Gdx.graphics.getBackBufferHeight
    val shot = Pixmap.createFromFrameBuffer(0, 0, w, h)
    // GL rows are bottom-up; PNG rows are top-down
    val flipped = new Pixmap(w, h, Pixmap.Format.RGBA8888)
    (0 until h).foreach(y =>
      flipped.drawPixmap(shot, 0, y, w, 1, 0, h - 1 - y, w, 1)
    )
    PixmapIO.writePNG(Gdx.files.absolute(path), flipped)
    shot.dispose()
    flipped.dispose()
    Gdx.app.log("SnowedIn", s"Saved screenshot to $path")
  }

  override def dispose(): Unit = {
    shapeRenderer.dispose()
    worldBatch.dispose()
    uiBatch.dispose()
    font.dispose()
    worldBuffer.dispose()
    Assets.dispose()
  }
}
