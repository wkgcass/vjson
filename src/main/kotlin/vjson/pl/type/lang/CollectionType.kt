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

import vjson.cs.LineCol
import vjson.pl.inst.*
import vjson.pl.type.*

abstract class CollectionType(
  private val templateType: TypeInstance,
  private val iteratorType: IteratorType,
  protected val elementType: TypeInstance
) : TypeInstance {
  companion object {
    private val COLL_ADD_STACK_INFO = StackInfo("Collection", "add", LineCol.EMPTY)
    private val COLL_REMOVE_STACK_INFO = StackInfo("Collection", "remove", LineCol.EMPTY)
    private val COLL_TO_STRING_STACK_INFO = StackInfo("Collection", "toString", LineCol.EMPTY)
  }

  protected abstract fun newCollection(initialCap: Int): Collection<*>

  private val constructorDescriptor = object : ExecutableConstructorFunctionDescriptor(
    listOf(ParamInstance("size", IntType, 0)),
    VoidType,
    FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, refTotal = 1))
  ) {
    override fun execute(ctx: ActionContext, exec: Execution) {
      val mem = ctx.getCurrentMem()
      mem.setRef(0, newCollection(mem.getInt(0)))
    }
  }

  override fun constructor(ctx: TypeContext): FunctionDescriptor {
    return constructorDescriptor
  }

  protected fun memoryAllocatorForSingleElementTypeFunction(): MemoryAllocatorProvider {
    return FixedMemoryAllocatorProvider(
      when (elementType) {
        is IntType -> RuntimeMemoryTotal(intTotal = 1)
        is LongType -> RuntimeMemoryTotal(longTotal = 1)
        is FloatType -> RuntimeMemoryTotal(floatTotal = 1)
        is DoubleType -> RuntimeMemoryTotal(doubleTotal = 1)
        is BoolType -> RuntimeMemoryTotal(boolTotal = 1)
        else -> RuntimeMemoryTotal(refTotal = 1)
      }
    )
  }

  override fun field(ctx: TypeContext, name: String, accessFrom: TypeInstance?): Field? {
    return when (name) {
      "size" -> object : ExecutableField(name, IntType) {
        override fun execute(ctx: ActionContext, exec: Execution) {
          val obj = exec.values.refValue as ActionContext
          val coll = obj.getCurrentMem().getRef(0) as Collection<*>
          exec.values.intValue = coll.size
        }
      }
      "add" -> {
        val type = ctx.getFunctionDescriptorAsInstance(
          listOf(ParamInstance("e", elementType, 0)),
          BoolType,
          memoryAllocatorForSingleElementTypeFunction()
        )
        when (elementType) {
          IntType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Int>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getInt(0))
                }
              }
            }
          }
          LongType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Long>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getLong(0))
                }
              }
            }
          }
          FloatType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Float>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getFloat(0))
                }
              }
            }
          }
          DoubleType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Double>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getDouble(0))
                }
              }
            }
          }
          BoolType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Boolean>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getBool(0))
                }
              }
            }
          }
          else -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Any?>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_ADD_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.add(ctx.getCurrentMem().getRef(0))
                }
              }
            }
          }
        }
      }
      "remove" -> {
        val type = ctx.getFunctionDescriptorAsInstance(
          listOf(ParamInstance("e", elementType, 0)),
          BoolType,
          memoryAllocatorForSingleElementTypeFunction()
        )
        when (elementType) {
          IntType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Int>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getInt(0))
                }
              }
            }
          }
          LongType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Long>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getLong(0))
                }
              }
            }
          }
          FloatType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Float>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getFloat(0))
                }
              }
            }
          }
          DoubleType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Double>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getDouble(0))
                }
              }
            }
          }
          BoolType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Boolean>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getBool(0))
                }
              }
            }
          }
          else -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Any?>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.remove(ctx.getCurrentMem().getRef(0))
                }
              }
            }
          }
        }
      }
      "toArray" -> {
        val type = ArrayTypeInstance(elementType)
        when (elementType) {
          IntType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Int>
              exec.values.refValue = coll.toIntArray()
            }
          }
          LongType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Long>
              exec.values.refValue = coll.toLongArray()
            }
          }
          FloatType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Float>
              exec.values.refValue = coll.toFloatArray()
            }
          }
          DoubleType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Double>
              exec.values.refValue = coll.toDoubleArray()
            }
          }
          BoolType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Boolean>
              exec.values.refValue = coll.toBooleanArray()
            }
          }
          else -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Any?>
              exec.values.refValue = coll.toTypedArray()
            }
          }
        }
      }
      "contains" -> {
        val type = ctx.getFunctionDescriptorAsInstance(
          listOf(ParamInstance("e", elementType, 0)),
          BoolType,
          memoryAllocatorForSingleElementTypeFunction()
        )
        when (elementType) {
          IntType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Int>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getInt(0))
                }
              }
            }
          }
          LongType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Long>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getLong(0))
                }
              }
            }
          }
          FloatType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Float>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getFloat(0))
                }
              }
            }
          }
          DoubleType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Double>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getDouble(0))
                }
              }
            }
          }
          BoolType -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Boolean>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getBool(0))
                }
              }
            }
          }
          else -> object : ExecutableField(name, type) {
            override fun execute(ctx: ActionContext, exec: Execution) {
              val obj = exec.values.refValue as ActionContext
              @Suppress("UNCHECKED_CAST") val coll = obj.getCurrentMem().getRef(0) as MutableCollection<Any?>
              exec.values.refValue = object : InstructionWithStackInfo(COLL_REMOVE_STACK_INFO) {
                override fun execute0(ctx: ActionContext, exec: Execution) {
                  exec.values.boolValue = coll.contains(ctx.getCurrentMem().getRef(0))
                }
              }
            }
          }
        }
      }
      "iterator" -> object : ExecutableField(name, iteratorType) {
        override fun execute(ctx: ActionContext, exec: Execution) {
          val obj = exec.values.refValue as ActionContext
          val coll = obj.getCurrentMem().getRef(0) as MutableCollection<*>

          val iteObj = ActionContext(RuntimeMemoryTotal(refTotal = 1), parent = null)
          iteObj.getCurrentMem().setRef(0, coll.iterator())

          exec.values.refValue = iteObj
        }
      }
      "toString" -> {
        val type = ctx.getFunctionDescriptorAsInstance(listOf(), StringType, DummyMemoryAllocatorProvider)
        object : ExecutableField(name, type) {
          override fun execute(ctx: ActionContext, exec: Execution) {
            val obj = exec.values.refValue as ActionContext
            val coll = obj.getCurrentMem().getRef(0) as MutableCollection<*>
            exec.values.refValue = object : InstructionWithStackInfo(COLL_TO_STRING_STACK_INFO) {
              override fun execute0(ctx: ActionContext, exec: Execution) {
                exec.values.refValue = coll.toString()
              }
            }
          }
        }
      }
      else -> null
    }
  }

  override fun templateType(): TypeInstance {
    return templateType
  }

  override fun templateTypeParams(): List<TypeInstance>? {
    return listOf(elementType)
  }
}
