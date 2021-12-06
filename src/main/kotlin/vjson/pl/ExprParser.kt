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

package vjson.pl

import vjson.JSON
import vjson.ex.ParserException
import vjson.pl.ast.*
import vjson.pl.token.Token
import vjson.pl.token.TokenType
import vjson.simple.SimpleString
import vjson.util.CastUtils.cast

class ExprParser(private val tokenizer: ExprTokenizer) {
  fun parse(): Expr {
    val ctx = ParserContext(null, null)
    exprEntry(ctx)
    return ctx.exprStack.pop()
  }

  private fun exprEntry(ctx: ParserContext) {
    val token = tokenizer.peek() ?: throw ParserException("unexpected eof")
    when (token.type) {
      TokenType.INTEGER -> integer(ctx)
      TokenType.FLOAT -> float(ctx)
      TokenType.BOOL_TRUE -> bool(ctx)
      TokenType.BOOL_FALSE -> bool(ctx)
      TokenType.KEY_NULL -> exprNull(ctx)
      TokenType.VAR_NAME -> accessVar(ctx)
      TokenType.LEFT_PAR -> par(ctx)
      TokenType.PLUS -> positive(ctx)
      TokenType.MINUS -> negative(ctx)
      TokenType.LOGIC_NOT -> logicNot(ctx)
      TokenType.STRING -> string(ctx)
      else -> throw ParserException("unexpected token $token")
    }
  }

  private fun exprBinOp(ctx: ParserContext) {
    if (!ctx.unaryOpStack.isEmpty()) {
      return
    }
    if (ctx.exprStack.isEmpty()) {
      exprEntry(ctx)
      return
    }
    val token = tokenizer.peek()
    if (token == null) {
      ctx.foldBinOp(0)
      return
    }
    when (token.type) {
      TokenType.PLUS -> binOp(ctx, BinOpType.PLUS)
      TokenType.MINUS -> binOp(ctx, BinOpType.MINUS)
      TokenType.MULTIPLY -> binOp(ctx, BinOpType.MULTIPLY)
      TokenType.DIVIDE -> binOp(ctx, BinOpType.DIVIDE)
      TokenType.CMP_GT -> binOp(ctx, BinOpType.CMP_GT)
      TokenType.CMP_GE -> binOp(ctx, BinOpType.CMP_GE)
      TokenType.CMP_LT -> binOp(ctx, BinOpType.CMP_LT)
      TokenType.CMP_LE -> binOp(ctx, BinOpType.CMP_LE)
      TokenType.CMP_NE -> binOp(ctx, BinOpType.CMP_NE)
      TokenType.CMP_EQ -> binOp(ctx, BinOpType.CMP_EQ)
      TokenType.LOGIC_AND -> binOp(ctx, BinOpType.LOGIC_AND)
      TokenType.LOGIC_OR -> binOp(ctx, BinOpType.LOGIC_OR)
      else -> exprEntry(ctx)
    }
  }

  private fun exprContinue(ctx: ParserContext) {
    val token = tokenizer.peek()
    if (token == null) {
      ctx.foldBinOp(0)
      return
    }
    when (token.type) {
      TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY, TokenType.DIVIDE,
      TokenType.CMP_GT, TokenType.CMP_GE, TokenType.CMP_LT, TokenType.CMP_LE, TokenType.CMP_NE, TokenType.CMP_EQ,
      TokenType.LOGIC_AND, TokenType.LOGIC_OR,
      -> exprBinOp(ctx)
      TokenType.PLUS_ASSIGN -> opAssign(ctx, BinOpType.PLUS)
      TokenType.MINUS_ASSIGN -> opAssign(ctx, BinOpType.MINUS)
      TokenType.MULTIPLY_ASSIGN -> opAssign(ctx, BinOpType.MULTIPLY)
      TokenType.DIVIDE_ASSIGN -> opAssign(ctx, BinOpType.DIVIDE)
      TokenType.RIGHT_PAR -> parEnd(ctx)
      TokenType.RIGHT_BRACKET -> bracketEnd(ctx)
      TokenType.DOT -> accessField(ctx)
      TokenType.COLON -> methodInvocation(ctx)
      TokenType.LEFT_BRACKET -> accessIndex(ctx)
      TokenType.COMMA -> terminate(ctx)
      else -> throw ParserException("unexpected token $token, expecting bin-operators, assignments or dot")
    }
  }

