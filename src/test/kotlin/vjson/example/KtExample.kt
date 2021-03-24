package vjson.example

import vjson.JSON.deserialize
import vjson.deserializer.rule.ArrayRule
import vjson.deserializer.rule.DoubleRule
import vjson.deserializer.rule.ObjectRule
import vjson.deserializer.rule.StringRule
import vjson.example.Example.Good
import vjson.example.Example.Shop
import vjson.util.ArrayBuilder
import java.util.*
import kotlin.collections.ArrayList

fun main() {
  val array = ArrayBuilder()
    .addObject {
      it.put("id", UUID.randomUUID().toString())
        .put("name", "pizza")
        .put("price", 5.12)
    }
    .addObject {
      it.put("id", UUID.randomUUID().toString())
        .put("name", "milk")
        .put("price", 1.28)
    }
    .build()
  println("build result == $array")
  println("build result pretty() == ${array.pretty()}")

  val shopRule = ObjectRule(::Shop)
    .put("name", Shop::setName, StringRule())
    .put(
      "goods", Shop::setGoods, ArrayRule(
        ::ArrayList,
        { ls, e -> ls.add(e) },
        ObjectRule(::Good)
          .put("id", Good::setId, StringRule())
          .put("name", Good::setName, StringRule())
          .put("price", Good::setPrice, DoubleRule())
      )
    )
  val shop = deserialize("{\"name\":\"HuaLian\",\"goods\":" + array.stringify() + "}", shopRule)
  println("deserialize result: $shop")
}
