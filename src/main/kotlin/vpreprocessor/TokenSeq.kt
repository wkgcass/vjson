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
