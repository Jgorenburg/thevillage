import scala.collection.mutable

var actorObs: mutable.Map[Int, mutable.HashSet[String]] =
  mutable.Map()

actorObs(1) = mutable.HashSet("a")
actorObs(1).add("b")
actorObs.get(1) match
  case Some(v) => v.remove("b")
  case None    =>
actorObs

import scala.reflect._
classTag[Int].runtimeClass

def foo(bar: (Int) => Boolean, v: Int): Boolean = {
  bar(v)
}

print(foo((a: Int) => a == 1, 1))

case class bob(var x: Int, var y: Int) {
  def copy(): bob = new bob(x, y)

}
var a = bob(1, 2)
var b = a.copy()

a.x = 3
b.x

// def blop[A: ClassTag](a: A*): Unit =
//   a match {
//     case _: List[Int]    => print("Int")
//     case _: List[String] => print("String")
//     case _               => print("Other")
//   }

// blop(List(true, false))
// blop(List("ab"))
// blop(List(1, 2, 3))
