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
import vjson.parser.ParserUtils
import vpreprocessor.token.EOFToken
import vpreprocessor.token.Macro
import vpreprocessor.token.Plain
import vpreprocessor.token.Token

class Tokenizer(private val opts: PreprocessorOptions) {
  private val texts = StringBuilder()
  fun getTexts(): String = texts.toString()

  private val pendingTexts = StringBuilder()
  fun getPendingTexts(): String = pendingTexts.toString()

  var textState = TextState.INIT
    private set
  var commentStackDepth = 0
    private set
  private var stringIsSingleQuote = false // for 'a' or "a", handling process are almost the same

  private fun feed(cs: CharStream, c: Char, res: FeedExtraResult): TokenType? {
    when (textState) {
      TextState.INIT -> {
        when (c) {
          '/' -> {
            if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              if (nx == '/' || nx == '*') { // got // or /*
                // pend until macro or comment ends
                cs.moveNextAndGet() // consume one more
              }
              when (nx) {
                '/' -> { // got //
                  pendingTexts.append("//")
                  textState = TextState.SINGLE_LINE_COMMENT
                  checkAndResumeMacro()
                }
                '*' -> { // got /*
                  pendingTexts.append("/*")
                  textState = TextState.MULTI_LINE_COMMENT
                  ++commentStackDepth
                  checkAndResumeMacro()
                }
                else -> {
                  texts.append(c)
                }
              }
            } else { // eof
              texts.append(c)
            }
            return null // nothing to generate
          }
          '"' -> {
            /*1*/if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              /*2*/if (nx == '"') { // got ""
                /*3*/if (cs.hasNext(3)) {
                  val nx2 = cs.peekNext(3)
                  /*4*/if (nx2 == '"') { // got """
                    cs.moveNextAndGet() // move for 2nd "
                    cs.moveNextAndGet() // move for 3rd "
                    textState = TextState.MULTI_LINE_STRING
                    texts.append("\"\"\"")
                  }/*4*/ else { // got ""x
                    // normal string ends
                    cs.moveNextAndGet() // move for 2nd "
                    texts.append("\"\"")
                  }
                }/*3*/ else { // "" eof
                  cs.moveNextAndGet() // 2nd "
                  texts.append("\"\"")
                }
              }/*2*/ else { // got "x, normal string
                stringIsSingleQuote = false
                textState = TextState.SINGLE_LINE_STRING
                texts.append(c)
              }
            }/*1*/ else { // eof, probably invalid source code, but we do not care
              stringIsSingleQuote = false
              textState = TextState.SINGLE_LINE_STRING
              texts.append(c)
            }
            return null
          }
          '\'' -> {
            stringIsSingleQuote = true
            textState = TextState.SINGLE_LINE_STRING
            texts.append(c)
            return null
          }
          else -> {
            texts.append(c)
            return null
          }
        }
      }
      TextState.SINGLE_LINE_COMMENT -> {
        when (c) {
          '\r', '\n' -> {
            return commentEnds(false, c, res)
          }
          else -> return feedMacro(cs, c, res)
        }
      }
      TextState.MULTI_LINE_COMMENT -> {
        when (c) {
          '*' -> {
            /*1*/if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              /*2*/if (nx == '/') { // */, ends the comment
                cs.moveNextAndGet()
                /*3*/if (commentStackDepth <= 1) {
                  // comment ends
                  commentStackDepth = 0
                  return commentEnds(true, c, res)
                }/*3*/ else {
                  // comment stack decreases
                  --commentStackDepth
                  pendingTexts.append("*/")
                  return null // nothing to return
                }
              }/*2*/
            }/*1*/
            return feedMacro(cs, c, res)
          }
          else -> {
            /*1*/if (noMacro()) { // only handle comment if no macro
              /*2*/if (c == '/' && opts.nestedComment) {
                /*3*/if (cs.hasNext(2)) {
                  val nx = cs.peekNext(2)
                  /*4*/if (nx == '*') { // /*, nested comment
                    ++commentStackDepth
                    cs.moveNextAndGet()
                    pendingTexts.append("/*")
                    // also it's definitely not macro, so disable it for current comment
                    macroState = MacroState.IGNORE
                    return null
                  }/*4*/
                }/*3*/ // else: eof, probably invalid source code, but we don't care
              }/*2*/
            }/*1*/
            return feedMacro(cs, c, res)
          }
        }
      }
      TextState.SINGLE_LINE_STRING -> {
        val ending = if (stringIsSingleQuote) '\'' else '"'
        when (c) {
          '\\' -> {
            /*1*/if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              /*2*/if (nx == ending) { // \"
                cs.moveNextAndGet()
                texts.append("\\").append(ending)
                return null
              }/*2*/ else {
                // other escape
                texts.append(c).append(nx)
                cs.moveNextAndGet()
                return null
              }
            }/*1*/ // else: eof, probably invalid source code, but we don't care
            texts.append(c)
            return null
          }
          ending -> {
            // string ends
            texts.append(c)
            textState = TextState.INIT
            return null
          }
          else -> {
            texts.append(c)
            return null
          }
        }
      }
      TextState.MULTI_LINE_STRING -> {
        when (c) {
          '"' -> {
            /*1*/if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              /*2*/if (nx == '"') { // ""
                /*3*/if (cs.hasNext(3)) {
                  val nx2 = cs.peekNext(3)
                  /*4*/if (nx2 == '"') { // """
                    // multi line string ends
                    cs.moveNextAndGet() // move for 2nd "
                    cs.moveNextAndGet() // move for 3rd "
                    texts.append("\"\"\"")
                    textState = TextState.INIT
                    return null
                  }/*4*/
                }/*3*/
              }/*2*/
            }/*1*/
            texts.append(c)
            return null
          }
          else -> {
            texts.append(c)
            return null
          }
        }
      }
    }
  }

  private fun commentEnds(multiLineComment: Boolean, c: Char, res: FeedExtraResult): TokenType? {
    textState = TextState.INIT // back to init state
    if (noMacro()) {
      // this comment does not contain macro
      // all are simple comment text
      texts.merge(pendingTexts)
      if (multiLineComment) {
        texts.append("*/")
      } else {
        texts.append(c)
      }
    } else {
      // macro appeared
      if (!multiLineComment) {
        // the \n or \r should not be merged into macro
        res.moveCursor = false
      }
    }
    return commentEndsForMacro()
  }

  enum class TextState {
    INIT, // normal reading, expecting comment (/, read one more: // -> SINGLE_LINE_COMMENT, /* -> MULTI_LINE_COMMENT),
    // (", read two more, ""? -> SINGLE_LINE_STRING, """ -> MULTI_LINE_STRING)

    SINGLE_LINE_COMMENT, // got //, expecting eol or eof -> INIT
    MULTI_LINE_COMMENT, // got /*, expecting *, read one more, */ -> MULTI_LINE_COMMENT_END,
    // /, read one more, if /* -> NESTED_COMMENT_SLASH

    SINGLE_LINE_STRING, // expecting \, peek one more for \", and expecting " for end -> INIT
    MULTI_LINE_STRING, // expecting ", peek two more, if """ -> INIT, otherwise not changed
  }

  var macroState = MacroState.INIT
    private set

  private fun noMacro(): Boolean {
    return macroState == MacroState.INIT || macroState == MacroState.IGNORE
  }

  private fun commentEndsForMacro(): TokenType? {
    val noMacro = noMacro()
    // check and reset state
    if (macroState != MacroState.TEXT) {
      macroState = MacroState.INIT
    }
    if (noMacro || macroState == MacroState.TEXT) {
      return null // it's normal comment or is TEXT (note that TEXT is special because it generates Plain(text))
    } else {
      return TokenType.MACRO
    }
  }

  private fun checkAndResumeMacro() {
    if (macroState == MacroState.TEXT) {
      beginMacro()
      // restore the state to text
      macroState = MacroState.TEXT
    }
  }

  private fun beginMacro() {
    // discard the cached comment starter (// or /*)
    pendingTexts.clear()
    macroState = MacroState.READING
    if (commentStackDepth == 1) {
      --commentStackDepth
    }
  }

  private fun feedMacro(cs: CharStream, c: Char, res: FeedExtraResult): TokenType? {
    when (macroState) {
      MacroState.INIT -> {
        when {
          ParserUtils.isWhiteSpace(c) -> {
            // should skip, and pend the text in case it's a normal comment
            pendingTexts.append(c)
            return null
          }
          c == '#' -> {
            beginMacro()
            return TokenType.PLAIN // generate text before the macro comment
          }
          else -> {
            // got other symbols
            macroState = MacroState.IGNORE
            pendingTexts.append(c)
            return null
          }
        }
      }
      MacroState.READING -> {
        when {
          ParserUtils.isWhiteSpace(c) -> {
            // do nothing and skip
            return null
          }
          ParserUtils.isInitialVarName(c) -> {
            texts.append(c)
            macroState = MacroState.READING_VAR
            return null
          }
          c == '{' -> {
            if (cs.hasNext(2)) {
              val nx = cs.peekNext(2)
              if (nx == '{') { // {{
                cs.moveNextAndGet()
                macroState = MacroState.TEXT
                texts.append("{{")
                return TokenType.MACRO
              }
            }
            // single {, is code block
            texts.append(c)
            return TokenType.MACRO
          }
          c == '}' -> {
            texts.append(c)
            return TokenType.MACRO
          }
          else -> {
            // there may be more conditions to be added in the future
            // but for now we do not use other conditions
            throw ParserException("unexpected character $c")
          }
        }
      }
      MacroState.READING_VAR -> {
        if (ParserUtils.isVarName(c)) {
          texts.append(c)
          return null
        } else { // not var anymore, back to READING state and feed again
          macroState = MacroState.READING
          res.moveCursor = false
          return TokenType.MACRO
        }
      }
      MacroState.TEXT -> {
        if (c == '}') {
          if (cs.hasNext(2)) {
            val nx = cs.peekNext(2)
            if (nx == '}') { // }}
              macroState = MacroState.TEXT_END
              return TokenType.PLAIN
            }
          }
        }
        // otherwise simply record the char
        texts.append(c)
        return null
      }
      MacroState.TEXT_END -> {
        // the }} should be generated into a standalone token
        // also the 2nd } is already checked
        macroState = MacroState.READING
        texts.append("}}")
        return TokenType.MACRO
      }
      MacroState.IGNORE -> {
        // simply record for the comment
        pendingTexts.append(c) // still adding into pending texts, will flush when comment ends
        return null
      }
    }
  }

  enum class MacroState {
    INIT, // init, expecting # -> READING, ws -> skip, otherwise -> IGNORE
    READING,
    // expecting {, peek one more, if {{ -> TEXT
    // expecting initialVarName -> READING_VAR
    // otherwise not changed

    READING_VAR, // expecting varName, otherwise -> READING and recalculate again
    TEXT, // expecting }, peek one more, if }} -> TEXT_END, otherwise not changed
    TEXT_END, // it's checked to be }}, generate }} and go to READING
    IGNORE, // ignore (until comment ends, will be reset to INIT when comment begins)
  }

  private enum class TokenType {
    MACRO,
    PLAIN,
  }

  private class FeedExtraResult(var moveCursor: Boolean)

  fun feed(cs: CharStream): Token {
    val res = FeedExtraResult(true)
    while (cs.hasNext()) {
      val c = cs.peekNext()

      // reset to default result before feeding
      res.moveCursor = true

      val retType = feed(cs, c, res)

      if (res.moveCursor) {
        cs.moveNextAndGet()
      }
      if (retType != null) {
        if (texts.isNotEmpty()) {
          return generateToken(retType)
        }
      }
    }
    return generateTokenForEOF()
  }

  private fun generateToken(type: TokenType): Token {
    return when (type) {
      TokenType.PLAIN -> Plain(texts.toStringAndClear())
      TokenType.MACRO -> Macro(texts.toStringAndClear())
    }
  }

  private fun generateTokenForEOF(): Token {
    texts.merge(pendingTexts)
    if (texts.isEmpty()) {
      return EOFToken() // nothing to generate
    }
    if (noMacro() || macroState == MacroState.TEXT) {
      return generateToken(TokenType.PLAIN)
    } else {
      return generateToken(TokenType.MACRO)
    }
  }

  private fun StringBuilder.merge(builder: StringBuilder): StringBuilder {
    append(builder)
    builder.clear()
    return this
  }

  private fun StringBuilder.toStringAndClear(): String {
    val str = toString()
    clear()
    return str
  }
}
