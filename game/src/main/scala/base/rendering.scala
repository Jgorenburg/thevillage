package Base

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Coarse draw layers. Within `Sorted`, things are ordered by y (see
  * [[DrawOrder]]).
  */
object DrawLayer {
  val Floor = 0 // floor tiles
  val Walls = 1 // house outline and interior walls
  val Ground = 2 // flat furniture that other things stand on (e.g. counter)
  val Sorted = 3 // furniture and characters, y-sorted
}

object DrawOrder {

  /** Sorts by layer (ascending), then by y descending: a higher y is farther
    * back in the top-down view, so it is drawn first and ends up behind things
    * nearer the bottom of the screen. The sort is stable, so ties keep their
    * input order.
    */
  def sort[T](items: Seq[T])(layer: T => Int, y: T => Float): Seq[T] =
    items.sortBy(t => (layer(t), -y(t)))

  def sortRenderables[T <: Renderable](items: Seq[T]): Seq[T] =
    sort(items)(_.drawLayer, _.sortY)
}

/** Where to place the fixed-resolution world on screen. */
case class ScreenFit(scale: Int, x: Int, y: Int, width: Int, height: Int)

object ScreenFit {

  /** Largest whole-number scale at which a `virtW` x `virtH` image fits in the
    * screen (never below 1), centred. If centring would overlap the
    * `reservedLeft` column (used by the UI panels) and there is room to its
    * right, the image is pushed right just enough to clear it.
    */
  def compute(
      screenW: Int,
      screenH: Int,
      virtW: Int,
      virtH: Int,
      reservedLeft: Int = 0
  ): ScreenFit = {
    val scale = math.max(1, math.min(screenW / virtW, screenH / virtH))
    val w = virtW * scale
    val h = virtH * scale
    val centredX = (screenW - w) / 2
    val x =
      if (centredX < reservedLeft && reservedLeft + w <= screenW) reservedLeft
      else centredX
    ScreenFit(scale, x, (screenH - h) / 2, w, h)
  }
}

/** Helpers for drawing atlas regions at grid (tile) positions. */
object Draw {

  /** Seconds since start; drives looping object animations. */
  var elapsed: Float = 0f

  /** Draws `region` with its bottom-left corner at tile (`x`, `y`), scaled to
    * `w` x `h` tiles.
    */
  def tiles(
      batch: SpriteBatch,
      region: TextureRegion,
      x: Float,
      y: Float,
      w: Float,
      h: Float,
      flipX: Boolean = false
  ): Unit = {
    val (rx, ry) = BoxCoords.posToRealLocation(new BoxCoords(x, y))
    val pw = w * BoxCoords.boxSize
    val ph = h * BoxCoords.boxSize
    if (flipX) batch.draw(region, rx + pw, ry, -pw, ph)
    else batch.draw(region, rx, ry, pw, ph)
  }

  /** Draws the named region (first frame) at tile (`x`, `y`). */
  def named(
      batch: SpriteBatch,
      name: String,
      x: Float,
      y: Float,
      w: Float,
      h: Float
  ): Unit = tiles(batch, Assets.region(name), x, y, w, h)

  /** Draws the current frame of the named looping animation. A name with a
    * single frame behaves like [[named]], so art can add frames later without
    * code changes.
    */
  def animated(
      batch: SpriteBatch,
      name: String,
      x: Float,
      y: Float,
      w: Float,
      h: Float,
      frameDuration: Float = 0.25f
  ): Unit = tiles(
    batch,
    Assets.animation(name, frameDuration).getKeyFrame(elapsed, true),
    x,
    y,
    w,
    h
  )
}

/** An axis-aligned wall along grid lines, in tile coordinates. */
case class WallSegment(x1: Float, y1: Float, x2: Float, y2: Float) {
  def horizontal: Boolean = y1 == y2
}

object Walls {
  // Wall thickness in virtual pixels
  val Thickness = 4f

