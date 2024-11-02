package Snowedin

import Base.Story
import Base.Importance

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
