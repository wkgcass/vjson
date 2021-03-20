package vpreprocessor.ast

interface Exp : AST {
  fun exec(): Any?
}