  /** Draws each segment as a strip of `wall_h` / `wall_v` tiles plus
    * `wall_corner` caps, kept inside the world bounds `maxX` x `maxY` (virtual
    * pixels) so the outer walls are not clipped by the framebuffer.
    */
  def render(
      batch: SpriteBatch,
      segments: Seq[WallSegment],
      maxX: Float,
      maxY: Float
  ): Unit = {
    val bs = BoxCoords.boxSize
    val t = Thickness
    def clampX(v: Float) = math.max(0f, math.min(maxX - t, v))
    def clampY(v: Float) = math.max(0f, math.min(maxY - t, v))
    val hWall = Assets.region("wall_h")
    val vWall = Assets.region("wall_v")
    val corner = Assets.region("wall_corner")

    segments.foreach { s =>
      val (ox, oy) = BoxCoords.housePos
      if (s.horizontal) {
        val y = clampY(oy + s.y1 * bs - t / 2)
        val from = math.min(s.x1, s.x2).toInt
        val to = math.max(s.x1, s.x2).toInt
        (from until to).foreach(i => batch.draw(hWall, ox + i * bs, y, bs, t))
        batch.draw(corner, clampX(ox + from * bs - t / 2), y, t, t)
        batch.draw(corner, clampX(ox + to * bs - t / 2), y, t, t)
      } else {
        val x = clampX(ox + s.x1 * bs - t / 2)
        val from = math.min(s.y1, s.y2).toInt
        val to = math.max(s.y1, s.y2).toInt
        (from until to).foreach(i => batch.draw(vWall, x, oy + i * bs, t, bs))
        batch.draw(corner, x, clampY(oy + from * bs - t / 2), t, t)
        batch.draw(corner, x, clampY(oy + to * bs - t / 2), t, t)
      }
    }
  }
}

/** Body pose used to pick a character sprite. */
enum Pose {
  case Stand, Sit
}

/** Which atlas region/animation to draw for a character. */
case class SpriteChoice(name: String, flipX: Boolean, animated: Boolean)

object PersonSprites {

  /** Seconds per walk frame */
  val WalkFrameDuration = 0.12f

  // Characters are one tile wide and 1.5 tiles (16x24 px) tall
  val WidthTiles = 1f
  val HeightTiles = 1.5f

  def facingName(dir: Direction.Dir): String = dir match
    case Direction.Down                   => "down"
    case Direction.Up                     => "up"
    case Direction.Left | Direction.Right => "side"

  /** Pure mapping from a character's visible state to a region name:
    *   - moving: `<prefix>_walk_<down|up|side>` (animated, flipped for left)
    *   - sitting: `<prefix>_sit`
    *   - otherwise: `<prefix>_idle_<down|up|side>` using the last facing
    */
  def choose(
      prefix: String,
      moving: Option[Direction.Dir],
      pose: Pose,
      facing: Direction.Dir
  ): SpriteChoice = moving match
    case Some(dir) =>
      SpriteChoice(
        s"${prefix}_walk_${facingName(dir)}",
        dir == Direction.Left,
        animated = true
      )
    case None if pose == Pose.Sit =>
      SpriteChoice(s"${prefix}_sit", false, animated = false)
    case None =>
      SpriteChoice(
        s"${prefix}_idle_${facingName(facing)}",
        facing == Direction.Left,
        animated = false
      )

  def spriteName(person: Person): SpriteChoice =
    choose(
      person.spritePrefix,
      person.spriteState.moving,
      person.storyPose(person.getCurStory()),
      person.spriteState.facing
    )
}

/** Render-only bookkeeping for a character; never read by the simulation. */
class PersonSpriteState {
  var lastLocation: (Float, Float) = (Float.NaN, Float.NaN)
  var moving: Option[Direction.Dir] = None
  var facing: Direction.Dir = Direction.Down
  var currentName: String = ""
  var stateTime: Float = 0f
}

/** Direction of a small movement delta, or None if there was no movement. */
object Motion {
  def direction(dx: Float, dy: Float): Option[Direction.Dir] =
    if (dx == 0f && dy == 0f) None
    else if (math.abs(dx) >= math.abs(dy))
      Some(if (dx < 0) Direction.Left else Direction.Right)
    else Some(if (dy < 0) Direction.Down else Direction.Up)
}

/** Colour/size description of one placeholder image, used by the placeholder
  * generator (`./millw game.genPlaceholders`).
  */
case class PlaceholderSpec(name: String, width: Int, height: Int, color: Color)

/** Shared debug-drawing helpers. */
object DebugDraw {
  def grid(shapeRenderer: ShapeRenderer, horiz: Int, vert: Int): Unit = {
    val bs = BoxCoords.boxSize
    val (ox, oy) = BoxCoords.housePos
    (0 to horiz).foreach { i =>
      if (i % 5 == 0) shapeRenderer.setColor(1, 0, 0, .5f)
      else shapeRenderer.setColor(.7f, .7f, .7f, 1)
      shapeRenderer.line(ox + i * bs, oy, ox + i * bs, oy + vert * bs)
    }
    (0 to vert).foreach { j =>
      if (j % 5 == 0) shapeRenderer.setColor(1, 0, 0, .5f)
      else shapeRenderer.setColor(.7f, .7f, .7f, 1)
      shapeRenderer.line(ox, oy + j * bs, ox + horiz * bs, oy + j * bs)
    }
    shapeRenderer.setColor(0, 0, 0, 1)
  }
}
