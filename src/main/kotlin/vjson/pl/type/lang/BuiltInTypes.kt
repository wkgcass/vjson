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

import vjson.pl.inst.ActionContext
import vjson.pl.inst.Instruction
import vjson.pl.inst.PrebuiltStackInfoInstruction
import vjson.pl.inst.ValueHolder
import vjson.pl.type.*

object BuiltInTypes {
  private val intToString = object : PrebuiltStackInfoInstruction("int", "toString") {
    override fun execute0(ctx: ActionContext, values: ValueHolder) {
      val n = values.intValue
      values.refValue = object : PrebuiltStackInfoInstruction("int", "toString") {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          values.refValue = n.toString()
        }
      }
    }
  }
  private val longToString = object : PrebuiltStackInfoInstruction("long", "toString") {
    override fun execute0(ctx: ActionContext, values: ValueHolder) {
      val n = values.longValue
      values.refValue = object : PrebuiltStackInfoInstruction("long", "toString") {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          values.refValue = n.toString()
        }
      }
    }
  }
  private val floatToString = object : PrebuiltStackInfoInstruction("float", "toString") {
    override fun execute0(ctx: ActionContext, values: ValueHolder) {
      val n = values.floatValue
      values.refValue = object : PrebuiltStackInfoInstruction("float", "toString") {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          values.refValue = n.toString()
        }
      }
    }
  }
  private val doubleToString = object : PrebuiltStackInfoInstruction("double", "toString") {
    override fun execute0(ctx: ActionContext, values: ValueHolder) {
      val n = values.doubleValue
      values.refValue = object : PrebuiltStackInfoInstruction("double", "toString") {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          values.refValue = n.toString()
        }
      }
    }
  }
  private val boolToString = object : PrebuiltStackInfoInstruction("bool", "toString") {
    override fun execute0(ctx: ActionContext, values: ValueHolder) {
      val n = values.boolValue
      values.refValue = object : PrebuiltStackInfoInstruction("bool", "toString") {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          values.refValue = n.toString()
        }
      }
    }
  }

  fun getField(type: TypeInstance, field: String): Instruction {
    return when (type) {
      is IntType -> {
        when (field) {
          "toString" -> intToString
          else -> throw UnsupportedOperationException()
        }
      }
      is LongType -> {
        when (field) {
          "toString" -> longToString
          else -> throw UnsupportedOperationException()
        }
      }
      is FloatType -> {
        when (field) {
          "toString" -> floatToString
          else -> throw UnsupportedOperationException()
        }
      }
      is DoubleType -> {
        when (field) {
          "toString" -> doubleToString
          else -> throw UnsupportedOperationException()
        }
      }
      is BoolType -> {
        when (field) {
          "toString" -> boolToString
          else -> throw UnsupportedOperationException()
        }
      }
      else -> throw UnsupportedOperationException()
    }
  }
}
