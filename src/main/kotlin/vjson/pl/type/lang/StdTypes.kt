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

package vjson.pl.type.lang

import vjson.pl.ast.Type
import vjson.pl.inst.*
import vjson.pl.type.*

class StdTypes : Types {
  private val stdObject = ActionContext(RuntimeMemoryTotal(refTotal = 1), null)
  private val consoleObject = ActionContext(RuntimeMemoryTotal(refTotal = 1), null)

  private var outputFunc: ((String) -> Unit)? = null

  init {
    stdObject.getCurrentMem().setRef(0, consoleObject)
    consoleObject.getCurrentMem().setRef(0, object : PrebuiltStackInfoInstruction("std.Console", "log") {
      override fun execute0(ctx: ActionContext, values: ValueHolder) {
        val outputFunc = this@StdTypes.outputFunc
        val str = ctx.getCurrentMem().getRef(0)
        if (outputFunc == null)
          println(str)
        else
          outputFunc(str as String)
      }
    })
  }

  override fun initiateType(ctx: TypeContext, offset: RuntimeMemoryTotal): RuntimeMemoryTotal {
    val stdClass = StdClass()
    ctx.addType(Type("std"), stdClass)
    val consoleClass = ConsoleClass()
    ctx.addType(Type("std.Console"), consoleClass)
    ctx.addVariable(Variable("std", stdClass, false, MemPos(0, ctx.getMemoryAllocator().nextRefIndex())))
    return RuntimeMemoryTotal(offset, intTotal = 1)
  }

  override fun initiateValues(ctx: ActionContext, offset: RuntimeMemoryTotal, values: RuntimeMemory?) {
    ctx.getCurrentMem().setRef(offset.refTotal, stdObject)
  }

  fun setOutput(func: (String) -> Unit) {
    this.outputFunc = func
  }
}

class StdClass : TypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "console" -> Field("console", ctx.getType(Type("std.Console")), MemPos(0, 0), false)
      else -> null
    }
  }
}

class ConsoleClass : TypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "log" -> {
        val type =
          ctx.getFunctionDescriptorAsInstance(
            listOf(ParamInstance(ctx.getType(Type("string")), 0)), ctx.getType(Type("void")),
            FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
          )
        return Field("log", type, MemPos(0, 0), false)
      }
      else -> null
    }
  }
}
