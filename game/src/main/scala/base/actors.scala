package Base

import Base.Importance.shouldInterrupt
import scala.collection.mutable.{HashMap, LinkedHashMap}
import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import Base.BoxCoords.boxSize
import Base.Room.Bedroom

case class curStory(
    var curStory: Story,
    var startTime: Int
) {
  def copy(): curStory = { new curStory(curStory, startTime) }
  override def toString(): String =
    s"Current Story: ${curStory.getClass.getSimpleName.stripSuffix("$")}, Start Time: ${startTime}"
}

// for objects that can only take a certain number of charecters
trait Spaces {
  self: Actor =>

  val maxCapacity: Int
  var curCapacity: Int

  val occupiers: HashMap[Occupy, Int] = HashMap()
  // Story -> starttime
  val activeEvents: LinkedHashMap[Story, Int] = LinkedHashMap()

  def hasSpace(size: Int): Boolean = size <= curCapacity
  def hasSpace(story: Occupy): Boolean = hasSpace(story.size)
  def occupy(size: Int): Unit = curCapacity -= size
  def occupy(story: Story, size: Int = -1): Unit = {
    if (activeEvents.contains(story)) {
      leave(story)
    }
    activeEvents += (story -> GameManager.tick)

    val occ = story.asInstanceOf[Occupy]
    var taking = if (size != -1) size else occ.size
    occupiers += (occ -> taking)
    occupy(taking)
  }
  def leave(size: Int): Unit = curCapacity += size
  def leave(story: Story): Unit = {
    val occ = story.asInstanceOf[Occupy]
    leave(if (occupiers.contains(occ)) occupiers(occ) else occ.size)
    occupiers.remove(occ)
    activeEvents.remove(story)
  }

  def vacate() = {
    curCapacity = maxCapacity
    occupiers.clear()
  }

}

trait Renderable {
  var location: BoxCoords
  var interactLoc: BoxCoords
  def rloc() = location.toRealLocation()

  /** Draws the sprite for this thing. The batch is already begun and uses the
    * world (virtual pixel) projection.
    */
  def render(batch: SpriteBatch): Unit

  /** Draws the debug outline (the original shape rendering). Toggled with F1.
    */
  def renderDebug(shapeRenderer: ShapeRenderer): Unit

  /** Coarse draw layer, see [[DrawLayer]]. */
  def drawLayer: Int = DrawLayer.Sorted

  /** Y used for depth sorting within a layer (higher y is drawn first). */
  def sortY: Float = location.y
}

// for things that aren't interacted with, but still exist
trait Static extends Renderable

trait Actor extends Subject[Actor] with Listener with Renderable {
  // common state is for things every actor has
  // common state:
  //    1: current activity
  //    2: activity start time
  var commonState: curStory = (Vibe, 0)
  var interrupted: curStory = commonState.copy()

  var room: Room = Room.Bedroom

  lazy val myEvents: Array[Any]

  def getCurStory(): Story = commonState.curStory
  def getCurStoryImportance(): Importance.Importance =
    commonState.curStory.importance

  def beginStory(story: Story, tick: Int): Unit = {
    interrupted = commonState.copy()
    if (shouldInterrupt(story)) {
      interrupted.curStory.interruptStory(tick)
    }

    commonState.curStory = story
    commonState.startTime = tick

    actorSpecificBeginning(tick)
  }
  def actorSpecificBeginning(tick: Int): Unit

  def tick(tick: Int): Unit

  def endStory(tick: Int): Unit = {
    actorSpecificEnding(tick)
    if (commonState.curStory.importance == Importance.Instantaneous) {
      commonState = interrupted.copy()
    } else if (
      this
        .isInstanceOf[Spaces] && !this.asInstanceOf[Spaces].activeEvents.isEmpty
    ) {
      commonState.curStory = this.asInstanceOf[Spaces].activeEvents.head._1
      commonState.startTime = this.asInstanceOf[Spaces].activeEvents.head._2
    } else {
      commonState.curStory = Vibe
      commonState.startTime = tick
    }
  }
  def actorSpecificEnding(tick: Int): Unit

  def shouldInterrupt(newStory: Story): Boolean = {
    if (GameManager.characters.contains(this)) {
      return true
    } else if (this.isInstanceOf[Spaces] && newStory.isInstanceOf[Occupy]) {
      return newStory.asInstanceOf[Occupy].size >
        this.asInstanceOf[Spaces].curCapacity
    }
    return true
  }

  def interruptStory(tick: Int): Unit = {
    actorSpecificInterrupt(tick)

    commonState.curStory = Vibe
    commonState.startTime = tick
  }

  def actorSpecificInterrupt(tick: Int): Unit

  // TODO: improve resets
  def reset(): Unit
  def defaultReset(): Unit = {
    commonState = (Vibe, 0)
    interrupted = (Vibe, 0)
    room = Room.Bedroom
  }

  def log(): String

  implicit def actorcommonState_to_tuple(
      cs: curStory
  ): (Story, Int) =
    (cs.curStory, cs.startTime)

  implicit def tuple_to_actorcommonstate(
      t: (Story, Int)
  ): curStory = curStory(t._1, t._2)

}

trait Movement {

  var location: BoxCoords = Globals.bedloc
  var destination: BoxCoords = location
  lazy val speed: Float =
    Globals.secsPerTick / 8f // (2 * GlobalVars.secsPerTick)

