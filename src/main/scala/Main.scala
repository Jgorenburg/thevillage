import Snowedin.ControlRoom
import MyLogger.MyLogger

@main def hello(): Unit =
  ControlRoom.runGame(30, true, "firstlog")
  println("salmon")

def msg = "I was compiled by Scala 3. :)"
