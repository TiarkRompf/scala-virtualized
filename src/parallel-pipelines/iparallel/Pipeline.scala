/* Implicitly-parallel pipelines
 * 
 * (c) 2011, Philipp Haller
 */

package iparallel

import collection.immutable
import concurrent.ops._

// this trait is not yet used
trait Combiner[Elem] {
  def combine(other: Combiner[Elem]): Combiner[Elem]
}

object Pipeline {

  def main(args: Array[String]) {
    val set = ParSet(immutable.Set(1, 2, 3, 4))
    val delayed = set map { elem => println(elem); elem + 1 }
    println("now forcing delayed mapped set")
    val forcedSet = delayed.force()
    println(forcedSet)

    val set2 = ParSet(immutable.Set(10, 20, 30, 40))
    val set3 = set2 map { elem => println(elem); elem + 1 }
    val set4 = set3 map { elem => println(elem); elem + 1 }
    println("now forcing doubly-mapped set")
    val forcedSet4 = set4.force()
    println(forcedSet4)

    val map = ParMap(immutable.Set((1, 1), (2, 2), (3, 3), (4, 4)))
    val res = map foreach { (l, r) => println(l + r) }
    println("now forcing delayed foreach on map")
    res.force()
  }

}
