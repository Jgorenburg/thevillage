package DateNight

import DateNightPositionConstants.*
import Base.Actor
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import javax.swing.text.Position
import Base.BoxCoords
import Base.CurveRenderer
import com.badlogic.gdx.math.Vector2

object PicnicBlanket extends Actor {
  val location = bottomLeft + (4, 1)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.rect(rloc()._1, rloc()._2, 2 * boxSize, 2 * boxSize)
  }

  lazy val myEvents: Array[Any] = Array()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}

  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log() = commonState.toString()
}

object Bench extends Actor {
  val location = bottomLeft + (3, 8)
  var interactLoc = location
  def render(shapeRenderer: ShapeRenderer) = {
    def renderLine(p1: (Float, Float), p2: (Float, Float)) =
      shapeRenderer.line(p1._1, p1._2, p2._1, p2._2)

    renderLine(
      (location + (0.5f, 0f)).toRealLocation(),
      (location + (1f, 0f)).toRealLocation()
    )
    renderLine(
      (location + (1f, 2f)).toRealLocation(),
      (location + (1f, 0f)).toRealLocation()
    )
    renderLine(
      (location + (0.5f, 2f)).toRealLocation(),
      (location + (1f, 2f)).toRealLocation()
    )
  }

  lazy val myEvents: Array[Any] = Array()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}

  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log() = commonState.toString()
}

object Theater extends Actor {
  val location = bottomRight - (2, -10)

  var interactLoc = location + (0, 2)
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      2 * boxSize,
      5 * boxSize
    )

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      0.25f * boxSize,
      1 * boxSize
    )

    val tr = topRight.toRealLocation()

    val vertices = Array(
      tr._1 - 0.25f * boxSize,
      tr._2 - 1.5f * boxSize,
      tr._1 - 0.25f * boxSize,
      tr._2 - 2f * boxSize,
      tr._1 - 0.75f * boxSize,
      tr._2 - 2f * boxSize,
      tr._1 - 0.75f * boxSize,
      tr._2 - 3.5f * boxSize,
      tr._1 - 1.25f * boxSize,
      tr._2 - 3.5f * boxSize,
      tr._1 - 1.25f * boxSize,
      tr._2 - 2f * boxSize,
      tr._1 - 1.75f * boxSize,
      tr._2 - 2f * boxSize,
      tr._1 - 1.75f * boxSize,
      tr._2 - 1.5f * boxSize
    )
    shapeRenderer.polygon(vertices)

  }

  lazy val myEvents: Array[Any] = Array()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}

  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log() = commonState.toString()
}

object Restuarant extends Actor {
  val location = bottomRight - (2, -5)
  var interactLoc = location + (0, 2)
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      2 * boxSize,
      5 * boxSize
    )

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2 + 2 * boxSize,
      0.25f * boxSize,
      1 * boxSize
    )

    val vertices = Array(
      rloc()._1 + 0.5f * boxSize,
      rloc()._2 + 3.5f * boxSize,
      rloc()._1 + 1.5f * boxSize,
      rloc()._2 + 3.5f * boxSize,
      rloc()._1 + 1.5f * boxSize,
      rloc()._2 + 3f * boxSize,
      rloc()._1 + 1f * boxSize,
      rloc()._2 + 3f * boxSize,
      rloc()._1 + 1f * boxSize,
      rloc()._2 + 2.5f * boxSize,
      rloc()._1 + 1.5f * boxSize,
      rloc()._2 + 2.5f * boxSize,
      rloc()._1 + 1.5f * boxSize,
      rloc()._2 + 2f * boxSize,
      rloc()._1 + 1f * boxSize,
      rloc()._2 + 2f * boxSize,
      rloc()._1 + 1f * boxSize,
      rloc()._2 + 1f * boxSize,
      rloc()._1 + 0.5f * boxSize,
      rloc()._2 + 1f * boxSize
    )
    shapeRenderer.polygon(vertices)
  }

  lazy val myEvents: Array[Any] = Array()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}

  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log() = commonState.toString()
}

object Shop extends Actor {
  val location = bottomRight - (2, 0)

  var interactLoc = location + (0, 2)
  def render(shapeRenderer: ShapeRenderer): Unit = {

    shapeRenderer.setColor(0, 0, 0, 1)

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2,
      2 * boxSize,
      5 * boxSize
    )

    shapeRenderer.rect(
      rloc()._1,
      rloc()._2 + 4 * boxSize,
      0.25f * boxSize,
      1 * boxSize
    )

    CurveRenderer.drawSpline(
      Array(
        Vector2(rloc()._1 + 3.5f * boxSize, rloc()._2 + 3.75f * boxSize),
        Vector2(rloc()._1 + 1.5f * boxSize, rloc()._2 + 3.75f * boxSize),
        Vector2(rloc()._1 + 0.5f * boxSize, rloc()._2 + 3f * boxSize),
        Vector2(rloc()._1 + 1.5f * boxSize, rloc()._2 + 2.5f * boxSize),
        Vector2(rloc()._1 + 0.5f * boxSize, rloc()._2 + 1.75f * boxSize),
        Vector2(rloc()._1 - 2.5f * boxSize, rloc()._2 + 1.75f * boxSize)
      )
    )
  }

  lazy val myEvents: Array[Any] = Array()

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {}

  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log() = commonState.toString()
}
