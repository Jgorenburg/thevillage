package Snowedin

import Base.{Actor, Story}

object Couch extends Actor {
//   var state: Array[Any]
  var commonState: (Story, Int) = (Vibe, 0)

  var myEvents: Array[Any] = Array(Nap)
}
