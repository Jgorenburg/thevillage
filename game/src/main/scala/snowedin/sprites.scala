package Snowedin

import Base.{Person, PlaceholderSpec, Pose, Story}
import com.badlogic.gdx.graphics.Color

/** Snowed In specific sprite logic: story -> pose, object state names, and the
  * list of every atlas region the game draws.
  */
object SnowedInSprites {

  val Floor = "floor"

  /** THE table of which stories show a character sitting once they have
    * arrived. Add a case here to give a story a different pose; anything not
    * listed is `Pose.Stand`.
    */
  val storyPoses: PartialFunction[Story, Pose] = {
    // Couch / Sofachair
    case Nap | Read | Movie | JoinMovie | Knit => Pose.Sit
    // Dining table (Snack sits at the table, sofachair or couch)
    case Lunch | Dinner | Code | Boardgame | Snack => Pose.Sit
    case _: IndivBreakfast                         => Pose.Sit
  }

  def poseFor(story: Story): Pose =
    storyPoses.applyOrElse(story, _ => Pose.Stand)

  def fireplaceState(tending: Boolean, completed: Boolean): String =
    if (tending || completed) "lit" else "unlit"

  def stoveState(
      kitchenFire: Boolean,
      cooking: Boolean,
      unattended: Boolean
  ): String =
    if (kitchenFire) "fire"
    else if (cooking || unattended) "cooking"
    else "idle"

  def dishwasherState(running: Boolean, open: Boolean): String =
    if (running) "running" else if (open) "open" else "idle"

  def frontDoorState(broken: Boolean): String =
    if (broken) "broken" else "fixed"

  // ---- Placeholder manifest -------------------------------------------------

  val TilePx = 16
  val CharacterW = 16
  val CharacterH = 24
  val WalkFrames = 4
  def characters: List[Person] = List(Father, Mother, Son, Daughter)

  private def px(tiles: Float): Int = math.round(tiles * TilePx)
  private def c(hex: String): Color = Color.valueOf(hex)

  private def one(name: String, w: Float, h: Float, color: Color) =
    List(PlaceholderSpec(name, px(w), px(h), color))

  private def frames(
      name: String,
      n: Int,
      w: Float,
      h: Float,
      color: Color
  ) = (0 until n).map(i => PlaceholderSpec(s"${name}_$i", px(w), px(h), color))

  def characterPlaceholders(person: Person): List[PlaceholderSpec] = {
    val p = person.spritePrefix
    val dirs = List("down", "up", "side")
    def spec(name: String) =
      PlaceholderSpec(name, CharacterW, CharacterH, person.color)
    dirs.map(d => spec(s"${p}_idle_$d")) ++
      dirs.flatMap(d =>
        (0 until WalkFrames).map(i => spec(s"${p}_walk_${d}_$i"))
      ) :+ spec(s"${p}_sit")
  }

  /** Every region the game draws, with the size (px) and colour used for its
    * placeholder. Indexed names (`_N`) are animation frames.
    */
  def placeholders: List[PlaceholderSpec] =
    one(Floor, 1, 1, c("d9c6a5")) ++
      one("wall_h", 1, 0.25f, c("4a3b2c")) ++
      one("wall_v", 0.25f, 1, c("4a3b2c")) ++
      one("wall_corner", 0.25f, 0.25f, c("3a2d20")) ++
      one("counter_bottom", 9, 3, c("b0b0b0")) ++
      one("counter_side", 3, 7, c("b0b0b0")) ++
      one("coffee_table", 1, 3, c("8b5a2b")) ++
      one("living_room_table", 2, 2, c("8b5a2b")) ++
      one("front_door_broken", 2, 0.5f, c("7a2e2e")) ++
      one("front_door_fixed", 2, 0.5f, c("6b4423")) ++
      one("bedroom_door", 0.5f, 2, c("6b4423")) ++
      one("fireplace_unlit", 0.5f, 3, c("555555")) ++
      frames("fireplace_lit", 2, 0.5f, 3, c("e25822")) ++
      one("fridge", 2, 3, c("e8f4f8")) ++
      one("washing_machine", 2, 2, c("dfe6ee")) ++
      one("worktable", 2, 4, c("a0522d")) ++
      one("couch", 2, 5, c("4f7942")) ++
      one("sofachair", 2, 1.5f, c("6b8e23")) ++
      one("table", 6, 4, c("c19a6b")) ++
      one("easel", 1, 1, c("deb887")) ++
      one("stove_idle", 3, 2, c("707070")) ++
      one("stove_cooking", 3, 2, c("c87533")) ++
      frames("stove_fire", 2, 3, 2, c("ff4500")) ++
      one("dishwasher_idle", 2, 3, c("9aa5b1")) ++
      one("dishwasher_running", 2, 3, c("5d8aa8")) ++
      one("dishwasher_open", 2, 3, c("c5d1dc")) ++
      characters.flatMap(characterPlaceholders)
}
