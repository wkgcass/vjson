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
import vjson.pl.inst.BreakInstruction
import vjson.pl.inst.Instruction
import vjson.pl.type.TypeContext

data class BreakStatement(val flag: String? = null) : Statement() {
  override fun copy(): BreakStatement {
    val ret = BreakStatement(flag)
    ret.lineCol = lineCol
    return ret
  }

  override fun checkAST(ctx: TypeContext) {
    val ctxAST = ctx.getContextAST {
      it is ClassDefinition || it is FunctionDefinition ||
        (it is LoopStatement && (flag == null || it.flag == flag))
    }
    if (ctxAST == null || ctxAST !is LoopStatement) {
      if (flag == null) {
        throw ParserException("`break` is not in a loop, current context is $ctxAST", lineCol)
      } else {
        throw ParserException("unable to find loop $flag for `break`", lineCol)
      }
    }
    ctxAST.isInfiniteLoop = false
  }

  override fun generateInstruction(): Instruction {
    if (flag != null) {
      throw UnsupportedOperationException("break with flag is not supported yet")
    }
    return BreakInstruction(1)
  }

  override fun functionTerminationCheck(): Boolean {
    return false
  }

  override fun toString(indent: Int): String {
    return if (flag == null) {
      "break"
    } else {
      "break: $flag"
    }
  }

  override fun toString(): String {
    return toString(0)
  }
}
