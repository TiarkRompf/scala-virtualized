## Scala-Virtualized

Scala-Virtualized is an experimental branch of the Scala compiler and standard library that contains a few additions to provide even better support for embedded DSLs (we call that *language virtualization*).

The key features are as follows:

- overloadable while-loops, if-then-else statements, object construction, etc. (not only for-comprehensions)
- extension methods: define new infix-methods on existing types (pimp-my-library with less boilerplate)

Further descriptions and examples are available on the [Scala-Virtualized Wiki](http://github.com/tiarkrompf/scala-virtualized/wiki). 

An accompanying technial paper has even more in-depth information: [pdf preprint](http://lampwww.epfl.ch/~amin/pub/hosc2013.pdf), [published version](http://link.springer.com/article/10.1007%2Fs10990-013-9096-9).

Tutorial source code is available at [github.com/adriaanm/scala-virtualized-tutorial](https://github.com/adriaanm/scala-virtualized-tutorial).

## How to Use it

Here is a sample `build.sbt` file for use with the Simple Build Tool (SBT):

    name := "My Scala-Virtualized Project"
    version := "1.0"
    scalaVersion := "2.10.0-RC1"
    scalaOrganization := "org.scala-lang.virtualized"

Releases of Scala-Virtualized are binary compatible with the corresponding regular Scala release.

Note: The code in this repository is no longer up to date and kept for archival purposes only. Up-to-date sources live in branch `topic-virt` [here](https://github.com/namin/scala/tree/topic-virt).
