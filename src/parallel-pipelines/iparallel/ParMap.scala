/* Implicitly-parallel pipelines
 * 
 * (c) 2011, Philipp Haller
 */

package iparallel

import collection.immutable
import concurrent.ops._
import concurrent.SyncVar

object ParMap {

  /* Factory method for creating implicitly-parallel maps.
   */
  def apply[T, U](elems: immutable.Set[(T, U)]): ParMap[T, U] =
    new ParMapImpl(elems)

}

trait ParMap[T, U] extends ParIterable[(T, U)] {

  def force(): ParMap[T, U]

  def foreach[V](f: (T, U) => V): ParObject

  // This variant of foreach is inspired by FlumeJava:
  // Instead of returning a parallel map/set, a sink collects
  // produced objects as a side effect.
  //def foreachEmit[V](sink: V => Unit)(f: (T, U) => V): Unit
}

abstract class ParObject {
  def force(): ParObject
}

class ParMapImpl[T, U](override val elements: immutable.Set[(T, U)])
  extends ParMap[T, U] with DivideAndConquerIterable[(T, U)] { self =>

  class ForeachNode[V](f: (T, U) => V) extends ParObject {
    val forced = new SyncVar[ParObject]

    // the actual implementation of a parallel foreach
    def fork(): Unit = {
      // split set into parts
      // TODO: take #processors into account or do adaptive work stealing
      val futures = for (splitter <- self.parallelIterator.split) yield
        future {
          // split task further
          for (s <- splitter.split; elem <- s) yield
            f(elem._1, elem._2)
        }
      var newElems = immutable.Set.empty[V]
      for (fut <- futures) {
        newElems ++= fut()
      }
      forced set this
    }

    def force(): ParObject = {
      fork()
      forced.get
    }
  }

  def force(): ParMap[T, U] =
    this

  def foreach[V](f: (T, U) => V): ParObject = {
    new ForeachNode(f)
  }

//  def map[U](f: T => U): ParSet[U] =
//    new MapNode(this, f)

/*
  def newCombiner = new Combiner[T] {
    def combine(other: Combiner[Elem, To]): Combiner[Elem, To] = {
      
    }
  }
*/
}
