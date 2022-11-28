package vjson;

import org.junit.Test;
import vjson.parser.ParserUtils;
import vjson.pl.InterpreterBuilder;
import vjson.pl.ScriptifyContext;

import static org.junit.Assert.assertEquals;

public class TestScriptify {
    @Test
    public void syntax() {
        JSON.Instance<?> inst = ParserUtils.buildFrom(
            CharStream.from(TestInterpreterSamplePrograms.SYNTAX_PROG),
            InterpreterBuilder.Companion.interpreterOptions());
        StringBuilder sb = new StringBuilder();
        inst.scriptify(sb, new ScriptifyContext(2));
        assertEquals("{\n" +
            "var variableName = 1\n" +
            "function max {a: int, b: int, c: int} int {\n" +
            "  if: a >= b && a >= c; then { return: a }\n" +
            "  else if: b >= a && b >= c; then { return: b }\n" +
            "  else { return: c }\n" +
            "}\n" +
            "var result = max:[1, 2, 3]\n" +
            "var intVar = 1\n" +
            "var longVar = 1000000000\n" +
            "var doubleVar = 1.2\n" +
            "var floatVar = doubleVar.toFloat\n" +
            "var boolVar = true\n" +
            "var strVar = ('hello')\n" +
            "var calc = (1 + 2 * 3 - 4)\n" +
            "var calc2 = 1 + 2 * 3 - 4\n" +
            "var nullVariable = { null: string }\n" +
            "nullVariable = ('abc')\n" +
            "nullVariable class Person {name: string, age: int} do {\n" +
            "  public var publicField = 1\n" +
            "  private var privateField = 2\n" +
            "  const var constField = 3\n" +
            "  var alsoPrivateField = 3\n" +
            "  function talkTo {person: Person} void { std.console.log:[(\"Hi \" + person.name + \", I\\'m \" + name)] }\n" +
            "  private function privateFunc {} void { }\n" +
            "}\n" +
            "var alice = new Person:[('alice'), 24]\n" +
            "var bob = new Person:[('bob'), 25]\n" +
            "alice.talkTo:[bob]\n" +
            "var eve = new Person {\n" +
            "  name = eve\n" +
            "  age = 26\n" +
            "}\n" +
            "bob.talkTo:[eve]\n" +
            "template { T, U } class PlusToInt {t: T, u: U} do {\n" +
            "  function plus {} int { return: t.toInt + u.toInt }\n" +
            "}\n" +
            "let IntLongPlusToInt { PlusToInt:[int, long] }\n" +
            "let IntLongPlusToInt2 { PlusToInt:[int, long] }\n" +
            "var plusObj = new IntLongPlusToInt:[1, 2.toLong]\n" +
            "std.console.log:[('plusObj.plus result is ' + plusObj.plus:[])]\n" +
            "var sum = 0\n" +
            "for: [{ var i = 1 }; i <= 10; i += 1] do { sum = sum + i }\n" +
            "std.console.log:[('sum of 1 to 10 is ' + sum)]\n" +
            "sum = 0\n" +
            "var n = 1\n" +
            "while: true; do {\n" +
            "  sum = sum + n\n" +
            "  n = n + 1\n" +
            "  if: n >= 10; then { break  }\n" +
            "}\n" +
            "std.console.log:[('sum of 1 until 10 is ' + sum)]\n" +
            "class Counter {} do {\n" +
            "  var n = 0\n" +
            "  private function incr {} int { return: n += 1 }\n" +
            "  executable public var next = incr\n" +
            "}\n" +
            "var counter = new Counter:[]\n" +
            "var cnt1 = counter.next\n" +
            "var cnt2 = counter.next\n" +
            "var cnt3 = counter.next\n" +
            "std.console.log:[(\n" +
            "  'cnt1 = ' + cnt1 +\n" +
            "  ', cnt2 = ' + cnt2 +\n" +
            "  ', cnt3 = ' + cnt3\n" +
            ")]\n" +
            "function badFunction {msg: string} void { throw: msg }\n" +
            "function catchFunction {} void {\n" +
            "  badFunction:[('bad function call')]\n" +
            "  if: err != null; then { std.console.log:[('caught exception: ' + err.message)], std.console.log:[err.formatException] }\n" +
            "  badFunction:[('the second bad function call')]\n" +
            "  if: err != null; then { std.console.log:[('caught second exception: ' + err.message)] }\n" +
            "}\n" +
            "catchFunction:[]\n" +
            "}\n", sb.toString());
        assertEquals(inst, ParserUtils.buildFrom(
            CharStream.from(sb.toString()),
            InterpreterBuilder.Companion.interpreterOptions()));
    }
}
