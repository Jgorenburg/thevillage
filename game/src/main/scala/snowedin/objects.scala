package Snowedin

import Base.{Actor, Story, Vibe, Spaces}
import Snowedin.Tools.Screwdriver
import scala.collection.mutable.HashSet
import Snowedin.Tools.Knife
import Base.Occupy
import Snowedin.Location.Kitchen
import Snowedin.Location.LivingRoom
import Snowedin.Location.DiningRoom
import Snowedin.Location.Workroom
import Snowedin.SnowedInPositionConstants.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

object Worktable extends Actor {
  val location = (topRight._1 - 2 * boxSize, topRight._2 - 5 * boxSize)
  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    shapeRenderer.rect(
      location._1,
      location._2,
      2 * boxSize,
      4 * boxSize
    )
  }
  lazy val myEvents: Array[Any] = Array(Construction)
  val startingTools: HashSet[Tools.Tools] = HashSet(Screwdriver, Knife)
  var tools: HashSet[Tools.Tools] = startingTools
  room = Workroom

  def actorSpecificBeginning(tick: Int): Unit = {}
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    tools = startingTools
  }

  def log() = commonState.toString() +
    " Tools: " + tools.mkString(", ")
}

object Couch extends Actor with Spaces {

  // TODO other objects should have this method
  def getSeatLoc(): (Float, Float) = {
    if (curCapacity == 1) {
      seat2Loc
    } else {
      seat1Loc
    }
  }
  val seat1Loc = (topLeft._1 + 4 * boxSize, topLeft._2 - 8 * boxSize)
  val seat2Loc = (topLeft._1 + 4 * boxSize, topLeft._2 - 6 * boxSize)
  val location =
    (topLeft._1 + 4 * boxSize, topLeft._2 - 9 * boxSize)

  def render(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.setColor(0, 0, 0, 1)

    // Draw the box
    val vertices = Array(
      location._1,
      location._2,
      location._1 + 2 * boxSize,
      location._2,
      location._1 + 2 * boxSize,
      location._2 + 5 * boxSize,
      location._1,
      location._2 + 5 * boxSize,
      location._1,
      location._2 + 4 * boxSize,
      location._1 + 1 * boxSize,
      location._2 + 4 * boxSize,
      location._1 + 1 * boxSize,
      location._2 + 1 * boxSize,
      location._1,
      location._2 + 1 * boxSize
    )

    shapeRenderer.polygon(vertices)
    shapeRenderer.line(
      location._1,
      location._2 + 2.5f * boxSize,
      location._1 + boxSize,
      location._2 + 2.5f * boxSize
    )
    shapeRenderer.curve(
      location._1,
      location._2 + 4 * boxSize,
      location._1 - .2f * boxSize,
      location._2 + 3.8f * boxSize,
      location._1 - .2f * boxSize,
      location._2 + 2.7f * boxSize,
      location._1,
      location._2 + 2.5f * boxSize,
      100
    )
    shapeRenderer.curve(
      location._1,
      location._2 + 1 * boxSize,
      location._1 - .2f * boxSize,
      location._2 + 1.2f * boxSize,
      location._1 - .2f * boxSize,
      location._2 + 2.3f * boxSize,
      location._1,
      location._2 + 2.5f * boxSize,
      100
    )

  }

  val maxCapacity = 2
  var curCapacity = maxCapacity
  room = LivingRoom

  lazy val myEvents: Array[Any] = Array(Nap, Read, Movie, JoinMovie)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState.curStory match
      case Nap => occupy(Nap.size)
      case Movie =>
        if (Movie.needToSeat > curCapacity) {
          Movie.needToSeat -= curCapacity
          occupy(Movie, curCapacity)
        } else if (Movie.needToSeat > 0) {
          Movie.needToSeat = 0
          occupy(Movie)
        }
      case story: Occupy => occupy(story)
      case _: Story      => vacate()
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    vacate()
  }

  def log() = commonState.toString() +
    s"  Current Capacity: ${curCapacity}  Max Capacity: ${maxCapacity}"

}

object Sofachair extends Actor with Spaces {
  val location = (topLeft._1 + 3.5f * boxSize, topLeft._2 - 3.5f * boxSize)
  val seatingLoc = (topLeft._1 + 2 * boxSize, topLeft._2 - 3.5f * boxSize)
  def getSeatingLoc() = seatingLoc
  def render(shapeRenderer: ShapeRenderer): Unit = {

    val vertices: Array[Float] =
      Array(
        location._1,
        location._2,
        location._1,
        location._2 + 1.5f * boxSize,
        location._1 - 2 * boxSize,
        location._2 + 1.5f * boxSize,
        location._1 - 2 * boxSize,
        location._2,
        location._1 - 1.5f * boxSize,
        location._2,
        location._1 - 1.5f * boxSize,
        location._2 + 1.0f * boxSize,
        location._1 - 0.5f * boxSize,
        location._2 + 1.0f * boxSize,
        location._1 - 0.5f * boxSize,
        location._2
      )

    shapeRenderer.polygon(vertices)
    shapeRenderer.curve(
      location._1 - 0.5f * boxSize,
      location._2,
      location._1 - 0.7f * boxSize,
      location._2 - 0.2f * boxSize,
      location._1 - 1.3f * boxSize,
      location._2 - 0.2f * boxSize,
      location._1 - 1.5f * boxSize,
      location._2,
      100
    )
  }
  val maxCapacity = 1
  var curCapacity = maxCapacity
  room = LivingRoom

