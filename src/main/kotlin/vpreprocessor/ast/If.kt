/*
 * The MIT License
 *
 * Copyright 2021 wkgcass (https://github.com/wkgcass)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package vpreprocessor.ast

import vjson.ex.ParserException
import vjson.util.CastUtils
import vpreprocessor.PreprocessorContext

class If /*#ifndef KOTLIN_NATIVE {{ */ @JvmOverloads/*}}*/ constructor(
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
