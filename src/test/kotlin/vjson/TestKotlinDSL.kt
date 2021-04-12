package vjson

import org.junit.Assert.assertEquals
import org.junit.Test
import vjson.deserializer.rule.*
import vjson.util.ObjectBuilder

class TestKotlinDSL {
  open class Xxx(val x: Int, val y: String, val z: Aaa) {
    protected fun toJsonObject(f: ObjectBuilder.() -> Unit): ObjectBuilder = ObjectBuilder {
      put("x", x)
      put("y", y)
      putInst("z", z.toJson())
      f(this)
    }

    open fun toJson(): JSON.Object = toJsonObject {}.build()
  }

  open class XxxBuilder(var x: Int = 0, var y: String = "", var z: Aaa = Aaa()) {
    open fun build(): Xxx = Xxx(x, y, z)
  }

  class Yyy(x: Int, y: String, z: Aaa, val a: Long) : Xxx(x, y, z) {
    override fun toJson(): JSON.Object = toJsonObject {
      put("a", a)
    }.build()
  }

  open class YyyBuilder(var a: Long = 0) : XxxBuilder() {
    override fun build(): Yyy = Yyy(x, y, z, a)
  }

  open class Aaa(var a: Int = 0, var b: String = "") {
    protected fun toJsonObject(f: ObjectBuilder.() -> Unit): ObjectBuilder = ObjectBuilder {
      type("aaa")
      put("a", a)
      put("b", b)
      f(this)
    }

    open fun toJson(): JSON.Object = toJsonObject {}.build()
  }

  class Bbb(a: Int = 0, b: String = "", var c: Long = 0) : Aaa(a, b) {
    override fun toJson(): JSON.Object = toJsonObject {
      type(Bbb::class)
      put("c", c)
    }.build()
  }

  class Mmm(var m: Int = 0, var n: String = "", var o: List<Aaa> = emptyList()) {
    fun toJson(): JSON.Object = ObjectBuilder {
      put("m", m)
      put("n", n)
      putArray("o") {
        for (e in o) {
          addInst(e.toJson())
        }
      }
    }.build()
  }

  class Ls<E> {
    private val ls = ArrayList<E>()

    fun add(e: E) {
      ls.add(e)
    }

    fun build(): List<E> = ls
  }

  private val aaaRule = ObjectRule(::Aaa) {
    put("a", IntRule) { a = it }
    put("b", StringRule) { b = it }
  }
  private val bbbRule = ObjectRule(::Bbb, aaaRule) {
    put("c", LongRule) { c = it }
  }
  private val aaaTypeRule = TypeRule<Aaa>()
    .type("aaa") { aaaRule }
    .type(Bbb::class) { bbbRule }
  private val xxxRule = ObjectRule.builder(::XxxBuilder, { build() }) {
    put("x", IntRule) { x = it }
    put("y", StringRule) { y = it }
    put("z", aaaRule) { z = it }
  }
  private val yyyRule = ObjectRule.builder(::YyyBuilder, xxxRule, { build() }) {
    put("a", LongRule) { a = it }
  }
  private val lsRule = ArrayRule({ ArrayList<Aaa>() }, { add(it) }) { aaaTypeRule }
  private val lsRuleAnotherWay = ArrayRule({ ArrayList<Aaa>() }, aaaTypeRule) { add(it) }
  private val lsBRule = ArrayRule.builder({ Ls<Aaa>() }, { build() }, { add(it) }) { aaaTypeRule }
  private val mmmRule = ObjectRule(::Mmm) {
    put("m", IntRule) { m = it }
    put("n", StringRule) { n = it }
    put("o", lsRule) { o = it }
  }
  private val mmmRuleAnotherWay = ObjectRule(::Mmm) {
    put("m", IntRule) { m = it }
    put("n", StringRule) { n = it }
    put("o", { o = it }) { lsRuleAnotherWay }
  }
  private val mmmBRule = ObjectRule(::Mmm) {
    put("m", IntRule) { m = it }
    put("n", StringRule) { n = it }
    put("o", lsBRule) { o = it }
  }

  @Test
  fun simpleObject() {
    val aaa = Aaa(1, "2")
    val aaa2 = JSON.deserialize(aaa.toJson().stringify(), aaaRule)
    assertEquals(aaa.toJson(), aaa2.toJson())
  }

  @Test
  fun inherit() {
    val bbb = Bbb(1, "2", 3)
    val bbb2 = JSON.deserialize(bbb.toJson().stringify(), bbbRule)
    assertEquals(bbb.toJson(), bbb2.toJson())
  }

  @Test
  fun objectBuilder() {
    val xxx = Xxx(1, "2", Aaa(3, "4"))
    val xxx2 = JSON.deserialize(xxx.toJson().stringify(), xxxRule)
    assertEquals(xxx.toJson(), xxx2.toJson())
  }

  @Test
  fun builderInherit() {
    val yyy = Yyy(1, "2", Aaa(3, "4"), 5)
    val yyy2 = JSON.deserialize(yyy.toJson().stringify(), yyyRule)
    assertEquals(yyy.toJson(), yyy2.toJson())
  }

  @Test
  fun simpleArray() {
    val mmm = Mmm(1, "2", listOf(Bbb(3, "4", 5), Bbb(7, "8", 9), Aaa(10, "11"), Aaa(12, "13")))
    val mmm2 = JSON.deserialize(mmm.toJson().stringify(), mmmRule)
    assertEquals(mmm.toJson(), mmm2.toJson())
    val mmm3 = JSON.deserialize(mmm.toJson().stringify(), mmmRuleAnotherWay)
    assertEquals(mmm.toJson(), mmm3.toJson())
  }

  @Test
  fun arrayBuilder() {
    val mmm = Mmm(1, "2", listOf(Bbb(3, "4", 5), Bbb(7, "8", 9), Aaa(10, "11"), Aaa(12, "13")))
    val mmm2 = JSON.deserialize(mmm.toJson().stringify(), mmmBRule)
    assertEquals(mmm.toJson(), mmm2.toJson())
  }
}
