/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$

package scala.collection
package generic

/** This trait forms part of collections that can be reduced
 *  using a `-=` operator.
 *        
 *  @author   Martin Odersky
 *  @owner   Martin Odersky
 *  @version 2.8
 *  @since   2.8
 *  @define coll shrinkable collection
 *  @define Coll Shrinkable
 */
trait Shrinkable[-A] { 

  /** Removes a single element from this $coll.
   *
   *  @param elem  the element to remove.
   *  @return the $coll itself
   */
  def -=(elem: A): this.type

  /** Removes two or more elements from this $coll.
   *
   *  @param elem1 the first element to remove.
   *  @param elem2 the second element to remove.
   *  @param elems the remaining elements to remove.
   *  @return the $coll itself
   */
  def -=(elem1: A, elem2: A, elems: A*): this.type = {
    this -= elem1 
    this -= elem2
    this --= elems
  }

  /** Removes all elements produced by an iterator from this $coll.
   *
   *  @param iter  the iterator producing the elements to remove.
   *  @return the $coll itself
   */
  def --=(iter: Iterator[A]): this.type = { iter foreach -=; this }

  /** Removes all elements contained in a traversable collection from this $coll.
   *
   *  @param iter  the collection containing the elements to remove.
   *  @return the $coll itself
   */
  def --=(iter: Traversable[A]): this.type = { iter foreach -=; this }
}
  

  

