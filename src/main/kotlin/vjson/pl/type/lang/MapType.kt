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

import vjson.cs.LineCol
import vjson.pl.inst.*
import vjson.pl.type.*

open class MapType(private val key: TypeInstance, private val value: TypeInstance) : TypeInstance {
  companion object {
    private val MAP_TO_STRING_STACK_INFO = StackInfo("Map", "toString", LineCol.EMPTY)
  }

  private val keySetType = SetType(key)

  private val constructorDescriptor = object : ExecutableConstructorFunctionDescriptor(
    listOf(ParamInstance(IntType, 0)),
    VoidType,
    FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, refTotal = 1))
  ) {
    override fun execute(ctx: ActionContext, values: ValueHolder) {
      ctx.getCurrentMem().setRef(0, newMap(values.intValue))
    }
  }

  protected open fun newMap(cap: Int): Map<*, *> {
    return HashMap<Any?, Any?>()
  }

  override fun constructor(ctx: TypeContext): FunctionDescriptor {
    return constructorDescriptor
  }

  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    if (name == "put" || name == "get" || name == "remove") {
      return generatedForMap0(key, value, ctx, name)
    }
    return when (name) {
      "size" -> object : ExecutableField(name, IntType, MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          val obj = values.refValue as ActionContext
          val map = obj.getCurrentMem().getRef(0) as Map<*, *>
          values.intValue = map.size
        }
      }
      "keySet" -> object : ExecutableField(name, keySetType, MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          val obj = values.refValue as ActionContext
          val map = obj.getCurrentMem().getRef(0) as Map<*, *>
          val newObj = ActionContext(RuntimeMemoryTotal(refTotal = 1), parent = null)
          newObj.getCurrentMem().setRef(0, map.keys)
          values.refValue = newObj
        }
      }
      "toString" -> object : ExecutableField(
        name,
        ctx.getFunctionDescriptorAsInstance(listOf(), StringType, DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      ) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          val obj = values.refValue as ActionContext
          val map = obj.getCurrentMem().getRef(0) as Map<*, *>
          values.refValue = object : Instruction() {
            override val stackInfo = MAP_TO_STRING_STACK_INFO
            override fun execute0(ctx: ActionContext, values: ValueHolder) {
              values.refValue = map.toString()
            }
          }
        }
      }
      else -> null
    }
  }

  override fun toString(): String {
    return "Map<$key, $value>"
  }
}
