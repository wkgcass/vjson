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

import vjson.JSON
import vjson.pl.inst.Instruction
import vjson.pl.inst.LiteralDouble
import vjson.pl.inst.LiteralFloat
import vjson.pl.type.DoubleType
import vjson.pl.type.TypeContext
import vjson.pl.type.TypeInstance

data class FloatLiteral(val n: JSON.Double) : Expr() {
  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    return DoubleType
  }

  override fun typeInstance(): TypeInstance {
    return DoubleType
  }

  override fun generateInstruction(): Instruction {
    return LiteralDouble(n.doubleValue())
  }

  override fun toString(): String {
    return "" + n.stringify()
  }
}