  private fun parEnd(ctx: ParserContext) {
    if (ctx.beginToken == null) {
      throw ParserException("unexpected `)`, no matching `(` found for it")
    }
    if (ctx.beginToken != TokenType.LEFT_PAR) {
      throw ParserException("unexpected `)`, the begin token is not `(`: ${ctx.beginToken}")
    }
    tokenizer.next()
    ctx.foldBinOp(0)
    ctx.ends = true
    return
  }

  private fun bracketEnd(ctx: ParserContext) {
    if (ctx.beginToken == null) {
      throw ParserException("unexpected `]`, no matching `[` found for it")
    }
    if (ctx.beginToken != TokenType.LEFT_BRACKET) {
      throw ParserException("unexpected `]`, the begin token is not `[`: ${ctx.beginToken}")
    }
    tokenizer.next()
    ctx.foldBinOp(0)
    ctx.ends = true
    return
  }

  private fun accessField(ctx: ParserContext) {
    tokenizer.next() // .
    val exp = ctx.exprStack.pop()
    val next = tokenizer.next() ?: throw ParserException("unexpected eof when trying to get field of $exp")
    if (next.type != TokenType.VAR_NAME) {
      throw ParserException("unexpected token $next, expecting field name for accessing $exp")
    }
    ctx.exprStack.push(Access(next.raw, from = exp))

    exprContinue(ctx)
  }

  private fun methodInvocation(ctx: ParserContext) {
    tokenizer.next() // :
    val exp = ctx.exprStack.pop()
    var next = tokenizer.next() ?: throw ParserException("unexpected eof when trying to invoke function $exp")
    if (next.type != TokenType.LEFT_BRACKET) {
      throw ParserException("unexpected token $next, expecting `[` for invoking $exp")
    }
    next = tokenizer.peek() ?: throw ParserException("unexpected eof when preparing arguments for invoking function $exp")
    if (next.type == TokenType.RIGHT_BRACKET) {
      tokenizer.next()
      ctx.exprStack.push(FunctionInvocation(exp))

      exprContinue(ctx)
      return
    }

    val subCtx = ParserContext(ctx, TokenType.LEFT_BRACKET)
    var argIdx = 0
    val args = ArrayList<Expr>()
    while (true) {
      exprEntry(subCtx)
      val arg = subCtx.exprStack.pop()
      args.add(arg)

      if (subCtx.ends) {
        break
      }
      next = tokenizer.peek() ?: throw ParserException("unexpected eof when preparing arguments[$argIdx] for invoking function $exp")
      if (next.type == TokenType.RIGHT_BRACKET) {
        tokenizer.next()
        break
      }
      ++argIdx
    }
    ctx.exprStack.push(FunctionInvocation(exp, args))
    exprContinue(ctx)
  }

  private fun accessIndex(ctx: ParserContext) {
    tokenizer.next() // [

    val expr = ctx.exprStack.pop()
    val next = tokenizer.peek() ?: throw ParserException("unexpected eof when trying to access index of $expr")
    if (next.type == TokenType.RIGHT_BRACKET) {
      throw ParserException("unexpected token $next, index must be specified for accessing $expr")
    }
    val subCtx = ParserContext(ctx, TokenType.LEFT_BRACKET)
    exprEntry(subCtx)
    if (!subCtx.ends) {
      throw ParserException("only one element can be used to access index of $expr, the next token is " + (if (tokenizer.peek() == null) "eof" else tokenizer.peek()))
    }
    val indexExpr = subCtx.exprStack.pop()
    ctx.exprStack.push(AccessIndex(expr, indexExpr))

    exprContinue(ctx)
  }

  private fun terminate(ctx: ParserContext) {
    tokenizer.next()
    ctx.foldBinOp(0)
    return
  }

