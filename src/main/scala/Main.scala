import Snowedin.ControlRoom
import MyLogger.MyLogger

@main def hello(): Unit =
  ControlRoom.runGame(100, true, "expanding")
  println("salmon")

def msg = "I was compiled by Scala 3. :)"