  lazy val myEvents: Array[Any] = Array(Snack, Read, JoinMovie, Movie)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState.curStory match
      case Snack => occupy(Snack)
      case Movie => {
        if (Movie.needToSeat > curCapacity) {
          occupy(Movie, curCapacity)
        } else if (Movie.needToSeat > 0) {
          occupy(Movie)
        }
      }
      case story: Occupy => {
        occupy(story)
      }
      case _: Story => vacate()
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    vacate()
  }

  def log() = commonState.toString() +
    s"  Current Capacity: ${curCapacity}  Max Capacity: ${maxCapacity}"

}

object Table extends Actor with Spaces {
  val location = (bottomLeft._1 + 6 * boxSize, bottomLeft._2 + 5 * boxSize)
  def getLoc1() = (bottomLeft._1 + 7 * boxSize, bottomLeft._2 + 9 * boxSize)
  def getLoc2() = (bottomLeft._1 + 10 * boxSize, bottomLeft._2 + 9 * boxSize)
  def getLoc3() = (bottomLeft._1 + 10 * boxSize, bottomLeft._2 + 4 * boxSize)
  def getLoc4() = (bottomLeft._1 + 7 * boxSize, bottomLeft._2 + 4 * boxSize)
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.rect(location._1, location._2, 6 * boxSize, 4 * boxSize)
  }

  var readyToClear: Boolean = false
  var readyForLunch: Boolean = false
  var readyForDinner: Boolean = false
  val maxCapacity = 4
  var curCapacity = maxCapacity
  room = DiningRoom

  lazy val myEvents: Array[Any] = Array(Snack)

  def actorSpecificBeginning(tick: Int): Unit = {
    if (commonState.curStory != Boardgame && Boardgame.isPaused) {
      Boardgame.beginAnew()
    }
    commonState.curStory match
      case story: Occupy => occupy(story)
      case _: Story      => vacate()
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    vacate()
  }

  def log() = {
    val status = {
      if (readyForDinner) {
        "Ready for Dinner"
      } else if (readyForLunch) {
        "Ready for Lunch"
      } else if (readyToClear) {
        "Ready to Clear"
      } else {
        "Not Food Related"
      }
    }
    commonState.toString() +
      s"  Current Capacity: ${curCapacity}  Max Capacity: ${maxCapacity} Status: ${status}"
  }

}

object Easle extends Actor with Spaces {
  val location = (topLeft._1 + 6 * boxSize, topLeft._2 - 2 * boxSize)
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.rect(location._1, location._2, boxSize, boxSize)
  }
  val maxCapacity = 1
  var curCapacity = maxCapacity
  room = LivingRoom

  lazy val myEvents: Array[Any] = Array(Art)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => occupy(story)
      case _: Story      => vacate()
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story =>
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => leave(story)
      case _: Story      =>
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    vacate()
  }

  def log() = commonState.toString() +
    s"  Current Capacity: ${curCapacity}  Max Capacity: ${maxCapacity}"

}

object Stove extends Actor {
  val location = (bottomLeft._1 + 6 * boxSize, bottomLeft._2 + boxSize)
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.rect(location._1, location._2, 3 * boxSize, 2 * boxSize)
  }
  lazy val myEvents: Array[Any] = Array(CookLunch, CookDinner)
  var unattended = false
  var leftAlone = -1
  room = Kitchen

  def actorSpecificBeginning(tick: Int): Unit = {
    unattended = false
  }
  def tick(tick: Int): Unit = {
    commonState.curStory match
      case _: Story => // stories without relevant progress
  }
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case _: Story => // stories without relevant endings
  }

  def actorSpecificInterrupt(tick: Int): Unit = {
    unattended = true
    leftAlone = tick
  }

  def reset(): Unit = {
    commonState = (Vibe, 0)
    unattended = false
    leftAlone = -1
  }

  def log() = commonState.toString() +
    s" Unattended: ${unattended}, Left At: ${leftAlone}"
}

object Dishwasher extends Actor {
  val location = (bottomRight._1 - 3 * boxSize, bottomRight._2 + 3 * boxSize)
  def render(shapeRenderer: ShapeRenderer) = {
    shapeRenderer.rect(location._1, location._2, 2 * boxSize, 3 * boxSize)
  }

  var dirty = true
  var readyToWash = false
  var running = false
  var clean = false
  room = Kitchen
  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case CleanTable => readyToWash = true
      case StartDishwasher => {
        running = true
        dirty = false
        readyToWash = false
      }
      case RunDishwasher => {
        running = false
        clean = true
      }
      case UnloadDishwasher => {
        dirty = true
        clean = false
      }
      case _: Story =>
  }
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = {
    var frag = commonState.toString() + ", State: "

    if (clean) {
      frag += "Clean"
    } else if (running) {
      frag += "Running"
    } else if (readyToWash) {
      frag += "Loaded"
    } else {
      frag += "Empty"
    }
    return frag
  }
  lazy val myEvents: Array[Any] =
    Array(CleanTable, StartDishwasher, RunDishwasher, UnloadDishwasher)
  def reset(): Unit = {
    readyToWash = false
    running = false
    clean = false
    commonState = (Vibe, 0)
  }
  def tick(tick: Int): Unit = {}

}
