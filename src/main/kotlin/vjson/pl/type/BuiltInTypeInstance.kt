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

import vjson.pl.ast.Type
import vjson.pl.inst.ActionContext
import vjson.pl.inst.ValueHolder

interface BuiltInTypeInstance : TypeInstance {
}

interface PrimitiveTypeInstance : BuiltInTypeInstance {
}

interface NumericTypeInstance : PrimitiveTypeInstance {
}

object IntType : NumericTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toInt" -> object : ExecutableField(name, ctx.getType(Type("int")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
        }
      }
      "toLong" -> object : ExecutableField(name, ctx.getType(Type("long")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.longValue = values.intValue.toLong()
        }
      }
      "toFloat" -> object : ExecutableField("toFloat", ctx.getType(Type("float")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.floatValue = values.intValue.toFloat()
        }
      }
      "toDouble" -> object : ExecutableField(name, ctx.getType(Type("double")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.doubleValue = values.intValue.toDouble()
        }
      }
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "IntType"
  }
}

object LongType : NumericTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toInt" -> object : ExecutableField(name, ctx.getType(Type("int")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.intValue = values.longValue.toInt()
        }
      }
      "toLong" -> object : ExecutableField(name, ctx.getType(Type("long")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
        }
      }
      "toFloat" -> object : ExecutableField("toFloat", ctx.getType(Type("float")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.floatValue = values.longValue.toFloat()
        }
      }
      "toDouble" -> object : ExecutableField(name, ctx.getType(Type("double")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.doubleValue = values.longValue.toDouble()
        }
      }
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "LongType"
  }
}

object FloatType : NumericTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toInt" -> object : ExecutableField("toInt", ctx.getType(Type("int")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.intValue = values.floatValue.toInt()
        }
      }
      "toLong" -> object : ExecutableField("toLong", ctx.getType(Type("long")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.longValue = values.floatValue.toLong()
        }
      }
      "toFloat" -> object : ExecutableField(name, ctx.getType(Type("float")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
        }
      }
      "toDouble" -> object : ExecutableField("toDouble", ctx.getType(Type("double")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.doubleValue = values.floatValue.toDouble()
        }
      }
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "FloatType"
  }
}

object DoubleType : NumericTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toInt" -> object : ExecutableField("toInt", ctx.getType(Type("int")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.intValue = values.doubleValue.toInt()
        }
      }
      "toLong" -> object : ExecutableField("toLong", ctx.getType(Type("long")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.longValue = values.doubleValue.toLong()
        }
      }
      "toFloat" -> object : ExecutableField("toFloat", ctx.getType(Type("float")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
          values.floatValue = values.doubleValue.toFloat()
        }
      }
      "toDouble" -> object : ExecutableField(name, ctx.getType(Type("double")), MemPos(0, 0), false) {
        override fun execute(ctx: ActionContext, values: ValueHolder) {
        }
      }
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "DoubleType"
  }
}

object BoolType : PrimitiveTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "BoolType"
  }
}

object StringType : BuiltInTypeInstance {
  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "toString" -> Field(
        "toString",
        ctx.getFunctionDescriptorAsInstance(listOf(), ctx.getType(Type("string")), DummyMemoryAllocatorProvider),
        MemPos(0, 0),
        false
      )
      else -> null
    }
  }

  override fun toString(): String {
    return "StringType"
  }
}
