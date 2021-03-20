package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class StatementSeq(override val context: PreprocessorContext, val seq: List<Statement>) : AbstractAST(), Statement {
  override fun exec(builder: StringBuilder) {
    for (stmt in seq) {
      stmt.exec(builder)
    }
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append("{")
    for (exp in seq) {
      builder.append(" ")
      exp.buildSyntaxString(builder)
    }
    if (seq.isNotEmpty()) {
      builder.append(" ")
    }
    builder.append("}")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as StatementSeq

    if (seq != other.seq) return false

    return true
  }

  override fun hashCode(): Int {
    return seq.hashCode()
  }
}
