package vpreprocessor.ast

interface Statement : AST {
  fun exec(builder: StringBuilder)
}
