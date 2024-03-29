@file:Suppress("USELESS_ELVIS_RIGHT_IS_NULL")

package vjson.pl.type.lang

import vjson.cs.LineCol
import vjson.pl.inst.*
import vjson.pl.type.*

internal val MAP_PUT_STACK_INFO = StackInfo("Map", "put", LineCol.EMPTY)
internal val MAP_GET_STACK_INFO = StackInfo("Map", "get", LineCol.EMPTY)
internal val MAP_REMOVE_STACK_INFO = StackInfo("Map", "remove", LineCol.EMPTY)

// ----- BEGIN -----
internal fun generatedForMap0(key: TypeInstance, value: TypeInstance, ctx: TypeContext, name: String): Field {
  return when (name) {
    "put" -> {
      when (key) {
        IntType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", IntType, 1)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getInt(1)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0), ParamInstance("value", value, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1, refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getInt(0), ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
        LongType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1, intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", LongType, 1)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getLong(1)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1, floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1, doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1, boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0), ParamInstance("value", value, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1, refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getLong(0), ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
        FloatType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1, intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1, longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", FloatType, 1)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getFloat(1)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1, doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1, boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0), ParamInstance("value", value, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1, refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getFloat(0), ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
        DoubleType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1, intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1, longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1, floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", DoubleType, 1)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getDouble(1)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1, boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0), ParamInstance("value", value, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1, refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getDouble(0), ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
        BoolType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1, intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1, longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1, floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1, doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", BoolType, 1)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getBool(1)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0), ParamInstance("value", value, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1, refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getBool(0), ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
        else -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1, intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1, longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1, floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1, doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1, boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0), ParamInstance("value", value, 1)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 2))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_PUT_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.put(ctx.getCurrentMem().getRef(0), ctx.getCurrentMem().getRef(1)) ?: null
                  }
                }
              }
            }
          }
        }
      }
    }
    "get" -> {
      when (key) {
        IntType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getInt(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getInt(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getInt(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getInt(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getInt(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getInt(0)] ?: null
                  }
                }
              }
            }
          }
        }
        LongType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getLong(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getLong(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getLong(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getLong(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getLong(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getLong(0)] ?: null
                  }
                }
              }
            }
          }
        }
        FloatType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getFloat(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getFloat(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getFloat(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getFloat(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getFloat(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getFloat(0)] ?: null
                  }
                }
              }
            }
          }
        }
        DoubleType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getDouble(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getDouble(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getDouble(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getDouble(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getDouble(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getDouble(0)] ?: null
                  }
                }
              }
            }
          }
        }
        BoolType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getBool(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getBool(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getBool(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getBool(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getBool(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getBool(0)] ?: null
                  }
                }
              }
            }
          }
        }
        else -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map[ctx.getCurrentMem().getRef(0)] ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map[ctx.getCurrentMem().getRef(0)] ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map[ctx.getCurrentMem().getRef(0)] ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map[ctx.getCurrentMem().getRef(0)] ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map[ctx.getCurrentMem().getRef(0)] ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_GET_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map[ctx.getCurrentMem().getRef(0)] ?: null
                  }
                }
              }
            }
          }
        }
      }
    }
    "remove" -> {
      when (key) {
        IntType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", IntType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(intTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Int, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getInt(0)) ?: null
                  }
                }
              }
            }
          }
        }
        LongType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", LongType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(longTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Long, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getLong(0)) ?: null
                  }
                }
              }
            }
          }
        }
        FloatType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", FloatType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(floatTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Float, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getFloat(0)) ?: null
                  }
                }
              }
            }
          }
        }
        DoubleType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", DoubleType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(doubleTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Double, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getDouble(0)) ?: null
                  }
                }
              }
            }
          }
        }
        BoolType -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", BoolType, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(boolTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Boolean, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getBool(0)) ?: null
                  }
                }
              }
            }
          }
        }
        else -> when (value) {
          IntType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), IntType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Int>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.intValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: 0
                  }
                }
              }
            }
          }
          LongType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), LongType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Long>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.longValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: 0L
                  }
                }
              }
            }
          }
          FloatType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), FloatType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Float>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.floatValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: 0f
                  }
                }
              }
            }
          }
          DoubleType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), DoubleType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Double>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.doubleValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: 0.0
                  }
                }
              }
            }
          }
          BoolType -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), BoolType,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Boolean>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.boolValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: false
                  }
                }
              }
            }
          }
          else -> {
            val type = ctx.getFunctionDescriptorAsInstance(
              listOf(ParamInstance("key", key, 0)), value,
              FixedMemoryAllocatorProvider(RuntimeMemoryTotal(refTotal = 1))
            )
            object : ExecutableField(name, type) {
              override fun execute(ctx: ActionContext, exec: Execution) {
                val obj = exec.values.refValue as ActionContext
                @Suppress("UNCHECKED_CAST") val map = obj.getCurrentMem().getRef(0) as MutableMap<Any?, Any?>
                exec.values.refValue = object : InstructionWithStackInfo(MAP_REMOVE_STACK_INFO) {
                  override fun execute0(ctx: ActionContext, exec: Execution) {
                    exec.values.refValue = map.remove(ctx.getCurrentMem().getRef(0)) ?: null
                  }
                }
              }
            }
          }
        }
      }
    }
    else -> throw IllegalStateException()
  }
}
// ----- END -----
