package DateNight

import Base.Person
import com.badlogic.gdx.graphics.Color
import Base.Vibe
import com.badlogic.gdx.Gdx
import Base.Direction.*
import Base.PlayerBased

object Partner extends Person {

  val color = Color.RED
  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}

  lazy val myEvents: Array[Any] = Array()
  def tick(tick: Int): Unit = {}

  def log(): String = commonState.toString()
    + ", Importance: " + commonState.curStory.importance
    + ", Location: " + room

}

object Player extends Person {

  val color = Color.BLUE
  location = (4, 4)
  room = DNRoom.Park
  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}

  lazy val myEvents: Array[Any] = Array()
  def tick(tick: Int): Unit = {
    if (moving) {
      updateMotion()
    }
  }

  def log(): String = commonState.toString()
    + ", Location: " + room

  val playerSpeed = speed * 64
  var vertDir: Direction = null
  var horizDir: Direction = null
  var moving = false

  def updateMotion(): Unit = {
    horizDir match
      case Left  => location.x -= playerSpeed * Gdx.graphics.getDeltaTime()
      case Right => location.x += playerSpeed * Gdx.graphics.getDeltaTime()
      case _     =>

    vertDir match
      case Up   => location.y += playerSpeed * Gdx.graphics.getDeltaTime()
      case Down => location.y -= playerSpeed * Gdx.graphics.getDeltaTime()
      case _    =>
  }

  def startMoving(dir: Direction): Unit = {
    if (
      commonState.curStory.isInstanceOf[PlayerBased] &&
      commonState.curStory.asInstanceOf[PlayerBased].canMove
    ) {
      dir match
        case Left  => horizDir = Left
        case Right => horizDir = Right
        case Up    => vertDir = Up
        case Down  => vertDir = Down
      moving = true
    }
  }

  def stopMoving(dir: Direction): Unit = {
    if (dir == Right || dir == Left) {
      if (dir == horizDir) {
        horizDir = null
      }
    } else if (dir == vertDir) {
      vertDir = null
    }

    moving = horizDir != null || vertDir != null

  }

}
