package vjson.parser

class KotlinNativeParserCacheHolder : ParserCacheHolder {
  /* #ifdef KOTLIN_NATIVE {{@ThreadLocal}}*/
  companion object {
    private var arrayParser: ArrayParser? = null
    private var objectParser: ObjectParser? = null
    private var stringParser: StringParser? = null
    private var arrayParser4j: ArrayParser? = null
    private var objectParser4j: ObjectParser? = null
    private var stringParser4j: StringParser? = null
  }

  override fun threadLocalArrayParser(): ArrayParser? {
    return arrayParser
  }

  override fun threadLocalArrayParser(parser: ArrayParser) {
    arrayParser = parser
  }

  override fun threadLocalObjectParser(): ObjectParser? {
    return objectParser
  }

  override fun threadLocalObjectParser(parser: ObjectParser) {
    objectParser = parser
  }

  override fun threadLocalStringParser(): StringParser? {
    return stringParser
  }

  override fun threadLocalStringParser(parser: StringParser) {
    stringParser = parser
  }

  override fun threadLocalArrayParserJavaObject(): ArrayParser? {
    return arrayParser4j
  }

  override fun threadLocalArrayParserJavaObject(parser: ArrayParser) {
    arrayParser4j = parser
  }

  override fun threadLocalObjectParserJavaObject(): ObjectParser? {
    return objectParser4j
  }

  override fun threadLocalObjectParserJavaObject(parser: ObjectParser) {
    objectParser4j = parser
  }

  override fun threadLocalStringParserJavaObject(): StringParser? {
    return stringParser4j
  }

  override fun threadLocalStringParserJavaObject(parser: StringParser) {
    stringParser4j = parser
  }
}
