import Snowedin.ControlRoom
import MyLogger.MyLogger

@main def hello(): Unit =
  ControlRoom.runGame(120, true, "lunch")
  println("salmon")

def msg = "I was compiled by Scala 3. :)"
