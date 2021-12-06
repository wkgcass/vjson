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

import vjson.pl.ast.*

class ClassTypeInstance(val cls: ClassDefinition) : TypeInstance {
  private val constructorParams: List<Param>
  private val fields: MutableMap<String, VariableDefinition> = HashMap()
  private val functions: MutableMap<String, FunctionDefinition> = HashMap()

  init {
    constructorParams = cls.params
    for (stmt in cls.code) {
      if (stmt is VariableDefinition) {
        fields[stmt.name] = stmt
      } else if (stmt is FunctionDefinition) {
        functions[stmt.name] = stmt
      }
    }
  }

  override fun constructor(ctx: TypeContext): FunctionDescriptor {
    return ctx.getFunctionDescriptor(
      constructorParams.map { ParamInstance(it.typeInstance(), it.memIndex) },
      ctx.getType(Type("void")),
      cls
    )
  }

  @Suppress("LiftReturnOrAssignment")
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    val field = fields[name]
    if (field != null) {
      if (field.modifiers.isPublic() || accessFrom == this) {
        return Field(name, field.value.typeInstance(), field.getMemPos(), !field.modifiers.isConst())
      } else {
        return null
      }
    }
    val func = functions[name]
    if (func != null) {
      if (!func.modifiers.isPrivate() || accessFrom == this) {
        return Field(name, FunctionDescriptorTypeInstance(func.descriptor(ctx)), func.getMemPos(), false)
      } else {
        return null
      }
    }
    return null
  }
}
