
/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2007, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |                                         **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$

// generated by genprod on Tue Feb 20 15:57:32 CET 2007

package scala

/** Tuple4 is the canonical representation of a @see Product4 */
case class Tuple4[+T1, +T2, +T3, +T4](_1:T1, _2:T2, _3:T3, _4:T4) 
  extends Product4[T1, T2, T3, T4]  {

   override def toString() = {
     val sb = new compat.StringBuilder
     sb.append('(').append(_1).append(',').append(_2).append(',').append(_3).append(',').append(_4).append(')')
     sb.toString
   }
}
