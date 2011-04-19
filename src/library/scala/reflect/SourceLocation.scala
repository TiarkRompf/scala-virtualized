package scala.reflect

trait SourceLocation {
  def line: Int

  def charOffset: Int = 0

  def fileName: String = ""

  override def toString: String =
    fileName + ":" + line + ":" + charOffset
}

object SourceLocation {
  def apply(line: Int, fileName: String): SourceLocation =
    new ConcreteSourceLocation(line, fileName)

  def apply(line: Int, offset: Int, fileName: String): SourceLocation =
    new ConcreteSourceLocation(line, offset, fileName)

  private class ConcreteSourceLocation(override val line: Int,
                                       override val charOffset: Int,
                                       override val fileName: String)
    extends SourceLocation {
    def this(line: Int, file: String) {
      this(line, 0, file)
    }
  }
  
}
