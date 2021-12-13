package vjson;

import kotlin.Pair;
import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.pl.ASTGen;
import vjson.pl.InterpreterBuilder;
import vjson.pl.ast.Access;
import vjson.pl.ast.Expr;
import vjson.pl.ast.Statement;
import vjson.pl.ast.VariableDefinition;
import vjson.pl.inst.RuntimeMemoryTotal;
import vjson.pl.type.*;
import vjson.pl.type.lang.StdTypes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTypeCheck {
    @Test
    public void access() {
        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
        ctx.addVariable(new Variable("a", IntType.INSTANCE, true, new MemPos(0, 0)));

        Access access = new Access("a");
        access.check(ctx);
        assertEquals(IntType.INSTANCE, access.typeInstance());

        access = new Access("toString", new Access("a"));
        access.check(ctx);
        FunctionDescriptorTypeInstance type = (FunctionDescriptorTypeInstance) access.typeInstance();
        FunctionDescriptor desc = type.functionDescriptor(ctx);
        assertEquals(0, desc.getParams().size());
        assertEquals(StringType.INSTANCE, desc.getReturnType());
    }

    private List<Statement> ast(String prog) {
        int[] idx = {0};
        prog = "{\n" +
            Arrays.stream(prog.split("\n")).map(line -> "var var" + (idx[0]++) + " = (" + line + ")")
                .collect(Collectors.joining("\n")) +
            "\n}";
        JSON.Object o = new ObjectParser(InterpreterBuilder.Companion.interpreterOptions())
            .last(prog);
        //noinspection ConstantConditions
        ASTGen ast = new ASTGen(o);
        List<Statement> stmts = ast.parse();

        MemoryAllocator globalMem = new MemoryAllocator();
        TypeContext ctx = new TypeContext(globalMem);
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
