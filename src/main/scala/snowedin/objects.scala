package Snowedin

import Base.{Actor, Story}

object Worktable extends Actor {
  var commonState = (Vibe, 0)
  var myEvents: Array[Any] = Array(Construction)
}

object Couch extends Actor {
//   var state: Array[Any]
  var commonState = (Vibe, 0)

  var myEvents: Array[Any] = Array(Nap)
}
