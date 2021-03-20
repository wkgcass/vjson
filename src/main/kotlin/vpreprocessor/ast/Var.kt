package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class Var(override val context: PreprocessorContext, val name: String) : AbstractAST(), Exp {
  override fun exec(): Any? {
    TODO("Not yet implemented")
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append(name)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as Var

    if (name != other.name) return false

    return true
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
