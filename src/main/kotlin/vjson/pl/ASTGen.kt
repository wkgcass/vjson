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

package vjson.pl

import vjson.JSON
import vjson.ex.ParserException
import vjson.pl.ast.*
import vjson.util.CastUtils.cast

class ASTGen(_prog: JSON.Object) {
  private val prog = _prog.entryList().listIterator()
  fun parse(): List<Statement> {
    val ls = ArrayList<Statement>()
    while (prog.hasNext()) {
      val entry = prog.next()
      val stmt = when (entry.key) {
        "class" -> aClass(entry)
        "public" -> visiblity(entry, Visibility.PUBLIC)
        "private" -> visiblity(entry, Visibility.PRIVATE)
        "function" -> function(entry)
        "var" -> aVar(entry)
        "new" -> aNew(entry)
        "for" -> aFor(entry)
        "while" -> aWhile(entry)
        "if" -> aIf(entry)
        "break" -> aBreak(entry)
        "continue" -> aContinue(entry)
        "return" -> aReturn(entry)
        else -> exprKey(entry)
      }
      ls.add(stmt)
    }
    return ls
  }

  /**
   * ```
   * class ClassName: { param: "type" } typeof: {
   *   statements
   * }
   * ```
   */
  private fun aClass(entry: JSON.ObjectEntry): ClassDefinition {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `class`")
    }
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting class name")
    }
    val nameAndParams = prog.next()
    val className = nameAndParams.key
    if (nameAndParams.value !is JSON.Object) {
      throw ParserException("expecting parameters for class `$className`, but got ${nameAndParams.value}")
    }
    val params = nameAndParams.value
    if (params.keySet().size != params.keyList().size) {
      throw ParserException("duplicated parameter name for class `$className`")
    }
    val astParams = ArrayList<Param>()
    params.entryList().forEachIndexed { idx, e ->
      if (e.value !is JSON.String) {
        throw ParserException("parameter type must be a string, type for parameters[$idx] is ${e.value} in class `$className`")
      }
      val type = e.value.toJavaObject()
      astParams.add(Param(e.key, Type(type)))
    }

    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting class content")
    }
    val typeofAndCode = prog.next()
    if (typeofAndCode.key != "typeof") {
      throw ParserException("unexpected token $typeofAndCode, expecting `typeof` and class content")
    }

    if (typeofAndCode.value !is JSON.Object) {
      throw ParserException("class content must be encapsulated into a json object, but got ${typeofAndCode.value} for class `$className`")
    }
    val astCode = ASTGen(typeofAndCode.value).parse()

    return ClassDefinition(className, astParams, astCode)
  }

  private fun visiblity(entry: JSON.ObjectEntry, visibility: Visibility): Statement {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `public`")
    }
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting variable or function definition")
    }
    val nextEntry = prog.next()
    return when (nextEntry.key) {
      "var" -> {
        val res = aVar(nextEntry)
        VariableDefinition(res.name, res.value, visibility)
      }
      "function" -> {
        val res = function(nextEntry)
        FunctionDefinition(res.name, res.params, res.returnType, res.code, visibility)
      }
      else -> {
        throw ParserException("unexpected token $nextEntry, expecting variable or function definition")
      }
    }
  }

  /**
   * ```
   * function funcName: { param: "type" } returnType: {
   *   statements
   * }
   * ```
   */
  private fun function(entry: JSON.ObjectEntry): FunctionDefinition {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `function`")
    }
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting function name")
    }
    val nameAndParams = prog.next()
    val funcName = nameAndParams.key
    if (nameAndParams.value !is JSON.Object) {
      throw ParserException("expecting parameters for function `$funcName`, but got ${nameAndParams.value}")
    }
    val params = nameAndParams.value
    if (params.keySet().size != params.keyList().size) {
      throw ParserException("duplicated parameter name for function `$funcName`")
    }
    val astParams = ArrayList<Param>()
    params.entryList().forEachIndexed { idx, e ->
      if (e.value !is JSON.String) {
        throw ParserException("parameter type must be a string, type for parameters[$idx] is ${e.value} in function `$funcName`")
      }
      val type = e.value.toJavaObject()
      astParams.add(Param(e.key, Type(type)))
    }

    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting function return type")
    }
    val returnTypeAndCode = prog.next()
    val astReturnType = Type(returnTypeAndCode.key)

    if (returnTypeAndCode.value !is JSON.Object) {
      throw ParserException("function payload must be encapsulated into a json object, but got ${returnTypeAndCode.value} for function `$funcName`")
    }
    val astCode = ASTGen(returnTypeAndCode.value).parse()

    return FunctionDefinition(funcName, astParams, astReturnType, astCode)
  }

  /**
   * ```
   * var varname: initValue
   * ```
   */
  private fun aVar(entry: JSON.ObjectEntry): VariableDefinition {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `var`")
    }
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting variable name")
    }
    val nextEntry = prog.next()
    val varname = nextEntry.key
    val value = expr(nextEntry.value)
    return VariableDefinition(varname, value)
  }

  /**
   * ```
   * new type
   * // or
   * new type: [ ...args... ]
   * ```
   */
  private fun aNew(entry: JSON.ObjectEntry): Expr {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `new`")
    }
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting the type to be instantiated")
    }
    val nextEntry = prog.next()
    val typeStr = nextEntry.key
    return if (typeStr.contains("[")) {
      if (!typeStr.endsWith("]")) {
        throw ParserException("unexpected type for creating array: found `[` in type string but it does not end with `]`: $typeStr")
      }
      val type = typeStr.substring(0, typeStr.indexOf("["))
      val lenStr = typeStr.substring(typeStr.indexOf("[") + 1, typeStr.length - 1)
      val lenExpr = exprString(lenStr)
      if (nextEntry.value !is JSON.Null) {
        throw ParserException("unexpected token ${nextEntry.value} for new array statement, expecting null value after key `$typeStr`")
      }
      NewArray(Type(type), lenExpr)
    } else {
      when (nextEntry.value) {
        is JSON.Null -> NewInstance(Type(typeStr), listOf())
        is JSON.Array -> NewInstance(Type(typeStr), exprArray(nextEntry.value))
        else ->
          throw ParserException("unexpected token ${nextEntry.value} for new instance statement, expecting null or array value after key `$typeStr`")
      }
    }
  }

  private fun aFor(entry: JSON.ObjectEntry): ForLoop {
    if (entry.value !is JSON.Array) {
      throw ParserException("unexpected token ${entry.value}, expecting a 3-element array for `for` loop")
    }
    val array = entry.value
    if (array.length() != 3) {
      throw ParserException("unexpected token ${entry.value}, expecting a 3-element array for `for` loop")
    }
    val init = array[0]
    val cond = array[1]
    val incr = array[2]

    val astInit = if (init is JSON.Object) {
      ASTGen(init).parse()
    } else {
      listOf(expr(init))
    }
    val astCond = expr(cond)
    val astIncr = if (incr is JSON.Object) {
      ASTGen(incr).parse()
    } else {
      listOf(expr(incr))
    }

    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting code for the `for` loop")
    }
    val nextEntry = prog.next()
    if (nextEntry.key != "do") {
      throw ParserException("unexpected token $nextEntry, expecting `do` to begin the `for` loop code")
    }
    if (nextEntry.value !is JSON.Object) {
      throw ParserException("unexpected token ${nextEntry.value}, expecting code block for the `for` loop")
    }
    val code = ASTGen(nextEntry.value).parse()

    return ForLoop(astInit, astCond, astIncr, code)
  }

  private fun aWhile(entry: JSON.ObjectEntry): WhileLoop {
    val astCond = expr(entry.value)
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting code for the `while` loop")
    }
    val nextEntry = prog.next()
    if (nextEntry.key != "do") {
      throw ParserException("unexpected token $nextEntry, expecting `do` to begin the `while` loop code")
    }
    if (nextEntry.value !is JSON.Object) {
      throw ParserException("unexpected token ${nextEntry.value}, expecting code block for the `while` loop")
    }
    val code = ASTGen(nextEntry.value).parse()

    return WhileLoop(astCond, code)
  }

  private fun aIf(entry: JSON.ObjectEntry): IfStatement {
    val astCond = expr(entry.value)
    if (!prog.hasNext()) {
      throw ParserException("unexpected eof, expecting content for `if`")
    }
    val nextEntry = prog.next()
    if (nextEntry.key != "then") {
      throw ParserException("unexpected token $nextEntry, expecting `then` after `if`")
    }
    if (nextEntry.value !is JSON.Object) {
      throw ParserException("unexpected token ${nextEntry.value}, expecting code block for `if`")
    }
    val ifCode = ASTGen(nextEntry.value).parse()

    if (!prog.hasNext()) {
      return IfStatement(astCond, ifCode, listOf())
    }
    val nextNextEntry = prog.next()
    if (nextNextEntry.key == "else") {
      when (nextNextEntry.value) {
        is JSON.Null -> {
          // expecting else if
          if (!prog.hasNext()) {
            throw ParserException("unexpected eof, found `else: null`, but not following another `if`")
          }
          val nextNextNextEntry = prog.next()
          if (nextNextNextEntry.key != "if") {
            throw ParserException("unexpected token $nextNextNextEntry, found `else: null`, but not following another `if`")
          }
          val nextIf = aIf(nextNextNextEntry)
          return IfStatement(astCond, ifCode, listOf(nextIf))
        }
        is JSON.Object -> {
          val elseCode = ASTGen(nextNextEntry.value).parse()
          return IfStatement(astCond, ifCode, elseCode)
        }
        else -> throw ParserException("unexpected token ${nextNextEntry.value}, expecting code block for `else`")
      }
    } else {
      // not if statement
      prog.previous()
      return IfStatement(astCond, ifCode, listOf())
    }
  }

  private fun aBreak(entry: JSON.ObjectEntry): BreakStatement {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `break`")
    }
    return BreakStatement()
  }

  private fun aContinue(entry: JSON.ObjectEntry): ContinueStatement {
    if (entry.value !is JSON.Null) {
      throw ParserException("unexpected token ${entry.value}, expecting null as value for key `continue`")
    }
    return ContinueStatement()
  }

  private fun aReturn(entry: JSON.ObjectEntry): ReturnStatement {
    return if (entry.value is JSON.Null) {
      ReturnStatement()
    } else {
      ReturnStatement(expr(entry.value))
    }
  }

  private fun exprKey(entry: JSON.ObjectEntry): Expr {
    val tokenizer = ExprTokenizer(entry.key)
    val parser = ExprParser(tokenizer)
    val expr = parser.parse()
    if (tokenizer.peek() != null) {
      throw ParserException("only one expression can be used, but multiple found, next token: ${tokenizer.peek()}")
    }
    return if (entry.value is JSON.Array) {
      callFunction(expr, entry.value)
    } else {
      if (expr !is AssignableExpr) {
        throw ParserException("unable to assign value to $expr")
      }
      Assignment(expr, expr(entry.value))
    }
  }

  private fun callFunction(funcExpr: Expr, args: JSON.Array): FunctionInvocation {
    val exprArgs = ArrayList<Expr>(args.length())
    for (i in 0 until args.length()) {
      exprArgs.add(expr(args[i]))
    }
    return FunctionInvocation(funcExpr, exprArgs)
  }

  private fun expr(json: JSON.Instance<*>): Expr {
    return when (json) {
      is JSON.Object -> exprObject(json)
      is JSON.String -> exprString(json.toJavaObject())
      is JSON.Bool -> BoolLiteral(json.booleanValue())
      is JSON.Integer, is JSON.Long -> IntegerLiteral(cast(json))
      is JSON.Double -> FloatLiteral(json)
      is JSON.Null -> NullLiteral()
      else -> throw ParserException("unexpected expression $json")
    }
  }

  private fun exprObject(json: JSON.Object): Expr {
    val stmts = ASTGen(json).parse()
    if (stmts.size != 1) {
      throw ParserException("unexpected ast $stmts, expecting one and only one expression to be generated")
    }
    val stmt = stmts[0]
    if (stmt !is Expr) {
      throw ParserException("unexpected ast $stmt, expecting expression")
    }
    return stmt
  }

  private fun exprArray(json: JSON.Array): List<Expr> {
    val res = ArrayList<Expr>(json.length())
    for (i in 0 until json.length()) {
      res.add(expr(json[i]))
    }
    return res
  }

  private fun exprString(input: String): Expr {
    if (input.startsWith("'") && input.endsWith("'")) {
      return StringLiteral(input.substring(1, input.length - 1))
    }

    val tokenizer = ExprTokenizer(input)
    val parser = ExprParser(tokenizer)
    val expr = parser.parse()
    if (tokenizer.peek() != null) {
      throw ParserException("only one expression can be used, but multiple found, next token: ${tokenizer.peek()}")
    }
    return expr
  }
}
