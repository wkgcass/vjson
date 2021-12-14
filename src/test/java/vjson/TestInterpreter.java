package vjson;

import org.junit.Test;
import vjson.pl.Interpreter;
import vjson.pl.InterpreterBuilder;
import vjson.pl.inst.ActionContext;
import vjson.pl.inst.RuntimeMemory;
import vjson.pl.type.lang.StdTypes;

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
            .execute();

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
            .execute();
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
            .execute();
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
            .execute();
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
        RuntimeMemory mem = interpreter.execute();
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
        RuntimeMemory mem = interpreter.execute();
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
        RuntimeMemory mem = interpreter.execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
        assertEquals(1, mem.getInt(0));
        assertEquals(2, mem.getInt(1));
    }

    @Test
    public void functionRecursion() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{\n" +
                "function fib: { n: int } int: {\n" +
                "  var cache = { new int[n+1] }\n" +
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
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
            .execute();
        assertEquals(55, mem.getInt(0));
    }

    @Test
    public void newAsExpr() {
        RuntimeMemory mem = new InterpreterBuilder()
            .compile("{var a = new int[3].length}")
            .execute();
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
            .execute();
        assertEquals("A(x=1, y=a)", mem.getRef(0));
    }

    @Test
    public void pass() {
        new InterpreterBuilder()
            .addTypes(new StdTypes())
            .compile(TestFeature.TEST_PROG)
            .execute();
    }
}
