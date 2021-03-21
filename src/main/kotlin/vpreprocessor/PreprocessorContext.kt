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
package vpreprocessor

import vpreprocessor.semantic.Definition

class PreprocessorContext {
  private val parent: PreprocessorContext?
  val opts: PreprocessorOptions
  private val map = HashMap<String, Definition<*>>()

  /*#ifndef KOTLIN_NATIVE {{*/@JvmOverloads/*}}*/
  constructor(opts: PreprocessorOptions = PreprocessorOptions.KT) {
    this.parent = null
    this.opts = PreprocessorOptions(opts)
  }

  private constructor(parent: PreprocessorContext) {
    this.parent = parent
    this.opts = parent.opts
  }

  /*#ifndef KOTLIN_NATIVE {{*/@JvmOverloads/*}}*/
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
