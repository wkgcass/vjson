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
import vjson.pl.inst.*
import vjson.pl.type.*

data class NewArray(
  val type: Type,
  val len: Expr,
) : Expr() {
  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    val arrayType = type.check(ctx)
    if (arrayType !is ArrayTypeInstance) {
      throw ParserException("$this: $arrayType is not array type")
    }
    val lenType = len.check(ctx)
    if (lenType !is IntType) {
      throw ParserException("$this: typeof $len ($lenType) is not int")
    }
    return arrayType
  }

  override fun typeInstance(): TypeInstance {
    return type.typeInstance()
  }

  override fun generateInstruction(): Instruction {
    return when (type.typeInstance().elementType(ctx)) {
      is IntType -> NewArrayInt(len.generateInstruction())
      is LongType -> NewArrayLong(len.generateInstruction())
      is FloatType -> NewArrayFloat(len.generateInstruction())
      is DoubleType -> NewArrayDouble(len.generateInstruction())
      is BoolType -> NewArrayBool(len.generateInstruction())
      else -> NewArrayRef(len.generateInstruction())
    }
  }

  override fun toString(): String {
    val typeStr = type.toString()
    val bracketLeft = typeStr.indexOf("[")
    val bracketRight = typeStr.indexOf("]", bracketLeft + 1)
    return "new: ${typeStr.substring(0, bracketLeft + 1)}$len${typeStr.substring(bracketRight)}"
  }
}
