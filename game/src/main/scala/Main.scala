// import Snowedin.ControlRoom
// import MyLogger.MyLogger

// @main def hello(): Unit =
//   ControlRoom.runGame(120, true, "snow")
//   println("salmon")

// def msg = "I was compiled by Scala 3. :)"

import com.badlogic.gdx.{ApplicationAdapter, Gdx}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import Snowedin.SnowedIn
import DateNight.DateNight
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import scala.util.matching.Regex

object DesktopLauncher {
  def main(args: Array[String]): Unit = {
    println(args)
    val DateNightPattern: Regex = """(?i)date.*""".r
    val SnowedInPattern: Regex = """(?i)snow.*""".r
    var game: ApplicationAdapter = new DateNight
    if (args.nonEmpty) game = (args(0) match
      case DateNightPattern() => new DateNight
      case SnowedInPattern()  => new SnowedIn
    )

    val config = new Lwjgl3ApplicationConfiguration
    config.setTitle("Snowed In")
//   config.setWindowedMode(800, 600)
    config.setMaximized(true)
    config.setForegroundFPS(120)

//   config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())

    new Lwjgl3Application(game, config)
  }
}
