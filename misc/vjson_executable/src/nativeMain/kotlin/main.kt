import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import vjson.CharStream
import vjson.JSON
import vjson.Parser
import vjson.cs.LineColCharStream
import vjson.cs.PeekCharStream
import vjson.ex.JsonParseException
import vjson.parser.*
import vjson.pl.InterpreterBuilder
import vjson.pl.type.lang.ExtFunctions
import vjson.pl.type.lang.ExtTypes
import vjson.pl.type.lang.StdTypes
import vjson.util.Manager
import kotlin.random.Random

private const val HELP_STR = """Usage:
  vjson [options] [filename]
Options:
  --pretty[=true|false]                  print pretty json, default true
  --one[=true|false]                     only parse one json instance from input, default true
  --features=<feature1,feature2,...>     enable additional vjson features, default no features
                                         available features:
                                           AllowSkippingCommas
                                           AllowObjectEntryWithoutValue
                                           AllowOmittingColonBeforeBraces
                                           EqualAsColon
                                           KeyNoQuotes
                                           KeyNoQuotesAnyChar
                                           SemicolonAsComma
                                           StringSingleQuotes
                                           StringValueNoQuotes
                                           all
"""

private val allowedFeatures = mapOf<String, (ParserOptions) -> Unit>(
  "AllowSkippingCommas" to { it.setAllowSkippingCommas(true) },
  "AllowObjectEntryWithoutValue" to { it.setAllowObjectEntryWithoutValue(true) },
  "AllowOmittingColonBeforeBraces" to { it.setAllowOmittingColonBeforeBraces(true) },
  "EqualAsColon" to { it.setEqualAsColon(true) },
  "KeyNoQuotes" to { it.setKeyNoQuotes(true) },
  "KeyNoQuotesAnyChar" to { it.setKeyNoQuotesAnyChar(true) },
  "SemicolonAsComma" to { it.setSemicolonAsComma(true) },
  "StringSingleQuotes" to { it.setStringSingleQuotes(true) },
  "StringValueNoQuotes" to { it.setStringValueNoQuotes(true) },
)

fun main(args: Array<String>) {
  var filename: String? = null
  var script: Boolean? = null
  //
  var pretty: Boolean? = null
  var one: Boolean? = null
  val features = ArrayList<(ParserOptions) -> Unit>()
  //
  var ast: Boolean? = null

  for (arg in args) {
    if (arg == "--help" || arg == "-h" || arg == "-help" || arg == "help") {
      print(HELP_STR)
      return
    }
    if (!arg.startsWith("--")) {
      filename = arg.trim()
      continue
    } else {
      if (filename != null) {
        throw IllegalArgumentException("extra argument provided: $arg")
      }
    }
    if (
      parseBoolArg(arg, "pretty") { pretty = it } ||
      parseBoolArg(arg, "one") { one = it } ||
      parseBoolArg(arg, "script") { script = it } ||
      parseBoolArg(arg, "ast") { ast = it }
    ) {
      continue
    }
    if (arg.startsWith("--features=")) {
      val v = arg.substring("--features=".length).trim()
      if (v == "") {
        throw IllegalArgumentException("empty features list")
      }
      val featuresArray = v.split(",")
      for (_f in featuresArray) {
        val f = _f.trim()
        if (f == "all") {
          features.addAll(allowedFeatures.values)
        } else if (!allowedFeatures.containsKey(f)) {
          throw IllegalArgumentException("unknown feature: $f")
        } else {
          features.add(allowedFeatures[f]!!)
        }
      }
      continue
    }
    throw IllegalArgumentException("unknown argument: $arg")
  }

  if (script == null) script = false
  if (script!!) {
    if (ast == null) ast = false
  } else {
    if (pretty == null) pretty = true
    if (one == null) one = true
  }

  if (script!!) {
    if (filename == null) {
      throw IllegalArgumentException("missing script file to be executed")
    }
    runScript(RunScriptOptions(filename = filename, ast = ast!!))
  } else {
    val filecontent = if (filename == null) null else readAll(filename)
    runJson(input = filecontent, RunJsonOptions(filename = filename, pretty = pretty!!, one = one!!, features = features))
  }
}

private fun parseBoolArg(arg: String, expected: String, func: (Boolean) -> Unit): Boolean {
  if (!arg.startsWith("--$expected")) {
    return false
  }
  var v = arg.substring(expected.length + 2)
  if (v == "") {
    func(true)
    return true
  }
  if (!v.startsWith("=")) {
    throw IllegalArgumentException("unknown argument: $arg")
  }
  v = v.substring(1)
  func(parseBool(expected, v))
  return true
}

