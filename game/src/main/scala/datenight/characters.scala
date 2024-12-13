package DateNight

import Base.Person
import com.badlogic.gdx.graphics.Color
import Base.Vibe

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
  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}
  def actorSpecificInterrupt(tick: Int): Unit = {}

  lazy val myEvents: Array[Any] = Array()
  def tick(tick: Int): Unit = {}

  def log(): String = commonState.toString()
    + ", Location: " + room

}
