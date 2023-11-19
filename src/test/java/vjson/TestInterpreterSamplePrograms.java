package vjson;

import org.junit.Test;
import vjson.pl.Interpreter;
import vjson.pl.InterpreterBuilder;
import vjson.pl.RuntimeMemoryExplorer;
import vjson.pl.inst.RuntimeMemory;
import vjson.pl.type.lang.StdTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestInterpreterSamplePrograms {
    public static final String SYNTAX_PROG = "{ // vjson-script starts with `{`\n" +
        "// variable definition\n" +
        "var variableName = 1\n" +
        "\n" +
        "// function definition\n" +
        "function max {a: int, b: int, c: int} int {\n" +
        "  // if statement\n" +
        "  if: a >= b && a >= c; then { return: a }\n" +
        "  else if: b >= a && b >= c; then { return: b }\n" +
        "  else { return: c }\n" +
        "  // the `;` can be omitted if `then` is on a new line\n" +
        "}\n" +
        "\n" +
        "// function invocation\n" +
        "var result = max:[1, 2, 3]\n" +
        "\n" +
        "// number, bool literals can be directly used\n" +
        "var intVar = 1\n" +
        "var longVar = 1000000000 // exceeds Integer.MAX_VALUE\n" +
        "var doubleVar = 1.2\n" +
        "// use toFloat to convert other number types to float\n" +
        "var floatVar = doubleVar.toFloat\n" +
        "var boolVar = true\n" +
        "// string literals or expressions starting with string literals\n" +
        "// must be inside (...)\n" +
        "var strVar = ('hello')\n" +
        "// other expressions might also be inside (...)\n" +
        "var calc = (1 + 2 * 3 - 4)\n" +
        "// it's ok for most expressions without (...)\n" +
        "var calc2 = 1 + 2 * 3 - 4\n" +
        "\n" +
        "// to define a variable with null, you must specify the type\n" +
        "var nullVariable = {null: string}\n" +
        "nullVariable = ('abc') // assign a string to it\n" +
        "// assigning a variable with null does not require a type\n" +
        "nullVariable = null\n" +
        "\n" +
        "// class definition\n" +
        "class Person {name: string, age: int} do {\n" +
        "  public var publicField = 1\n" +
        "  private var privateField = 2\n" +
        "  const var constField = 3\n" +
        "  // variables directly inside class are considered private by default\n" +
        "  var alsoPrivateField = 3\n" +
        "  // functions directly inside class are considered public by default\n" +
        "  function talkTo {person: Person} void {\n" +
        "    std.console.log:[ (\"Hi \" + person.name + \", I\\'m \" + name) ]\n" +
        "  }\n" +
        "  private function privateFunc {} void { }\n" +
        "}\n" +
        "// objects\n" +
        "var alice = new Person:[('alice'), 24]\n" +
        "var bob = new Person:[('bob'), 25]\n" +
        "alice.talkTo:[bob]\n" +
        "\n" +
        "// you may also create an object using json syntax\n" +
        "// see sample program `Configuration` for more info about this syntax\n" +
        "var eve = new Person {\n" +
        "  name = eve\n" +
        "  age = 26\n" +
        "}\n" +
        "bob.talkTo:[eve]\n" +
        "\n" +
        "\n" +
        "// template class definition\n" +
        "template { T, U } class PlusToInt { t: T, u: U } do {\n" +
        "  function plus {} int {\n" +
        "    return: t.toInt + u.toInt\n" +
        "  }\n" +
        "}\n" +
        "// concrete types\n" +
        "let IntLongPlusToInt = { PlusToInt:[ int, long ] }\n" +
        "let IntLongPlusToInt2 = { PlusToInt:[ int, long ] }\n" +
        "// IntLongPlusToInt and IntLongPlusToInt2 are the same type\n" +
        "// variables of these types can be passed to each other\n" +
        "\n" +
        "var plusObj = new IntLongPlusToInt:[1, 2.toLong]\n" +
        "std.console.log:[('plusObj.plus result is ' + plusObj.plus:[])]\n" +
        "\n" +
        "// loops\n" +
        "// for loop\n" +
        "var sum = 0\n" +
        "for: [{ var i = 1 }; i <= 10; i += 1] do {\n" +
        "  sum = sum + i\n" +
        "}\n" +
        "std.console.log:[ ('sum of 1 to 10 is ' + sum) ]\n" +
        "// while loop\n" +
        "sum = 0\n" +
        "var n = 1\n" +
        "while: true; do {\n" +
        "  sum = sum + n\n" +
        "  n = n + 1\n" +
        "  if: n >= 10; then {\n" +
        "    break\n" +
        "  }\n" +
        "}\n" +
        "// the `;` can be omitted if `do` is on a new line\n" +
        "std.console.log:[ ('sum of 1 until 10 is ' + sum) ]\n" +
        "\n" +
        "// executable variables/fields\n" +
        "class Counter {} do {\n" +
        "  var n = 0\n" +
        "  private function incr {} int {\n" +
        "    return: n += 1\n" +
        "  }\n" +
        "  // use `executable` modifier and a zero-param function\n" +
        "  // to define an executable variable/field\n" +
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
        "\n" +
        "// exception and error handling\n" +
        "function badFunction {msg: string} void {\n" +
        "  throw: msg // can also be null or error object\n" +
        "}\n" +
        "function catchFunction {} void {\n" +
        "  badFunction:[('bad function call')]\n" +
        "  // use if: err != null to catch errors\n" +
        "  // the following statement will catch all errors in current\n" +
        "  // code block before the error handler\n" +
        "  if: err != null; then {\n" +
        "    // a variable {err: error} is automatically defined\n" +
        "    // and can be used in the error handling code\n" +
        "    std.console.log:[('caught exception: ' + err.message)]\n" +
        "    // also you can print stacktrace:\n" +
        "    std.console.log:[err.formatException]\n" +
        "  }\n" +
        "\n" +
        "  badFunction:[('the second bad function call')]\n" +
        "  // the following statement will catch all errors after the last error handler\n" +
        "  // and before this error handler\n" +
        "  if: err != null; then {\n" +
        "    std.console.log:[('caught second exception: ' + err.message)]\n" +
        "  }\n" +
        "}\n" +
        "catchFunction:[]\n" +
        "} // vjson-script program ends with `}`\n";

    @Test
    public void syntax() {
        String prog = SYNTAX_PROG;
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
            "Hi eve, I'm bob",
            "plusObj.plus result is 3",
            "sum of 1 to 10 is 55",
            "sum of 1 until 10 is 45",
            "cnt1 = 1, cnt2 = 2, cnt3 = 3",
            "caught exception: bad function call",
            "bad function call\n" +
                "  badFunction at (122:3)\n" +
                "  catchFunction at (125:3)\n" +
                "  <no info> at (144:1)",
            "caught second exception: the second bad function call"
        ), output);
    }

    @Test
    public void builtIn() {
        String prog = "{\n" +
            "var intVar = 1\n" +
            "var longVar = 10000000000\n" +
            "var doubleVar = 0.8\n" +
            "var floatVar = 0.8.toFloat\n" +
            "var boolVar = true\n" +
            "var stringVar = ('hello')\n" +
            "var nullVar = {null: string}\n" +
            "// assign string value to the nullVar\n" +
            "nullVar = ('null')\n" +
            "\n" +
            "// convert number types\n" +
            "// all number types can convert to each other\n" +
            "var toInt = doubleVar.toInt\n" +
            "var toLong = intVar.toLong\n" +
            "var toFloat = intVar.toFloat\n" +
            "var toDouble = intVar.toDouble\n" +
            "\n" +
            "// format to string\n" +
            "/* All built-in types (except string),\n" +
            " * has .toString:[] function.\n" +
            " */\n" +
            "var toString = intVar.toString:[]\n" +
            "\n" +
            "/* If a type has .toString:[] function,\n" +
            " * you can use `+` to concat it to a string\n" +
            " */\n" +
            "var stringPlusInt = ('' + intVar)\n" +
            "var intPlusString = intVar + ''\n" +
            "class MyType {a: int, b: double} do {\n" +
            "  function toString {} string {\n" +
            "    return: ('MyType(a=' + a + ', b=' + b + ')')\n" +
            "  }\n" +
            "}\n" +
            "var myInstance = new MyType:[1, 1.2]\n" +
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
            "  '\\nmyInstance = ' + myInstance" +
            ")]\n" +
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

    @Test
    public void pi() {
        String prog = "{\n" +
            "var Pi = 0.0\n" +
            "for: [{var i = 1}; i < 1000000; i += 1] do {\n" +
            "    if: i % 2 == 0; then {\n" +
            "        Pi = Pi - 1.0 / (2 * i - 1).toDouble\n" +
            "    } else {\n" +
            "        Pi = Pi + 1.0 / (2 * i - 1).toDouble\n" +
            "    }\n" +
            "}\n" +
            "Pi = Pi * 4.toDouble\n" +
            "std.console.log:[('Pi: ' + Pi)]\n" +
            "}\n" +
            "// This program may take a while to run\n";
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
        assertEquals("Pi: 3.1415936535907742", output[0]);
    }

    @Test
    public void collections() {
        String prog = "{\n" +
            "// List, Set and Map\n" +
            "let StringList = { std.List:[ string ] }\n" +
            "let IntSet = { std.LinkedHashSet:[ int ] }\n" +
            "// std.LinkedHashSet is also available\n" +
            "let StringIntMap = { std.LinkedHashMap:[ string, int ] }\n" +
            "// std.Map is also available\n" +
            "\n" +
            "var list = new StringList:[16] // initial capacity\n" +
            "var set = new IntSet:[16]\n" +
            "var map = new StringIntMap:[16]\n" +
            "list.add:[('hello')]\n" +
            "list.add:[('world')]\n" +
            "std.console.log:[('list = ' + list)]\n" +
            "\n" +
            "set.add:[1]\n" +
            "set.add:[2]\n" +
            "set.add:[1] // will not add into the set\n" +
            "            // and will return false\n" +
            "std.console.log:[('set = ' + set)]\n" +
            "\n" +
            "map.put:[('alice'), 1]\n" +
            "map.put:[('bob'), 2]\n" +
            "map.put:[('eve'), 3]\n" +
            "std.console.log:[('map = ' + map)]\n" +
            "\n" +
            "// Iterator\n" +
            "let StringIterator = { std.Iterator:[ string ] }\n" +
            "function printIterator {ite: StringIterator} void {\n" +
            "  while: ite.hasNext; do {\n" +
            "    std.console.log:[ite.next + '']\n" +
            "  }\n" +
            "}\n" +
            "// pass iterators into the function\n" +
            "printIterator:[list.iterator]\n" +
            "printIterator:[map.keySet.iterator]\n" +
            "}\n";
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
            "list = [hello, world]",
            "set = [1, 2]",
            "map = {alice=1, bob=2, eve=3}",
            "hello", "world",
            "alice", "bob", "eve"
        ), output);
    }

    @Test
    public void configuration() {
        String prog = "{\n" +
            "\n" +
            "class MysqlConfig { _host: string, _port: int, _user: string, _pass: string, _db: string } do {\n" +
            "  public var host = _host\n" +
            "  public var port = _port\n" +
            "  public var user = _user\n" +
            "  public var pass = _pass\n" +
            "  public var db = _db\n" +
            "\n" +
            "  function check {} void {\n" +
            "    if: port < 1 || port > 65535; then {\n" +
            "      throw: ('invalid port number for mysql')\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "class SentinelAddress { _host: string, _port: int } do {\n" +
            "  public var host = _host\n" +
            "  public var port = _port\n" +
            "\n" +
            "  function check {index: int} void {\n" +
            "    if: port < 1 || port > 65535; then {\n" +
            "      throw: ('invalid port number for sentinel[' + index + ']')\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "class SentinelConfig { _name: string, _sentinels: SentinelAddress[] } do {\n" +
            "  public var name = _name\n" +
            "  public var sentinels = _sentinels\n" +
            "\n" +
            "  function check {} void {\n" +
            "    for: [ {var i = 0}; i < sentinels.length; i += 1 ] do {\n" +
            "      sentinels[i].check:[i]\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "class RedisConfig { _host: string = '', _port: int = 0, _pass: string, _sentinel: SentinelConfig = null } do {\n" +
            "  public var host = _host\n" +
            "  public var port = _port\n" +
            "  public var pass = _pass\n" +
            "  public var sentinel = _sentinel\n" +
            "\n" +
            "  function check {} void {\n" +
            "    if: port < 0 || port > 65535; then {\n" +
            "      throw: ('invalid port number for redis')\n" +
            "    }\n" +
            "    if: (host != '' && port == 0) || (host == '' && port != 0); then {\n" +
            "      throw: ('you must set or unset host and port at the same time')\n" +
            "    }\n" +
            "    if: sentinel != null; then {\n" +
            "      sentinel.check:[]\n" +
            "    }\n" +
            "    if: sentinel != null; then {\n" +
            "      if: host != '' || port != 0; then {\n" +
            "        throw: ('cannot specify host|port and sentinel at the same time')\n" +
            "      }\n" +
            "    } else {\n" +
            "      if: host == '' || port == 0; then {\n" +
            "        throw: ('you must specify one of host|port or sentinel')\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "class Config { _mysql: MysqlConfig, _redis: RedisConfig } do {\n" +
            "  public var mysql = _mysql\n" +
            "  public var redis = _redis\n" +
            "\n" +
            "  function check {} void {\n" +
            "    mysql.check:[]\n" +
            "    redis.check:[]\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "var config = {\n" +
            "  new Config: /* you can use #include to separate checking program and config file */ {\n" +
            "    mysql {\n" +
            "      host = '10.1.0.1'\n" +
            "      port = 3306\n" +
            "      user = root\n" +
            "      pass = '123456'\n" +
            "      db = test\n" +
            "    }\n" +
            "    redis {\n" +
            "      sentinel {\n" +
            "        name = 'mymaster'\n" +
            "        sentinels = [\n" +
            "          {\n" +
            "            host = '10.2.0.1'\n" +
            "            port = 26379\n" +
            "          }\n" +
            "          {\n" +
            "            host = '10.2.0.2'\n" +
            "            port = 26379\n" +
            "          }\n" +
            "          {\n" +
            "            host = '10.2.0.3'\n" +
            "            port = 26379\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "      pass = '123456'\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "config.check:[]\n" +
            "if: err != null; then {\n" +
            "  std.console.log: [ ('bad config: ' + err.formatException) ]\n" +
            "} else {\n" +
            "  std.console.log: [ ('config is ok') ]\n" +
            "}\n" +
            "\n" +
            "}\n";
        StdTypes std = new StdTypes();
        List<String> output = new ArrayList<>();
        std.setOutput(s -> {
            output.add(s);
            return null;
        });
        Interpreter interpreter = new InterpreterBuilder()
            .addTypes(std)
            .compile(prog);
        RuntimeMemoryExplorer explorer = interpreter.getExplorer();
        RuntimeMemory mem = interpreter.execute();

        RuntimeMemoryExplorer configExplorer = explorer.getExplorerByType("Config");
        RuntimeMemory configMem = (RuntimeMemory) explorer.getVariable("config", mem);
        assertNotNull(configMem);

        assertEquals("{\"mysql\":{\"host\":\"10.1.0.1\",\"port\":3306,\"user\":\"root\",\"pass\":\"123456\",\"db\":\"test\"},\"redis\":{\"host\":\"\",\"port\":0,\"pass\":\"123456\",\"sentinel\":{\"name\":\"mymaster\",\"sentinels\":[{\"host\":\"10.2.0.1\",\"port\":26379},{\"host\":\"10.2.0.2\",\"port\":26379},{\"host\":\"10.2.0.3\",\"port\":26379}]}}}",
            configExplorer.toJson(configMem).stringify());

        assertEquals(Collections.singletonList(
            "config is ok"
        ), output);
    }
}