private fun parseBool(argname: String, value: String): Boolean {
  if (value == "true" || value == "on") {
    return true
  } else if (value == "false" || value == "off") {
    return false
  } else {
    throw IllegalArgumentException("$value is not a valid bool value for parameter $argname")
  }
}

private fun readAll(filename: String): String {
  return FileSystem.SYSTEM.read(filename.toPath()) { readUtf8() }
}

data class RunScriptOptions(val filename: String, val ast: Boolean)

class FileContentManager(baseFile: String) : Manager<String> {
  private val baseDir = baseFile.toPath().parent!!
  override fun provide(name: String): (() -> String)? {
    val file = baseDir / name
    if (FileSystem.SYSTEM.exists(file)) {
      val meta = FileSystem.SYSTEM.metadata(file)
      if (meta.isRegularFile) {
        return { readFile(file) }
      }
    }
    return null
  }

  private fun readFile(file: Path): String = FileSystem.SYSTEM.read(file) { readUtf8() }
}

private fun runScript(options: RunScriptOptions) {
  val stdTypes = StdTypes()
  stdTypes.setOutput { println(it) }
  val extTypes = ExtTypes(ExtFunctions()
    .setCurrentTimeMillisBlock { Clock.System.now().toEpochMilliseconds() }
    .setRandBlock { Random.nextDouble() })
  val builder = InterpreterBuilder()
    .addTypes(stdTypes)
    .addTypes(extTypes)
  val interpreter = builder.compile(FileContentManager(options.filename), options.filename.toPath().name)

  @Suppress("UNUSED_VARIABLE")
  val mem = interpreter.execute()
}

data class RunJsonOptions(val filename: String?, val pretty: Boolean, val one: Boolean, val features: List<(ParserOptions) -> Unit>)

private fun runJson(input: String?, options: RunJsonOptions) {
  val opts = ParserOptions()
  for (f in options.features) {
    f(opts)
  }
  opts.setMode(ParserMode.DEFAULT)

  if (input == null) {
    var started = false
    var ended = true
    var useObjectParser = false
    val objectParser = ObjectParser(opts)
    val arrayParser = ArrayParser(opts)
    var cs: CharStream = CharStream.from("")

    fun runObjectParser() {
      if (feedParser(objectParser, cs, options)) {
        objectParser.reset()
        ended = true
        cs.skipBlank()
      } else {
        useObjectParser = true
      }
    }

    fun runArrayParser() {
      if (feedParser(arrayParser, cs, options)) {
        arrayParser.reset()
        ended = true
        cs.skipBlank()
      } else {
        useObjectParser = false
      }
    }

    while (true) {
      if (!cs.hasNext()) {
        val line = readLine()
        if (line == null) {
          if (!started && ended) {
            throw IllegalArgumentException("no input json")
          }
          @Suppress("ControlFlowWithEmptyBody")
          if (ended) {
          } else if (useObjectParser) {
            objectParser.end()
          } else {
            arrayParser.end()
          }
          break
        }
        cs = CharStream.from(line)
        cs.skipBlank()
        if (!cs.hasNext()) {
          continue
        }
        started = true
        ended = false
      }

      if (!ended) {
        @Suppress("LocalVariableName")
        val _cs = cs
        val pcs = PeekCharStream(_cs)
        cs = pcs
        try {
          runObjectParser()
          if (ended) {
            if (options.one) {
              break
            }
          } else {
            _cs.skip(pcs.getCursor())
            cs = _cs
          }
        } catch (e1: JsonParseException) {
          objectParser.reset()
          cs = _cs
          try {
            runArrayParser()
            if (ended) {
              if (options.one) {
                break
              }
            } else {
              _cs.skip(pcs.getCursor())
              cs = _cs
            }
          } catch (e2: JsonParseException) {
            throw IllegalArgumentException("input is not a valid json object or array")
          }
        }
      } else if (useObjectParser) {
        runObjectParser()
        if (ended && options.one) {
          break
        }
      } else {
        runArrayParser()
        if (ended && options.one) {
          break
        }
      }
    }
  } else {
    val cs = LineColCharStream(CharStream.from(input), options.filename!!)
    output(ParserUtils.buildFrom(cs, opts), options)
  }
}

private fun feedParser(
  parser: Parser<*>,
  cs: CharStream,
  options: RunJsonOptions
): Boolean { // return true if parser state needs to be reset
  val obj = parser.feed(cs) ?: return false
  output(obj, options)
  if (options.one) {
    cs.skipBlank()
    if (cs.hasNext()) {
      throw IllegalArgumentException("input stream contains extra characters")
    }
  }
  return true
}

private fun output(json: JSON.Instance<*>, options: RunJsonOptions) {
  if (options.pretty) {
    println(json.pretty())
  } else {
    print(json)
  }
}
