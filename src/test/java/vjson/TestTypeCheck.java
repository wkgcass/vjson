package vjson;

import kotlin.Pair;
import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.pl.ASTGen;
import vjson.pl.InterpreterBuilder;
import vjson.pl.ast.*;
import vjson.pl.inst.RuntimeMemoryTotal;
import vjson.pl.type.*;
import vjson.pl.type.lang.StdTypes;
import vjson.simple.SimpleDouble;
import vjson.simple.SimpleInteger;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTypeCheck {
    @Test
    public void access() {
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        ctx.addVariable(new Variable("a", IntType.INSTANCE, true, null, new MemPos(0, 0)));

        Access access = new Access("a");
        access.check(ctx, null);
        assertEquals(IntType.INSTANCE, access.typeInstance());

        access = new Access("toString", new Access("a"));
        access.check(ctx, null);
        FunctionDescriptorTypeInstance type = (FunctionDescriptorTypeInstance) access.typeInstance();
        FunctionDescriptor desc = type.functionDescriptor(ctx);
        assertEquals(0, desc.getParams().size());
        assertEquals(StringType.INSTANCE, desc.getReturnType());
    }

    private List<Statement> ast(String prog) {
        return ast(prog, false);
    }

    private List<Statement> ast(String prog, boolean useStd) {
        int[] idx = {0};
        if (!prog.startsWith("{")) {
            prog = "{\n" +
                Arrays.stream(prog.split("\n")).map(line -> "var var" + (idx[0]++) + " = (" + line + ")")
                    .collect(Collectors.joining("\n")) +
                "\n}";
        }
        JSON.Object o = new ObjectParser(InterpreterBuilder.Companion.interpreterOptions())
            .last(prog);
        //noinspection ConstantConditions
        ASTGen ast = new ASTGen(o);
        List<Statement> stmts = ast.parse();

        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        if (useStd) {
            StdTypes std = new StdTypes();
            std.initiateType(ctx, new RuntimeMemoryTotal());
        }
        ctx.checkStatements(stmts);
        return stmts;
    }

    private Expr exprOf(List<Statement> stmt, int idx) {
        return ((VariableDefinition) stmt.get(idx)).getValue();
    }

    @Test
    public void numericBinOp() {
        StringBuilder prog = new StringBuilder();
        List<String> ops = Arrays.asList("+", "-", "*", "/");
        for (String op : ops) {
            for (Pair<String, String> nums : Arrays.asList(
                new Pair<>("1", "2"),
                new Pair<>("1.toLong", "2.toLong"),
                new Pair<>("1.toFloat", "2.toFloat"),
                new Pair<>("1.0", "2.0")
            )) {
                prog.append(nums.getFirst()).append(" ").append(op).append(" ").append(nums.getSecond()).append("\n");
            }
        }

        List<Statement> ast = ast(prog.toString());

        for (int opIdx = 0; opIdx < ops.size(); opIdx += 4) {
            Expr s0 = exprOf(ast, opIdx);
            assertTrue(ops.get(opIdx) + " int", s0.typeInstance() instanceof IntType);
            Expr s1 = exprOf(ast, opIdx + 1);
            assertTrue(ops.get(opIdx) + " long", s1.typeInstance() instanceof LongType);
            Expr s2 = exprOf(ast, opIdx + 2);
            assertTrue(ops.get(opIdx) + " float", s2.typeInstance() instanceof FloatType);
            Expr s3 = exprOf(ast, opIdx + 3);
            assertTrue(ops.get(opIdx) + " double", s3.typeInstance() instanceof DoubleType);
        }
    }

    @Test
    public void modBinOp() {
        StringBuilder prog = new StringBuilder();
        for (Pair<String, String> nums : Arrays.asList(
            new Pair<>("1", "2"),
            new Pair<>("1.toLong", "2.toLong")
        )) {
            prog.append(nums.getFirst()).append(" % ").append(nums.getSecond()).append("\n");
        }

        List<Statement> ast = ast(prog.toString());

        Expr s0 = exprOf(ast, 0);
        assertTrue("% int", s0.typeInstance() instanceof IntType);
        Expr s1 = exprOf(ast, 1);
        assertTrue("% long", s1.typeInstance() instanceof LongType);
    }

    @Test
    public void cmpBinOp() {
        StringBuilder prog = new StringBuilder();
        List<String> ops = Arrays.asList(">", ">=", "<", "<=");
        for (String op : ops) {
            for (Pair<String, String> nums : Arrays.asList(
                new Pair<>("1", "2"),
                new Pair<>("1.toLong", "2.toLong"),
                new Pair<>("1.toFloat", "2.toFloat"),
                new Pair<>("1.0", "2.0")
            )) {
                prog.append(nums.getFirst()).append(" ").append(op).append(" ").append(nums.getSecond()).append("\n");
            }
        }

        List<Statement> ast = ast(prog.toString());

        for (int i = 0; i < ast.size(); ++i) {
            Expr s0 = exprOf(ast, i);
            assertTrue("var" + i, s0.typeInstance() instanceof BoolType);
        }
    }

    @Test
    public void eqBinOp() {
        StringBuilder prog = new StringBuilder();
        List<String> ops = Arrays.asList("!=", "==");
        for (String op : ops) {
            for (Pair<String, String> nums : Arrays.asList(
                new Pair<>("1", "2"),
                new Pair<>("1.toLong", "2.toLong"),
                new Pair<>("1.toFloat", "2.toFloat"),
                new Pair<>("1.0", "2.0"),
                new Pair<>("true", "false"),
                new Pair<>("'a'", "'b'"),
                new Pair<>("'a'", "null")
            )) {
                prog.append(nums.getFirst()).append(" ").append(op).append(" ").append(nums.getSecond()).append("\n");
            }
        }

        List<Statement> ast = ast(prog.toString());

        for (int i = 0; i < ast.size(); ++i) {
            Expr s0 = exprOf(ast, i);
            assertTrue("var" + i, s0.typeInstance() instanceof BoolType);
        }
    }

    @Test
    public void logicBinOp() {
        StringBuilder prog = new StringBuilder();
        List<String> ops = Arrays.asList("||", "&&");
        for (String op : ops) {
            prog.append("true ").append(op).append(" false\n");
        }

        List<Statement> ast = ast(prog.toString());

        for (int i = 0; i < ast.size(); ++i) {
            Expr s0 = exprOf(ast, i);
            assertTrue("var" + i, s0.typeInstance() instanceof BoolType);
        }
    }

    @Test
    public void templateType() {
        List<Statement> stmts = ast("{\n" +
            "template: { T, U } class BiContainer: { _t: T, _u: U } do: {\n" +
            "  public var t = _t\n" +
            "  public var u = _u\n" +
            "}\n" +
            "let IntLongContainer = { BiContainer:[ int, long ] }\n" +
            "var c = new IntLongContainer:[1, 2.toLong]\n" +
            "}");
        VariableDefinition varDef = (VariableDefinition) stmts.get(stmts.size() - 1);
        ClassTypeInstance type = (ClassTypeInstance) varDef.getValue().typeInstance();
        assertEquals(IntType.INSTANCE, type.getCls().getParams().get(0).typeInstance());
        assertEquals(LongType.INSTANCE, type.getCls().getParams().get(1).typeInstance());
        assertEquals(IntType.INSTANCE, ((VariableDefinition) type.getCls().getCode().get(0)).getValue().typeInstance());
        assertEquals(LongType.INSTANCE, ((VariableDefinition) type.getCls().getCode().get(1)).getValue().typeInstance());
    }

    @Test
    public void templateInstancesCheck() {
        // should pass type check
        ast("{\n" +
            "template: { T, U } class BiContainer: { _t: T, _u: U } do: {\n" +
            "  public var t = _t\n" +
            "  public var u = _u\n" +
            "}\n" +
            "let IntLongContainer = { BiContainer:[ int, long ] }\n" +
            "let IntLong2Container = { BiContainer:[ int, long ] }\n" +
            "let IntLong3Container = { BiContainer:[ int, long ] }\n" +
            "var c = new IntLongContainer:[1, 2.toLong]\n" +
            "c = new IntLong2Container:[1, 2.toLong]\n" +
            "function x: {container: IntLong3Container} void: {\n" +
            "}\n" +
            "x:[c]\n" +
            "}");
    }

    @Test
    public void templateCompare() {
        // should pass type check
        ast("{\n" +
                "let IntList = { std.List:[int] }\n" +
                "let IntSet = { std.Set:[int] }\n" +
                "let IntLongMap = { std.Map:[int, long] }\n" +
                "let IntIterator = { std.Iterator:[int] }\n" +
                "function takeIte:{ite: IntIterator} void: {}\n" +
                "\n" +
                "var ls = new IntList:[16]\n" +
                "var set = new IntSet:[16]\n" +
                "var map = new IntLongMap:[16]\n" +
                "takeIte:[ls.iterator]\n" +
                "takeIte:[set.iterator]\n" +
                "takeIte:[map.keySet.iterator]\n" +
                "}",
            true);
    }

    @Test
    public void executableVariable() {
        List<Statement> stmts = ast("{\n" +
            "var incr = 0\n" +
            "function runIncr:{} int: {\n" +
            "  incr = incr + 1\n" +
            "  return: incr\n" +
            "}\n" +
            "executable var x = runIncr\n" +
            "var y = x\n" +
            "class Test: {} do: {\n" +
            "  public executable var field = runIncr\n" +
            "}\n" +
            "var z = new Test:[].field\n" +
            "}");
        assertTrue(stmts.get(0) instanceof VariableDefinition);
        assertTrue(stmts.get(1) instanceof FunctionDefinition);
        assertTrue(stmts.get(2) instanceof VariableDefinition);
        assertTrue(stmts.get(3) instanceof VariableDefinition);
        assertTrue(stmts.get(4) instanceof ClassDefinition);
        assertTrue(stmts.get(5) instanceof VariableDefinition);

        VariableDefinition x = (VariableDefinition) stmts.get(2);
        assertEquals(IntType.INSTANCE, x.typeInstance());
        assertTrue(x.getValue().typeInstance() instanceof FunctionDescriptorTypeInstance);

        VariableDefinition y = (VariableDefinition) stmts.get(3);
        assertEquals(IntType.INSTANCE, y.typeInstance());
        assertEquals(IntType.INSTANCE, y.getValue().typeInstance());

        VariableDefinition z = (VariableDefinition) stmts.get(5);
        assertEquals(IntType.INSTANCE, z.typeInstance());
        assertEquals(IntType.INSTANCE, z.getValue().typeInstance());
    }

    @Test
    public void errorHandling() {
        List<Statement> stmts = ast("{\n" +
            "if: err != null; then: {\n" +
            "  var x = err\n" +
            "  var msg = x.message\n" +
            "}\n" +
            "}");
        assertEquals(1, stmts.size());
        ErrorHandlingStatement errorHandling = (ErrorHandlingStatement) stmts.get(0);
        assertEquals(2, errorHandling.getErrorCode().size());
        VariableDefinition x = (VariableDefinition) errorHandling.getErrorCode().get(0);
        VariableDefinition msg = (VariableDefinition) errorHandling.getErrorCode().get(1);

        assertEquals(ErrorType.INSTANCE, x.typeInstance());
        assertEquals(StringType.INSTANCE, msg.typeInstance());
    }

    @Test
    public void newWithJson() {
        List<Statement> stmts = ast("{\n" +
            "class A:{x: int, y: double} do: {}\n" +
            "new A {x: 1, y: 2.0}\n" +
            "}");
        assertEquals(2, stmts.size());
        assertTrue(stmts.get(0) instanceof ClassDefinition);
        NewInstanceWithJson aNew = (NewInstanceWithJson) stmts.get(1);
        ClassTypeInstance aNewType = (ClassTypeInstance) aNew.typeInstance();
        assertEquals("A", aNewType.getCls().getName());
    }

    @Test
    public void newWithJsonNested() {
        List<Statement> stmts = ast("{\n" +
            "class Good:{id: int, name: string, price: double} do: {}\n" +
            "class Shop:{name: string, goods: Good[]} do: {}\n" +
            "new Shop {\n" +
            "  name: drf\n" +
            "  goods: [\n" +
            "    {\n" +
            "      id: 1\n" +
            "      name: water\n" +
            "      price: 1.5\n" +
            "    }\n" +
            "    {\n" +
            "      id: 2\n" +
            "      name: juice\n" +
            "      price: 3.0\n" +
            "    }\n" +
            "    {\n" +
            "      id: 3\n" +
            "      name: wine\n" +
            "      price: 8.0\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "}");
        assertEquals(3, stmts.size());
        assertTrue(stmts.get(0) instanceof ClassDefinition);
        assertTrue(stmts.get(1) instanceof ClassDefinition);
        NewInstanceWithJson aNew = (NewInstanceWithJson) stmts.get(2);
        ClassTypeInstance aNewType = (ClassTypeInstance) aNew.typeInstance();
        assertEquals("Shop", aNewType.getCls().getName());
    }

    @Test
    public void typeHint() {
        assertEquals(Arrays.asList(
            new FunctionDefinition("longFunc", Collections.singletonList(new Param("i", new Type("long"))), new Type("void"), Collections.emptyList(), new Modifiers(0)),
            new ClassDefinition("LongClass", Collections.singletonList(new Param("l", new Type("long"))), Collections.emptyList()),
            new FunctionInvocation(new Access("longFunc"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(1)))),
            new VariableDefinition("a", new Access("toLong", new IntegerLiteral(new SimpleInteger(1))), new Modifiers(0)),
            new Assignment(new Access("a"), new IntegerLiteral(new SimpleInteger(2))),
            new NewInstance(new Type("LongClass"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(3)))),
            new VariableDefinition("arr", new NewArray(new Type("long[]"), new IntegerLiteral(new SimpleInteger(1))), new Modifiers(0)),
            new Assignment(new AccessIndex(new Access("arr"), new IntegerLiteral(new SimpleInteger(0))), new IntegerLiteral(new SimpleInteger(4)))
        ), ast("{\n" +
            "function longFunc {i: long} void {}\n" +
            "class LongClass {l: long} do {}\n" +
            "longFunc:[1]\n" +
            "var a = 1.toLong\n" +
            "a = 2\n" +
            "new LongClass:[3]\n" +
            "var arr = new long[1]\n" +
            "arr[0] = 4\n" +
            "}"));
        assertEquals(Arrays.asList(
            new FunctionDefinition("floatFunc", Collections.singletonList(new Param("f", new Type("float"))), new Type("void"), Collections.emptyList(), new Modifiers(0)),
            new ClassDefinition("FloatClass", Collections.singletonList(new Param("f", new Type("float"))), Collections.emptyList()),
            new FunctionInvocation(new Access("floatFunc"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(1)))),
            new FunctionInvocation(new Access("floatFunc"), Collections.singletonList(new FloatLiteral(new SimpleDouble(1.1)))),
            new VariableDefinition("a", new Access("toFloat", new FloatLiteral(new SimpleDouble(2.4))), new Modifiers(0)),
            new Assignment(new Access("a"), new IntegerLiteral(new SimpleInteger(2))),
            new Assignment(new Access("a"), new FloatLiteral(new SimpleDouble(3.6))),
            new NewInstance(new Type("FloatClass"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(4)))),
            new NewInstance(new Type("FloatClass"), Collections.singletonList(new FloatLiteral(new SimpleDouble(4.8)))),
            new VariableDefinition("arr", new NewArray(new Type("float[]"), new IntegerLiteral(new SimpleInteger(1))), new Modifiers(0)),
            new Assignment(new AccessIndex(new Access("arr"), new IntegerLiteral(new SimpleInteger(0))), new IntegerLiteral(new SimpleInteger(5)))
        ), ast("{\n" +
            "function floatFunc {f: float} void {}\n" +
            "class FloatClass {f: float} do {}\n" +
            "floatFunc:[1]\n" +
            "floatFunc:[1.1]\n" +
            "var a = 2.4.toFloat\n" +
            "a = 2\n" +
            "a = 3.6\n" +
            "new FloatClass:[4]\n" +
            "new FloatClass:[4.8]\n" +
            "var arr = new float[1]\n" +
            "arr[0] = 5\n" +
            "}"));
        assertEquals(Arrays.asList(
            new FunctionDefinition("doubleFunc", Collections.singletonList(new Param("d", new Type("double"))), new Type("void"), Collections.emptyList(), new Modifiers(0)),
            new ClassDefinition("DoubleClass", Collections.singletonList(new Param("d", new Type("double"))), Collections.emptyList()),
            new FunctionInvocation(new Access("doubleFunc"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(1)))),
            new VariableDefinition("a", new FloatLiteral(new SimpleDouble(1.6)), new Modifiers(0)),
            new Assignment(new Access("a"), new IntegerLiteral(new SimpleInteger(2))),
            new NewInstance(new Type("DoubleClass"), Collections.singletonList(new IntegerLiteral(new SimpleInteger(3)))),
            new VariableDefinition("arr", new NewArray(new Type("double[]"), new IntegerLiteral(new SimpleInteger(1))), new Modifiers(0)),
            new Assignment(new AccessIndex(new Access("arr"), new IntegerLiteral(new SimpleInteger(0))), new IntegerLiteral(new SimpleInteger(4)))
        ), ast("{\n" +
            "function doubleFunc {d: double} void {}\n" +
            "class DoubleClass {d: double} do {}\n" +
            "doubleFunc:[1]\n" +
            "var a = 1.6\n" +
            "a = 2\n" +
            "new DoubleClass:[3]\n" +
            "var arr = new double[1]\n" +
            "arr[0] = 4\n" +
            "}"));
    }

    @Test
    public void pass() {
        //noinspection ConstantConditions
        ASTGen gen = new ASTGen(new ObjectParser(InterpreterBuilder.Companion.interpreterOptions())
            .last(TestFeature.TEST_PROG));
        List<Statement> stmts = gen.parse();
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        new StdTypes().initiateType(ctx, new RuntimeMemoryTotal());
        ctx.checkStatements(stmts);
    }
}
