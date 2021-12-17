package vjson;

import kotlin.Pair;
import org.junit.Test;
import vjson.pl.Interpreter;
import vjson.pl.InterpreterBuilder;
import vjson.pl.inst.ActionContext;
import vjson.pl.inst.RuntimeMemory;
import vjson.pl.type.lang.ExtFunctions;
import vjson.pl.type.lang.ExtTypes;
import vjson.pl.type.lang.StdTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class TestInterpreter {
    @Test
    public void simple() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = '1234 + 56 * 78 - 910 / 11'\n" +
                "var b = 'a * 3'\n" +
                "var c = 'b.toString:[]'\n" +
                "var d = 1.2\n" +
                "var e = 100000000000\n" +
                "var f = {null: 'string'}\n" +
                "var g = \"'a'\"\n" +
                "g = null\n" +
                "var h = \"'a'\"\n" +
                "h = 'null'\n" +
                "}")
            .executeBlock();

        assertEquals(5520, mem.getInt(0));
        assertEquals(16560, mem.getInt(1));
        assertEquals(2, mem.intLen());
        assertEquals("16560", mem.getRef(0));
        assertNull(mem.getRef(1));
        assertNull(mem.getRef(2));
        assertNull(mem.getRef(3));
        assertEquals(4, mem.refLen());
        assertEquals(1.2, mem.getDouble(0), 0.0);
        assertEquals(1, mem.doubleLen());
        assertEquals(100000000000L, mem.getLong(0));
        assertEquals(1, mem.longLen());
    }

    @Test
    public void access() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = 1\n" +
                "var b = 'a'\n" +
                "}")
            .executeBlock();
        assertEquals(1, mem.getInt(0));
        assertEquals(1, mem.getInt(1));
        assertEquals(2, mem.intLen());
    }

    @Test
    public void accessField() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "class A: {} do: {\n" +
                "  public var a = 1\n" +
                "}\n" +
                "var a = {new A:[]}\n" +
                "var b = 'a.a'\n" +
                "a.a = 123\n" +
                "var c = 'a.a'\n" +
                "}")
            .executeBlock();
        assertEquals(1, mem.getInt(0));
        assertEquals(123, mem.getInt(1));
        assertEquals(2, mem.intLen());
    }

    @Test
    public void accessIndex() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = {new 'int[1]'}\n" +
                "'a[0]' = 1\n" +
                "var b = 'a[0]'\n" +
                "}")
            .executeBlock();
        assertEquals(1, mem.getInt(0));
        assertEquals(1, mem.intLen());
    }

    @Test
    public void binOp() {
        Interpreter interpreter = new InterpreterBuilder()
            .compile("{\n" +
                "var a = '1 + 3'\n" +
                "var b = '2 * 4'\n" +
                "var c = '5 - 6'\n" +
                "var d = '12 / 3'\n" +
                "var e = 'a > 3'\n" +
                "var f = 'a >= 4'\n" +
                "var g = 'b < 9'\n" +
                "var h = 'b <= 8'\n" +
                "var i = 'c != -1'\n" +
                "var j = 'd == a'\n" +
                "var k = 'h && i'\n" +
                "var l = 'h || i'\n" +
                "var m = '100 % 7'\n" +
                "}");
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals(4, mem.getInt(0));
        assertEquals(8, mem.getInt(1));
        assertEquals(-1, mem.getInt(2));
        assertEquals(4, mem.getInt(3));
        assertEquals(2, mem.getInt(4));
        assertEquals(5, mem.intLen());
        assertTrue(mem.getBool(0));
        assertTrue(mem.getBool(1));
        assertTrue(mem.getBool(2));
        assertTrue(mem.getBool(3));
        assertFalse(mem.getBool(4));
        assertTrue(mem.getBool(5));
        assertFalse(mem.getBool(6));
        assertTrue(mem.getBool(7));
        assertEquals(8, mem.boolLen());
    }

    @Test
    public void binOpNull() {
        Interpreter interpreter = new InterpreterBuilder()
            .compile("{\n" +
                "var a = ('a' == null)\n" +
                "var b = (null == 'a')\n" +
                "var c = (null == null)\n" +
                "var d = ('a' != null)\n" +
                "var e = (null != 'a')\n" +
                "var f = (null != null)\n" +
                "}");
        RuntimeMemory mem = interpreter.executeBlock();
        assertFalse(mem.getBool(0));
        assertFalse(mem.getBool(1));
        assertTrue(mem.getBool(2));
        assertTrue(mem.getBool(3));
        assertTrue(mem.getBool(4));
        assertFalse(mem.getBool(5));
        assertEquals(6, mem.boolLen());
    }

    @Test
    public void unaryOp() {
        Interpreter interpreter = new InterpreterBuilder()
            .compile("{\n" +
                "var a = '!true'\n" +
                "var b = '!false'\n" +
                "var c = '+5'\n" +
                "var d = '-4'\n" +
                "var e = '5 + -4'\n" +
                "}");
        RuntimeMemory mem = interpreter.executeBlock();
        assertFalse(mem.getBool(0));
        assertTrue(mem.getBool(1));
        assertEquals(2, mem.boolLen());
        assertEquals(5, mem.getInt(0));
        assertEquals(-4, mem.getInt(1));
        assertEquals(1, mem.getInt(2));
        assertEquals(3, mem.intLen());
    }

    @Test
    public void newArray() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = {new 'int[10]'}\n" +
                "'a[0]' = 1\n" +
                "'a[1]' = 2\n" +
                "var x = 'a[0]'\n" +
                "var y = 'a[1]'\n" +
                "}")
            .executeBlock();
        int[] a = (int[]) mem.getRef(0);
        assertEquals(1, mem.refLen());
        assertNotNull(a);
        assertEquals(10, a.length);
        assertEquals(1, a[0]);
        assertEquals(2, a[1]);
        for (int i = 2; i < a.length; ++i) {
            assertEquals(0, a[i]);
        }
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));
        assertEquals(2, mem.intLen());
    }

    @Test
    public void new2DArray() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = {new 'int[10][]'}\n" +
                "'a[0]' = {new 'int[5]'}\n" +
                "'a[1]' = {new 'int[2]'}\n" +
                "'a[0][0]' = 1\n" +
                "'a[1][1]' = 2\n" +
                "var x = 'a[0][0]'\n" +
                "var y = 'a[1][1]'\n" +
                "}")
            .executeBlock();
        Object[] a = (Object[]) mem.getRef(0);
        assertEquals(1, mem.refLen());
        assertNotNull(a);
        int[] a0 = (int[]) a[0];
        int[] a1 = (int[]) a[1];
        assertEquals(10, a.length);
        assertEquals(5, a0.length);
        assertEquals(2, a1.length);
        assertEquals(1, a0[0]);
        assertEquals(2, a1[1]);
        for (int i = 2; i < a.length; ++i) {
            assertNull(a[i]);
        }
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));
        assertEquals(2, mem.intLen());
    }

    @Test
    public void newStringArray() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = {new 'string[10]'}\n" +
                "'a[0]' = \"'hello'\"\n" +
                "'a[1]' = \"'world'\"\n" +
                "var x = 'a[0]'\n" +
                "var y = 'a[1]'\n" +
                "}")
            .executeBlock();
        Object[] a = (Object[]) mem.getRef(0);
        assertNotNull(a);
        assertEquals(10, a.length);
        assertEquals("hello", a[0]);
        assertEquals("world", a[1]);
        for (int i = 2; i < a.length; ++i) {
            assertNull(a[i]);
        }
        assertEquals("hello", mem.getRef(1));
        assertEquals("world", mem.getRef(2));
        assertEquals(3, mem.refLen());
    }

    @Test
    public void newObjectArray() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "class Test: {} do: {\n" +
                "  public var a = 0\n" +
                "}\n" +
                "var a = {new 'Test[10]'}\n" +
                "'a[0]' = {new Test:[]}\n" +
                "'a[1]' = {new Test:[]}\n" +
                "'a[0].a' = 1\n" +
                "'a[1].a' = 2\n" +
                "var x = 'a[0].a'\n" +
                "var y = 'a[1].a'\n" +
                "}")
            .executeBlock();
        Object[] a = (Object[]) mem.getRef(0);
        assertNotNull(a);
        assertEquals(10, a.length);
        ActionContext a0 = (ActionContext) a[0];
        ActionContext a1 = (ActionContext) a[1];
        assertEquals(1, a0.getCurrentMem().getInt(0));
        assertEquals(2, a1.getCurrentMem().getInt(0));
        for (int i = 2; i < a.length; ++i) {
            assertNull(a[i]);
        }
        assertEquals(1, mem.refLen());
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));
        assertEquals(2, mem.intLen());
    }

    @Test
    public void functionCall() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = 1\n" +
                "function test: {} void: {\n" +
                "  a = 112233\n" +
                "}\n" +
                "test:[]\n" +
                "}")
            .executeBlock();
        assertEquals(112233, mem.getInt(0));
    }

    @Test
    public void classConstructor() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = 1\n" +
                "class Test: {} do: {\n" +
                "  a = 112233\n" +
                "}\n" +
                "new Test:[]\n" +
                "}")
            .executeBlock();
        assertEquals(112233, mem.getInt(0));
    }

    @Test
    public void classFunction() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a = 1\n" +
                "class Test: {} do: {\n" +
                "  function test: {} void: {\n" +
                "    a = 112233\n" +
                "  }\n" +
                "}\n" +
                "var t = {new Test:[]}\n" +
                "t.test:[]\n" +
                "}")
            .executeBlock();
        assertEquals(112233, mem.getInt(0));
    }

    @Test
    public void functionReturn() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "function get1: {} int: {\n" +
                "  return: 1\n" +
                "}\n" +
                "function get2: {} int: {\n" +
                "  return: 2\n" +
                "}\n" +
                "var a = {get1:[]}\n" +
                "var b = {get2:[]}\n" +
                "}")
            .executeBlock();
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));
    }

    @Test
    public void functionRecursion() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "function fib: { n: int } int: {\n" +
                "  var cache = new int[n+1]\n" +
                "  function fib0: { a: int } int: {\n" +
                "    if: cache[a] == 0; then: {\n" +
                "      if: a == 1; then: { cache[a] = 1 }\n" +
                "      else if: a == 2; then: { cache[a] = 1 }\n" +
                "      else: { cache[a] = fib0:[a-1] + fib0:[a-2] }\n" +
                "    }\n" +
                "    return: cache[a]\n" +
                "  }\n" +
                "  return: fib0:[n]\n" +
                "}\n" +
                "var result = fib:[10]\n" +
                "}")
            .executeBlock();
        assertEquals(55, mem.getInt(0));
    }

    @Test
    public void ifStatement() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "function check: {grade: 'int'} string: {\n" +
                "  if: 'grade >= 90' then: {\n" +
                "    return: \"'excellent'\"\n" +
                "  } else if: 'grade >= 80' then: {\n" +
                "    return: \"'good'\"\n" +
                "  } else if: 'grade >= 60' then: {\n" +
                "    return: \"'pass'\"" +
                "  } else: {\n" +
                "    return: \"'fail'\"" +
                "  }\n" +
                "}\n" +
                "var excellent = {check:[90]}\n" +
                "var good = {check:[80]}\n" +
                "var pass = {check:[60]}\n" +
                "var fail = {check:[59]}\n" +
                "}")
            .executeBlock();
        assertEquals("excellent", mem.getRef(1));
        assertEquals("good", mem.getRef(2));
        assertEquals("pass", mem.getRef(3));
        assertEquals("fail", mem.getRef(4));
    }

    @Test
    public void simpleLoop() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var sum = 0\n" +
                "for: [ {var n = 1}, 'n <= 10', 'n += 1' ] do: {\n" +
                "  sum = 'sum + n'\n" +
                "}\n" +
                "}")
            .executeBlock();
        assertEquals(55, mem.getInt(0));
    }

    @Test
    public void loopBreak() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var sum = 0\n" +
                "for: [ {var n = 1}, true, 'n += 1' ] do: {\n" +
                "  if: 'n > 10' then: {\n" +
                "    break\n" +
                "  }\n" +
                "  sum = 'sum + n'\n" +
                "}\n" +
                "}")
            .executeBlock();
        assertEquals(55, mem.getInt(0));
    }

    @Test
    public void loopContinue() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var sum = 0\n" +
                "for: [ {var n = 1}, 'n <= 10', 'n += 1' ] do: {\n" +
                "  if: 'n == 5' then: {\n" +
                "    continue\n" +
                "  }\n" +
                "  sum = 'sum + n'\n" +
                "}\n" +
                "}")
            .executeBlock();
        assertEquals(50, mem.getInt(0));
    }

    @Test
    public void whileLoop() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var sum = 0\n" +
                "var n = 0\n" +
                "while: true, do: {\n" +
                "  n = 'n + 1'\n" +
                "  sum = 'sum + n'\n" +
                "  if: 'n >= 10' then: {\n" +
                "    break\n" +
                "  }\n" +
                "}\n" +
                "}")
            .executeBlock();
        assertEquals(55, mem.getInt(0));
    }

    @Test
    public void newAsExpr() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{var a = new int[3].length}")
            .executeBlock();
        assertEquals(3, mem.getInt(0));
        mem = new InterpreterBuilder()
            .compile("{\n" +
                "class A: {x: int, y: string} do: {\n" +
                "  function toString:{} string: {\n" +
                "    return: ('A(x=' + x + ', y=' + y + ')')\n" +
                "  }\n" +
                "}\n" +
                "var a = new A:[1, 'a'].toString:[]\n" +
                "}")
            .executeBlock();
        assertEquals("A(x=1, y=a)", mem.getRef(0));
    }

    @Test
    public void templateType() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "template: { T, U } class BiContainer: { t: T, u: U } do: {\n" +
                "  function calc:{} int: {\n" +
                "    return: t.toInt + u.toInt\n" +
                "  }\n" +
                "}\n" +
                "let DoubleFloatContainer = { BiContainer:[double, float] }\n" +
                "var container = new DoubleFloatContainer:[1.2, 3.4.toFloat]\n" +
                "var res = container.calc:[]\n" +
                "}")
            .executeBlock();
        assertEquals(4, mem.getInt(0));
    }

    @Test
    public void builtInTypesToSelf() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "var a0 = 1.toInt\n" +
                "var a1 = 100000000000000.toLong\n" +
                "var a3 = 2.4.toFloat.toFloat\n" +
                "var a4 = 4.8.toDouble\n" +
                "var a5 = ('abc'.toString:[])\n" +
                "}")
            .executeBlock();
        assertEquals(1, mem.getInt(0));
        assertEquals(100000000000000L, mem.getLong(0));
        assertEquals(2.4, mem.getFloat(0), 0.0000001);
        assertEquals(4.8, mem.getDouble(0), 0.0000001);
        assertEquals("abc", mem.getRef(0));
    }

    private Pair<RuntimeMemory, String> executeWithStdTypes(String prog) {
        StdTypes stdTypes = new StdTypes();
        StringBuilder sb = new StringBuilder();
        stdTypes.setOutput(s -> {
            sb.append(s).append("\n");
            return null;
        });
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(stdTypes)
            .compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        return new Pair<>(mem, sb.toString());
    }

    @Test
    public void builtInSetTypeNumericAndString() {
        for (int i = 0; i < 5; ++i) {
            String Type;
            String type;
            String first;
            String second;
            String third;
            String outputFirst;
            String outputThird;
            switch (i) {
                case 0:
                    Type = "Int";
                    type = "int";
                    first = "100";
                    second = "200";
                    third = "300";
                    outputFirst = first;
                    outputThird = third;
                    break;
                case 1:
                    Type = "Long";
                    type = "long";
                    first = "10000000000";
                    second = "20000000000";
                    third = "30000000000";
                    outputFirst = first;
                    outputThird = third;
                    break;
                case 2:
                    Type = "Float";
                    type = "float";
                    first = "100.0.toFloat";
                    second = "200.0.toFloat";
                    third = "300.0.toFloat";
                    outputFirst = "100.0";
                    outputThird = "300.0";
                    break;
                case 3:
                    Type = "Double";
                    type = "double";
                    first = "100.0";
                    second = "200.0";
                    third = "300.0";
                    outputFirst = first;
                    outputThird = third;
                    break;
                default:
                    Type = "String";
                    type = "string";
                    first = "('100')";
                    second = "('200')";
                    third = "('300')";
                    outputFirst = "100";
                    outputThird = "300";
                    break;
            }
            String prog = "{\n" +
                "let " + Type + "Set = { std.LinkedHashSet:[ " + type + " ] }\n" +
                "var set = new " + Type + "Set:[16]\n" +
                "set.add:[" + first + "]\n" +
                "std.console.log:[('contains1=' + set.contains:[" + first + "])]\n" +
                "set.add:[" + second + "]\n" +
                "std.console.log:[('contains2=' + set.contains:[" + second + "])]\n" +
                "set.add:[" + third + "]\n" +
                "set.remove:[(" + second + ")]\n" +
                "std.console.log:[('contains3=' + set.contains:[" + second + "])]\n" +
                "var res = set.toString:[]\n" +
                "var size = set.size\n" +
                "var ite = set.iterator\n" +
                "var cnt = 0\n" +
                "while: ite.hasNext; do: {\n" +
                "  std.console.log:[ite.next + '']\n" +
                "  cnt = cnt + 1\n" +
                "}\n" +
                "}";

            Pair<RuntimeMemory, String> pair = executeWithStdTypes(prog);
            RuntimeMemory mem = pair.getFirst();

            // var res
            String res = "[" + outputFirst + ", " + outputThird + "]";
            assertEquals(res, mem.getRef(2));
            assertEquals(4, mem.refLen());
            // var size
            assertEquals(2, mem.getInt(0));
            // var cnt
            assertEquals(2, mem.getInt(1));
            assertEquals(2, mem.intLen());

            String output = "" +
                "contains1=true\n" +
                "contains2=true\n" +
                "contains3=false\n" +
                outputFirst + "\n" +
                outputThird + "\n";
            assertEquals(output, pair.getSecond());
        }
    }

    @Test
    public void builtInSetTypeBool() {
        String prog = "{\n" +
            "let BoolSet = { std.LinkedHashSet:[ bool ] }\n" +
            "var set = new BoolSet:[16]\n" +
            "set.add:[true]\n" +
            "std.console.log:[set+'']\n" +
            "std.console.log:[('contains1=' + set.contains:[true])]\n" +
            "set.add:[false]\n" +
            "std.console.log:[set+'']\n" +
            "std.console.log:[('contains2=' + set.contains:[false])]\n" +
            "set.add:[true]\n" +
            "std.console.log:[set+'']\n" +
            "set.remove:[true]\n" +
            "std.console.log:[set+'']\n" +
            "std.console.log:[('contains3=' + set.contains:[true])]\n" +
            "var res = set.toString:[]\n" +
            "var size = set.size\n" +
            "var ite = set.iterator\n" +
            "var cnt = 0\n" +
            "while: ite.hasNext; do: {\n" +
            "  std.console.log:[ite.next + '']\n" +
            "  cnt = cnt + 1\n" +
            "}\n" +
            "}";

        Pair<RuntimeMemory, String> pair = executeWithStdTypes(prog);
        RuntimeMemory mem = pair.getFirst();
        assertEquals("[false]", mem.getRef(2));
        assertEquals(4, mem.refLen());
        assertEquals(1, mem.getInt(0));
        assertEquals(1, mem.getInt(1));
        assertEquals(2, mem.intLen());
        String output = pair.getSecond();
        assertEquals("" +
            "[true]\n" +
            "contains1=true\n" +
            "[true, false]\n" +
            "contains2=true\n" +
            "[true, false]\n" +
            "[false]\n" +
            "contains3=false\n" +
            "false\n" +
            "", output);
    }

    @Test
    public void builtInListTypeAll() {
        for (int i = 0; i < 6; ++i) {
            String Type;
            String type;
            String first;
            String second;
            String third;
            String fourth;
            String outputFirst;
            String outputSecond;
            String outputThird;
            String outputFourth;
            switch (i) {
                case 0:
                    Type = "Int";
                    type = "int";
                    first = "100";
                    second = "200";
                    third = "300";
                    fourth = "400";
                    outputFirst = first;
                    outputSecond = second;
                    outputThird = third;
                    outputFourth = fourth;
                    break;
                case 1:
                    Type = "Long";
                    type = "long";
                    first = "10000000000";
                    second = "20000000000";
                    third = "30000000000";
                    fourth = "40000000000";
                    outputFirst = first;
                    outputSecond = second;
                    outputThird = third;
                    outputFourth = fourth;
                    break;
                case 2:
                    Type = "Float";
                    type = "float";
                    first = "100.0.toFloat";
                    second = "200.0.toFloat";
                    third = "300.0.toFloat";
                    fourth = "400.0.toFloat";
                    outputFirst = "100.0";
                    outputSecond = "200.0";
                    outputThird = "300.0";
                    outputFourth = "400.0";
                    break;
                case 3:
                    Type = "Double";
                    type = "double";
                    first = "100.0";
                    second = "200.0";
                    third = "300.0";
                    fourth = "400.0";
                    outputFirst = first;
                    outputSecond = second;
                    outputThird = third;
                    outputFourth = fourth;
                    break;
                case 4:
                    Type = "Bool";
                    type = "bool";
                    first = "true";
                    second = "false";
                    third = "true";
                    fourth = "false";
                    outputFirst = first;
                    outputSecond = second;
                    outputThird = third;
                    outputFourth = fourth;
                    break;
                default:
                    Type = "String";
                    type = "string";
                    first = "('100')";
                    second = "('200')";
                    third = "('300')";
                    fourth = "('400')";
                    outputFirst = "100";
                    outputSecond = "200";
                    outputThird = "300";
                    outputFourth = "400";
                    break;
            }
            String prog = "{\n" +
                "let " + Type + "List = { std.List:[ " + type + " ] }\n" +
                "var list = new " + Type + "List:[16]\n" +
                "list.add:[" + first + "]\n" +
                "std.console.log:[list+'']\n" +
                "std.console.log:[('contains1=' + list.contains:[" + first + "])]\n" +
                "std.console.log:[('indexOf1=' + list.indexOf:[" + first + "])]" +
                "list.add:[" + second + "]\n" +
                "std.console.log:[list+'']\n" +
                "std.console.log:[('contains2=' + list.contains:[" + second + "])]\n" +
                "std.console.log:[('indexOf2=' + list.indexOf:[" + second + "])]" +
                "list.insert:[1," + third + "]\n" +
                "std.console.log:[list+'']\n" +
                "list.set:[0," + fourth + "]\n" +
                "std.console.log:[list+'']\n" +
                "list.removeAt:[2]\n" +
                "std.console.log:[list+'']\n" +
                "std.console.log:[('contains3=' + list.contains:[" + second + "])]\n" +
                "var res = list.toString:[]\n" +
                "var ls0 = list.get:[0]\n" +
                "}";
            Pair<RuntimeMemory, String> pair = executeWithStdTypes(prog);
            RuntimeMemory mem = pair.getFirst();
            // var res
            String res = "[" + outputFourth + ", " + outputThird + "]";
            assertEquals(res, mem.getRef(2));
            // var ls0 and total check
            switch (i) {
                case 0:
                    assertEquals(400, mem.getInt(0));
                    assertEquals(1, mem.intLen());
                    assertEquals(3, mem.refLen());
                    break;
                case 1:
                    assertEquals(40000000000L, mem.getLong(0));
                    assertEquals(1, mem.longLen());
                    assertEquals(3, mem.refLen());
                    break;
                case 2:
                    assertEquals(400f, mem.getFloat(0), 0.000001);
                    assertEquals(1, mem.floatLen());
                    assertEquals(3, mem.refLen());
                    break;
                case 3:
                    assertEquals(400.0, mem.getDouble(0), 0.0000001);
                    assertEquals(1, mem.doubleLen());
                    assertEquals(3, mem.refLen());
                    break;
                case 4:
                    assertFalse(mem.getBool(0));
                    assertEquals(1, mem.boolLen());
                    assertEquals(3, mem.refLen());
                    break;
                default:
                    assertEquals("400", mem.getRef(3));
                    assertEquals(4, mem.refLen());
            }
            // output
            String output = "" +
                "[" + outputFirst + "]\n" +
                "contains1=true\n" +
                "indexOf1=0\n" +
                "[" + outputFirst + ", " + outputSecond + "]\n" +
                "contains2=true\n" +
                "indexOf2=1\n" +
                "[" + outputFirst + ", " + outputThird + ", " + outputSecond + "]\n" +
                "[" + outputFourth + ", " + outputThird + ", " + outputSecond + "]\n" +
                "[" + outputFourth + ", " + outputThird + "]\n" +
                (type.equals("bool") ? "contains3=true\n" : "contains3=false\n");
            assertEquals(output, pair.getSecond());
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test
    public void sublist() {
        String prog = "{\n" +
            "let IntList = { std.List:[ int ] }\n" +
            "var ls = new IntList:[16]\n" +
            "ls.add:[100]\n" +
            "ls.add:[200]\n" +
            "ls.add:[300]\n" +
            "ls.add:[400]\n" +
            "ls.add:[500]\n" +
            "var sub1 = ls.subList:[1,4]\n" +
            "var sub2 = ls.subList:[2,5]\n" +
            "}";
        Pair<RuntimeMemory, String> pair = executeWithStdTypes(prog);
        RuntimeMemory mem = pair.getFirst();
        ActionContext lsObj = (ActionContext) mem.getRef(1);
        List<Integer> ls = (List<Integer>) lsObj.getCurrentMem().getRef(0);
        assertEquals(Arrays.asList(100, 200, 300, 400, 500), ls);
        ActionContext sub1Obj = (ActionContext) mem.getRef(2);
        List<Integer> sub1 = (List<Integer>) sub1Obj.getCurrentMem().getRef(0);
        assertEquals(Arrays.asList(200, 300, 400), sub1);
        ActionContext sub2Obj = (ActionContext) mem.getRef(3);
        List<Integer> sub2 = (List<Integer>) sub2Obj.getCurrentMem().getRef(0);
        assertEquals(Arrays.asList(300, 400, 500), sub2);
    }

    private static class MapCaseData {
        final String Type;
        final String type;
        final String firstKey;
        final String firstValue;
        final String secondKey;
        final String secondValue;
        final String secondValue2;
        final String thirdKey;
        final String thirdValue;
        final String outputFirstKey;
        final String outputFirstValue;
        final String outputSecondKey;
        final String outputSecondValue;
        final String outputSecondValue2;
        final String outputThirdKey;
        final String outputThirdValue;

        MapCaseData(String Type, String type, String firstKey, String firstValue, String secondKey, String secondValue, String secondValue2, String thirdKey, String thirdValue, String outputFirstKey, String outputFirstValue, String outputSecondKey, String outputSecondValue, String outputSecondValue2, String outputThirdKey, String outputThirdValue) {
            this.Type = Type;
            this.type = type;
            this.firstKey = firstKey;
            this.firstValue = firstValue;
            this.secondKey = secondKey;
            this.secondValue = secondValue;
            this.secondValue2 = secondValue2;
            this.thirdKey = thirdKey;
            this.thirdValue = thirdValue;
            this.outputFirstKey = outputFirstKey;
            this.outputFirstValue = outputFirstValue;
            this.outputSecondKey = outputSecondKey;
            this.outputSecondValue = outputSecondValue;
            this.outputSecondValue2 = outputSecondValue2;
            this.outputThirdKey = outputThirdKey;
            this.outputThirdValue = outputThirdValue;
        }

        MapCaseData(String Type, String type, String firstKey, String firstValue, String secondKey, String secondValue, String secondValue2, String thirdKey, String thirdValue) {
            this(Type, type, firstKey, firstValue, secondKey, secondValue, secondValue2, thirdKey, thirdValue, firstKey, firstValue, secondKey, secondValue, secondValue2, thirdKey, thirdValue);
        }
    }

    List<MapCaseData> mapCaseTypes = Arrays.asList(
        new MapCaseData("Int", "int",
            "1", "2",
            "3", "4", "7",
            "5", "6"),
        new MapCaseData("Long", "long",
            "1000000000000", "2000000000000",
            "3000000000000", "4000000000000", "7000000000000",
            "5000000000000", "6000000000000"),
        new MapCaseData("Float", "float",
            "1.toFloat", "2.toFloat",
            "3.toFloat", "4.toFloat", "7.toFloat",
            "5.toFloat", "6.toFloat",
            "1.0", "2.0",
            "3.0", "4.0", "7.0",
            "5.0", "6.0"),
        new MapCaseData("Double", "double",
            "1.0", "2.0",
            "3.0", "4.0", "7.0",
            "5.0", "6.0"),
        new MapCaseData("String", "string",
            "('1')", "('2')",
            "('3')", "('4')", "('7')",
            "('5')", "('6')",
            "1", "2",
            "3", "4", "7",
            "5", "6")
    );

    @Test
    public void builtInMapTypeNumericAndString() {
        List<MapCaseData> keyTypes = mapCaseTypes;
        List<MapCaseData> valueTypes = new ArrayList<>(keyTypes);
        valueTypes.add(4, new MapCaseData("Bool", "bool",
            "true", "true",
            "false", "false", "true",
            "false", "false"));
        for (MapCaseData keyType : keyTypes) {
            for (MapCaseData valueType : valueTypes) {
                Pair<RuntimeMemory, String> pair = executeWithStdTypes("{\n" +
                    "let " + keyType.Type + valueType.Type + "Map = { std.LinkedHashMap:[" + keyType.type + ", " + valueType.type + "] }\n" +
                    "var map = new " + keyType.Type + valueType.Type + "Map:[16]\n" +
                    "map.put:[" + keyType.firstKey + ", " + valueType.firstValue + "]\n" +
                    "std.console.log:[map.toString:[] + '']\n" +
                    "std.console.log:[map.keySet.toString:[] + '']\n" +
                    "std.console.log:[('contains1=' + map.containsKey:[" + keyType.firstKey + "])]" +
                    "map.put:[" + keyType.secondKey + ", " + valueType.secondValue + "]\n" +
                    "std.console.log:[map.toString:[] + '']\n" +
                    "std.console.log:[map.keySet.toString:[] + '']\n" +
                    "std.console.log:[('contains2=' + map.containsKey:[" + keyType.secondKey + "])]" +
                    "map.put:[" + keyType.thirdKey + ", " + valueType.thirdValue + "]\n" +
                    "std.console.log:[map.toString:[] + '']\n" +
                    "std.console.log:[map.keySet.toString:[] + '']\n" +
                    "var putRes = map.put:[" + keyType.secondKey + ", " + valueType.secondValue2 + "]\n" +
                    "std.console.log:[map.toString:[] + '']\n" +
                    "std.console.log:[map.keySet.toString:[] + '']\n" +
                    "var removeRes = map.remove:[" + keyType.secondKey + "]\n" +
                    "std.console.log:[map.toString:[] + '']\n" +
                    "std.console.log:[map.keySet.toString:[] + '']\n" +
                    "std.console.log:[('contains3=' + map.containsKey:[" + keyType.secondKey + "])]" +
                    "var getRes = map.get:[" + keyType.thirdKey + "]\n" +
                    "}");
                RuntimeMemory mem = pair.getFirst();
                switch (valueType.type) {
                    case "int":
                        assertEquals(Integer.parseInt(valueType.outputSecondValue), mem.getInt(0));
                        assertEquals(Integer.parseInt(valueType.outputSecondValue2), mem.getInt(1));
                        assertEquals(Integer.parseInt(valueType.outputThirdValue), mem.getInt(2));
                        break;
                    case "long":
                        assertEquals(Long.parseLong(valueType.outputSecondValue), mem.getLong(0));
                        assertEquals(Long.parseLong(valueType.outputSecondValue2), mem.getLong(1));
                        assertEquals(Long.parseLong(valueType.outputThirdValue), mem.getLong(2));
                        break;
                    case "float":
                        assertEquals(Float.parseFloat(valueType.outputSecondValue), mem.getFloat(0), 0.0000001);
                        assertEquals(Float.parseFloat(valueType.outputSecondValue2), mem.getFloat(1), 0.0000001);
                        assertEquals(Float.parseFloat(valueType.outputThirdValue), mem.getFloat(2), 0.0000001);
                        break;
                    case "double":
                        assertEquals(Double.parseDouble(valueType.outputSecondValue), mem.getDouble(0), 0.0000001);
                        assertEquals(Double.parseDouble(valueType.outputSecondValue2), mem.getDouble(1), 0.0000001);
                        assertEquals(Double.parseDouble(valueType.outputThirdValue), mem.getDouble(2), 0.0000001);
                        break;
                    case "bool":
                        assertEquals(Boolean.parseBoolean(valueType.outputSecondValue), mem.getBool(0));
                        assertEquals(Boolean.parseBoolean(valueType.outputSecondValue2), mem.getBool(1));
                        assertEquals(Boolean.parseBoolean(valueType.outputThirdValue), mem.getBool(2));
                        break;
                    default:
                        assertEquals(valueType.outputSecondValue, mem.getRef(2));
                        assertEquals(valueType.outputSecondValue2, mem.getRef(3));
                        assertEquals(valueType.outputThirdValue, mem.getRef(4));
                }
                String output = pair.getSecond();
                assertEquals("" +
                    "{" + keyType.outputFirstKey + "=" + valueType.outputFirstValue + "}\n" +
                    "[" + keyType.outputFirstKey + "]\n" +
                    "contains1=true\n" +
                    "{" + keyType.outputFirstKey + "=" + valueType.outputFirstValue + ", " + keyType.outputSecondKey + "=" + valueType.outputSecondValue + "}\n" +
                    "[" + keyType.outputFirstKey + ", " + keyType.outputSecondKey + "]\n" +
                    "contains2=true\n" +
                    "{" + keyType.outputFirstKey + "=" + valueType.outputFirstValue + ", " + keyType.outputSecondKey + "=" + valueType.outputSecondValue + ", " + keyType.outputThirdKey + "=" + valueType.outputThirdValue + "}\n" +
                    "[" + keyType.outputFirstKey + ", " + keyType.outputSecondKey + ", " + keyType.outputThirdKey + "]\n" +
                    "{" + keyType.outputFirstKey + "=" + valueType.outputFirstValue + ", " + keyType.outputSecondKey + "=" + valueType.outputSecondValue2 + ", " + keyType.outputThirdKey + "=" + valueType.outputThirdValue + "}\n" +
                    "[" + keyType.outputFirstKey + ", " + keyType.outputSecondKey + ", " + keyType.outputThirdKey + "]\n" +
                    "{" + keyType.outputFirstKey + "=" + valueType.outputFirstValue + ", " + keyType.outputThirdKey + "=" + valueType.outputThirdValue + "}\n" +
                    "[" + keyType.outputFirstKey + ", " + keyType.outputThirdKey + "]\n" +
                    "contains3=false\n" +
                    "", output);
            }
        }
    }

    @Test
    public void builtInMapTypeKeyBool() {
        for (MapCaseData valueType : mapCaseTypes) {
            Pair<RuntimeMemory, String> pair = executeWithStdTypes("{\n" +
                "let Bool" + valueType.Type + "Map = { std.LinkedHashMap:[bool, " + valueType.type + "] }\n" +
                "var map = new Bool" + valueType.Type + "Map:[16]\n" +
                "map.put:[true" + ", " + valueType.firstValue + "]\n" +
                "std.console.log:[map.toString:[] + '']\n" +
                "std.console.log:[map.keySet.toString:[] + '']\n" +
                "std.console.log:[('contains1=' + map.containsKey:[true])]\n" +
                "map.put:[false" + ", " + valueType.secondValue + "]\n" +
                "std.console.log:[map.toString:[] + '']\n" +
                "std.console.log:[map.keySet.toString:[] + '']\n" +
                "std.console.log:[('contains2=' + map.containsKey:[false])]\n" +
                "var putRes = map.put:[false" + ", " + valueType.secondValue2 + "]\n" +
                "std.console.log:[map.toString:[] + '']\n" +
                "std.console.log:[map.keySet.toString:[] + '']\n" +
                "var removeRes = map.remove:[false]\n" +
                "std.console.log:[map.toString:[] + '']\n" +
                "std.console.log:[map.keySet.toString:[] + '']\n" +
                "std.console.log:[('contains3=' + map.containsKey:[false])]\n" +
                "var getRes = map.get:[true]\n" +
                "}");
            RuntimeMemory mem = pair.getFirst();
            switch (valueType.type) {
                case "int":
                    assertEquals(Integer.parseInt(valueType.outputSecondValue), mem.getInt(0));
                    assertEquals(Integer.parseInt(valueType.outputSecondValue2), mem.getInt(1));
                    assertEquals(Integer.parseInt(valueType.outputFirstValue), mem.getInt(2));
                    break;
                case "long":
                    assertEquals(Long.parseLong(valueType.outputSecondValue), mem.getLong(0));
                    assertEquals(Long.parseLong(valueType.outputSecondValue2), mem.getLong(1));
                    assertEquals(Long.parseLong(valueType.outputFirstValue), mem.getLong(2));
                    break;
                case "float":
                    assertEquals(Float.parseFloat(valueType.outputSecondValue), mem.getFloat(0), 0.0000001);
                    assertEquals(Float.parseFloat(valueType.outputSecondValue2), mem.getFloat(1), 0.0000001);
                    assertEquals(Float.parseFloat(valueType.outputFirstValue), mem.getFloat(2), 0.0000001);
                    break;
                case "double":
                    assertEquals(Double.parseDouble(valueType.outputSecondValue), mem.getDouble(0), 0.0000001);
                    assertEquals(Double.parseDouble(valueType.outputSecondValue2), mem.getDouble(1), 0.0000001);
                    assertEquals(Double.parseDouble(valueType.outputFirstValue), mem.getDouble(2), 0.0000001);
                    break;
                case "bool":
                    assertEquals(Boolean.parseBoolean(valueType.outputSecondValue), mem.getBool(0));
                    assertEquals(Boolean.parseBoolean(valueType.outputSecondValue2), mem.getBool(1));
                    assertEquals(Boolean.parseBoolean(valueType.outputFirstValue), mem.getBool(2));
                    break;
                default:
                    assertEquals(valueType.outputSecondValue, mem.getRef(2));
                    assertEquals(valueType.outputSecondValue2, mem.getRef(3));
                    assertEquals(valueType.outputFirstValue, mem.getRef(4));
            }
            String output = pair.getSecond();
            assertEquals("" +
                "{true=" + valueType.outputFirstValue + "}\n" +
                "[true]\n" +
                "contains1=true\n" +
                "{true=" + valueType.outputFirstValue + ", false=" + valueType.outputSecondValue + "}\n" +
                "[true, false]\n" +
                "contains2=true\n" +
                "{true=" + valueType.outputFirstValue + ", false=" + valueType.outputSecondValue2 + "}\n" +
                "[true, false]\n" +
                "{true=" + valueType.outputFirstValue + "}\n" +
                "[true]\n" +
                "contains3=false\n" +
                "", output);
        }
    }

    @Test
    public void stringFunctions() {
        String prog = "{\n" +
            "var raw = (\"  hello world \")\n" +
            "var indexOf = raw.indexOf:[(\"hello\")]\n" +
            "var substring = raw.substring:[2, 7]\n" +
            "var trim = raw.trim:[]\n" +
            "var startsWith = raw.startsWith:[(\"  hello \")]\n" +
            "var endsWith = raw.endsWith:[(\"world \")]\n" +
            "var contains = raw.contains:[(\"hello world\")]\n" +
            "var startsWithFalse = raw.startsWith:[(\"xxx\")]\n" +
            "var endsWithFalse = raw.endsWith:[(\"xxx\")]\n" +
            "var containsFalse = raw.contains:[(\"xxx\")]\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder().compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals("  hello world ", mem.getRef(0));
        assertEquals("hello", mem.getRef(1));
        assertEquals("hello world", mem.getRef(2));
        assertEquals(3, mem.refLen());
        assertEquals(2, mem.getInt(0));
        assertEquals(1, mem.intLen());
        assertTrue(mem.getBool(0));
        assertTrue(mem.getBool(1));
        assertTrue(mem.getBool(2));
        assertFalse(mem.getBool(3));
        assertFalse(mem.getBool(4));
        assertFalse(mem.getBool(5));
        assertEquals(6, mem.boolLen());
    }

    @Test
    public void stringParse() {
        String prog = "{\n" +
            "var aInt = (\"123\".toInt)\n" +
            "var aLong = (\"1234567890123456789\".toLong)\n" +
            "var aFloat = (\"123.4\".toFloat)\n" +
            "var aDouble = (\"123.4\".toDouble)\n" +
            "var aBool = (\"true\".toBool)\n" +
            "var aBoolFalse = (\"false\".toBool)\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder().compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals(123, mem.getInt(0));
        assertEquals(1, mem.intLen());
        assertEquals(1234567890123456789L, mem.getLong(0));
        assertEquals(1, mem.longLen());
        assertEquals(123.4f, mem.getFloat(0), 0.0000001);
        assertEquals(1, mem.floatLen());
        assertEquals(123.4, mem.getDouble(0), 0.000000001);
        assertEquals(1, mem.doubleLen());
        assertTrue(mem.getBool(0));
        assertFalse(mem.getBool(1));
        assertEquals(2, mem.boolLen());
    }

    @Test
    public void ext() {
        String prog = "{\n" +
            "var rnd = ext.rand:[]\n" +
            "var time = ext.currentTimeMillis\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(new StdTypes())
            .addTypes(new ExtTypes(new ExtFunctions()
                .setCurrentTimeMillisBlock(System::currentTimeMillis)
                .setRandBlock(() -> ThreadLocalRandom.current().nextDouble())
            ))
            .compile(prog);
        long timeS = System.currentTimeMillis();
        RuntimeMemory mem = interpreter.executeBlock();
        long timeX = System.currentTimeMillis();
        assertTrue(mem.getLong(0) >= timeS);
        assertTrue(mem.getLong(0) <= timeX);
        assertEquals(1, mem.longLen());
        assertTrue(mem.getDouble(0) > 0); // it's rare to be 0
        assertTrue(mem.getDouble(0) < 1);
        assertEquals(1, mem.doubleLen());
    }

    @Test
    public void executableVariable() {
        String prog = "{\n" +
            "var i = 0\n" +
            "function incr: {} int: {\n" +
            "  return: i += 1\n" +
            "}\n" +
            "executable var x = incr\n" +
            "executable var r = ext.rand\n" +
            "var y = x\n" +
            "var z = x\n" +
            "var m = r\n" +
            "var n = r\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(new ExtTypes(new ExtFunctions()
                .setRandBlock(() -> ThreadLocalRandom.current().nextDouble())))
            .compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals(3, mem.intLen());
        assertEquals(2, mem.getInt(0));
        assertEquals(1, mem.getInt(1));
        assertEquals(2, mem.getInt(2));

        assertEquals(2, mem.doubleLen());
        assertNotEquals(0.0, mem.getDouble(0));
        assertNotEquals(0.0, mem.getDouble(1));
        assertTrue(mem.getDouble(0) < 1);
        assertTrue(mem.getDouble(1) < 1);
        assertNotEquals(mem.getDouble(0), mem.getDouble(1));
    }

    @Test
    public void executableField() {
        String prog = "{\n" +
            "class Test: {} do: {\n" +
            "  var i = 0\n" +
            "  private function incr: {} int: {\n" +
            "    return: i += 1\n" +
            "  }\n" +
            "  public executable var x = incr\n" +
            "\n" +
            "  public executable var r = ext.rand\n" +
            "}\n" +
            "var test = new Test:[]\n" +
            "var y = test.x\n" +
            "var z = test.x\n" +
            "var m = test.r\n" +
            "var n = test.r\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(new ExtTypes(new ExtFunctions().setRandBlock(() -> ThreadLocalRandom.current().nextDouble())))
            .compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals(2, mem.intLen());
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));

        assertEquals(2, mem.doubleLen());
        assertNotEquals(0.0, mem.getDouble(0));
        assertNotEquals(0.0, mem.getDouble(1));
        assertTrue(mem.getDouble(0) < 1);
        assertTrue(mem.getDouble(1) < 1);
        assertNotEquals(mem.getDouble(0), mem.getDouble(1));
    }

    @Test
    public void errorHandling() {
        String prog = "{\n" +
            "function parseInt: {s: string} int: {\n" +
            "  var n = s.toInt\n" +
            "  if: err != null; then: {\n" +
            "    std.console.log:[err.message]\n" +
            "    n = 1234\n" +
            "  }\n" +
            "  return: n\n" +
            "}\n" +
            "var x = parseInt:[('10')]\n" +
            "var y = parseInt:[('a')]\n" +
            "}";
        Pair<RuntimeMemory, String> pair = executeWithStdTypes(prog);
        RuntimeMemory mem = pair.getFirst();
        assertEquals(2, mem.intLen());
        assertEquals(10, mem.getInt(0));
        assertEquals(1234, mem.getInt(1));

        String output = pair.getSecond();
        assertEquals("For input string: \"a\"\n", output);
    }

    @Test
    public void aThrow() {
        String prog = "{\n" +
            "function stack4: {} void: {\n" +
            "  throw: ('surprise')\n" +
            "}\n" +
            "function stack3: {} void: {\n" +
            "  stack4:[]\n" +
            "}\n" +
            "function stack2: {} void: {\n" +
            "  stack3:[]\n" +
            "}\n" +
            "function stack1: {} void: {\n" +
            "  stack2:[]\n" +
            "}\n" +
            "stack1:[]\n" +
            "var msg = { null: string }" +
            "if: err != null; then: {\n" +
            "  msg = err.message\n" +
            "}\n" +
            "}";
        Interpreter interpreter = new InterpreterBuilder()
            .compile(prog);
        RuntimeMemory mem = interpreter.executeBlock();
        assertEquals(5, mem.refLen());
        assertEquals("surprise", mem.getRef(4));

        prog = "{\n" +
            "function stack4: {} void: {\n" +
            "  throw: ('surprise')\n" +
            "}\n" +
            "function stack3: {} void: {\n" +
            "  stack4:[]\n" +
            "}\n" +
            "function stack2: {} void: {\n" +
            "  stack3:[]\n" +
            "}\n" +
            "function stack1: {} void: {\n" +
            "  stack2:[]\n" +
            "}\n" +
            "stack1:[]\n" +
            "}";
        interpreter = new InterpreterBuilder()
            .compile(prog, "test.vjson");
        try {
            interpreter.executeBlock();
            fail();
        } catch (Exception e) {
            assertEquals("surprise\n" +
                "  stack4 at test.vjson(3:3)\n" +
                "  stack3 at test.vjson(6:3)\n" +
                "  stack2 at test.vjson(9:3)\n" +
                "  stack1 at test.vjson(12:3)\n" +
                "  <no info> at test.vjson(14:1)", e.getMessage());
        }
    }

    @Test
    public void pass() {
        new InterpreterBuilder()
            .addTypes(new StdTypes())
            .compile(TestFeature.TEST_PROG)
            .executeBlock();
    }
}
