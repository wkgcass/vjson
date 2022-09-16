package vjson.deserializer.rule

import vjson.util.functional.`Supplier$`

open class NullableRule<V>(val rule: Rule<V>, val opIfNull: () -> V?) : Rule<V?>() {
  // for java
  constructor(rule: Rule<V>, opIfNull: `Supplier$`<V?>) : this(rule, opIfNull as (() -> V?))

  // for convenience
  constructor(rule: Rule<V>) : this(rule, { null })

  override fun toString(sb: StringBuilder, processedListsOrObjects: MutableSet<Rule<*>>) {
    sb.append("(").append(rule.toString(sb, processedListsOrObjects)).append(")?")
  }

  override fun real(): Rule<*> {
    return rule.real()
  }
}
