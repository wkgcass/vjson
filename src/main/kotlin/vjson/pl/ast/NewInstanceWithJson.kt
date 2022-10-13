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
package vjson.pl.ast

import vjson.ex.ParserException
import vjson.pl.inst.CompositeInstruction
import vjson.pl.inst.Instruction
import vjson.pl.type.ArrayTypeInstance
import vjson.pl.type.ParamInstance
import vjson.pl.type.TypeContext
import vjson.pl.type.TypeInstance
import vjson.simple.SimpleInteger

data class NewInstanceWithJson(val type: Type, val json: Map<String, Any>) : Expr() {
  companion object {
    const val syntheticVariablePrefix = "\$\$tmp\$\$"
  }

  private lateinit var generatedInstruction: CompositeInstruction

  override fun copy(): Expr {
    val ret = NewInstanceWithJson(type, json)
    ret.lineCol = lineCol
    return ret
  }

  override fun check(ctx: TypeContext): TypeInstance {
    this.ctx = ctx
    val type = this.type.check(ctx)
    checkObject("$", type, json)

    generatedInstruction = _generateInstruction()

    return type
  }

  @Suppress("UNCHECKED_CAST")
  private fun checkInstance(path: String, type: TypeInstance, inst: Any) {
    if (inst is Map<*, *>) {
      checkObject(path, type, inst as Map<String, Any>)
      return
    } else if (inst is List<*>) {
      checkArray(path, type, inst as List<Any>)
      return
    }
    val t = (inst as Expr).check(ctx)
    if (type != t) {
      throw ParserException("$inst is not $type at $path", lineCol)
    }
  }

  private fun checkObject(path: String, type: TypeInstance, obj: Map<String, Any>) {
    val cons = type.constructor(ctx) ?: throw ParserException("no constructor found for type $type at $path", lineCol)
    val paramsMap = HashMap<String, ParamInstance>()
    for (p in cons.params) {
      val pname = if (p.name.startsWith("_")) p.name.substring(1) else p.name
      paramsMap[pname] = p
    }
    for ((p, pt) in paramsMap) {
      var inst = obj[p]
      if (inst == null) {
        if (pt.defaultValue != null) {
          inst = pt.defaultValue
        } else {
          throw ParserException("missing argument for parameter $p at $path", lineCol)
        }
      }
      checkInstance("$path.$p", pt.type, inst)
    }
  }

  private fun checkArray(path: String, type: TypeInstance, arr: List<Any>) {
    val elemType = type.elementType(ctx) ?: throw ParserException("type $type is not an array at $path", lineCol)
    for (i in arr.indices) {
      checkInstance("$path[$i]", elemType, arr[i])
    }
  }

  override fun typeInstance(): TypeInstance {
    return type.check(ctx)
  }

  override fun generateInstruction(): Instruction {
    return generatedInstruction
  }

  private fun _generateInstruction(): CompositeInstruction {
    val instList = ArrayList<Instruction>()
    generateInstruction(type.typeInstance(), instList, null, json)
    return CompositeInstruction(instList)
  }

  @Suppress("UNCHECKED_CAST")
  private fun generateInstruction(type: TypeInstance, instList: ArrayList<Instruction>, tmpvarname: String?, json: Map<String, Any>) {
    val args = ArrayList<Expr>()
    for (p in type.constructor(ctx)!!.params) {
      val pname = if (p.name.startsWith("_")) p.name.substring(1) else p.name
      val v = json[pname]
      if (v == null) {
        args.add(p.defaultValue!!)
      } else if (v is Expr) {
        args.add(v)
      } else {
        val varname = "$syntheticVariablePrefix${ctx.nextCounter()}"
        if (v is Map<*, *>) {
          generateInstruction(p.type, instList, varname, v as Map<String, Any>)
        } else {
          generateInstruction(p.type.elementType(ctx)!!, instList, p.type as ArrayTypeInstance, varname, v as List<Any>)
        }
        val placeHolder = Access(varname)
        placeHolder.check(ctx)
        args.add(placeHolder)
      }
    }
    val expr = NewInstance(Type(""), args)
    expr.typeInstance = type
    if (tmpvarname == null) {
      expr.check(ctx)
      instList.add(expr.generateInstruction())
    } else {
      val vardef = VariableDefinition(tmpvarname, expr)
      vardef.checkAST(ctx)
      instList.add(vardef.generateInstruction())
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun generateInstruction(
    elementType: TypeInstance,
    instList: ArrayList<Instruction>,
    arrType: ArrayTypeInstance,
    tmpvarname: String,
    json: List<Any>
  ) {
    val newarray = NewArray(Type(""), IntegerLiteral(SimpleInteger(json.size)))
    newarray.arrayTypeInstance = arrType
    val vardef = VariableDefinition(tmpvarname, newarray)
    vardef.checkAST(ctx)
    instList.add(vardef.generateInstruction())

    for (i in json.indices) {
      val e = json[i]
      val expr = if (e is Expr) {
        e
      } else {
        val varname = "$syntheticVariablePrefix${ctx.nextCounter()}"
        val placeHolder = Access(varname)
        if (e is Map<*, *>) {
          generateInstruction(elementType, instList, varname, e as Map<String, Any>)
        } else {
          generateInstruction(
            elementType.elementType(ctx)!!,
            instList,
            arrType.elementType(ctx) as ArrayTypeInstance,
            tmpvarname,
            e as List<Any>
          )
        }
        placeHolder
      }
      val assignment = Assignment(AccessIndex(Access(tmpvarname), IntegerLiteral(SimpleInteger(i))), expr)
      assignment.check(ctx)
      instList.add(assignment.generateInstruction())
    }
  }

  override fun toString(indent: Int): String {
    return "new $type $json"
  }

  override fun toString(): String {
    return toString(0)
  }
}
