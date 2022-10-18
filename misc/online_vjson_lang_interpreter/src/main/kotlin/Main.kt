import vjson.CharStream
import vjson.cs.LineCol
import vjson.cs.LineColCharStream
import vjson.ex.JsonParseException
import vjson.ex.ParserException
import vjson.parser.ObjectParser
import vjson.pl.*
import vjson.pl.ast.VariableDefinition
import vjson.pl.inst.ActionContext
import vjson.pl.inst.RuntimeMemory
import vjson.pl.type.*
import vjson.pl.type.lang.ExtFunctions
import vjson.pl.type.lang.ExtTypes
import vjson.pl.type.lang.StdTypes
import vjson.simple.SimpleString
import kotlin.js.Date
import kotlin.random.Random

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
  val extTypes = ExtTypes(ExtFunctions()
    .setCurrentTimeMillisBlock { Date.now().toLong() }
    .setRandBlock { Random.nextDouble() }
  )

  val builder = InterpreterBuilder()
    .addTypes(stdTypes)
    .addTypes(extTypes)

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
    doPrintMem(interpreter.getExplorer(), mem, 0)
  }
}

fun doPrintMem(explorer: RuntimeMemoryExplorer, mem: RuntimeMemory, indent: Int) {
  val variables = explorer.listVariables()
  for (name in variables) {
    val modifiers = explorer.getModifiersOfVariable(name)
    var modifiersStr = modifiers.toString()
    if (modifiersStr.isNotEmpty()) {
      modifiersStr += " "
    }

    val value = explorer.getVariable(name, mem)
    if (value is RuntimeMemory) {
      val typeExp = explorer.getExplorerByVariable(name)
      if (typeExp == null) {
        outputFunc("${" ".repeat(indent)}$modifiersStr$name = <no info: $value>")
      } else {
        outputFunc("${" ".repeat(indent)}$modifiersStr$name =")
        doPrintMem(typeExp, value, indent + 2)
      }
    } else if (value is Array<*>) {
      val arrType = explorer.getTypeByVariable(name)
      val elementType = arrType.elementType(TypeContext(MemoryAllocator()))
      doPrintArray(value, elementType, explorer, indent, "$modifiersStr$name = ")
    } else if (value is String) {
      outputFunc("${" ".repeat(indent)}$modifiersStr$name = ${SimpleString(value).stringify()}")
    } else {
      outputFunc("${" ".repeat(indent)}$modifiersStr$name = $value")
    }
  }
}

fun doPrintArray(array: Array<*>, elementType: TypeInstance?, explorer: RuntimeMemoryExplorer, indent: Int, printPrefix: String) {
  outputFunc("${" ".repeat(indent)}$printPrefix[")
  for (e in array) {
    doPrintArrayElement(e, elementType, explorer, indent + 2)
  }
  outputFunc("${" ".repeat(indent)}]")
}

fun doPrintArrayElement(e: Any?, elementType: TypeInstance?, explorer: RuntimeMemoryExplorer, indent: Int) {
  if (e is ActionContext) {
    if (elementType == null || elementType !is ClassTypeInstance) {
      outputFunc("${" ".repeat(indent)}<no info: $e>")
    } else {
      val clsName = elementType._concreteTypeName ?: elementType.cls.name
      val typeExp = try {
        explorer.getExplorerByType(clsName)
      } catch (_: NoSuchElementException) {
        null
      }
      if (typeExp == null) {
        outputFunc("${" ".repeat(indent)}<no info: $e>")
      } else {
        outputFunc("${" ".repeat(indent)}{")
        doPrintMem(typeExp, e.getCurrentMem(), indent + 2)
        outputFunc("${" ".repeat(indent)}}")
      }
    }
  } else if (e is Array<*>) {
    val subElementType = elementType?.elementType(TypeContext(MemoryAllocator()))
    doPrintArray(e, subElementType, explorer, indent, "")
  } else {
    outputFunc("${" ".repeat(indent)}$e")
  }
}

@ExperimentalJsExport
@JsExport
fun eval(_prog: String) {
  var prog = _prog
  if (!prog.startsWith("{")) {
    // the last line should be an expression
    val ls = ArrayList(prog.split("\n"))
    var lastLine = ""
    while (lastLine.isBlank()) {
      lastLine = ls.removeLast()
    }
    if (ls.isNotEmpty()) {
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
        val newLine = "var \"@@@\" = (${lastLine.trim()})"
        outputFunc("### the last line is now transformed into: ###\n### $newLine ###")
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

  val extTypes = ExtTypes(ExtFunctions()
    .setCurrentTimeMillisBlock { Date.now().toLong() }
    .setRandBlock { Random.nextDouble() }
  )

  val interpreter = try {
    Interpreter(listOf(stdTypes, extTypes), ast)
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
  val index = lastVarDef.getMemPos().index
  when (val type = lastVarDef.value.typeInstance()) {
    IntType -> outputFunc(mem.getInt(index).toString())
    LongType -> outputFunc(mem.getLong(index).toString())
    FloatType -> outputFunc(mem.getFloat(index).toString())
    DoubleType -> outputFunc(mem.getDouble(index).toString())
    BoolType -> outputFunc(mem.getBool(index).toString())
    StringType -> outputFunc(mem.getRef(index).toString())
    else -> {
      val explorer = interpreter.getExplorer()
      val value = mem.getRef(index)
      if (type is ClassTypeInstance && value is ActionContext) {
        val typeExp = explorer.getExplorerByVariable(lastVarDef.name)
        if (typeExp == null) {
          outputFunc("<no info: $value>")
        } else {
          doPrintMem(typeExp, value.getCurrentMem(), 0)
        }
      } else if (value is Array<*>) {
        val elementType = type.elementType(TypeContext(MemoryAllocator()))
        doPrintArray(value, elementType, explorer, 0, "")
      } else {
        outputFunc("<no info: $value>")
      }
    }
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
  for (stmt in ast) {
    outputFunc(stmt.toString())
  }
}
