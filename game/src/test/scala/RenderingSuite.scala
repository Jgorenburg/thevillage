import Base.{
  BoxCoords,
  Direction,
  DrawOrder,
  Motion,
  PersonSprites,
  Pose,
  ScreenFit,
  SpriteChoice,
  Vibe,
  curStory
}
import Snowedin.{Couch, Father, Lunch, Sofachair, Mother, Nap, Read, SnowedInSprites, Watercolor}

// Pure rendering logic only: nothing here touches GL or libGDX natives.

class DrawOrderSuite extends munit.FunSuite {
  case class Item(name: String, layer: Int, y: Float)
  private def order(items: Item*) =
    DrawOrder.sort(items)(_.layer, _.y).map(_.name)

  test("higher y is drawn first") {
    assertEquals(
      order(Item("front", 3, 1), Item("back", 3, 10), Item("mid", 3, 5)),
      Seq("back", "mid", "front")
    )
  }

  test("layers come before y") {
    assertEquals(
      order(
        Item("person", 3, 20),
        Item("counter", 2, 0),
        Item("floor", 0, 0),
        Item("wall", 1, 11)
      ),
      Seq("floor", "wall", "counter", "person")
    )
  }

  test("ties keep their input order") {
    assertEquals(
      order(Item("a", 3, 4), Item("b", 3, 4), Item("c", 3, 4)),
      Seq("a", "b", "c")
    )
  }

  test("fractional positions sort correctly") {
    assertEquals(
      order(Item("walker", 3, 4.25f), Item("table", 3, 5), Item("x", 3, 4.5f)),
      Seq("table", "x", "walker")
    )
  }

  test("seating is drawn under the person sitting on it") {
    val seats = Seq(
      Couch -> Couch.seat1Loc,
      Couch -> Couch.seat2Loc,
      Sofachair -> Sofachair.seatingLoc
    )
    seats.foreach { (furniture, seat) =>
      assertEquals(
        order(Item("sitter", 3, seat.y), Item("seat", 3, furniture.sortY)),
        Seq("seat", "sitter")
      )
    }
  }
}

class ScreenFitSuite extends munit.FunSuite {
  test("largest whole-number scale that fits, centred") {
    val f = ScreenFit.compute(1710, 1045, 272, 352)
    assertEquals(f.scale, 2)
    assertEquals((f.width, f.height), (544, 704))
    assertEquals((f.x, f.y), ((1710 - 544) / 2, (1045 - 704) / 2))
  }

  test("limited by the tighter axis") {
    assertEquals(ScreenFit.compute(3000, 2000, 272, 352).scale, 5)
    assertEquals(ScreenFit.compute(600, 3000, 272, 352).scale, 2)
  }

  test("exact multiples fit") {
    val f = ScreenFit.compute(816, 1056, 272, 352)
    assertEquals(f.scale, 3)
    assertEquals((f.x, f.y), (0, 0))
  }

  test("never below 1 on tiny windows") {
    val f = ScreenFit.compute(100, 100, 272, 352)
    assertEquals(f.scale, 1)
    assertEquals((f.width, f.height), (272, 352))
  }

  test("pushed right of the reserved UI column when there is room") {
    // centred x would be 228, inside a 340px column
    val f = ScreenFit.compute(1000, 800, 272, 352, reservedLeft = 340)
    assertEquals(f.scale, 2)
    assertEquals(f.x, 340)
  }

  test("stays centred when the UI column does not fit beside it") {
    val f = ScreenFit.compute(700, 800, 272, 352, reservedLeft = 340)
    assertEquals(f.x, (700 - 544) / 2)
  }
}

class SpriteNameSuite extends munit.FunSuite {
  import Direction.*

  test("walking picks the direction and flips for left") {
    assertEquals(
      PersonSprites.choose("father", Some(Down), Pose.Stand, Down),
      SpriteChoice("father_walk_down", false, true)
    )
    assertEquals(
      PersonSprites.choose("father", Some(Up), Pose.Sit, Down).name,
      "father_walk_up"
    )
    assertEquals(
      PersonSprites.choose("son", Some(Right), Pose.Stand, Down),
      SpriteChoice("son_walk_side", false, true)
    )
    assertEquals(
      PersonSprites.choose("son", Some(Left), Pose.Stand, Down),
      SpriteChoice("son_walk_side", true, true)
    )
  }

