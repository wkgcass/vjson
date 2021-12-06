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

data class AccessIndex(val from: Expr, val index: Expr) : AssignableExpr() {
  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    val type = from.check(ctx)
    val elementType = type.elementType(ctx)
    if (elementType == null) {
      throw ParserException("$this: $elementType doesn't have elements")
    }
    val indexType = index.check(ctx)
    if (indexType !is IntType) {
      throw ParserException("$this: typeof $index ($indexType) is not `int`")
    }
    return elementType
  }

  override fun typeInstance(): TypeInstance {
    return from.typeInstance().elementType(ctx)!!
  }

  override fun isModifiable(): Boolean {
    return true
  }

  override fun generateInstruction(): Instruction {
    return when (typeInstance()) {
      is IntType -> GetIndexInt(from.generateInstruction(), index.generateInstruction())
      is LongType -> GetIndexLong(from.generateInstruction(), index.generateInstruction())
      is FloatType -> GetIndexFloat(from.generateInstruction(), index.generateInstruction())
      is DoubleType -> GetIndexDouble(from.generateInstruction(), index.generateInstruction())
      is BoolType -> GetIndexBool(from.generateInstruction(), index.generateInstruction())
      else -> GetIndexRef(from.generateInstruction(), index.generateInstruction())
    }
  }

  override fun generateSetInstruction(valueInst: Instruction): Instruction {
    return when (typeInstance()) {
      is IntType -> SetIndexInt(from.generateInstruction(), index.generateInstruction(), valueInst)
      is LongType -> SetIndexLong(from.generateInstruction(), index.generateInstruction(), valueInst)
      is FloatType -> SetIndexFloat(from.generateInstruction(), index.generateInstruction(), valueInst)
      is DoubleType -> SetIndexDouble(from.generateInstruction(), index.generateInstruction(), valueInst)
      is BoolType -> SetIndexBool(from.generateInstruction(), index.generateInstruction(), valueInst)
      else -> SetIndexRef(from.generateInstruction(), index.generateInstruction(), valueInst)
    }
  }

  override fun toString(): String {
    return "$from[$index]"
  }
}
