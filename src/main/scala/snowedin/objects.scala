package Snowedin

import Base.{Actor, Story, Vibe, Spaces}
import Snowedin.Tools.Screwdriver
import scala.collection.mutable.HashSet
import Snowedin.Tools.Knife
import Base.Occupy

object Worktable extends Actor {
  lazy val myEvents: Array[Any] = Array(Construction)
  val startingTools: HashSet[Tools.Tools] = HashSet(Screwdriver, Knife)
  var tools: HashSet[Tools.Tools] = startingTools

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
  val maxCapacity = 2
  var curCapacity = maxCapacity

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
      case _: Story      =>
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
  val maxCapacity = 1
  var curCapacity = maxCapacity

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
      case _: Story =>
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

  var readyToClear: Boolean = false
  var readyForLunch: Boolean = false
  var readyForDinner: Boolean = false
  val maxCapacity = 4
  var curCapacity = maxCapacity

  lazy val myEvents: Array[Any] = Array(Snack)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => occupy(story)
      case _: Story      =>
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

object Easle extends Actor with Spaces {
  val maxCapacity = 1
  var curCapacity = maxCapacity

  lazy val myEvents: Array[Any] = Array(Art)

  def actorSpecificBeginning(tick: Int): Unit = {
    commonState.curStory match
      case story: Occupy => occupy(story)
      case _: Story      =>
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
  lazy val myEvents: Array[Any] = Array(CookLunch, CookDinner)
  var unattended = false
  var leftAlone = -1

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
  var dirty = true
  var readyToWash = false
  var running = false
  var clean = false
  var unloaded = false
  def actorSpecificBeginning(tick: Int): Unit = {}
  def actorSpecificEnding(tick: Int): Unit = {
    commonState.curStory match
      case CleanTable => readyToWash = true
      case StartDishwasher => {
        running = true
        dirty = false
      }
      case RunDishwasher => clean = true
      case UnloadDishwasher => {
        unloaded = true
        dirty = true
      }
      case _: Story =>
  }
  def actorSpecificInterrupt(tick: Int): Unit = {}
  def log(): String = {
    var frag = commonState.toString() + ", State: "
    if (unloaded) {
      frag += "Unloaded"
    } else if (clean) {
      frag += "Clean"
    } else if (running) {
      frag += "Running"
    } else if (readyToWash) {
      frag += "Loaded"
    } else {
      frag += "Untouched"
    }
    return frag
  }
  lazy val myEvents: Array[Any] =
    Array(CleanTable, StartDishwasher, RunDishwasher, UnloadDishwasher)
  def reset(): Unit = {
    readyToWash = false
    running = false
    clean = false
    unloaded = false
    commonState = (Vibe, 0)
  }
  def tick(tick: Int): Unit = {}

}
