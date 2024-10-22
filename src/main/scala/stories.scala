trait Story extends Subject[Story] with Listener {
  var conditions: List[() => Boolean]
  var active: Boolean
  var state: List[Any]
}
