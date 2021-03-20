package vpreprocessor.ast

import vpreprocessor.PreprocessorContext

interface AST {
  val context: PreprocessorContext
  fun buildSyntaxString(builder: StringBuilder)
}
