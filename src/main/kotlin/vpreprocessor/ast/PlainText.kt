package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

class PlainText(override val context: PreprocessorContext, val text: String) : AbstractAST(), Statement {
  override fun exec(builder: StringBuilder) {
    builder.append(text)
  }

  override fun buildSyntaxString(builder: StringBuilder) {
    builder.append("{{").append(text).append("}}")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false

    other as PlainText

    if (text != other.text) return false

    return true
  }

  override fun hashCode(): Int {
    return text.hashCode()
  }
}
