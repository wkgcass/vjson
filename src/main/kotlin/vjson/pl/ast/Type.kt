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
import vjson.pl.type.ArrayTypeInstance
import vjson.pl.type.MemoryAllocator
import vjson.pl.type.TypeContext
import vjson.pl.type.TypeInstance

data class Type(private val name: String) : TypedAST {
  private var ctx: TypeContext = TypeContext(MemoryAllocator())
  private val isArray: Boolean
  private val elementType: Type

  init {
    if (name.contains("[")) {
      isArray = true
      val leftBracketIndex = name.lastIndexOf("[")
      val rightBracketIndex = name.lastIndexOf("]")
      if (leftBracketIndex != name.length - 2) {
        throw ParserException("$name is not a valid array type")
      }
      if (rightBracketIndex != name.length - 1) {
        throw ParserException("$name is not a valid array type")
      }
      elementType = Type(name.substring(0, name.length - 2))
    } else {
      isArray = false
      elementType = this
    }
  }

  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    if (ctx.hasType(this)) {
      return ctx.getType(this)
    }
    if (isArray) {
      val arrayType = ArrayTypeInstance(elementType.check(ctx))
      ctx.addType(this, arrayType)
      return arrayType
    } else {
      throw IllegalStateException("$this is not array type and is not recorded in type context")
    }
  }

  override fun typeInstance(): TypeInstance {
    return ctx.getType(this)
  }

  override fun generateInstruction(): Instruction {
    throw UnsupportedOperationException()
  }

  override fun toString(): String {
    return name
  }
}