  def move(dir: Direction.Dir, percent: Float) = {
    val mpercent = math.min(percent, 1f)
    dir match
      case Direction.Left  => moveLeft(mpercent)
      case Direction.Right => moveRight(mpercent)
      case Direction.Down  => moveDown(mpercent)
      case Direction.Up    => moveUp(mpercent)
  }
  def moveLeft(percent: Float): Unit = location =
    (location._1 - percent, location._2)
  def moveRight(percent: Float): Unit = location =
    (location._1 + percent, location._2)
  def moveDown(percent: Float): Unit = location =
    (location._1, location._2 - percent)
  def moveUp(percent: Float): Unit = location =
    (location._1, location._2 + percent)

  var movementStack: List[Direction.Dir] = List()
  var stepPercent: Float = 0
}

trait Person extends Actor with Movement {
  var wakeTime: Double = 0
  var bedTime = GameManager.ending
  def timeForBed(): Boolean = {
    return GameManager.tick >= bedTime
  }
  var interactLoc = location
  var traveling: Boolean = false

  def walk(): Boolean = {
    var stepSpeed = speed
    while (stepSpeed > 0) {
      if (movementStack.isEmpty) {
        location = destination
        return true
      }
      val step = movementStack.head
      var moveSpeed = math.min(stepSpeed, 1f - stepPercent)
      move(step, moveSpeed)
      stepPercent += moveSpeed
      if (stepPercent >= 1) {
        movementStack = movementStack.drop(1)
        stepPercent = 0f
      }
      stepSpeed -= moveSpeed
    }

    return false
  }

  def adjustForStart(): Unit = {
    location = (location.x.toInt, location.y.toInt)
    stepPercent = 0
  }

  def makePathFromLocToDest(): Unit = {
    movementStack = makePath(location, destination)
    adjustForStart()
  }

  def makePath(
      start: BoxCoords,
      end: BoxCoords
  ): List[Direction.Dir] = {
    GameManager.pathfinder.makePath(location, destination)
  }

  def setDestination(pos: BoxCoords): Unit = {
    destination = pos
    makePathFromLocToDest()

  }

  // TODO: Is this still possible?
  // Current - just apes setDestination
  def setDestinationNoAdjust(pos: BoxCoords): Unit = {
    setDestination(pos)
  }

  val color: Color

  /** Prefix of this character's atlas regions, e.g. `father`. */
  def spritePrefix: String =
    getClass.getSimpleName.stripSuffix("$").toLowerCase

  /** Pose to show while arrived at a story. Overridden per game. */
  def storyPose(story: Story): Pose = Pose.Stand

  /** Render-only state (facing, animation time); not used by the sim. */
  val spriteState: PersonSpriteState = new PersonSpriteState()

  /** Updates render-only animation state from how the character moved since the
    * last frame. Call once per frame after the simulation step.
    */
  def updateSprite(delta: Float): Unit = {
    val s = spriteState
    val (x, y) = (location.x, location.y)
    val observed =
      if (s.lastLocation._1.isNaN) None
      else Motion.direction(x - s.lastLocation._1, y - s.lastLocation._2)
    // Prefer the planned step; fall back to the observed delta (the last
    // step has already been popped when the character arrives)
    s.moving = observed.map(d => movementStack.headOption.getOrElse(d))
    s.moving.foreach(d => s.facing = d)
    s.lastLocation = (x, y)

    val name = PersonSprites.spriteName(this).name
    if (name != s.currentName) {
      s.currentName = name
      s.stateTime = 0f
    } else {
      s.stateTime += delta
    }
  }

  def render(batch: SpriteBatch): Unit = {
    if (room == Bedroom) return
    val choice = PersonSprites.spriteName(this)
    val frame =
      if (choice.animated)
        Assets
          .animation(choice.name, PersonSprites.WalkFrameDuration)
          .getKeyFrame(spriteState.stateTime, true)
      else Assets.region(choice.name)
    Draw.tiles(
      batch,
      frame,
      location.x,
      location.y,
      PersonSprites.WidthTiles,
      PersonSprites.HeightTiles,
      choice.flipX
    )
  }

  def renderDebug(shapeRenderer: ShapeRenderer) = {
    if (room == Bedroom) return
    shapeRenderer.setColor(color)
    // Squares draw from bottom left but circles from the center,
    // the 0.5 fixes the disconnect
    shapeRenderer.circle(
      rloc()._1 + 0.5f * boxSize,
      rloc()._2 + 0.5f * boxSize,
      boxSize / 2
    )
    shapeRenderer.setColor(0, 0, 0, 1)
  }

  def report(
      font: BitmapFont,
      batch: SpriteBatch,
      loc: (Float, Float),
      indivPortion: String = ""
  ): Unit = {
    def simpleName(obj: Any) = obj.getClass.getSimpleName.stripSuffix("$")
    font.draw(
      batch,
      (s"[#${color}]${simpleName(this)}:[BLACK]\n\tCurrent Story: ${simpleName(
          this.commonState.curStory
        )}\n\tLocation: ${this.location}\n\tRoom: ${this.room}" + indivPortion)
        // The default font has no tab glyph (it drew a box)
        .replace("\t", "    "),
      loc._1,
      loc._2
    )
  }
}
