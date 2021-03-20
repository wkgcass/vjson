package vpreprocessor.ast

import vjson.ex.ParserException
import vjson.util.CastUtils
import vpreprocessor.PreprocessorContext

class If @JvmOverloads constructor(
  override val context: PreprocessorContext,
  val condition: Exp, val code: StatementSeq, val elseCode: StatementSeq? = null
) : AbstractAST(), Statement {
  override fun exec(builder: StringBuilder) {
    val value = execCondition()
    if (value) {
      code.exec(builder)
    } else {
      elseCode?.exec(builder)
    }
  }

  @Suppress("NOTHING_TO_INLINE")
  private inline
  // #ifdef COVERAGE {{@lombok.Generated}}
  fun execCondition(): Boolean {
    val value = condition.exec()
    if (!CastUtils.check(value, Boolean::class)) {
      throw ParserException("failed casting result of $condition ($value) to Boolean")
    }
    return CastUtils.cast(value)
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    if (!(condition is IfDef || condition is IfNotDef)) {
      builder.append("if ")
    }
    condition.buildSyntaxString(builder)
    builder.append(" ")
    code.buildSyntaxString(builder)
    buildSyntaxStringForElseCode(builder)
  }

  private fun buildSyntaxStringForElseCode(builder: StringBuilder) {
    if (elseCode != null) {
      builder.append(" ")
      if (elseCode.seq.size == 1 && elseCode.seq[0] is If) {
        (CastUtils.cast<If>(elseCode.seq[0])).buildSyntaxStringForElseBranch(builder)
      } else {
        builder.append("else ")
        elseCode.buildSyntaxString(builder)
      }
    }
  }

  private fun buildSyntaxStringForElseBranch(builder: StringBuilder) {
    builder.append("elif ")
    condition.buildSyntaxString(builder)
    builder.append(" ")
    code.buildSyntaxString(builder)
    buildSyntaxStringForElseCode(builder)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as If

    if (condition != other.condition) return false
    if (code != other.code) return false
    if (elseCode != other.elseCode) return false

    return true
  }

  override fun hashCode(): Int {
    var result = condition.hashCode()
    result = 31 * result + code.hashCode()
    result = 31 * result + (elseCode?.hashCode() ?: 0)
    return result
  }
}
