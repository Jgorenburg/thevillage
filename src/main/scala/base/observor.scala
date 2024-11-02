package Base

import scala.collection.mutable
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
