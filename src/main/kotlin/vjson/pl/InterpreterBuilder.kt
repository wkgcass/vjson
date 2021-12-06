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
import vjson.parser.ObjectParser
import vjson.parser.ParserOptions
import vjson.pl.ast.Statement
import vjson.pl.type.lang.Types

class InterpreterBuilder {
  private val types: MutableList<Types> = ArrayList()

  fun addTypes(types: Types): InterpreterBuilder {
    this.types.add(types)
    return this
  }

  fun compile(prog: String): Interpreter {
    val jsonParser = ObjectParser(
      ParserOptions()
        .setStringSingleQuotes(true)
        .setKeyNoQuotes(true)
        .setKeyNoQuotesWithDot(true)
        .setAllowSkippingCommas(true)
        .setAllowObjectEntryWithoutValue(true)
        .setEqualAsColon(true)
    )
    val json = jsonParser.last(prog)!!
    return compile(json)
  }

  fun compile(json: JSON.Object): Interpreter {
    val astGen = ASTGen(json)
    return interpreter(astGen.parse())
  }

  fun interpreter(ast: List<Statement>): Interpreter {
    return Interpreter(types, ast)
  }
}
