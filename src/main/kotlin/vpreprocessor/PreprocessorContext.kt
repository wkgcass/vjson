package vpreprocessor

import vpreprocessor.semantic.Definition

class PreprocessorContext {
  private val parent: PreprocessorContext?
  val opts: PreprocessorOptions
  private val map = HashMap<String, Definition<*>>()

  @JvmOverloads
  constructor(opts: PreprocessorOptions = PreprocessorOptions.KT) {
    this.parent = null
    this.opts = PreprocessorOptions(opts)
  }

  private constructor(parent: PreprocessorContext) {
    this.parent = parent
    this.opts = parent.opts
  }

  @JvmOverloads
  fun define(key: String, value: String? = null) {
    map[key] = Definition(key, value, String::class)
  }

  fun isDefined(key: String): Boolean {
    return getDefinition(key) != null
  }

  fun getDefinition(key: String): Definition<*>? {
    if (map.containsKey(key)) {
      return map[key]
    }
    if (parent == null) {
      return null
    }
    return parent.getDefinition(key)
  }

  fun createChild(): PreprocessorContext {
    return PreprocessorContext(this)
  }
}
