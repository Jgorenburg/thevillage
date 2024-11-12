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

// Desktop launcher example
object DesktopLauncher extends App {
  import com.badlogic.gdx.backends.lwjgl3.{
    Lwjgl3Application,
    Lwjgl3ApplicationConfiguration
  }

  val config = new Lwjgl3ApplicationConfiguration
  config.setTitle("Snowed In")
//   config.setWindowedMode(800, 600)
  config.setMaximized(true)
  config.setForegroundFPS(60)

//   config.title = "Box Example"
//   config.width = 800
//   config.height = 600
//   config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode())

  new Lwjgl3Application(new SnowedIn, config)
}