  test("walking wins over sitting until the character arrives") {
    assertEquals(
      PersonSprites.choose("mother", Some(Left), Pose.Sit, Up).name,
      "mother_walk_side"
    )
  }

  test("sitting once arrived") {
    assertEquals(
      PersonSprites.choose("mother", None, Pose.Sit, Left),
      SpriteChoice("mother_sit", false, false)
    )
  }

  test("idle keeps the last facing") {
    assertEquals(
      PersonSprites.choose("daughter", None, Pose.Stand, Up).name,
      "daughter_idle_up"
    )
    assertEquals(
      PersonSprites.choose("daughter", None, Pose.Stand, Left),
      SpriteChoice("daughter_idle_side", true, false)
    )
  }

  test("pose table: seated stories sit, others stand") {
    assertEquals(SnowedInSprites.poseFor(Lunch), Pose.Sit)
    assertEquals(SnowedInSprites.poseFor(Nap), Pose.Sit)
    assertEquals(SnowedInSprites.poseFor(Read), Pose.Sit)
    assertEquals(SnowedInSprites.poseFor(Watercolor), Pose.Stand)
    assertEquals(SnowedInSprites.poseFor(Vibe), Pose.Stand)
  }

  test("motion direction from a position delta") {
    assertEquals(Motion.direction(0, 0), None)
    assertEquals(Motion.direction(-0.25f, 0), Some(Left))
    assertEquals(Motion.direction(0, 0.25f), Some(Up))
    assertEquals(Motion.direction(0, -1), Some(Down))
  }

  test("spriteName(person) follows movement and the current story") {
    val savedLoc = Father.location
    val savedState = Father.commonState
    val savedStack = Father.movementStack
    try {
      Father.location = new BoxCoords(5f, 5f)
      Father.movementStack = List()
      Father.updateSprite(0.1f)
      assertEquals(PersonSprites.spriteName(Father).name, "father_idle_down")

      // Moves left along its planned path
      Father.movementStack = List(Left, Left)
      Father.location = new BoxCoords(4.75f, 5f)
      Father.updateSprite(0.1f)
      assertEquals(
        PersonSprites.spriteName(Father),
        SpriteChoice("father_walk_side", true, true)
      )
      assertEquals(Father.spriteState.stateTime, 0f)
      Father.location = new BoxCoords(4.5f, 5f)
      Father.updateSprite(0.1f)
      assertEqualsFloat(Father.spriteState.stateTime, 0.1f, 1e-6f)

      // Arrives at the table for lunch and sits
      Father.commonState = curStory(Lunch, 0)
      Father.movementStack = List()
      Father.updateSprite(0.1f)
      assertEquals(PersonSprites.spriteName(Father).name, "father_sit")

      // Standing story: idle, still facing left
      Father.commonState = curStory(Vibe, 0)
      Father.updateSprite(0.1f)
      assertEquals(
        PersonSprites.spriteName(Father),
        SpriteChoice("father_idle_side", true, false)
      )
    } finally {
      Father.location = savedLoc
      Father.commonState = savedState
      Father.movementStack = savedStack
    }
  }

  test("every character sprite has a placeholder") {
    val names = SnowedInSprites.placeholders.map(_.name).toSet
    val needed = for {
      p <- List(Father, Mother).map(_.spritePrefix)
      moving <- None :: Direction.values.toList.map(Some(_))
      pose <- Pose.values.toList
      facing <- Direction.values.toList
    } yield PersonSprites.choose(p, moving, pose, facing)
    needed.foreach { c =>
      if (c.animated) assert(names.contains(s"${c.name}_0"), c.name)
      else assert(names.contains(c.name), c.name)
    }
  }

  test("object state names") {
    assertEquals(SnowedInSprites.stoveState(true, true, false), "fire")
    assertEquals(SnowedInSprites.stoveState(false, true, false), "cooking")
    assertEquals(SnowedInSprites.stoveState(false, false, false), "idle")
    assertEquals(SnowedInSprites.dishwasherState(true, true), "running")
    assertEquals(SnowedInSprites.dishwasherState(false, true), "open")
    assertEquals(SnowedInSprites.fireplaceState(false, true), "lit")
    assertEquals(SnowedInSprites.fireplaceState(false, false), "unlit")
    assertEquals(SnowedInSprites.frontDoorState(true), "broken")
  }
}
