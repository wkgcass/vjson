package vjson.parser

interface ParserCacheHolder {
  fun threadLocalArrayParser(): ArrayParser?
  fun threadLocalArrayParser(parser: ArrayParser)
  fun threadLocalObjectParser(): ObjectParser?
  fun threadLocalObjectParser(parser: ObjectParser)
  fun threadLocalStringParser(): StringParser?
  fun threadLocalStringParser(parser: StringParser)
  fun threadLocalArrayParserJavaObject(): ArrayParser?
  fun threadLocalArrayParserJavaObject(parser: ArrayParser)
  fun threadLocalObjectParserJavaObject(): ObjectParser?
  fun threadLocalObjectParserJavaObject(parser: ObjectParser)
  fun threadLocalStringParserJavaObject(): StringParser?
  fun threadLocalStringParserJavaObject(parser: StringParser)
}
