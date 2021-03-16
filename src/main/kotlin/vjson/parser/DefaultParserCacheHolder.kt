// #ifndef KOTLIN_NATIVE {{
package vjson.parser

open class DefaultParserCacheHolder : ParserCacheHolder {
  var isStarted = false
    private set

  companion object {
    private val threadLocalArrayParser = ThreadLocal<ArrayParser>()
    private val threadLocalObjectParser = ThreadLocal<ObjectParser>()
    private val threadLocalStringParser = ThreadLocal<StringParser>()
    private val threadLocalArrayParserJavaObject = ThreadLocal<ArrayParser>()
    private val threadLocalObjectParserJavaObject = ThreadLocal<ObjectParser>()
    private val threadLocalStringParserJavaObject = ThreadLocal<StringParser>()
  }

  override fun threadLocalArrayParser(): ArrayParser? {
    return threadLocalArrayParser.get()
  }

  override fun threadLocalArrayParser(parser: ArrayParser) {
    isStarted = true
    threadLocalArrayParser.set(parser)
  }

  override fun threadLocalObjectParser(): ObjectParser? {
    return threadLocalObjectParser.get()
  }

  override fun threadLocalObjectParser(parser: ObjectParser) {
    isStarted = true
    threadLocalObjectParser.set(parser)
  }

  override fun threadLocalStringParser(): StringParser? {
    return threadLocalStringParser.get()
  }

  override fun threadLocalStringParser(parser: StringParser) {
    isStarted = true
    threadLocalStringParser.set(parser)
  }

  override fun threadLocalArrayParserJavaObject(): ArrayParser? {
    return threadLocalArrayParserJavaObject.get()
  }

  override fun threadLocalArrayParserJavaObject(parser: ArrayParser) {
    isStarted = true
    threadLocalArrayParserJavaObject.set(parser)
  }

  override fun threadLocalObjectParserJavaObject(): ObjectParser? {
    return threadLocalObjectParserJavaObject.get()
  }

  override fun threadLocalObjectParserJavaObject(parser: ObjectParser) {
    isStarted = true
    threadLocalObjectParserJavaObject.set(parser)
  }

  override fun threadLocalStringParserJavaObject(): StringParser? {
    return threadLocalStringParserJavaObject.get()
  }

  override fun threadLocalStringParserJavaObject(parser: StringParser) {
    isStarted = true
    threadLocalStringParserJavaObject.set(parser)
  }
}
// }}
