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

  var leftMove = false
  var rightMove = false

  val playerSpeed = speed * 8

  def updateMotion(): Unit = {
    moveDir match
      case Left  => location.x -= playerSpeed * Gdx.graphics.getDeltaTime()
      case Right => location.x += playerSpeed * Gdx.graphics.getDeltaTime()
      case Up    => location.y += playerSpeed * Gdx.graphics.getDeltaTime()
      case Down  => location.y -= playerSpeed * Gdx.graphics.getDeltaTime()
      case _     => throw new RuntimeException
  }

  var moveDir: Direction = null
  var moving = false

  def startMoving(dir: Direction): Unit = {
    if (
      commonState.curStory.isInstanceOf[PlayerBased] &&
      commonState.curStory.asInstanceOf[PlayerBased].canMove
    ) {
      moveDir = dir
      moving = true
    }
  }

  def stopMoving(dir: Direction): Unit = {
    if (dir == moveDir) {
      moveDir = null
      moving = false
    }
  }

}
