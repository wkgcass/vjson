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

import vjson.CharStream
import vjson.cs.LineCol
import vjson.cs.LineColCharStream
import vjson.ex.ParserException
import vjson.pl.token.*
import vjson.simple.SimpleBool
import vjson.simple.SimpleString
import vjson.util.collection.VList

class ExprTokenizer(cs: CharStream, offset: LineCol) {
  private val cs: LineColCharStream = LineColCharStream(offset.filename, cs, offset)

  constructor(str: String, offset: LineCol) : this(CharStream.from(str), offset)

  private val handlers: List<TokenHandler> = listOf(
    VariableNameTokenHandler(),
    IntTokenHandler(),
    FloatTokenHandler(),
    FullMatchTokenHandler(TokenType.BOOL_TRUE, "true", precedence = 1, value = SimpleBool(true)),
    FullMatchTokenHandler(TokenType.BOOL_FALSE, "false", precedence = 1, value = SimpleBool(false)),
    FullMatchTokenHandler(TokenType.KEY_NULL, "null", precedence = 1),
    FullMatchTokenHandler(TokenType.LEFT_PAR, "("),
    FullMatchTokenHandler(TokenType.RIGHT_PAR, ")"),
    FullMatchTokenHandler(TokenType.LEFT_BRACKET, "["),
    FullMatchTokenHandler(TokenType.RIGHT_BRACKET, "]"),
    FullMatchTokenHandler(TokenType.PLUS, "+"),
    FullMatchTokenHandler(TokenType.MINUS, "-"),
    FullMatchTokenHandler(TokenType.MULTIPLY, "*"),
    FullMatchTokenHandler(TokenType.DIVIDE, "/"),
    FullMatchTokenHandler(TokenType.PLUS_ASSIGN, "+="),
    FullMatchTokenHandler(TokenType.MINUS_ASSIGN, "-="),
    FullMatchTokenHandler(TokenType.MULTIPLY_ASSIGN, "*="),
    FullMatchTokenHandler(TokenType.DIVIDE_ASSIGN, "/="),
    FullMatchTokenHandler(TokenType.CMP_GT, ">"),
    FullMatchTokenHandler(TokenType.CMP_GE, ">="),
    FullMatchTokenHandler(TokenType.CMP_LT, "<"),
    FullMatchTokenHandler(TokenType.CMP_LE, "<="),
    FullMatchTokenHandler(TokenType.CMP_EQ, "=="),
    FullMatchTokenHandler(TokenType.CMP_NE, "!="),
    FullMatchTokenHandler(TokenType.LOGIC_NOT, "!"),
    FullMatchTokenHandler(TokenType.LOGIC_AND, "&&"),
    FullMatchTokenHandler(TokenType.LOGIC_OR, "||"),
    FullMatchTokenHandler(TokenType.DOT, "."),
    FullMatchTokenHandler(TokenType.COLON, ":"),
    FullMatchTokenHandler(TokenType.COMMA, ","),
  )

  private val tokenBuffer = VList<Token>()

  fun peek(n: Int = 1): Token? {
    if (tokenBuffer.size() >= n) return tokenBuffer.get(n - 1)
    var nn = n - tokenBuffer.size()
    while (true) {
      --nn
      val token = readToken() ?: return null
      tokenBuffer.add(token)
      if (nn == 0) {
        return token
      }
    }
  }

  fun next(n: Int = 1): Token? {
    if (tokenBuffer.size() >= n) {
      val ret = tokenBuffer.get(n - 1)
      tokenBuffer.removeFirst(n)
      return ret
    }
    var nn = n - tokenBuffer.size()
    tokenBuffer.clear()
    while (true) {
      --nn
      val token = readToken() ?: return null
      if (nn == 0) {
        return token
      }
    }
  }

  private fun readToken(): Token? {
    cs.skipBlank()
    if (!cs.hasNext()) {
      return null
    }

    val preCheck = cs.peekNext()
    if (preCheck == '\'') {
      return readStringToken()
    }

    val lineCol = cs.lineCol()

    for (h in handlers) {
      h.reset()
    }
    var last = ArrayList<TokenHandler>()
    last.addAll(handlers)

    val traveled = StringBuilder()
    var prevC: Char? = null
    while (true) {
      if (!cs.hasNext()) {
        return finish(lineCol, last, traveled, null)
      }
      val c = cs.peekNext()
      val current = ArrayList<TokenHandler>()
      for (h in last) {
        if (h.feed(c)) {
          current.add(h)
        }
      }
      if (current.isEmpty()) {
        if (traveled.isEmpty()) {
          throw ParserException(
            "unable to parse the token: all rules failed when reading the first character $c"
          )
        }
        if (!canSplitTokens(c) && (prevC != null && !canSplitTokens(prevC))) {
          throw ParserException(
            "unable to parse the token: all rules failed after reading `$traveled`, the next character is $c, " +
              "both ${traveled[traveled.length - 1]} and $c cannot be used to split a token, " +
              "last applicable rules: $last"
          )
        }
        return finish(lineCol, last, traveled, c)
      } else {
        cs.moveNextAndGet()
      }
      prevC = c
      traveled.append(c)
      last = current
    }
  }

  private fun readStringToken(): Token {
    val lineCol = cs.lineCol()
    val raw = StringBuilder()
    val sb = StringBuilder()
    raw.append(cs.moveNextAndGet()) // '
    var finishes = false
    var state = 0
    while (cs.hasNext()) {
      val c = cs.moveNextAndGet()
      raw.append(c)
      if (state == 0) {
        if (c == '\\') {
          state = 1
        } else if (c == '\'') {
          finishes = true
          break
        } else {
          sb.append(c)
        }
      } else {
        if (c == '\'' || c == '\\') {
          sb.append(c)
          state = 0
        } else {
          sb.append("\\")
          sb.append(c)
          state = 0
        }
      }
    }

    if (!finishes) {
      throw ParserException("unable to parse the token: incomplete string literal: $raw")
    }

    val str = sb.toString()
    return Token(TokenType.STRING, raw.toString(), lineCol, SimpleString(str))
  }

  private fun finish(lineCol: LineCol, last: ArrayList<TokenHandler>, traveled: StringBuilder, c: Char?): Token {
    val current = ArrayList<TokenHandler>()
    for (h in last) {
      if (h.check()) {
        current.add(h)
      }
    }
    if (current.size == 0) {
      throw ParserException("unable to parse the token: all rules failed after reading `$traveled`, the next character is ${c?.toString() ?: "(eof)"}, last applicable rules: $last")
    }
    val handler: TokenHandler
    if (current.size == 1) {
      handler = current[0]
    } else {
      val foo = ArrayList<TokenHandler>()
      for (h in current) {
        if (foo.isEmpty()) {
          foo.add(h)
        } else {
          if (foo[0].precedence() < h.precedence()) {
            foo.clear()
            foo.add(h)
          } else if (foo[0].precedence() == h.precedence()) {
            foo.add(h)
          }
        }
      }
      if (foo.size > 1) {
        throw ParserException("unable to parse the token: multiple rules conflict after reading `$traveled${c?.toString() ?: ""}`: $foo")
      }
      handler = foo[0]
    }
    return handler.build(lineCol)
  }

  private fun canSplitTokens(c: Char): Boolean {
    if (c in 'a'..'z') return false
    if (c in 'A'..'Z') return false
    if (c == '$') return false
    if (c == '_') return false
    if (c in '0'..'9') return false
    if (c.code < 128) return true
    return false
  }
}
