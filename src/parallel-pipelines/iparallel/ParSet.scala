/* Implicitly-parallel pipelines
 * 
 * (c) 2011, Philipp Haller
 */

package iparallel

import collection.immutable
import concurrent.ops._
import reflect.SourceLocation

object ParSet {

  /* Factory method for creating implicitly-parallel sets.
   */
  def apply[T](elems: immutable.Set[T]): ParSet[T] =
    new ParSetImpl(elems)

}

trait ParSet[T] extends ParIterable[T] {

  def map[U](f: T => U)(implicit s: SourceLocation): ParSet[U]

  def force(): ParSet[T]

}

class ParSetImpl[T](elements: immutable.Set[T]) extends ParSet[T] { self =>

  class MapNode[U](f: T => U) extends ParSet[U] {
    var forced: Option[ParSetImpl[U]] = None

    // the actual implementation of a parallel map
    def force(): ParSet[U] = {
      // split set into parts
      // TODO: take #processors into account or do adaptive work stealing
      val futures = for (splitter <- self.parallelIterator.split) yield
        future {
          // split task further
          for (s <- splitter.split; elem <- s) yield
            f(elem)
        }
      var newElems = immutable.Set.empty[U]
      for (fut <- futures) {
        newElems ++= fut()
      }
      val impl = new ParSetImpl(newElems)
      forced = Some(impl)
      impl
    }

    def getOrForce: ParSet[U] =
      if (forced.isEmpty) force()
      else forced.get

    def parallelIterator: Splitter[U] =
      getOrForce.parallelIterator

    def map[V](f: U => V)(implicit s: SourceLocation): ParSet[V] =
      getOrForce.map(f)
  }

  def force(): ParSet[T] =
    this

  private def splitInHalf(set: immutable.Set[T]): Splitter[T] = new Splitter[T] {
    val setIter = set.iterator

    def split: Seq[Splitter[T]] = {
      if (set.size % 2 == 0) {
        val left = set take (set.size / 2)
        val right = set drop (set.size / 2)
        Seq(splitInHalf(left), splitInHalf(right))
      } else Seq(splitInHalf(set))
    }

    def hasNext: Boolean = setIter.hasNext

    def next() = setIter.next()
  }

  def parallelIterator: Splitter[T] = {
    splitInHalf(elements)
  }

  def map[U](f: T => U)(implicit s: SourceLocation): ParSet[U] =
    new MapNode(f)

/*
  def newCombiner = new Combiner[T] {
    def combine(other: Combiner[Elem, To]): Combiner[Elem, To] = {
      
    }
  }
*/
}
