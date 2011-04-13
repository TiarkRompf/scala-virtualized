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

trait Forceable[T] 

trait ParSet[T] extends ParIterable[T] {

  def map[U](f: T => U)(implicit s: SourceLocation): ParSet[U]

  def force(): ParSet[T]

  def foreach[U](f: T => U): Unit

  override def toString(): String = {
    var elems: List[T] = List()
    foreach(elems ::= _)
    "ParSet(" + elems.mkString(",") + ")"
  }

}

/* A node representing a map operation in an execution graph.
 * Here, `input` may be another `MapNode` which produces a `ParSet`, or
 * a materialized or non-materialized `ParSet`.
 */
class MapNode[T, U](val input: ParSet[T], val f: T => U) extends ParSet[U] {
  var forced: Option[ParSetImpl[U]] = None

  // the actual implementation of a parallel map
  def force(): ParSet[U] = {
    // split set into parts
    // TODO: take #processors into account or do adaptive work stealing
    val futures = for (splitter <- input.parallelIterator.split) yield
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

  def map[V](g: U => V)(implicit s: SourceLocation): ParSet[V] = {
    // Instead of forcing the first set, we build an execution graph out of
    // MapNodes.
    //getOrForce.map(f)
    new MapNode(this, g)
  }

  def foreach[V](g: U => V): Unit =
    getOrForce.foreach(g)

  // Implements map fusion.
  def optimized: ParSet[U] = {
    input match {
      case inputNode: MapNode[s, T] =>
        new MapNode(inputNode.input, inputNode.f andThen this.f)
      case _ =>
        this
    }
  }
}

class ParSetImpl[T](elements: immutable.Set[T]) extends ParSet[T] {

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
    new MapNode(this, f)

  def foreach[U](f: T => U): Unit =
    elements.foreach(f)

/*
  def newCombiner = new Combiner[T] {
    def combine(other: Combiner[Elem, To]): Combiner[Elem, To] = {
      
    }
  }
*/
}
