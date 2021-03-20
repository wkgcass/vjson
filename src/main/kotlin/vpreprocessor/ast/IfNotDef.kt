package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class IfNotDef(override val context: PreprocessorContext, val name: String) : AbstractAST(), Exp {
  override fun exec(): Boolean {
    return !context.isDefined(name)
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append("ifndef ").append(name)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as IfNotDef

    if (name != other.name) return false

    return true
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
