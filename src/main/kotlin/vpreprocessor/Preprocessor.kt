package vpreprocessor

import vjson.CharStream

class Preprocessor(ctx: PreprocessorContext) {
  private val parser = Parser(ctx)

  fun process(cs: CharStream, builder: StringBuilder) {
    val seq = parser.parse(cs)
    seq.exec(builder)
  }
}
