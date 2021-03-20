package vpreprocessor.token

sealed class Token

data class Plain(val text: String) : Token() {
  override fun toString(): String {
    return "Plain($text)"
  }
}

data class Macro(val text: String) : Token() {
  val category: MacroCategory =
    if (text == "ifdef" || text == "ifndef" || text == "else") MacroCategory.KEYWORD
    else if (text == "{{") MacroCategory.DOUBLE_BRACKET_LEFT
    else if (text == "}}") MacroCategory.DOUBLE_BRACKET_RIGHT
    else if (text == "{") MacroCategory.BRACKET_LEFT
    else if (text == "}") MacroCategory.BRACKET_RIGHT
    else MacroCategory.VAR // currently we only allow variables

  override fun toString(): String {
    return "Macro($text)"
  }
}

enum class MacroCategory {
  KEYWORD,
  VAR,
  BRACKET_LEFT,
  BRACKET_RIGHT,
  DOUBLE_BRACKET_LEFT,
  DOUBLE_BRACKET_RIGHT,
}

class EOFToken : Token() {
  override fun toString(): String {
    return "EOF"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false
    return true
  }

  override fun hashCode(): Int {
    return 0
  }
}
