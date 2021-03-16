/*
 * The MIT License
 *
 * Copyright 2021 wkgcass (https://github.com/wkgcass)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package vjson.deserializer.rule

import vjson.util.CoverageUtils.cast
import vjson.util.functional.`BiConsumer$`

class ObjectRule<O : Any>(val construct: () -> O) : Rule<O>() {
  private val rules: MutableMap<String, ObjectField<O, *>> = LinkedHashMap()

  constructor(construct: () -> O, superRule: ObjectRule<in O>) : this(construct) {
    for ((key, value) in superRule.rules) {
      @Suppress("UNCHECKED_CAST")
      rules[key] = cast(value)
    }
  }

  fun <V> put(key: String, set: (O, V) -> Unit, type: Rule<V>): ObjectRule<O> {
    require(!rules.containsKey(key)) { "duplicated key: $key" }
    rules[key] = ObjectField(set, type)
    return this
  }

  fun <V> put(key: String, set: `BiConsumer$`<O, V>, type: Rule<V>): ObjectRule<O> {
    return put(key, set as (O, V) -> Unit, type)
  }

  fun getRule(key: String): ObjectField<*, *>? {
    return rules[key]
  }

  override fun toString(sb: StringBuilder, processedListsOrObjects: MutableSet<Rule<*>>) {
    if (!processedListsOrObjects.add(this)) {
      sb.append("Object{...recursive...}")
      return
    }
    sb.append("Object{")
    var isFirst = true
    for ((key, value) in rules) {
      if (isFirst) {
        isFirst = false
      } else {
        sb.append(",")
      }
      sb.append(key).append("=>")
      value.rule.toString(sb, processedListsOrObjects)
    }
    sb.append("}")
  }
}
