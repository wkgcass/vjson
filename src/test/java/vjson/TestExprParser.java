package vjson;

import org.junit.Test;
import vjson.cs.LineCol;
import vjson.pl.ExprParser;
import vjson.pl.ExprTokenizer;
import vjson.pl.ast.*;
import vjson.simple.SimpleDouble;
import vjson.simple.SimpleExp;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleLong;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TestExprParser {
    private Expr parse(String str) {
        ExprParser parser = new ExprParser(new ExprTokenizer(str, LineCol.Companion.getEMPTY()));
        return parser.parse();
    }

    @Test
    public void integer() throws Exception {
        assertEquals(new IntegerLiteral(new SimpleInteger(1)), parse("1"));
        assertEquals(new IntegerLiteral(new SimpleLong(100000000000000L)), parse("100000000000000"));
    }

    @Test
    public void floating() {
        assertEquals(new FloatLiteral(new SimpleDouble(1.0)), parse("1.0"));
        assertEquals(new FloatLiteral(new SimpleExp(2.0, 5)), parse("2e5"));
        assertEquals(new FloatLiteral(new SimpleExp(2.1, 5)), parse("2.1e5"));
    }

    @Test
    public void bool() {
        assertEquals(new BoolLiteral(true), parse("true"));
        assertEquals(new BoolLiteral(false), parse("false"));
    }

    @Test
    public void aNull() {
        assertEquals(new NullLiteral(), parse("null"));
    }

    @Test
    public void positive() {
        assertEquals(new Positive(new Access("a")), parse("+a"));
    }

    @Test
    public void negative() {
        assertEquals(new Negative(new Access("a")), parse("-a"));
    }

    @Test
    public void operators() {
        assertEquals(
            new BinOp(
                BinOpType.MINUS,
                new BinOp(
                    BinOpType.PLUS,
                    new IntegerLiteral(new SimpleInteger(1)),
                    new BinOp(
                        BinOpType.MULTIPLY,
                        new IntegerLiteral(new SimpleInteger(2)),
                        new IntegerLiteral(new SimpleInteger(3))
                    )
                ),
                new BinOp(BinOpType.MOD,
                    new BinOp(
                        BinOpType.DIVIDE,
                        new IntegerLiteral(new SimpleInteger(4)),
                        new IntegerLiteral(new SimpleInteger(5))
                    ),
                    new IntegerLiteral(new SimpleInteger(6)))
            ),
            parse("1 + 2 * 3 - 4 / 5 % 6")
        );
    }

    @Test
    public void operatorsFloat() {
        parse("5.0");
        assertEquals(
            new BinOp(
                BinOpType.MINUS,
                new BinOp(
                    BinOpType.PLUS,
                    new FloatLiteral(new SimpleDouble(1.0)),
                    new BinOp(
                        BinOpType.MULTIPLY,
                        new FloatLiteral(new SimpleDouble(2.0)),
                        new FloatLiteral(new SimpleDouble(3.0))
                    )
                ),
                new BinOp(BinOpType.MOD,
                    new BinOp(
                        BinOpType.DIVIDE,
                        new FloatLiteral(new SimpleDouble(4.0)),
                        new FloatLiteral(new SimpleDouble(5.0))
                    ),
                    new FloatLiteral(new SimpleDouble(6.0)))
            ),
            parse("1.0 + 2.0 * 3.0 - 4.0 / 5.0 % 6.0")
        );
    }

    @Test
    public void allBinOp() {
        Expr expr = new BinOp(BinOpType.LOGIC_OR,
            new BinOp(BinOpType.LOGIC_OR,
                new BinOp(BinOpType.LOGIC_OR,
                    new BinOp(BinOpType.CMP_GT,
                        new BinOp(BinOpType.MINUS,
                            new BinOp(BinOpType.PLUS,
                                new Access("a"),
                                new BinOp(BinOpType.MULTIPLY,
                                    new Access("b"),
                                    new Access("c"))),
                            new BinOp(BinOpType.MOD,
                                new BinOp(BinOpType.DIVIDE,
                                    new Access("d"),
                                    new Access("e")),
                                new Access("x"))
                        ),
                        new Access("f")),
                    new BinOp(BinOpType.LOGIC_AND,
                        new BinOp(BinOpType.CMP_LT,
                            new Access("g"),
                            new Access("h")),
                        new BinOp(BinOpType.CMP_GE,
                            new Access("i"),
                            new Access("j")))),
                new BinOp(BinOpType.LOGIC_AND,
                    new BinOp(BinOpType.CMP_LE,
                        new Access("k"),
                        new Access("l")),
                    new BinOp(BinOpType.CMP_EQ,
                        new Access("m"),
                        new Access("n")))
            ),
            new BinOp(BinOpType.CMP_NE,
                new Access("o"),
                new Access("p")));
        assertEquals(expr, parse("a + b * c - d / e % x > f || g < h && i >= j || k <= l && m == n || o != p"));
    }

    @Test
    public void logicNot() {
        Expr expr = new BinOp(BinOpType.LOGIC_OR,
            new BinOp(BinOpType.LOGIC_OR,
                new BinOp(BinOpType.LOGIC_OR,
                    new BinOp(BinOpType.LOGIC_OR,
                        new Access("a"),
                        new LogicNot(new Access("b"))),
                    new LogicNot(new LogicNot(new Access("c")))),
                new Access("d")),
            new LogicNot(new Access("e")));
        assertEquals(expr, parse("a || !b || !!c || d || !e"));
    }

    @Test
    public void string() throws Exception {
        Expr expr = new StringLiteral("abc");
        assertEquals(expr, parse("'abc'"));
    }

    @Test
    public void opAssign() {
        assertEquals(
            new OpAssignment(BinOpType.PLUS,
                new Access("a"),
                new IntegerLiteral(new SimpleInteger(1))),
            parse("a += 1")
        );
        assertEquals(
            new OpAssignment(BinOpType.MINUS,
                new Access("a"),
                new FloatLiteral(new SimpleDouble(1.0))),
            parse("a -= 1.0")
        );
        assertEquals(
            new OpAssignment(BinOpType.MULTIPLY,
                new Access("a"),
                new IntegerLiteral(new SimpleInteger(1))),
            parse("a *= 1")
        );
        assertEquals(
            new OpAssignment(BinOpType.DIVIDE,
                new Access("a"),
                new FloatLiteral(new SimpleDouble(1.0))),
            parse("a /= 1.0")
        );
        assertEquals(
            new OpAssignment(BinOpType.MOD,
                new Access("a"),
                new IntegerLiteral(new SimpleInteger(1))),
            parse("a %= 1")
        );
    }

    @Test
    public void par() {
        assertEquals(
            new BinOp(BinOpType.DIVIDE,
                new BinOp(
                    BinOpType.MULTIPLY,
                    new Access("a"),
                    new BinOp(
                        BinOpType.PLUS,
                        new Access("b"),
                        new Access("c")
                    )
                ),
                new BinOp(
                    BinOpType.MINUS,
                    new Access("d"),
                    new Access("e")
                )),
            parse("a * (b + c) / (d - e)")
        );
        assertEquals(
            new BinOp(
                BinOpType.PLUS,
                new Access("a"),
                new OpAssignment(BinOpType.PLUS,
                    new Access("b"),
                    new IntegerLiteral(new SimpleInteger(1)))
            ),
            parse("a + (b += 1)")
        );
    }

    @Test
    public void getField() {
        assertEquals(
            new BinOp(BinOpType.PLUS,
                new Access("b", new Access("a")),
                new Access("f",
                    new Access("e",
                        new Access("d",
                            new Access("c",
                                new Access("b",
                                    new Access("a"))))))),
            parse("a.b + a.b.c.d.e.f")
        );
    }

    @Test
    public void invocation() {
        assertEquals(new FunctionInvocation(new Access("a"), Arrays.asList(
                new IntegerLiteral(new SimpleInteger(1)),
                new IntegerLiteral(new SimpleInteger(2))
            )),
            parse("a: [1, 2]"));
        assertEquals(new FunctionInvocation(new Access("a"), Collections.singletonList(
                new IntegerLiteral(new SimpleInteger(1))
            )),
            parse("a: [1]"));
        assertEquals(new FunctionInvocation(new Access("a"), Collections.emptyList()),
            parse("a: []"));
    }

    @Test
    public void accessIndex() {
        assertEquals(new AccessIndex(new Access("a"), new IntegerLiteral(new SimpleInteger(1))),
            parse("a[1]"));
    }

    @Test
    public void newExpr() {
        assertEquals(new NewArray(new Type("A[]"), new IntegerLiteral(new SimpleInteger(1))),
            parse("new A[1]"));
        assertEquals(new NewArray(new Type("A[]"), new BinOp(BinOpType.MULTIPLY,
                new IntegerLiteral(new SimpleInteger(1)),
                new IntegerLiteral(new SimpleInteger(2)))),
            parse("new A[1 * 2]"));
        assertEquals(new NewArray(new Type("A[][]"), new IntegerLiteral(new SimpleInteger(1))),
            parse("new A[1][]"));
        assertEquals(new Access("length",
                new NewArray(new Type("A[][]"), new IntegerLiteral(new SimpleInteger(1)))),
            parse("new A[1][].length"));
        assertEquals(new NewInstance(new Type("A"), Collections.singletonList(
                new IntegerLiteral(new SimpleInteger(1))
            )),
            parse("new A:[1]"));
        assertEquals(new NewInstance(new Type("A"), Arrays.asList(
                new IntegerLiteral(new SimpleInteger(1)),
                new IntegerLiteral(new SimpleInteger(2))
            )),
            parse("new A:[1, 2]"));
        assertEquals(
            new FunctionInvocation(new Access("toString",
                new NewInstance(new Type("A"), Arrays.asList(
                    new IntegerLiteral(new SimpleInteger(1)),
                    new IntegerLiteral(new SimpleInteger(2))
                ))),
                Collections.emptyList()),
            parse("new A:[1, 2].toString:[]"));
    }
}
