import vjson.CharStream
import vjson.cs.LineCol
import vjson.cs.LineColCharStream
import vjson.ex.JsonParseException
import vjson.ex.ParserException
import vjson.parser.ObjectParser
import vjson.pl.*
import vjson.pl.ast.VariableDefinition
import vjson.pl.type.*
import vjson.pl.type.lang.StdTypes
import kotlin.js.Date

var outputFunc: (String) -> Unit = {
  println(it)
}

var cursorJumpFunc: (Int, Int) -> Unit = { _, _ ->
}

@ExperimentalJsExport
@JsExport
fun registerOutput(func: (String) -> Unit) {
  outputFunc = func
}

@ExperimentalJsExport
@JsExport
fun registerCursorJump(func: (Int, Int) -> Unit) {
  cursorJumpFunc = func
}

@ExperimentalJsExport
@JsExport
fun run(prog: String, printMem: Boolean) {
  val stdTypes = StdTypes()
  stdTypes.setOutput(outputFunc)
  val builder = InterpreterBuilder()
    .addTypes(stdTypes)

  val startTime = Date.now()

  val interpreter = try {
    builder.compile(prog)
  } catch (e: Throwable) {
    printParsingFailedMessage(e, "Compilation failed")
    return
  }

  val compileFinishTime = Date.now()

  val mem = try {
    interpreter.execute()
  } catch (e: Throwable) {
    outputFunc("Runtime failure")
    outputFunc(e.message ?: "")
    return
  }

  val executeFinishTime = Date.now()

  outputFunc("### compile time: ${compileFinishTime - startTime}ms, execute time: ${executeFinishTime - compileFinishTime}ms ###")

  if (printMem) {
    outputFunc(mem.toString())
  }
}

@ExperimentalJsExport
@JsExport
fun eval(_prog: String) {
  var prog = _prog
  if (!prog.startsWith("{")) {
    // the last line should be an expression
    val ls = ArrayList(prog.split("\n"))
    if (ls.isNotEmpty()) {
      val lastLine = ls.last()
      val ok = if (lastLine.trim().startsWith("new ")) {
        true
      } else {
        val parser = ExprParser(ExprTokenizer(CharStream.from(lastLine), LineCol.EMPTY))
        try {
          parser.parse()
          true
        } catch (e: Throwable) {
          false
        }
      }
      if (ok) {
        outputFunc("### extra characters are added to last line (${lastLine.trim()}) by eval process ###")
        val newLine = if (lastLine.trim().startsWith("new ")) {
          "var \"$$$\" = {${lastLine.trim()}};var \"@@@\" = $$$.toString:[]"
        } else {
          "var \"@@@\" = (${lastLine.trim()})"
        }
        outputFunc("### the last line is now transformed into: ###\n### $newLine ###")
        ls.removeLast()
        ls.add(newLine)

        prog = ls.joinToString(separator = "\n")
      }
    }
    prog = "{$prog}"
  }

  val startTime = Date.now()

  val jsonObj = try {
    ObjectParser(InterpreterBuilder.interpreterOptions()).last(
      LineColCharStream(CharStream.from(prog), "")
    )!!
  } catch (e: Throwable) {
    printParsingFailedMessage(e, "Compilation failed")
    return
  }
  val ast = ArrayList(
    try {
      ASTGen(jsonObj).parse()
    } catch (e: Throwable) {
      printParsingFailedMessage(e, "Compilation failed")
      return
    }
  )
  var lastVarDef: VariableDefinition? = null
  if (ast.isNotEmpty() && ast.last() is VariableDefinition) {
    lastVarDef = ast.last() as VariableDefinition
  }

  val stdTypes = StdTypes()
  stdTypes.setOutput(outputFunc)

  val interpreter = try {
    Interpreter(listOf(stdTypes), ast)
  } catch (e: Throwable) {
    printParsingFailedMessage(e, "Compilation failed")
    return
  }

  val compileFinishTime = Date.now()

  val mem = try {
    interpreter.execute()
  } catch (e: Throwable) {
    outputFunc("Runtime failure")
    outputFunc(e.message ?: "")
    return
  }

  val executeFinishTime = Date.now()

  outputFunc("### compile time: ${compileFinishTime - startTime}ms, execute time: ${executeFinishTime - compileFinishTime}ms ###")

  if (lastVarDef == null) {
    outputFunc("### Last statement is not expression nor variable definition ###")
    return
  }
  if (lastVarDef.value.typeInstance() !is BuiltInTypeInstance) {
    outputFunc("### Type of the last statement is not a built-in type ###")
    outputFunc("### you may need to add .toString:[] in order to print the value ###")
    return
  }
  val index = lastVarDef.getMemPos().index
  outputFunc(
    when (lastVarDef.value.typeInstance()) {
      IntType -> mem.getInt(index).toString()
      LongType -> mem.getLong(index).toString()
      FloatType -> mem.getFloat(index).toString()
      DoubleType -> mem.getDouble(index).toString()
      else -> mem.getRef(index).toString()
    }
  )
}

fun printParsingFailedMessage(e: Throwable, msg: String = "Parsing failed") {
  outputFunc(msg)
  outputFunc(e.message ?: "")
  if (e is ParserException) {
    val lineCol = e.lineCol
    if (lineCol != null) {
      cursorJumpFunc(lineCol.line, lineCol.col)
    }
  }
}

fun getJson(prog: String): vjson.JSON.Object? {
  val parser = ObjectParser(InterpreterBuilder.interpreterOptions())
  val obj = try {
    parser.last(LineColCharStream(CharStream.from(prog), ""))
  } catch (e: JsonParseException) {
    printParsingFailedMessage(e)
    return null
  }
  if (obj == null) { // should not happen, but we check it anyway
    outputFunc("Parsing failed, no output generated")
    return null
  }
  return obj
}

@ExperimentalJsExport
@JsExport
fun json(prog: String) {
  val obj = getJson(prog) ?: return
  outputFunc(obj.pretty())
}

@ExperimentalJsExport
@JsExport
fun ast(prog: String) {
  val obj = getJson(prog) ?: return
  val astGen = ASTGen(obj)
  val ast = try {
    astGen.parse()
  } catch (e: ParserException) {
    printParsingFailedMessage(e)
    return
  }
  outputFunc(ast.toString())
}
