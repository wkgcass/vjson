import vjson.pl.InterpreterBuilder
import vjson.pl.type.lang.StdTypes

var outputFunc: (String) -> Unit = {
  println(it)
}

@ExperimentalJsExport
@JsExport
fun registerOutput(func: (String) -> Unit) {
  outputFunc = func
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
    if (e.message != null) {
      outputFunc(e.message!!)
    }
    return
  }
  try {
    interpreter.execute()
  } catch (e: Throwable) {
    outputFunc("Runtime failure")
    if (e.message != null) {
      outputFunc(e.message!!)
    }
  }
}
