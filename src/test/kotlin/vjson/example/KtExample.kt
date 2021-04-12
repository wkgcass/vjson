package vjson.example

import vjson.JSON.deserialize
import vjson.deserializer.rule.ArrayRule
import vjson.deserializer.rule.DoubleRule
import vjson.deserializer.rule.ObjectRule
import vjson.deserializer.rule.StringRule
import vjson.util.ArrayBuilder
import java.util.*
import kotlin.collections.ArrayList

data class Shop(val name: String, val goods: List<Good>)
class ShopBuilder {
  var name: String = ""
  var goods: List<Good> = emptyList()
  fun build(): Shop = Shop(name, goods)
}

data class Good(val id: String, val name: String, val price: Double)
class GoodBuilder {
  var id: String = ""
  var name: String = ""
  var price: Double = 0.0
  fun build(): Good = Good(id, name, price)
}

fun main() {
  val array = ArrayBuilder()
    .addObject {
      put("id", UUID.randomUUID().toString())
      put("name", "pizza")
      put("price", 5.12)
    }
    .addObject {
      put("id", UUID.randomUUID().toString())
      put("name", "milk")
      put("price", 1.28)
    }
    .build()
  println("build result == $array")
  println("build result pretty() == ${array.pretty()}")

  val shopRule = ObjectRule.builder(::ShopBuilder, { build() }) {
    put("name", StringRule) { name = it }
    put("goods", { goods = it }) {
      ArrayRule.builder({ ArrayList<Good>() }, { Collections.unmodifiableList(this) }, { add(it) }) {
        ObjectRule.builder(::GoodBuilder, { build() }) {
          put("id", StringRule) { id = it }
          put("name", StringRule) { name = it }
          put("price", DoubleRule) { price = it }
        }
      }
    }
  }
  val shop = deserialize("{\"name\":\"HuaLian\",\"goods\":" + array.stringify() + "}", shopRule)
  println("deserialize result: $shop")
}
