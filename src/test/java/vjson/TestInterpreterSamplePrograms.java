package vjson;

import org.junit.Test;
import vjson.pl.Interpreter;
import vjson.pl.InterpreterBuilder;
import vjson.pl.type.lang.StdTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestInterpreterSamplePrograms {
    @Test
    public void syntax() {
        String prog = "{ // vjson-lang starts with `{`\n" +
            "// variable definition\n" +
            "var variableName = 1\n" +
            "\n" +
            "// function definition\n" +
            "function max:{a: 'int', b: 'int'} int: {\n" +
            "  // if statement\n" +
            "  if: (a > b) then: { return: (a) }\n" +
            "  else: { return: (b) }\n" +
            "}\n" +
            "\n" +
            "// function invocation\n" +
            "var result = (max:[1, 2])\n" +
            "\n" +
            "// number, bool literals can be directly used\n" +
            "var intVar = 1\n" +
            "var longVar = 1000000000 // exceeds Integer.MAX_VALUE\n" +
            "var doubleVar = 1.2\n" +
            "// use toFloat to convert other number types to float\n" +
            "var floatVar = (doubleVar.toFloat)\n" +
            "var boolVar = true\n" +
            "// strings should be inside (...)\n" +
            "var strVar = ('hello')\n" +
            "// other expressions should be inside (...)\n" +
            "var calc = (1 + 2 * 3 - 4)\n" +
            "// if you are not sure which expression can be used without parentheses\n" +
            "// simply add parentheses around it, it will always work in this way\n" +
            "\n" +
            "// to define a variable with null, you must specify the type\n" +
            "var nullVariable = {null: 'string'}\n" +
            "nullVariable = ('abc') // assign a string to it\n" +
            "// assigning a variable with null does not require a type\n" +
            "nullVariable = null\n" +
            "\n" +
            "// class definition\n" +
            "class Person: {name: 'string', age: 'int'} do: {\n" +
            "  public var publicField = 1\n" +
            "  private var privateField = 2\n" +
            "  // variables directly inside class are considered private by default\n" +
            "  var alsoPrivateField = 3\n" +
            "  // functions directly inside class are considered public by default\n" +
            "  function talkTo: {person: 'Person'} void: {\n" +
            "    std.console.log:[( \"Hi \" + person.name + \", I\\'m \" + name )]\n" +
            "  }\n" +
            "  private function privateFunc: {} void: { }\n" +
            "}\n" +
            "// objects\n" +
            "var alice = {new Person:[('alice'), 24]}\n" +
            "var bob = {new Person:[('bob'), 25]}\n" +
            "alice.talkTo:[(bob)]\n" +
            "\n" +
            "// loops\n" +
            "// for loop\n" +
            "var sum = 0\n" +
            "for: [{ var i = 1 }, (i <= 10), (i += 1)] do: {\n" +
            "  sum = (sum + i)\n" +
            "}\n" +
            "std.console.log:[( 'sum of 1 to 10 is ' + sum )]\n" +
            "// while loop\n" +
            "sum = 0\n" +
            "var n = 1\n" +
            "while: (true) do: {\n" +
            "  sum = (sum + n)\n" +
            "  n = (n + 1)\n" +
            "  if: (n >= 10) then: {\n" +
            "    break\n" +
            "  }\n" +
            "}\n" +
            "std.console.log:[( 'sum of 1 until 10 is ' + sum )]\n" +
            "} // vjson-lang program ends with `}`\n";
        StdTypes std = new StdTypes();
        List<String> output = new ArrayList<>();
        std.setOutput(s -> {
            output.add(s);
            return null;
        });
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(std)
            .compile(prog);
        interpreter.execute();
        assertEquals(Arrays.asList(
            "Hi bob, I'm alice",
            "sum of 1 to 10 is 55",
            "sum of 1 until 10 is 45"
        ), output);
    }

    @Test
    public void builtIn() {
        String prog = "{\n" +
            "var intVar = 1\n" +
            "var longVar = 10000000000\n" +
            "var doubleVar = 0.8\n" +
            "var floatVar = (0.8.toFloat)\n" +
            "var boolVar = true\n" +
            "var stringVar = ('hello')\n" +
            "var nullVar = {null: 'string'}\n" +
            "// assign string value to the nullVar\n" +
            "nullVar = ('null')\n" +
            "\n" +
            "// convert number types\n" +
            "// all number types can convert to each other\n" +
            "var toInt = (doubleVar.toInt)\n" +
            "var toLong = (intVar.toLong)\n" +
            "var toFloat = (intVar.toFloat)\n" +
            "var toDouble = (intVar.toDouble)\n" +
            "\n" +
            "// format to string\n" +
            "/* All built-in types (except string),\n" +
            " * has .toString:[] function.\n" +
            " */\n" +
            "var toString = (intVar.toString:[])\n" +
            "\n" +
            "/* If a type has .toString:[] function,\n" +
            " * you can use `+` to concat it to a string\n" +
            " */\n" +
            "var stringPlusInt = ('' + intVar)\n" +
            "var intPlusString = (intVar + '')\n" +
            "class MyType: {a: 'int', b: 'double'} do: {\n" +
            "  function toString:{} string: {\n" +
            "    return: ('MyType(a=' + a + ', b=' + b + ')')\n" +
            "  }\n" +
            "}\n" +
            "var myInstance = {new MyType:[1, 1.2]}\n" +
            "\n" +
            "// print them\n" +
            "std.console.log: [(\n" +
            "  'intVar = ' + intVar +\n" +
            "  '\\nlongVar = ' + longVar +\n" +
            "  '\\ndoubleVar = ' + doubleVar +\n" +
            "  '\\nfloatVar = ' + floatVar +\n" +
            "  '\\nboolVar = ' + boolVar +\n" +
            "  '\\nstringVar = ' + stringVar +\n" +
            "  '\\nnullVar = ' + nullVar +\n" +
            "  '\\ntoInt = ' + toInt +\n" +
            "  '\\ntoLong = ' + toLong +\n" +
            "  '\\ntoFloat = ' + toFloat +\n" +
            "  '\\ntoDouble = ' + toDouble +\n" +
            "  '\\ntoString = ' + toString +\n" +
            "  '\\nstringPlusInt = ' + stringPlusInt +\n" +
            "  '\\nintPlusString = ' + intPlusString +\n" +
            "  '\\nmyInstance = ' + myInstance\n" +
            ")]" +
            "}\n";
        StdTypes std = new StdTypes();
        String[] output = new String[1];
        std.setOutput(s -> {
            output[0] = s;
            return null;
        });
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(std)
            .compile(prog);
        interpreter.execute();
        assertEquals("intVar = 1\n" +
            "longVar = 10000000000\n" +
            "doubleVar = 0.8\n" +
            "floatVar = 0.8\n" +
            "boolVar = true\n" +
            "stringVar = hello\n" +
            "nullVar = null\n" +
            "toInt = 0\n" +
            "toLong = 1\n" +
            "toFloat = 1.0\n" +
            "toDouble = 1.0\n" +
            "toString = 1\n" +
            "stringPlusInt = 1\n" +
            "intPlusString = 1\n" +
            "myInstance = MyType(a=1, b=1.2)", output[0]);
    }
}
