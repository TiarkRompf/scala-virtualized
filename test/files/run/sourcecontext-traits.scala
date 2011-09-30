import annotation.sourceContext
import reflect.SourceContext

trait DSLTrait @sourceContext() {

  def relative(name: String) = {
    val lastSlash = name.lastIndexOf('/')
    if (lastSlash == -1)
      name.substring(name.lastIndexOf('\\') + 1)
    else
      name.substring(lastSlash + 1)
  }

  def printInfo(m: SourceContext) {
    val parentContext: SourceContext = {
      var current = m
      while (!current.parent.isEmpty)
        current = current.parent.get
      current
    }
    println("line: "+parentContext.line)
    println("method name: "+parentContext.methodName)
    println("file name: "+relative(parentContext.fileName))
  }
  
  def m1(x: Int) {
    printInfo(implicitly[SourceContext])
  }

  def m2(x: Int): Int = {
    printInfo(implicitly[SourceContext])
    x
  }

  def m3[T](x: T) {
    printInfo(implicitly[SourceContext])
  }

  def m4[T](x: T): T = {
    printInfo(implicitly[SourceContext])
    x
  }

  def m5[T: Manifest](x: T): T = {
    printInfo(implicitly[SourceContext])
    x
  }
}

object Test extends App {
  val obj = new DSLTrait {}
  obj.m1(7)
  obj.m2(7)
  obj.m3(7)
  obj.m4(7)
  obj.m5(7)
}
