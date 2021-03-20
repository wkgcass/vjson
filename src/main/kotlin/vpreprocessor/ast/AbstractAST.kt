package vpreprocessor.ast

abstract class AbstractAST : AST {
  override fun toString(): String {
    val builder = StringBuilder()
    buildSyntaxString(builder)
    return builder.toString()
  }
}
