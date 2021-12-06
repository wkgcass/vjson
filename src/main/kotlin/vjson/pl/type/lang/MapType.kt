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

package vjson.pl.type.lang

import vjson.pl.ast.Type
import vjson.pl.type.*

class MapType(private val alias: String, private val key: TypeInstance, private val value: TypeInstance) : TypeInstance, PluggableType {
  private val entryType = Entry()
  private val keySetType = SetType("$alias.KeySet", key)
  private val entrySetType = SetType("$alias.EntrySet", entryType)

  override fun initiate(ctx: TypeContext) {
    ctx.addType(Type(alias), this)
    ctx.addType(Type("$alias.Entry"), entryType)
    keySetType.initiate(ctx)
    entrySetType.initiate(ctx)
  }

  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    val type = when (name) {
      "size" -> IntType
      "put" -> ctx.getFunctionDescriptorAsInstance(
        listOf(ParamInstance(key, 0), ParamInstance(value, 1)), BoolType, DummyMemoryAllocatorProvider
      )
      "get" -> ctx.getFunctionDescriptorAsInstance(listOf(ParamInstance(key, 0)), value, DummyMemoryAllocatorProvider)
      "remove" -> ctx.getFunctionDescriptorAsInstance(listOf(ParamInstance(key, 0)), BoolType, DummyMemoryAllocatorProvider)
      "keySet" -> keySetType
      "entries" -> entrySetType
      else -> null
    } ?: return null
    return Field(name, type, MemPos(0, 0), false)
  }

  private inner class Entry : TypeInstance {
    override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
      val type = when (name) {
        "key" -> key
        "value" -> value
        else -> null
      } ?: return null
      return Field(name, type, MemPos(0, 0), false)
    }
  }

  override fun toString(): String {
    return "$alias (Map<$key, $value>)"
  }
}