  private fun binOp(ctx: ParserContext, op: BinOpType) {
    tokenizer.next()
    if (ctx.opStack.isEmpty() || ctx.opStack.peek().precedence < op.precedence) {
      ctx.opStack.push(op)
      exprEntry(ctx)
    } else {
      ctx.foldBinOp(op.precedence)
      ctx.opStack.push(op)
      exprEntry(ctx)
    }
  }

  private fun opAssign(ctx: ParserContext, op: BinOpType) {
    tokenizer.next()
    if (ctx.exprStack.size() != 1) {
      throw ParserException("unable to handle assignment with multiple pending expressions ${ctx.exprStack}")
    }
    val variable = ctx.exprStack.pop()
    exprEntry(ctx)
    val next = ctx.exprStack.pop()
    if (variable !is AssignableExpr) {
      throw ParserException("$variable is not assignable while trying to $op=$next to it")
    }
    ctx.exprStack.push(OpAssignment(op, variable = variable, value = next))

    val token = tokenizer.peek()
    if (!isTerminator(token)) {
      throw ParserException("expression not terminating after parsing ${ctx.exprStack.peek()}, got token $token")
    }
  }

  private fun integer(ctx: ParserContext) {
    val token = tokenizer.next()!!
    if (token.value is JSON.Integer || token.value is JSON.Long) {
      ctx.exprStack.push(IntegerLiteral(cast(token.value)))
    } else {
      throw ParserException("unexpected value in token $token, expecting JSON.Integer or JSON.Long, but got ${token.value}")
    }

    exprContinue(ctx)
  }

  private fun float(ctx: ParserContext) {
    val token = tokenizer.next()!!
    if (token.value is JSON.Double) {
      ctx.exprStack.push(FloatLiteral(token.value))
    } else {
      throw ParserException("unexpected value in token $token, expecting JSON.Double, but got ${token.value}")
    }

    exprContinue(ctx)
  }

  private fun bool(ctx: ParserContext) {
    val token = tokenizer.next()!!
    if (token.value is JSON.Bool) {
      ctx.exprStack.push(BoolLiteral(token.value.booleanValue()))
    } else {
      throw ParserException("unexpected value in token $token, expecting JSON.Bool, but got ${token.value}")
    }

    exprContinue(ctx)
  }

  private fun exprNull(ctx: ParserContext) {
    tokenizer.next()
    ctx.exprStack.push(NullLiteral())

    exprContinue(ctx)
  }

  private fun accessVar(ctx: ParserContext) {
    val token = tokenizer.next()!!
    val varname = token.raw
    ctx.exprStack.push(Access(varname))

    exprContinue(ctx)
  }

  private fun par(ctx: ParserContext) {
    val token = tokenizer.next()!!
    val nextCtx = ParserContext(ctx, token.type)
    exprEntry(nextCtx)
    ctx.exprStack.push(nextCtx.exprStack.pop())

    exprContinue(ctx)
  }

  private fun positive(ctx: ParserContext) {
    tokenizer.next()
    ctx.unaryOpStack.push(1)
    exprEntry(ctx)
    val expr = ctx.exprStack.pop()
    ctx.exprStack.push(Positive(expr))
    ctx.unaryOpStack.pop()

    exprContinue(ctx)
  }

  private fun negative(ctx: ParserContext) {
    tokenizer.next()
    ctx.unaryOpStack.push(1)
    exprEntry(ctx)
    val expr = ctx.exprStack.pop()
    ctx.exprStack.push(Negative(expr))
    ctx.unaryOpStack.pop()

    exprContinue(ctx)
  }

  private fun logicNot(ctx: ParserContext) {
    tokenizer.next()
    ctx.unaryOpStack.push(1)
    exprEntry(ctx)
    val expr = ctx.exprStack.pop()
    ctx.exprStack.push(LogicNot(expr))
    ctx.unaryOpStack.pop()

    exprContinue(ctx)
  }

  private fun string(ctx: ParserContext) {
    val token = tokenizer.next()
    val str = token!!.value as SimpleString
    ctx.exprStack.push(StringLiteral(str.toJavaObject()))

    exprContinue(ctx)
  }

  private fun isTerminator(token: Token?): Boolean {
    if (token == null) {
      return true
    }
    return token.type.isTerminator
  }
}
