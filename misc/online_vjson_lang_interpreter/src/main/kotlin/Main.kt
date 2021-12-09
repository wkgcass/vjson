import vjson.ex.ParserException
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
fun run(prog: String) {
  val stdTypes = StdTypes()
  stdTypes.setOutput(outputFunc)
  val builder = InterpreterBuilder()
    .addTypes(stdTypes)
  val interpreter = try {
    builder.compile(prog)
  } catch (e: Throwable) {
    outputFunc("Compilation failed")
    outputFunc(e.toString())
    if (e is ParserException) {
      val lineCol = e.lineCol
      if (lineCol != null) {
        cursorJumpFunc(lineCol.line, lineCol.col)
      }
    }
    return
  }
  try {
    interpreter.execute()
  } catch (e: Throwable) {
    outputFunc("Runtime failure")
    outputFunc(e.toString())
  }
}
