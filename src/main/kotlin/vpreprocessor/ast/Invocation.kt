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
package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class Invocation(
  override val context: PreprocessorContext, val function: String, val arguments: List<Exp>
) : AbstractAST(), Exp {
  override fun exec(): Any? {
    TODO("Not yet implemented")
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append(function).append("(")
    var isFirst = true
    for (arg in arguments) {
      if (isFirst) {
        isFirst = false
      } else {
        builder.append(", ")
      }
      arg.buildSyntaxString(builder)
    }
    builder.append(")")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as Invocation

    if (function != other.function) return false
    if (arguments != other.arguments) return false

    return true
  }

  override fun hashCode(): Int {
    var result = function.hashCode()
    result = 31 * result + arguments.hashCode()
    return result
  }
}
