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

package vjson.pl.ast

import vjson.ex.ParserException
import vjson.pl.inst.Instruction
import vjson.pl.type.NumericTypeInstance
import vjson.pl.type.TypeContext
import vjson.pl.type.TypeInstance

data class Positive(val expr: Expr) : Expr() {
  override fun copy(): Positive {
    val ret = Positive(expr.copy())
    ret.lineCol = lineCol
    return ret
  }

  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    val exprType = expr.check(ctx)
    if (exprType !is NumericTypeInstance) {
      throw ParserException("$this: $expr ($exprType) is not numeric", lineCol)
    }
    return exprType
  }

  override fun typeInstance(): TypeInstance {
    return expr.typeInstance()
  }

  override fun generateInstruction(): Instruction {
    return expr.generateInstruction()
  }

  override fun toString(indent: Int): String {
    return "+$expr"
  }

  override fun toString(): String {
    return toString(0)
  }
}
