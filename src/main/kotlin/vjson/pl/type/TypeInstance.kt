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

package vjson.pl.type

import vjson.ex.ParserException
import vjson.pl.ast.ParamType
import vjson.pl.inst.RuntimeMemoryTotal

interface TypeInstance {
  fun constructor(ctx: TypeContext): FunctionDescriptor? = null

  fun memory(): RuntimeMemoryTotal = RuntimeMemoryTotal()

  fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? = null

  fun functionDescriptor(ctx: TypeContext): FunctionDescriptor? = null

  fun elementType(ctx: TypeContext): TypeInstance? = null

  fun typeParameters(): List<ParamType>? = null

  /* #ifndef KOTLIN_NATIVE {{ */ @Throws(ParserException::class) // }}
  fun concrete(ctx: TypeContext, concreteName: String, typeParams: List<TypeInstance>): TypeInstance {
    throw ParserException("unsupported")
  }

  fun templateType(): TypeInstance? = null
  fun templateTypeParams(): List<TypeInstance>? = null
}
