trait Actor extends Subject[Actor] with Listener {
  var state: List[Any]
  var myEvents: List[Story]
}
