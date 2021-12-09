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

import vjson.cs.LineCol
import vjson.ex.ParserException
import vjson.pl.inst.Instruction
import vjson.pl.type.MemoryAllocator
import vjson.pl.type.TypeContext
import vjson.pl.type.TypeInstance
import vjson.simple.SimpleString

data class Param(
  val name: String,
  val type: Type
) : TypedAST {
  override var lineCol: LineCol = LineCol.EMPTY
  private var ctx: TypeContext = TypeContext(MemoryAllocator())

  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    if (!ctx.hasType(type)) {
      throw ParserException("type of parameter $name (${type}) is not defined", lineCol)
    }
    return ctx.getType(type)
  }

  override fun typeInstance(): TypeInstance {
    return type.typeInstance()
  }

  override fun generateInstruction(): Instruction {
    throw UnsupportedOperationException()
  }

  internal var memIndex: Int = -1

  override fun toString(): String {
    return name + ": " + SimpleString(type.toString()).stringify()
  }
}
