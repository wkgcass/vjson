package vpreprocessor.semantic

import kotlin.reflect.KClass

data class Definition<T> constructor(val key: String, val value: T, val type: KClass<*>) {
  override fun toString(): String {
    return "define " + key + (if (value == null) "" else " $value")
  }
}
