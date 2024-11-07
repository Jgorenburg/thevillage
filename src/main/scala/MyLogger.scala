package MyLogger

import scala.util.Using
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.File
import Base.Actor
import Base.Story
import scala.collection.mutable.HashSet

object MyLogger {
  var file = "logs/unnamed.txt"
  var line = ""

  def printHeader(actors: List[Actor]) = {
    var content = "Active Stories;" + actors
      .map(_.getClass.getSimpleName.stripSuffix("$"))
      .mkString(";")
    Using(BufferedWriter(FileWriter(File(file), false))) { bufferedWriter =>
      bufferedWriter.write(content)
      bufferedWriter.newLine()
    }
  }

  def addToLine(frag: String) = line += frag + ";"

  def addActiveStories(stories: HashSet[Story]) = addToLine(
    stories.map(_.getClass.getSimpleName.stripSuffix("$")).mkString(",")
  )

  def printLine() = {
    Using(BufferedWriter(FileWriter(File(file), true))) { bufferedWriter =>
      bufferedWriter.write(line)
      bufferedWriter.newLine()
    }
    line = ""
  }

  def setFile(filename: String) = file = "logs/" + filename + ".txt"
}
