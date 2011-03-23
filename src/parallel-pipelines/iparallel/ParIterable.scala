/* Implicitly-parallel pipelines
 * 
 * (c) 2011, Philipp Haller
 */

package iparallel

import collection.immutable

trait Splitter[T] extends Iterator[T] {
  def split: Seq[Splitter[T]]
}

trait ParIterable[T] /*extends Iterable[T]*/ {
  def parallelIterator: Splitter[T]
  //def newCombiner: Combiner[T]
}

trait DivideAndConquerIterable[T] extends ParIterable[T] { self =>

  def elements: immutable.Set[T]

  private def splitInHalf(set: immutable.Set[T]): Splitter[T] = new Splitter[T] {
    val setIter = set.iterator

    def split: Seq[Splitter[T]] = {
      val n = set.size
      if (n % 2 == 0) {
        val left = set take (n / 2)
        val right = set drop (n / 2)
        Seq(splitInHalf(left), splitInHalf(right))
      } else Seq(splitInHalf(set))
    }

    def hasNext: Boolean =
      setIter.hasNext

    def next() =
      setIter.next()
  }

  def parallelIterator: Splitter[T] =
    splitInHalf(elements)
}
