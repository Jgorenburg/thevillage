import Snowedin.ControlRoom
import MyLogger.MyLogger

@main def hello(): Unit =
  ControlRoom.runGame(120, true, "wSon")
  println("salmon")

def msg = "I was compiled by Scala 3. :)"
