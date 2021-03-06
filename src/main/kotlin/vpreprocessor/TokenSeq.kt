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
import vjson.util.collection.VList
import vpreprocessor.token.Token

class TokenSeq(private val cs: CharStream, private val tokenizer: Tokenizer) {
  private val list = VList<Token>()

  fun moveNextAndGet(): Token {
    if (list.isEmpty()) return tokenizer.feed(cs)
    return list.removeFirst()
  }

  fun peek(): Token {
    return peek(1)
  }

  fun peek(i: Int): Token {
    getUntil(i)
    return list.get(i - 1)
  }

  private fun getUntil(size: Int) {
    while (list.size() < size) {
      val token = tokenizer.feed(cs)
      list.add(token)
    }
  }
}
