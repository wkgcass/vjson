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
import vjson.pl.ast.BinOpType.*
import vjson.pl.inst.*
import vjson.pl.type.*

data class BinOp(
  val op: BinOpType,
  val left: Expr,
  val right: Expr,
) : Expr() {
  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    val leftType = left.check(ctx)
    val rightType = right.check(ctx)
    return when (op) {
      PLUS, MINUS, MULTIPLY, DIVIDE, CMP_GT, CMP_GE, CMP_LT, CMP_LE -> {
        if (op == PLUS && (leftType is StringType || rightType is StringType)) {
          if (leftType !is StringType || rightType !is StringType) {
            val typeToStringCheck: TypeInstance
            val variableToStringCheck: Expr
            if (leftType !is StringType) {
              typeToStringCheck = leftType
              variableToStringCheck = left
            } else {
              typeToStringCheck = rightType
              variableToStringCheck = right
            }
            val toStringField = typeToStringCheck.field(ctx, "toString", ctx.getContextType())
              ?: throw ParserException("$this: cannot concat string, $variableToStringCheck ($typeToStringCheck) does not have `toString` field")
            val toStringFunc = toStringField.type.functionDescriptor(ctx)
              ?: throw ParserException("$this: cannot concat string, $variableToStringCheck ($typeToStringCheck) `toString` field is not a function")
            if (toStringFunc.params.isNotEmpty())
              throw ParserException("$this: cannot concat string, $variableToStringCheck ($typeToStringCheck) `toString` function parameters list is not empty")
            if (toStringFunc.returnType !is StringType) {
              throw ParserException("$this: cannot concat string, $variableToStringCheck ($typeToStringCheck) `toString` function return type (${toStringField.type}) is not $StringType")
            }
          }
          StringType
        } else {
          if (leftType != rightType) {
            throw ParserException("$this: cannot calculate $leftType $op $rightType, type mismatch")
          }

          if (leftType != IntType && leftType != LongType && leftType != DoubleType) {
            throw ParserException("$this: cannot calculate $leftType $op $rightType, not numeric values")
          }
          when (op) {
            PLUS, MINUS, MULTIPLY, DIVIDE -> leftType
            else -> BoolType
          }
        }
      }
      LOGIC_AND, LOGIC_OR -> {
        if (leftType != BoolType) {
          throw ParserException("$this: cannot calculate $leftType $op $rightType, not boolean values")
        }
        if (rightType != BoolType) {
          throw ParserException("$this: cannot calculate $leftType $op $rightType, not boolean values")
        }
        BoolType
      }
      CMP_NE, CMP_EQ -> {
        if (leftType != rightType) {
          throw ParserException("$this: cannot calculate $leftType $op $rightType, type mismatch")
        }
        BoolType
      }
    }
  }

  override fun typeInstance(): TypeInstance {
    return when (op) {
      PLUS -> if (left.typeInstance() is StringType || right.typeInstance() is StringType) StringType else left.typeInstance()
      MINUS, MULTIPLY, DIVIDE -> left.typeInstance()
      else -> BoolType
    }
  }

  override fun generateInstruction(): Instruction {
    val lType = left.typeInstance()
    val rType = right.typeInstance()
    val leftInst = left.generateInstruction()
    val rightInst = right.generateInstruction()
    return when (op) {
      MULTIPLY -> when (lType) {
        is IntType -> MultiplyInt(leftInst, rightInst)
        is LongType -> MultiplyLong(leftInst, rightInst)
        is FloatType -> MultiplyFloat(leftInst, rightInst)
        is DoubleType -> MultiplyDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      DIVIDE -> when (lType) {
        is IntType -> DivideInt(leftInst, rightInst)
        is LongType -> DivideLong(leftInst, rightInst)
        is FloatType -> DivideFloat(leftInst, rightInst)
        is DoubleType -> DivideDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      PLUS -> {
        if (lType is StringType || rType is StringType) {
          if (lType is StringType && rType is StringType) {
            StringConcat(leftInst, rightInst)
          } else {
            val toStringFuncInst = Access.buildGetFieldInstruction(
              ctx,
              (if (lType is StringType) rightInst else leftInst),
              (if (lType is StringType) rType else lType),
              "toString"
            )
            val callToStringFuncInst = buildToStringInstruction(ctx, (if (lType is StringType) rType else lType), toStringFuncInst)
            if (lType is StringType)
              StringConcat(leftInst, callToStringFuncInst)
            else
              StringConcat(callToStringFuncInst, rightInst)
          }
        } else
          when (lType) {
            is IntType -> PlusInt(leftInst, rightInst)
            is LongType -> PlusLong(leftInst, rightInst)
            is FloatType -> PlusFloat(leftInst, rightInst)
            is DoubleType -> PlusDouble(leftInst, rightInst)
            else -> throw IllegalStateException("$lType $op $rType")
          }
      }
      MINUS -> when (lType) {
        is IntType -> MinusInt(leftInst, rightInst)
        is LongType -> MinusLong(leftInst, rightInst)
        is FloatType -> MinusFloat(leftInst, rightInst)
        is DoubleType -> MinusDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      CMP_GT -> when (lType) {
        is IntType -> CmpGTInt(leftInst, rightInst)
        is LongType -> CmpGTLong(leftInst, rightInst)
        is FloatType -> CmpGTFloat(leftInst, rightInst)
        is DoubleType -> CmpGTDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      CMP_GE -> when (lType) {
        is IntType -> CmpGEInt(leftInst, rightInst)
        is LongType -> CmpGELong(leftInst, rightInst)
        is FloatType -> CmpGEFloat(leftInst, rightInst)
        is DoubleType -> CmpGEDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      CMP_LT -> when (lType) {
        is IntType -> CmpLTInt(leftInst, rightInst)
        is LongType -> CmpLTLong(leftInst, rightInst)
        is FloatType -> CmpLTFloat(leftInst, rightInst)
        is DoubleType -> CmpLTDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      CMP_LE -> when (lType) {
        is IntType -> CmpLEInt(leftInst, rightInst)
        is LongType -> CmpLELong(leftInst, rightInst)
        is FloatType -> CmpLEFloat(leftInst, rightInst)
        is DoubleType -> CmpLEDouble(leftInst, rightInst)
        else -> throw IllegalStateException("$lType $op $rType")
      }
      CMP_NE -> when (lType) {
        is IntType -> CmpNEInt(leftInst, rightInst)
        is LongType -> CmpNELong(leftInst, rightInst)
        is FloatType -> CmpNEFloat(leftInst, rightInst)
        is DoubleType -> CmpNEDouble(leftInst, rightInst)
        is BoolType -> CmpNEBool(leftInst, rightInst)
        else -> CmpNERef(leftInst, rightInst)
      }
      CMP_EQ -> when (lType) {
        is IntType -> CmpEQInt(leftInst, rightInst)
        is LongType -> CmpEQLong(leftInst, rightInst)
        is FloatType -> CmpEQFloat(leftInst, rightInst)
        is DoubleType -> CmpEQDouble(leftInst, rightInst)
        is BoolType -> CmpEQBool(leftInst, rightInst)
        else -> CmpEQRef(leftInst, rightInst)
      }
      LOGIC_AND -> LogicAndBool(leftInst, rightInst)
      LOGIC_OR -> LogicOrBool(leftInst, rightInst)
    }
  }

  private fun buildToStringInstruction(ctx: TypeContext, variableType: TypeInstance, getFuncInst: Instruction): Instruction {
    val toStringField = variableType.field(ctx, "toString", ctx.getContextType())!!
    val toStringFunc = toStringField.type.functionDescriptor(ctx)!!
    val total = toStringFunc.mem.memoryAllocator().getTotal()

    val depth = if (variableType is ClassTypeInstance) {
      variableType.cls.getMemDepth()
    } else 0

    return object : Instruction() {
      override fun execute0(ctx: ActionContext, values: ValueHolder) {
        getFuncInst.execute(ctx, values)
        val func = values.refValue as Instruction
        val newCtx = ActionContext(total, ctx.getContext(depth))
        func.execute(newCtx, values)
      }
    }
  }

  override fun toString(): String {
    return "($left $op $right)"
  }
}
