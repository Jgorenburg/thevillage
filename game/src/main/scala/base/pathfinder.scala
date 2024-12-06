package Base
import Direction.*

case class Position(x: Int, y: Int) {
  def manhattan(other: Position): Int =
    Math.abs(x - other.x) + Math.abs(y - other.y)

  def neighbors: List[Position] = List(
    Position(x - 1, y),
    Position(x + 1, y),
    Position(x, y - 1),
    Position(x, y + 1)
  )
}

case class Node(
    pos: Position,
    parent: Option[Node],
    moveTo: Option[Direction.Dir],
    g: Int,
    h: Int
) {
  def f: Int = g + h
}

case class GameMap(
    vertWalls: Array[Array[Boolean]],
    horizWalls: Array[Array[Boolean]]
) {
  def validMove(place: Position, move: Direction.Dir): Boolean = {
    move match
      case Down  => vertWalls(place.y)(place.x)
      case Up    => vertWalls(place.y + 1)(place.x)
      case Left  => horizWalls(place.y)(place.x)
      case Right => horizWalls(place.y)(place.x + 1)
  }
}

class AStar(width: Int, height: Int, stage: GameMap) {
  // Check if position is within grid bounds and not blocked
  def isValid(pos: Position, move: Direction.Dir): Boolean =
    pos.x >= 0 && pos.x < width &&
      pos.y >= 0 && pos.y < height &&
      stage.validMove(pos, move)

  def makePath(
      start: BoxCoords,
      goal: BoxCoords
  ): List[Direction.Dir] = {
    makePath(
      Position(start.x.toInt, start.y.toInt),
      Position(goal.x.toInt, goal.y.toInt)
    )
  }

  def makePath(
      start: Position,
      goal: Position
  ): List[Direction.Dir] = {
    // Priority queue ordered by f-score
    implicit val ordering: Ordering[Node] = Ordering.by[Node, Int](_.f).reverse
    var openSet = collection.mutable.PriorityQueue[Node](
      Node(start, None, None, 0, start.manhattan(goal))
    )

    var closedSet = Set.empty[Position]
    var cameFrom = Map.empty[Position, Node]

    while (openSet.nonEmpty) {
      val current = openSet.dequeue()

      if (current.pos == goal) {
        // Reconstruct path
        def reconstructPath(node: Node): List[Direction.Dir] =
          (node.parent, node.moveTo) match {
            case (Some(parent), Some(move)) =>
              println(parent.pos)
              reconstructPath(parent) :+ move
            case _ => List()
          }
        return reconstructPath(current)
      }

      closedSet += current.pos

      // Check all valid neighbors
      for {
        (nextPos, move) <- current.pos.neighbors.zip(Direction.values)
        if isValid(current.pos, move) && !closedSet.contains(nextPos)
      } {
        val tentativeG = current.g + 1
        val existingNode = cameFrom.get(nextPos)

        if (existingNode.isEmpty || tentativeG < existingNode.get.g) {
          val neighbor = Node(
            pos = nextPos,
            parent = Some(current),
            moveTo = Some(move),
            g = tentativeG,
            h = nextPos.manhattan(goal)
          )

          openSet = openSet.filter(_.pos != nextPos)
          openSet.enqueue(neighbor)
          cameFrom += (nextPos -> neighbor)
        }
      }
    }

    // No path found
    List.empty
  }
}
