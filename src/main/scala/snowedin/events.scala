package Snowedin

import Base.Story
import Base.Importance
import scala.util.Random

object Laundry extends Story {
  var conditions: List[() => Boolean] =
    List(() => Importance.interrupt(Father.getCurStoryImportance(), importance))
  var active: Boolean = false
  var commonState = (false, -1, false, 7)
  val importance: Importance.Importance = Importance.Event
}

object NoticeBrokenDoor extends Story {
  var conditions: List[() => Boolean] = List(() => Random.nextFloat() > 0.99)
  var active: Boolean = false
  var commonState = (false, -1, false, 0)
  val importance: Importance.Importance = Importance.Instantaneous
}

object FixDoor extends Story {
  var conditions: List[() => Boolean] = List(
    () => Father.noticedBrokenDoor,
    () => Father.tools(Tools.Screwdriver.id),
    () => Importance.interrupt(Father.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  var commonState = (false, -1, false, 3)
  val importance: Importance.Importance = Importance.Event

}

object Construction extends Story {
  var conditions: List[() => Boolean] = List(
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Worktable.getCurStoryImportance(), importance)
  )
  var active: Boolean = false
  var commonState = (false, -1, true, 5)
  val importance: Importance.Importance = Importance.Base
}

object Vibe extends Story {
  var conditions: List[() => Boolean] = List()
  var commonState = (false, 0, true, -1)
  var active: Boolean = true
  val importance = Importance.Vibe
}

object Nap extends Story {

// conditions:
//     precedence > precedence of couch's activity
//     precedence > precedence of dad's activity

  var conditions: List[() => Boolean] = List(
    () => Importance.interrupt(Father.getCurStoryImportance(), importance),
    () => Importance.interrupt(Couch.getCurStoryImportance(), importance)
  )
  var active: Boolean = false

  var commonState = (false, -1, true, 10)

// state:
//     0: how many naps father has taken
//     1: when the nap started
  var state: Array[Any] = Array(0, -1)

  val importance: Importance.Importance = Importance.Base
}
