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

import vjson.pl.type.*

class ListType(private val alias: String, elementType: TypeInstance) : CollectionType(alias, elementType) {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    val ret = super.field(ctx, name, accessFrom)
    if (ret != null) return ret

    val type = when (name) {
      "removeByIndex" -> ctx.getFunctionDescriptorAsInstance(listOf(ParamInstance(IntType, 0)), VoidType, DummyMemoryAllocatorProvider)
      "get" -> ctx.getFunctionDescriptorAsInstance(listOf(ParamInstance(IntType, 0)), elementType, DummyMemoryAllocatorProvider)
      "set" -> ctx.getFunctionDescriptorAsInstance(
        listOf(ParamInstance(IntType, 0), ParamInstance(elementType, 1)),
        VoidType,
        DummyMemoryAllocatorProvider
      )
      "insert" -> ctx.getFunctionDescriptorAsInstance(
        listOf(ParamInstance(IntType, 0), ParamInstance(elementType, 1)),
        VoidType,
        DummyMemoryAllocatorProvider
      )
      else -> null
    } ?: return null
    return Field(name, type, MemPos(0, 0), false)
  }

  override fun toString(): String {
    return "$alias (List<$elementType>)"
  }
}
