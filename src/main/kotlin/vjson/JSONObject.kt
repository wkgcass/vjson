package vjson

import vjson.deserializer.rule.ArrayRule
import vjson.deserializer.rule.Rule
import vjson.util.ArrayBuilder

interface JSONObject {
  fun toJson(): JSON.Object

  companion object {
    fun <T : JSONObject> Collection<T?>.toJson(): JSON.Array {
      val ab = ArrayBuilder()
      for (e in this) {
        if (e == null) {
          ab.add(null)
        } else {
          ab.addInst(e.toJson())
        }
      }
      return ab.build()
    }

    /*#ifndef KOTLIN_NATIVE {{ */@JvmStatic/*}}*/
    fun <T : JSONObject> listToJson(ls: Collection<T?>): JSON.Array = ls.toJson()

    /*#ifndef KOTLIN_NATIVE {{ */@JvmStatic/*}}*/
    fun <T> buildArrayRule(rule: Rule<T>): ArrayRule<MutableList<T>, T> = ArrayRule({ ArrayList() }, { add(it) }, rule)
  }
}
