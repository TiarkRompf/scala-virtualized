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
    delayed.force()

    val map = ParMap(immutable.Set((1, 1), (2, 2), (3, 3), (4, 4)))
    val res = map foreach { (l, r) => println(l + r) }
    println("now forcing delayed foreach on map")
    res.force()
  }

}
