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
import vjson.pl.inst.NoOp
import vjson.pl.type.*

data class ClassDefinition(
  val name: String,
  val params: List<Param>,
  val code: List<Statement>
) : Statement(), MemoryAllocatorProvider {
  private var memDepth: Int = -1
  private val memoryAllocator = MemoryAllocator()

  override fun checkAST(ctx: TypeContext) {
    memDepth = ctx.getMemoryDepth()
    if (ctx.hasTypeInThisContext(Type(name))) {
      throw ParserException("type `$name` is already defined")
    }
    val thisType = ClassTypeInstance(this)
    ctx.addType(Type(name), thisType)
    val codeCtx = TypeContext(ctx, thisType, this)
    for (param in params) {
      val paramType = param.check(ctx)
      param.memIndex = memoryAllocator.nextIndexFor(paramType)
      codeCtx.addVariable(Variable(param.name, paramType, true, MemPos(codeCtx.getMemoryDepth(), param.memIndex)))
    }
    codeCtx.checkStatements(code)
  }

  override fun functionTerminationCheck(): Boolean {
    return false
  }

  override fun memoryAllocator(): MemoryAllocator {
    return memoryAllocator
  }

  override fun generateInstruction(): Instruction {
    return NoOp()
  }

  fun getMemDepth(): Int {
    return memDepth
  }

  override fun toString(): String {
    val sb = StringBuilder()
    sb.append("class ").append(name).append(": { ")
    sb.append(params.joinToString(prefix = " { ", postfix = " } "))
    sb.append("do: { ")
    sb.append(code.joinToString())
    sb.append(" }")
    return sb.toString()
  }
}
