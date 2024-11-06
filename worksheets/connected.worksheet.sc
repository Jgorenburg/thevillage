import scala.reflect.api.TypeTags
import scala.collection.mutable
import scala.collection.mutable.HashSet

trait Actor extends Listener with Subject[Actor] {
  val actor = "actor"
  val story = "story"
  override def receiveUpdate(subject: Actor): Unit = println(subject.actor)

  override def receiveUpdate(subject: Story): Unit = println(subject.story)
}

trait Story extends Listener with Subject[Story] {
  val actor = "actor"
  val story = "story"
  override def receiveUpdate(subject: Actor): Unit = println(subject.actor)

}

trait Listener {
  def receiveUpdate(subject: Actor): Unit = return
  def receiveUpdate(subject: Story): Unit = return

}

trait Subject[S <: (Actor | Story)] {
  this: S =>
  def addListener(listener: Listener): Unit =
    Switchboard.addListener(this, listener)

  def remListener(listener: Listener): Unit =
    Switchboard.remListener(this, listener)

  def notifySwitchboard(): Unit =
    println("notifying")
    Switchboard.notify(this)
}

object Switchboard {

  var actorObs: mutable.Map[Actor, mutable.HashSet[Listener]] =
    mutable.Map()
  var storyObs: mutable.Map[Story, mutable.HashSet[Listener]] =
    mutable.Map()

  def addListener(
      subject: Actor | Story,
      listener: Listener
  ): Unit =
    subject match
      case actor: Actor =>
        actorObs.get(actor) match {
          case Some(stories) => stories.add(listener)
          case None          => actorObs(actor) = mutable.HashSet(listener)
        }
      case story: Story =>
        storyObs.get(story) match {
          case Some(actors) => actors.add(listener)
          case None         => storyObs(story) = mutable.HashSet(listener)
        }

  def remListener(
      subject: Actor | Story,
      listener: Listener
  ): Unit =
    subject match
      case actor: Actor =>
        actorObs.get(actor) match {
          case Some(stories) => stories.remove(listener)
          case None          =>
        }
      case story: Story =>
        storyObs.get(story) match {
          case Some(actors) => actors.remove(listener)
          case None         =>
        }

  def notify(subject: Actor | Story): Unit =
    println("got to board")
    subject match
      case actor: Actor =>
        println("got actor")
        println(actorObs.get(actor))
        actorObs.get(actor) match
          case Some(listeners) => listeners.foreach(_.receiveUpdate(actor))
          case None            =>

      case story: Story =>
        println("got story")
        storyObs.get(story) match
          case Some(listeners) => listeners.foreach(_.receiveUpdate(story))
          case None            =>

}

object salmon extends Actor {}
object trout extends Story with Listener {}
Switchboard.addListener(trout, salmon)
Switchboard.addListener(trout, trout)
salmon.addListener(trout)
salmon.addListener(salmon)

salmon.notifySwitchboard()
trout.notifySwitchboard()

object A {
  var a = 1

  def statechange(x: Int): Boolean = {
    println(x)
    a += 1
    if (a % 2 == 0) {
      b.remove(x + 1)
    }
    return (a % 2 == 0)
  }

  val b = HashSet(1, 2, 3, 4)
}

A.b.filter(A.statechange(_))
1 + 1
