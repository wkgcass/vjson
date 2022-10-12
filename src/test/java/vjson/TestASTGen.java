package vjson;

import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.pl.ASTGen;
import vjson.pl.InterpreterBuilder;
import vjson.pl.ast.*;
import vjson.simple.SimpleDouble;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleLong;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestASTGen {
    private List<Statement> gen(String input) {
        //noinspection ConstantConditions
        ASTGen gen = new ASTGen(new ObjectParser(InterpreterBuilder.Companion.interpreterOptions()).last(input));
        return gen.parse();
    }

    @Test
    public void aClass() {
        assertEquals(Collections.singletonList(
            new ClassDefinition("Test", Arrays.asList(
                new Param("a", new Type("int")),
                new Param("b", new Type("int"))),
                Arrays.asList(
                    new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), new Modifiers(ModifierEnum.PRIVATE.getNum())),
                    new FunctionDefinition("b", Collections.emptyList(), new Type("void"), Collections.emptyList(), new Modifiers(ModifierEnum.PUBLIC.getNum()))
                )
            )
        ), gen("{" +
            "class Test: { a: \"int\", b: \"int\" } do: {" +
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
                    new Modifiers(0))
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
                    new VariableDefinition("i", new IntegerLiteral(new SimpleInteger(0)), new Modifiers(0))
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
                new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), new Modifiers(0))
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
                new NewArray(new Type("int[]"), new BinOp(BinOpType.PLUS,
                    new Access("a"),
                    new Access("b")))
            ),
            gen("{ new 'int[a + b]' }")
        );
        assertEquals(
            Collections.singletonList(
                new NewArray(new Type("int[][]"), new BinOp(BinOpType.PLUS,
                    new Access("a"),
                    new Access("b")))
            ),
            gen("{ new 'int[a + b][]' }")
        );
    }

    @Test
    public void newJson() {
        assertEquals(
            Collections.singletonList(
                new NewInstanceWithJson(new Type("A"), new LinkedHashMap<>())
            ),
            gen("{ new A {} }"));
        assertEquals(
            Collections.singletonList(
                new NewInstanceWithJson(new Type("A"), new LinkedHashMap<String, Object>() {{
                    put("x", new IntegerLiteral(new SimpleInteger(1)));
                    put("y", new FloatLiteral(new SimpleDouble(2.0)));
                    put("z", new StringLiteral("3"));
                }})
            ),
            gen("{ new A {x: 1, y: 2.0, z: '3'} }"));
        assertEquals(
            Collections.singletonList(
                new NewInstanceWithJson(new Type("A"), new LinkedHashMap<String, Object>() {{
                    put("x", new LinkedHashMap<String, Object>() {{
                        put("y", new IntegerLiteral(new SimpleInteger(1)));
                        put("z", new ArrayList<Object>() {{
                            add(new LinkedHashMap<String, Object>() {{
                                put("m", new FloatLiteral(new SimpleDouble(2.0)));
                                put("n", new ArrayList<Object>() {{
                                    add(new StringLiteral("3"));
                                    add(new Access("a"));
                                }});
                            }});
                        }});
                    }});
                }})
            ),
            gen("{ new A {x: {y: 1, z: [ {m: 2.0, n: ['3', ${a}]} ]}} }"));
        assertEquals(
            gen("{ new A: {x: {y: 1, z: [ {m: 2.0, n: ['3', ${a}]} ]}} }"),
            gen("{ new A {x: {y: 1, z: [ {m: 2.0, n: ['3', ${a}]} ]}} }"));
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
                    new NewArray(new Type("int[]"), new BinOp(BinOpType.PLUS,
                        new IntegerLiteral(new SimpleInteger(10)),
                        new IntegerLiteral(new SimpleInteger(5)))),
                    new Modifiers(0))
            ),
            gen("{" +
                "var a: { new 'int[10 + 5]' }" +
                "}"));
    }

    @Test
    public void modifiers() {
        assertEquals(Arrays.asList(
            new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), new Modifiers(ModifierEnum.PUBLIC.getNum())),
            new FunctionDefinition("x", Collections.emptyList(), new Type("void"), Collections.emptyList(), new Modifiers(ModifierEnum.PRIVATE.getNum())),
            new VariableDefinition("b", new IntegerLiteral(new SimpleInteger(2)), new Modifiers(ModifierEnum.PUBLIC.getNum() | ModifierEnum.CONST.getNum())),
            new VariableDefinition("c", new IntegerLiteral(new SimpleInteger(3)), new Modifiers(ModifierEnum.EXECUTABLE.getNum()))
        ), gen("{" +
            "public var a: 1\n" +
            "private function x: {} void: {}\n" +
            "public const var b: 2\n" +
            "executable var c = 3\n" +
            "}"));
    }

    @Test
    public void exprStartsWithParentheses() {
        assertEquals(Collections.singletonList(
                new VariableDefinition("a",
                    new BinOp(BinOpType.MINUS,
                        new BinOp(BinOpType.MULTIPLY,
                            new BinOp(BinOpType.PLUS,
                                new IntegerLiteral(new SimpleInteger(1)),
                                new IntegerLiteral(new SimpleInteger(2))),
                            new IntegerLiteral(new SimpleInteger(3))),
                        new IntegerLiteral(new SimpleInteger(4))),
                    new Modifiers(0))
            ),
            gen("{var a = (1 + 2) * 3 - 4}"));
    }

    @Test
    public void templateType() {
        assertEquals(Collections.singletonList(
                new TemplateClassDefinition(Collections.singletonList(
                    new ParamType("E")
                ), new ClassDefinition("Container", Collections.singletonList(
                    new Param("_e", new Type("E"))
                ), Collections.singletonList(
                    new VariableDefinition("e", new Access("_e"), new Modifiers(ModifierEnum.PUBLIC.getNum()))
                )))
            ),
            gen("{\n" +
                "template: { E } class Container: { _e: E } do: {\n" +
                "  public var e = _e\n" +
                "}\n" +
                "}"));
        assertEquals(Collections.singletonList(
                new TemplateClassDefinition(Arrays.asList(
                    new ParamType("T"),
                    new ParamType("U")
                ), new ClassDefinition("BiContainer", Arrays.asList(
                    new Param("t", new Type("T")),
                    new Param("u", new Type("U"))
                ), Collections.emptyList()))
            ),
            gen("{\n" +
                "template: { T, U } class BiContainer: { t: T, u: U } do: { }\n" +
                "}"));
    }

    @Test
    public void aLet() {
        assertEquals(Collections.singletonList(
            new TemplateTypeInstantiation("IntContainer", new Type("Container"), Collections.singletonList(
                new Type("int")
            ))
        ), gen("{\n" +
            "let IntContainer = { Container:[int] }\n" +
            "}"));
        assertEquals(Collections.singletonList(
            new TemplateTypeInstantiation("IntLongContainer", new Type("BiContainer"), Arrays.asList(
                new Type("int"),
                new Type("long")
            ))
        ), gen("{\n" +
            "let IntLongContainer = { BiContainer:[int, long] }\n" +
            "}"));
    }

    @Test
    public void errorHandling() {
        assertEquals(Arrays.asList(
            new ErrorHandlingStatement(Arrays.asList(
                new VariableDefinition("a", new IntegerLiteral(new SimpleInteger(1)), new Modifiers(0)),
                new VariableDefinition("b", new IntegerLiteral(new SimpleInteger(2)), new Modifiers(0))
            ),
                Collections.singletonList(
                    new ThrowStatement(new StringLiteral("hello"))
                ),
                Collections.singletonList(
                    new Assignment(new Access("b"), new IntegerLiteral(new SimpleInteger(4)))
                )),
            new ErrorHandlingStatement(Arrays.asList(
                new VariableDefinition("c", new IntegerLiteral(new SimpleInteger(5)), new Modifiers(0)),
                new VariableDefinition("d", new IntegerLiteral(new SimpleInteger(6)), new Modifiers(0))
            ),
                Collections.singletonList(
                    new Assignment(new Access("c"), new IntegerLiteral(new SimpleInteger(7)))
                ),
                Collections.emptyList())
        ), gen("{\n" +
            "var a = 1\n" +
            "var b = 2\n" +
            "if: err != null; then: {\n" +
            "  throw: ('hello')\n" +
            "} else: {\n" +
            "  b = 4\n" +
            "}\n" +
            "var c = 5\n" +
            "var d = 6\n" +
            "if: err != null; then: {\n" +
            "  c = 7\n" +
            "}\n" +
            "}"));
    }

    @Test
    public void binOpAssignmentInASTGen() {
        assertEquals(Arrays.asList(
                new OpAssignment(BinOpType.PLUS, new Access("a"), new IntegerLiteral(new SimpleInteger(1))),
                new OpAssignment(BinOpType.MINUS, new Access("b"), new IntegerLiteral(new SimpleInteger(2))),
                new OpAssignment(BinOpType.MULTIPLY, new Access("c"), new IntegerLiteral(new SimpleInteger(3))),
                new OpAssignment(BinOpType.DIVIDE, new Access("d"), new IntegerLiteral(new SimpleInteger(4))),
                new OpAssignment(BinOpType.MOD, new Access("e"), new IntegerLiteral(new SimpleInteger(5))),
                new OpAssignment(BinOpType.PLUS, new Access("f"), new StringLiteral("x")),
                new OpAssignment(BinOpType.PLUS, new Access("g"), new IntegerLiteral(new SimpleInteger(6))),
                new OpAssignment(BinOpType.MINUS, new Access("h"), new IntegerLiteral(new SimpleInteger(7))),
                new OpAssignment(BinOpType.MULTIPLY, new Access("i"), new IntegerLiteral(new SimpleInteger(8))),
                new OpAssignment(BinOpType.DIVIDE, new Access("j"), new IntegerLiteral(new SimpleInteger(9))),
                new OpAssignment(BinOpType.MOD, new Access("k"), new IntegerLiteral(new SimpleInteger(10))),
                new OpAssignment(BinOpType.PLUS, new Access("l"), new StringLiteral("y"))
            ),
            gen("{\n" +
                "a+= 1\n" +
                "b-= 2\n" +
                "c*= 3\n" +
                "d/= 4\n" +
                "e%= 5\n" +
                "f+= (\"x\")\n" +
                "g += 6\n" +
                "h -= 7\n" +
                "i *= 8\n" +
                "j /= 9\n" +
                "k %= 10\n" +
                "l += (\"y\")\n" +
                "}"));
    }

    @Test
    public void pass() {
        System.out.println(gen(TestFeature.TEST_PROG));
    }
}
