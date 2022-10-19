package vjson;

import org.junit.Test;
import vjson.pl.Interpreter;
import vjson.pl.InterpreterBuilder;
import vjson.pl.RuntimeMemoryExplorer;
import vjson.pl.inst.RuntimeMemory;

import static org.junit.Assert.assertEquals;

public class TestRuntimeMemoryExplorer {
    private Interpreter interpreter(String prog) {
        return new InterpreterBuilder()
            .compile(prog);
    }

    @Test
    public void builtInInspect() {
        Interpreter interpreter = interpreter("{\n" +
            "var i = 1\n" +
            "var l = 2.toLong\n" +
            "var f = 1.6.toFloat\n" +
            "var d = 3.2\n" +
            "var n = {null: string}\n" +
            "var s = ('abc')\n" +
            "}\n");
        RuntimeMemory mem = interpreter.execute();
        RuntimeMemoryExplorer explorer = interpreter.getExplorer();
        assertEquals("" +
            "i = 1\n" +
            "l = 2\n" +
            "f = 1.6\n" +
            "d = 3.2\n" +
            "n = null\n" +
            "s = \"abc\"", explorer.inspect(mem).toString());

        assertEquals("1", explorer.inspectVariable("i", mem).toString());
        assertEquals("2", explorer.inspectVariable("l", mem).toString());
        assertEquals("1.6", explorer.inspectVariable("f", mem).toString());
        assertEquals("3.2", explorer.inspectVariable("d", mem).toString());
        assertEquals("null", explorer.inspectVariable("n", mem).toString());
        assertEquals("\"abc\"", explorer.inspectVariable("s", mem).toString());
    }

    @Test
    public void classInspect() {
        Interpreter interpreter = interpreter("{\n" +
            "class X {_i: int, _s: string} do {\n" +
            "  public var i = _i\n" +
            "  public var s = _s\n" +
            "  var ii = i + 10\n" +
            "  var ss = s + 'x'\n" +
            "}\n" +
            "var a = new X {\n" +
            "  i = 1\n" +
            "  s = a\n" +
            "}\n" +
            "var b = new X {\n" +
            "  i = 2\n" +
            "  s = b\n" +
            "}\n" +
            "}\n");
        RuntimeMemory mem = interpreter.execute();
        RuntimeMemoryExplorer explorer = interpreter.getExplorer();
        assertEquals("" +
            "a = {\n" +
            "  public i = 1\n" +
            "  public s = \"a\"\n" +
            "  ii = 11\n" +
            "  ss = \"ax\"\n" +
            "}\n" +
            "b = {\n" +
            "  public i = 2\n" +
            "  public s = \"b\"\n" +
            "  ii = 12\n" +
            "  ss = \"bx\"\n" +
            "}", explorer.inspect(mem).toString());

        assertEquals("{\n" +
            "  public i = 1\n" +
            "  public s = \"a\"\n" +
            "  ii = 11\n" +
            "  ss = \"ax\"\n" +
            "}", explorer.inspectVariable("a", mem).toString());
        assertEquals("{\n" +
            "  public i = 2\n" +
            "  public s = \"b\"\n" +
            "  ii = 12\n" +
            "  ss = \"bx\"\n" +
            "}", explorer.inspectVariable("b", mem).toString());
    }

    @Test
    public void arrayInspect() {
        Interpreter interpreter = interpreter("{\n" +
            "class X {_i: int, _s: string} do {\n" +
            "  public var i = _i\n" +
            "  public var s = _s\n" +
            "}\n" +
            "class A {_i: int[], _l: long[], _f: float[], _d: double[], _s: string[], _x: X[]} do {\n" +
            "  public var i = _i\n" +
            "  public var l = _l\n" +
            "  public var f = _f\n" +
            "  public var d = _d\n" +
            "  public var s = _s\n" +
            "  public var x = _x\n" +
            "}\n" +
            "var a = new A {\n" +
            "  i = [1, 2, 3]\n" +
            "  l = [4, 5, 6]\n" +
            "  f = [0.8, 1.6, 3.2]\n" +
            "  d = [6.4, 12.8, 25.6]\n" +
            "  s = [a, b, c]\n" +
            "  x = [\n" +
            "    {\n" +
            "      i = 1\n" +
            "      s = a\n" +
            "    }\n" +
            "    {\n" +
            "      i = 2\n" +
            "      s = b\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "}\n");
        RuntimeMemory mem = interpreter.execute();
        RuntimeMemoryExplorer explorer = interpreter.getExplorer();

        assertEquals("a = {\n" +
            "  public i = [\n" +
            "    1\n" +
            "    2\n" +
            "    3\n" +
            "  ]\n" +
            "  public l = [\n" +
            "    4\n" +
            "    5\n" +
            "    6\n" +
            "  ]\n" +
            "  public f = [\n" +
            "    0.8\n" +
            "    1.6\n" +
            "    3.2\n" +
            "  ]\n" +
            "  public d = [\n" +
            "    6.4\n" +
            "    12.8\n" +
            "    25.6\n" +
            "  ]\n" +
            "  public s = [\n" +
            "    \"a\"\n" +
            "    \"b\"\n" +
            "    \"c\"\n" +
            "  ]\n" +
            "  public x = [\n" +
            "    {\n" +
            "      public i = 1\n" +
            "      public s = \"a\"\n" +
            "    }\n" +
            "    {\n" +
            "      public i = 2\n" +
            "      public s = \"b\"\n" +
            "    }\n" +
            "  ]\n" +
            "}", explorer.inspect(mem).toString());
    }
}
