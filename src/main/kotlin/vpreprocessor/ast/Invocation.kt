package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class Invocation(
  override val context: PreprocessorContext, val function: String, val arguments: List<Exp>
) : AbstractAST(), Exp {
  override fun exec(): Any? {
    TODO("Not yet implemented")
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append(function).append("(")
    var isFirst = true
    for (arg in arguments) {
      if (isFirst) {
        isFirst = false
      } else {
        builder.append(", ")
      }
      arg.buildSyntaxString(builder)
    }
    builder.append(")")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as Invocation

    if (function != other.function) return false
    if (arguments != other.arguments) return false

    return true
  }

  override fun hashCode(): Int {
    var result = function.hashCode()
    result = 31 * result + arguments.hashCode()
    return result
  }
}
