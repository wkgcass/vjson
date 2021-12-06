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
import vjson.pl.type.lang.BuiltInTypes

data class Access
/*#ifndef KOTLIN_NATIVE {{ */ @JvmOverloads/*}}*/
constructor(val name: String, val from: Expr? = null) : AssignableExpr() {
  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    if (from == null) {
      if (!ctx.hasVariable(name)) {
        throw ParserException("$this: variable $name is not defined")
      }
      return ctx.getVariable(name).type // ok
    }
    val fromType = from.check(ctx)
    val fieldType = fromType.field(ctx, name, ctx.getContextType())
    if (fieldType != null) {
      return fieldType.type // ok
    }
    throw ParserException("$this: $fromType doesn't have field `$name`")
  }

  override fun typeInstance(): TypeInstance {
    return if (from == null) {
      ctx.getVariable(name).type
    } else {
      from.typeInstance().field(ctx, name, ctx.getContextType())!!.type
    }
  }

  override fun isModifiable(): Boolean {
    return if (from == null) {
      ctx.getVariable(name).modifiable
    } else {
      val fromType = from.typeInstance()
      val fieldType = fromType.field(ctx, name, ctx.getContextType())!!
      fieldType.modifiable
    }
  }

  override fun generateInstruction(): Instruction {
    return if (from == null) {
      val variable = ctx.getVariable(name)
      when (typeInstance()) {
        is IntType -> GetInt(variable.memPos.depth, variable.memPos.index)
        is LongType -> GetLong(variable.memPos.depth, variable.memPos.index)
        is FloatType -> GetFloat(variable.memPos.depth, variable.memPos.index)
        is DoubleType -> GetDouble(variable.memPos.depth, variable.memPos.index)
        is BoolType -> GetBool(variable.memPos.depth, variable.memPos.index)
        else -> {
          val inst = GetRef(variable.memPos.depth, variable.memPos.index)
          if (variable.type.functionDescriptor(ctx) != null) {
            return FunctionInstance(null, inst)
          }
          inst
        }
      }
    } else {
      buildGetFieldInstruction(ctx, from.generateInstruction(), from.typeInstance(), name)
    }
  }

  override fun generateSetInstruction(valueInst: Instruction): Instruction {
    return if (from == null) {
      val variable = ctx.getVariable(name)
      when (typeInstance()) {
        is IntType -> SetInt(variable.memPos.depth, variable.memPos.index, valueInst)
        is LongType -> SetLong(variable.memPos.depth, variable.memPos.index, valueInst)
        is FloatType -> SetFloat(variable.memPos.depth, variable.memPos.index, valueInst)
        is DoubleType -> SetDouble(variable.memPos.depth, variable.memPos.index, valueInst)
        is BoolType -> SetBool(variable.memPos.depth, variable.memPos.index, valueInst)
        else -> SetRef(variable.memPos.depth, variable.memPos.index, valueInst)
      }
    } else {
      val fromInst = from.generateInstruction()
      val field = from.typeInstance().field(ctx, name, ctx.getContextType())
      val setField = when (field!!.type) {
        is IntType -> SetFieldInt(field.memPos.index, valueInst)
        is LongType -> SetFieldLong(field.memPos.index, valueInst)
        is FloatType -> SetFieldFloat(field.memPos.index, valueInst)
        is DoubleType -> SetFieldDouble(field.memPos.index, valueInst)
        is BoolType -> SetFieldBool(field.memPos.index, valueInst)
        else -> SetFieldRef(field.memPos.index, valueInst)
      }
      object : Instruction() {
        override fun execute0(ctx: ActionContext, values: ValueHolder) {
          fromInst.execute(ctx, values)
          val objCtx = values.refValue as ActionContext
          setField.execute(objCtx, values)
        }
      }
    }
  }

  override fun toString(): String {
    return if (from == null) {
      name
    } else {
      "$from.$name"
    }
  }

  companion object {
    fun buildGetFieldInstruction(ctx: TypeContext, from: Instruction, fromType: TypeInstance, name: String): Instruction {
      val field = fromType.field(ctx, name, ctx.getContextType())
      val getFieldInst = if (field is ExecutableField) {
        ExecutableFieldInstruction(field)
      } else if (fromType is BuiltInTypeInstance) {
        BuiltInTypes.getField(fromType, name)
      } else when (field!!.type) {
        is IntType -> GetFieldInt(field.memPos.index)
        is LongType -> GetFieldLong(field.memPos.index)
        is FloatType -> GetFieldFloat(field.memPos.index)
        is DoubleType -> GetFieldDouble(field.memPos.index)
        is BoolType -> GetFieldBool(field.memPos.index)
        else -> {
          val inst = GetFieldRef(field.memPos.index)
          if (field.type.functionDescriptor(ctx) != null) {
            return FunctionInstance(from, inst)
          }
          inst
        }
      }
      return CompositeInstruction(from, getFieldInst)
    }
  }
}
