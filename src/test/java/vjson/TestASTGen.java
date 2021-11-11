package vjson;

import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.pl.ASTGen;
import vjson.pl.ast.*;
import vjson.simple.SimpleDouble;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleLong;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestASTGen {
    private List<Statement> gen(String input) {
        //noinspection ConstantConditions
        ASTGen gen = new ASTGen(new ObjectParser(new ParserOptions()
            .setStringSingleQuotes(true)
            .setKeyNoQuotes(true)
            .setKeyNoQuotesWithDot(true)
            .setNullArraysAndObjects(true)
            .setAllowSkippingCommas(true)
            .setAllowObjectEntryWithoutValue(true)
        ).last(input));
        return gen.parse();
    }

    @Test
    public void aClass() {
        assertEquals(Collections.singletonList(
            new ClassDefinition("Test", Arrays.asList(
                new Param("a", new Type("int")),
                new Param("b", new Type("int"))),
                Arrays.asList(
                    new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), Visibility.PRIVATE),
                    new FunctionDefinition("b", Collections.emptyList(), new Type("void"), Collections.emptyList(), Visibility.PUBLIC)
                )
            )
        ), gen("{" +
            "class Test: { a: \"int\", b: \"int\" } typeof: {" +
            "  private var a: 1\n" +
            "  public function b: {} void: {}" +
            "}" +
            "}"));
    }

    @Test
    public void function() {
        assertEquals(Collections.singletonList(
                new FunctionDefinition("sum", Arrays.asList(
                    new Param("a", new Type("int")),
                    new Param("b", new Type("int"))),
                    new Type("int"),
                    Collections.singletonList(
                        new ReturnStatement(new BinOp(BinOpType.PLUS,
                            new Access("a"),
                            new Access("b")))
                    ),
                    Visibility.NONE)
            ),
            gen("{" +
                "function sum: { a: 'int', b: 'int' } int: {" +
                "  return: 'a + b'" +
                "}" +
                "}")
        );
    }

    @Test
    public void forLoop() {
        assertEquals(Collections.singletonList(
            new ForLoop(
                Collections.singletonList(
                    new VariableDefinition("i", new IntegerLiteral(new SimpleInteger(0)), Visibility.NONE)
                ),
                new BinOp(BinOpType.CMP_LT, new Access("i"), new IntegerLiteral(new SimpleInteger(10))),
                Collections.singletonList(
                    new OpAssignment(BinOpType.PLUS, new Access("i"), new IntegerLiteral(new SimpleInteger(1)))
                ),
                Collections.singletonList(
                    new FunctionInvocation(new Access("log", new Access("console")),
                        Collections.singletonList(
                            new Access("i")
                        ))
                ))
        ), gen("{" +
            "for: [ {var i: 0}, 'i < 10', 'i += 1' ] do: {" +
            "  console.log: ['i']" +
            "}" +
            "}"));
    }

    @Test
    public void whileLoop() {
        assertEquals(Collections.singletonList(
            new WhileLoop(new BinOp(BinOpType.CMP_LT,
                new Access("i"),
                new IntegerLiteral(new SimpleInteger(10))),
                Collections.singletonList(
                    new FunctionInvocation(new Access("log", new Access("console")),
                        Collections.singletonList(
                            new Access("i")
                        ))
                ))
        ), gen("" +
            "{" +
            "while: 'i < 10' do: {" +
            "  console.log: ['i']" +
            "}" +
            "}"));
    }

    @Test
    public void aIf() {
        assertEquals(Collections.singletonList(
            new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(60))),
                Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                    new StringLiteral("fail")
                ))),
                Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                    new StringLiteral("pass")
                ))))
        ), gen("{" +
            "if: 'a < 60' then: {" +
            "  console.log: [\"'fail'\"]" +
            "} else: {" +
            "  console.log: [\"'pass'\"]" +
            "}" +
            "}"));
        assertEquals(Arrays.asList(
            new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(60))),
                Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                    new StringLiteral("fail")
                ))),
                Collections.emptyList()),
            new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                new StringLiteral("end")
            ))
        ), gen("{" +
            "if: 'a < 60' then: {" +
            "  console.log: [\"'fail'\"]" +
            "}" +
            "console.log: [\"'end'\"]" +
            "}"));
        assertEquals(Collections.singletonList(
            new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(60))),
                Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                    new StringLiteral("fail")
                ))),
                Collections.singletonList(
                    new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(80))),
                        Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                            new StringLiteral("pass")
                        ))),
                        Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                            new StringLiteral("good")
                        ))))
                ))
        ), gen("{" +
            "if: 'a < 60' then: {" +
            "  console.log: [\"'fail'\"]" +
            "} else if: 'a < 80' then: {" +
            "  console.log: [\"'pass'\"]" +
            "} else: {" +
            "  console.log: [\"'good'\"]" +
            "}" +
            "}"));
        assertEquals(Arrays.asList(
            new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(60))),
                Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                    new StringLiteral("fail")
                ))),
                Collections.singletonList(
                    new IfStatement(new BinOp(BinOpType.CMP_LT, new Access("a"), new IntegerLiteral(new SimpleInteger(80))),
                        Collections.singletonList(new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                            new StringLiteral("pass")
                        ))),
                        Collections.emptyList())
                )),
            new FunctionInvocation(new Access("log", new Access("console")), Collections.singletonList(
                new StringLiteral("end")
            ))
        ), gen("{" +
            "if: 'a < 60' then: {" +
            "  console.log: [\"'fail'\"]" +
            "} else if: 'a < 80' then: {" +
            "  console.log: [\"'pass'\"]" +
            "}" +
            "console.log: [\"'end'\"]" +
            "}"));
    }

    @Test
    public void variable() {
        assertEquals(
            Collections.singletonList(
                new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), Visibility.NONE)
            ),
            gen("{ var a: 1 }")
        );
    }

    @Test
    public void newInstance() {
        assertEquals(
            Collections.singletonList(
                new NewInstance(new Type("a.b.c"), Collections.emptyList())
            ),
            gen("{ new a.b.c }")
        );
        assertEquals(
            Collections.singletonList(
                new NewInstance(new Type("a.b.c"), Arrays.asList(
                    new IntegerLiteral(new SimpleInteger(1)),
                    new IntegerLiteral(new SimpleInteger(2))
                ))
            ),
            gen("{ new a.b.c: [ 1, 2 ] }")
        );
    }

    @Test
    public void newArray() {
        assertEquals(
            Collections.singletonList(
                new NewArray(new Type("int"), new BinOp(BinOpType.PLUS,
                    new Access("a"),
                    new Access("b")))
            ),
            gen("{ new 'int[a + b]' }")
        );
    }

    @Test
    public void simpleReturnBreakContinue() {
        assertEquals(
            Arrays.asList(
                new ReturnStatement(),
                new ReturnStatement(new IntegerLiteral(new SimpleInteger(1))),
                new BreakStatement(),
                new ContinueStatement()
            ),
            gen("{" +
                "return\n" +
                "return: 1\n" +
                "break\n" +
                "continue" +
                "}"));
    }

    @Test
    public void simpleAssignment() {
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new IntegerLiteral(new SimpleInteger(1)))
        ), gen("{a: 1}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new IntegerLiteral(new SimpleLong(1000000000000L)))
        ), gen("{a: 1000000000000}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new FloatLiteral(new SimpleDouble(2.0)))
        ), gen("{a: 2.0}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new BoolLiteral(true))
        ), gen("{a: true}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new BoolLiteral(false))
        ), gen("{a: false}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new NullLiteral())
        ), gen("{a: null}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new BinOp(BinOpType.PLUS,
                new IntegerLiteral(new SimpleInteger(1)),
                new IntegerLiteral(new SimpleInteger(2))))
        ), gen("{a: \"1 + 2\"}"));
        assertEquals(Collections.singletonList(
            new Assignment(new Access("a"), new StringLiteral("abc"))
        ), gen("{a: \"'abc'\"}"));
    }

    @Test
    public void invocation() {
        assertEquals(Collections.singletonList(
            new FunctionInvocation(new Access("sum"), Arrays.asList(
                new IntegerLiteral(new SimpleInteger(1)),
                new IntegerLiteral(new SimpleInteger(2)),
                new IntegerLiteral(new SimpleInteger(3))
            ))
        ), gen("{sum: [1, 2, 3]}"));
    }

    @Test
    public void objectAsValue() {
        assertEquals(
            Collections.singletonList(
                new VariableDefinition("a",
                    new NewArray(new Type("int"), new BinOp(BinOpType.PLUS,
                        new IntegerLiteral(new SimpleInteger(10)),
                        new IntegerLiteral(new SimpleInteger(5)))),
                    Visibility.NONE)
            ),
            gen("{" +
                "var a: { new 'int[10 + 5]' }" +
                "}"));
    }

    @Test
    public void pass() {
        System.out.println(gen("{\n" +
            "function printPrimes: { searchRange: \"int\" } void: {\n" +
            "  var notPrime: { new \"bool[searchRange + 1]\" }\n" +
            "  for: [ { var i: 2 }, \"i <= searchRange\", \"i += 1\"] do: {\n" +
            "    if: \"!notPrime[i]\" then: {\n" +
            "      var j: 2\n" +
            "      while: true do: {\n" +
            "        var n: \"i * j\"\n" +
            "        if: \"n > searchRange\" then: {\n" +
            "          break\n" +
            "        }\n" +
            "        \"notPrime[n]\": true\n" +
            "        j: \"j + 1\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  console.log: [ \"'primes:'\" ]\n" +
            "  for: [ { var i: 2 }, \"i < notPrime.length\", \"i += 1\"] do: {\n" +
            "    if: \"!notPrime[i]\" then: {\n" +
            "      console.log: [ \"i\" ]\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "printPrimes: [ 10 ]\n" +
            "}"));
    }
}
