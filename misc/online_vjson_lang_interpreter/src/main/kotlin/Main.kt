import vjson.CharStream
import vjson.cs.LineColCharStream
import vjson.ex.JsonParseException
import vjson.ex.ParserException
import vjson.parser.ObjectParser
import vjson.pl.ASTGen
import vjson.pl.InterpreterBuilder
import vjson.pl.type.lang.StdTypes

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
  val interpreter = try {
    builder.compile(prog)
  } catch (e: Throwable) {
    printParsingFailedMessage(e, "Compilation failed")
    return
  }
  val mem = try {
    interpreter.execute()
  } catch (e: Throwable) {
    outputFunc("Runtime failure")
    outputFunc(e.message ?: "")
    return
  }
  if (printMem) {
    outputFunc(mem.toString())
  }
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
