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
package vpreprocessor

import vjson.CharStream
import vjson.ex.ParserException
import vjson.util.CastUtils
import vpreprocessor.ast.*
import vpreprocessor.token.EOFToken
import vpreprocessor.token.Macro
import vpreprocessor.token.MacroCategory
import vpreprocessor.token.Plain

class Parser(private val rootContext: PreprocessorContext) {
  private val tokenizer = Tokenizer(rootContext.opts)

  fun parse(cs: CharStream): StatementSeq {
    val tokenSeq = TokenSeq(cs, tokenizer)
    val ctx = rootContext.createChild()
    val ls = ArrayList<Statement>()
    while (true) {
      val stmt = statement(ctx, tokenSeq) ?: break
      ls.add(stmt)
    }
    return StatementSeq(ctx, ls)
  }

  private fun statement(ctx: PreprocessorContext, tokenSeq: TokenSeq): Statement? {
    return when (tokenSeq.peek()) {
      is EOFToken -> null
      is Plain -> statementPlain(ctx, tokenSeq)
      is Macro -> statementMacro(ctx, tokenSeq)
    }
  }

  private fun statementPlain(ctx: PreprocessorContext, tokenSeq: TokenSeq): PlainText {
    val token = CastUtils.cast<Plain>(tokenSeq.moveNextAndGet())
    return PlainText(ctx, token.text)
  }

  private fun statementMacro(ctx: PreprocessorContext, tokenSeq: TokenSeq): Statement {
    val token = CastUtils.cast<Macro>(tokenSeq.peek())
    val tokenStr = token.text
    return if (tokenStr == "ifdef") {
      ifx(ctx, tokenSeq) { ifdef(it, tokenSeq) }
    } else if (tokenStr == "ifndef") {
      ifx(ctx, tokenSeq) { ifndef(it, tokenSeq) }
    } else if (tokenStr == "{") {
      statementSeq(ctx, tokenSeq)
    } else if (tokenStr == "{{") {
      CastUtils.cast<PlainText>(statementSeq(ctx, tokenSeq).seq[0])
    } else {
      throw ParserException("unexpected token for statement: $token")
    }
  }

  private fun ifx(ctx: PreprocessorContext, tokenSeq: TokenSeq, conditionFunc: (PreprocessorContext) -> Exp): If {
    val conditionCtx = ctx.createChild()
    val condition = conditionFunc(conditionCtx)

    val bracketToken = tokenSeq.peek()
    if (CastUtils.typeIsNotExpectedOr(bracketToken, Macro::class) {
        it.text != "{{" && it.text != "{"
      }) {
      throw ParserException("expecting {{ or { for if code, but got $bracketToken")
    }

    val codeCtx = conditionCtx.createChild()
    val code = statementSeq(codeCtx, tokenSeq)

    val elseToken = tokenSeq.peek()
    if (CastUtils.typeIsNotExpectedOr(elseToken, Macro::class) { it.text != "else" }) {
      // no else
      // ends
      return If(ctx, condition, code)
    }
    tokenSeq.moveNextAndGet()

    val elseBracketToken = tokenSeq.peek()
    if (CastUtils.typeIsNotExpectedOr(elseBracketToken, Macro::class) {
        it.text != "{{" && it.text != "{"
      }) {
      throw ParserException("expecting {{ or { for else branch code, but got $elseBracketToken")
    }

    val elseCtx = conditionCtx.createChild()
    val elseCode = statementSeq(elseCtx, tokenSeq)

    return If(ctx, condition, code, elseCode)
  }

  private fun ifdef(ctx: PreprocessorContext, tokenSeq: TokenSeq): IfDef {
    tokenSeq.moveNextAndGet() // ifdef
    val varname = varname(tokenSeq)
    return IfDef(ctx, varname)
  }

  private fun ifndef(ctx: PreprocessorContext, tokenSeq: TokenSeq): IfNotDef {
    tokenSeq.moveNextAndGet() // ifndef
    val varname = varname(tokenSeq)
    return IfNotDef(ctx, varname)
  }

  private fun varname(tokenSeq: TokenSeq): String {
    val token = tokenSeq.moveNextAndGet()
    if (CastUtils.typeIsExpectedAnd(token, Macro::class) { it.category == MacroCategory.VAR }) {
      return CastUtils.cast<Macro>(token).text
    } else throw ParserException("expecting variable name, but got $token")
  }

  private fun statementSeq(ctx: PreprocessorContext, tokenSeq: TokenSeq): StatementSeq {
    val bracketToken = CastUtils.cast<Macro>(tokenSeq.moveNextAndGet()).text
    val bracketEndToken = if (bracketToken == "{{") "}}" else "}"
    val ls = ArrayList<Statement>()
    while (true) {
      val peekToken = tokenSeq.peek()
      if (peekToken is Macro) {
        if (peekToken.text == bracketEndToken) {
          break
        }
      }
      val stmt = statement(ctx, tokenSeq) ?: break // check the end token out of loop
      ls.add(stmt)
    }
    val endToken = tokenSeq.moveNextAndGet()
    // assert EOF or endToken.text == bracketEndToken
    if (!CastUtils.check(endToken, Macro::class)) {
      throw ParserException("missing ending symbol $bracketEndToken")
    }
    return StatementSeq(ctx, ls)
  }
}
