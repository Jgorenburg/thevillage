package DateNight

import scala.compiletime.uninitialized

import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.graphics.Color

import MyLogger.MyLogger

import Base.GameManager
import Base.StoryRunner
import Base.Updater
import Base.Story
import Base.Actor
import Base.Static
import Base.GameManager.log
import Base.Person
import Base.GameMap
import Base.BoxCoords
import Base.AStar
import Base.Position
import Base.Direction.*
import Base.ControlRoom
import Base.GlobalVars
import Base.GameTemplate

import DateNightPositionConstants.*
import DateNightControls.{statics, objects, characters, stories}

object DateNightControls extends ControlRoom {
  val stories: List[Story] =
    List(
    )
  val characters: List[Person] = List(Partner)
  val objects: List[Actor] =
    List(PicnicBlanket, Bench, Shop, Restuarant, Theater)
  val statics: List[Static] =
    List(Park, Road, StatueArea, ParkPaths, River)

  def setup(
      gameLen: Int = 720,
      logging: Boolean = false,
      loggerFile: String = "DateNight"
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
    GameManager.setup(
      gameLen,
      updaters,
      stories,
      characters,
      objects,
      DateNightPositionConstants
    )
    if (logging) {
      MyLogger.setFile(loggerFile)
      MyLogger.printHeader(GameManager.characters ::: GameManager.objects)
    }
  }

}

class DateNight extends GameTemplate {
  override def create(): Unit = {
    // Initialize the shape renderer
    super.create()
    BoxCoords.setup(StageBase, boxSize, HorizBoxes, VertBoxes)

    DateNightControls.setup(43200, true)()
  }

  override def render(): Unit = {
    // if (tick < DateNightControls.endTick) {
    //   tick = GameManager.step(DateNightControls.isLogging)
    // }

    // Laundry.tick(0)
    // Clear the screen
    Gdx.gl.glClearColor(255.0f, 255.0f, 255.0f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update camera
    camera.update()
    shapeRenderer.setProjectionMatrix(camera.combined)

    // Begin shape rendering
    shapeRenderer.begin(ShapeType.Line)

    // Toggle to show grid
    if (false) {
      // temp grid for object placement
      shapeRenderer.setColor(.7f, .7f, .7f, 1)
      val TR = topRight.toRealLocation()
      (BigDecimal(stageX) to BigDecimal(TR._1) by BigDecimal(boxSize))
        .foreach(x =>
          shapeRenderer.line(
            x.toFloat,
            stageY,
            x.toFloat,
            stageY + VertBoxes * boxSize
          )
        )
      (BigDecimal(stageY) to BigDecimal(TR._2) by BigDecimal(boxSize))
        .foreach(y =>
          shapeRenderer.line(
            stageX,
            y.toFloat,
            stageX + HorizBoxes * boxSize,
            y.toFloat
          )
        )
      shapeRenderer.setColor(1, 0, 0, .5f)
      (BigDecimal(stageX) to BigDecimal(TR._1) by BigDecimal(5 * boxSize))
        .foreach(x =>
          shapeRenderer.line(
            x.toFloat,
            stageY,
            x.toFloat,
            stageY + VertBoxes * boxSize
          )
        )
      (BigDecimal(stageY) to BigDecimal(TR._2) by BigDecimal(5 * boxSize))
        .foreach(y =>
          shapeRenderer.line(
            stageX,
            y.toFloat,
            stageX + HorizBoxes * boxSize,
            y.toFloat
          )
        )
    }
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
}
