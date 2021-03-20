package vpreprocessor;

import kotlin.NotImplementedError;
import org.junit.Test;
import vpreprocessor.ast.*;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class TestPreprocessorAST {
    private PreprocessorContext dummyContext() {
        return new PreprocessorContext();
    }

    private String exec(Statement stmt) {
        StringBuilder sb = new StringBuilder();
        stmt.exec(sb);
        return sb.toString();
    }

    @Test
    public void plainText() {
        PlainText ast = new PlainText(dummyContext(), "abc");
        assertEquals("abc", ast.getText());
        assertNotNull(ast.getContext());
        assertEquals("{{abc}}", ast.toString());
        assertEquals("abc", exec(ast));

        PlainText equals = new PlainText(dummyContext(), "abc");
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        assertEquals(ast.hashCode(), ast.getText().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);

        PlainText differs = new PlainText(dummyContext(), "a");
        assertNotEquals(ast, differs);
    }

    @Test
    public void statementSeq() {
        StatementSeq ast = new StatementSeq(dummyContext(),
            Arrays.asList(
                new PlainText(dummyContext(), "abc"),
                new PlainText(dummyContext(), "def")
            ));
        assertEquals(Arrays.asList(
            new PlainText(dummyContext(), "abc"),
            new PlainText(dummyContext(), "def")
        ), ast.getSeq());
        assertNotNull(ast.getContext());
        assertEquals("" +
                "{ {{abc}} {{def}} }"
            , ast.toString());
        assertEquals("abcdef", exec(ast));

        StatementSeq equals = new StatementSeq(dummyContext(),
            Arrays.asList(
                new PlainText(dummyContext(), "abc"),
                new PlainText(dummyContext(), "def")
            ));
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        assertEquals(ast.hashCode(), ast.getSeq().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);

        StatementSeq differs = new StatementSeq(dummyContext(),
            Collections.singletonList(
                new PlainText(dummyContext(), "abc")
            ));
        assertNotEquals(ast, differs);
    }

    @Test
    public void ifdef() {
        PreprocessorContext ctx = new PreprocessorContext();
        IfDef ast = new IfDef(ctx, "abc");
        assertEquals("abc", ast.getName());
        assertNotNull(ast.getContext());
        assertEquals("ifdef abc", ast.toString());
        assertEquals(false, ast.exec());
        ctx.define("abc");
        assertEquals(true, ast.exec());

        IfDef equals = new IfDef(dummyContext(), "abc");
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        assertEquals(ast.hashCode(), ast.getName().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);

        IfDef differs = new IfDef(dummyContext(), "def");
        assertNotEquals(ast, differs);
    }

    @Test
    public void ifndef() {
        PreprocessorContext ctx = new PreprocessorContext();
        IfNotDef ast = new IfNotDef(ctx, "abc");
        assertEquals("abc", ast.getName());
        assertNotNull(ast.getContext());
        assertEquals("ifndef abc", ast.toString());
        assertEquals(true, ast.exec());
        ctx.define("abc");
        assertEquals(false, ast.exec());

        IfNotDef equals = new IfNotDef(dummyContext(), "abc");
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        assertEquals(ast.hashCode(), ast.getName().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);

        IfNotDef differs = new IfNotDef(dummyContext(), "def");
        assertNotEquals(ast, differs);
    }

    @Test
    public void ifx() {
        PreprocessorContext ctx = new PreprocessorContext();
        If ast = new If(ctx, new IfDef(ctx, "a"),
            new StatementSeq(dummyContext(), Collections.singletonList(
                new PlainText(dummyContext(), "abc")
            )));
        assertEquals("", exec(ast));
        ctx.define("a");
        assertEquals("abc", exec(ast));

        assertEquals(new IfDef(dummyContext(), "a"), ast.getCondition());
        assertEquals(new StatementSeq(dummyContext(), Collections.singletonList(
            new PlainText(dummyContext(), "abc")
        )), ast.getCode());
        assertEquals("ifdef a { {{abc}} }", ast.toString());
        assertNull(ast.getElseCode());
        assertNotNull(ast.getContext());

        ctx = new PreprocessorContext();
        If elseX = new If(ctx, new IfDef(ctx, "a"),
            new StatementSeq(dummyContext(), Collections.emptyList()),
            new StatementSeq(dummyContext(), Arrays.asList(
                new PlainText(dummyContext(), "abc"),
                new PlainText(dummyContext(), "def")
            )));
        assertEquals("abcdef", exec(elseX));
        ctx.define("a");
        assertEquals("", exec(elseX));

        assertEquals(new StatementSeq(dummyContext(), Arrays.asList(
            new PlainText(dummyContext(), "abc"),
            new PlainText(dummyContext(), "def")
        )), elseX.getElseCode());
        assertEquals("ifdef a {} else { {{abc}} {{def}} }", elseX.toString());

        If ifelifelse = new If(dummyContext(), new IfNotDef(dummyContext(), "a"),
            new StatementSeq(dummyContext(), Arrays.asList(
                new PlainText(dummyContext(), "abc"),
                new PlainText(dummyContext(), "def")
            )),
            new StatementSeq(dummyContext(), Collections.singletonList(
                new If(dummyContext(), new Invocation(dummyContext(), "defined", Collections.singletonList(new Var(dummyContext(), "x"))),
                    new StatementSeq(dummyContext(), Collections.singletonList(
                        new PlainText(dummyContext(), "ghi")
                    )),
                    new StatementSeq(dummyContext(), Collections.singletonList(
                        new If(dummyContext(), new Invocation(dummyContext(), "aaa", Collections.emptyList()),
                            new StatementSeq(dummyContext(), Collections.singletonList(
                                new PlainText(dummyContext(), "jkl")
                            )),
                            new StatementSeq(dummyContext(), Collections.singletonList(
                                new PlainText(dummyContext(), "mno")
                            )))
                    ))
                )
            ))
        );
        assertEquals("" +
                "ifndef a { {{abc}} {{def}} } elif defined(x) { {{ghi}} } elif aaa() { {{jkl}} } else { {{mno}} }"
            , ifelifelse.toString());

        If if2 = new If(dummyContext(), new Invocation(dummyContext(), "defined", Collections.singletonList(new Var(dummyContext(), "abc"))),
            new StatementSeq(dummyContext(), Collections.emptyList()));
        assertEquals("if defined(abc) {}", if2.toString());

        If if3 = new If(ctx, new IfDef(ctx, "a"),
            new StatementSeq(dummyContext(), Collections.singletonList(
                new PlainText(dummyContext(), "abc")
            )),
            new StatementSeq(dummyContext(), Collections.emptyList()));

        If equals = new If(dummyContext(), new IfDef(dummyContext(), "a"),
            new StatementSeq(dummyContext(), Collections.singletonList(
                new PlainText(dummyContext(), "abc")
            )));
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        //noinspection IntegerMultiplicationImplicitCastToLong
        assertEquals(ast.hashCode(),
            (31 * (31 * ast.getCondition().hashCode() + ast.getCode().hashCode()))
        );
        //noinspection IntegerMultiplicationImplicitCastToLong
        assertEquals(elseX.hashCode(),
            (31 * (31 * elseX.getCondition().hashCode() + elseX.getCode().hashCode())) + elseX.getElseCode().hashCode()
        );

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);
        assertNotEquals(ast, elseX);
        assertNotEquals(ast, ifelifelse);
        assertNotEquals(ast, if2);
        assertNotEquals(ast, if3);
    }

    @Test
    public void var() {
        Var ast = new Var(dummyContext(), "abc");
        assertEquals("abc", ast.getName());
        assertNotNull(ast.getContext());
        assertEquals("abc", ast.toString());

        Var equals = new Var(dummyContext(), "abc");
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        assertEquals(ast.hashCode(), ast.getName().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);

        Var differs = new Var(dummyContext(), "def");
        assertNotEquals(ast, differs);

        try {
            ast.exec();
            fail();
        } catch (NotImplementedError ignore) {
        }
    }

    @Test
    public void invocation() {
        Invocation ast = new Invocation(dummyContext(), "abc", Collections.emptyList());
        assertEquals("abc", ast.getFunction());
        assertEquals(Collections.emptyList(), ast.getArguments());
        assertNotNull(ast.getContext());
        assertEquals("abc()", ast.toString());

        Invocation invocation2 = new Invocation(dummyContext(), "def", Collections.singletonList(
            new Var(dummyContext(), "a")
        ));
        assertEquals("def(a)", invocation2.toString());

        Invocation invocation3 = new Invocation(dummyContext(), "abc", Arrays.asList(
            new Var(dummyContext(), "a"),
            new Var(dummyContext(), "b")
        ));
        assertEquals("abc(a, b)", invocation3.toString());

        Invocation equals = new Invocation(dummyContext(), "abc", Collections.emptyList());
        assertEquals(ast, ast);
        assertEquals(ast, equals);
        //noinspection IntegerMultiplicationImplicitCastToLong
        assertEquals(ast.hashCode(),
            (31 * ast.getFunction().hashCode()) + ast.getArguments().hashCode());

        assertNotEquals(ast, null);
        assertNotEquals(ast, 1);
        assertNotEquals(ast, invocation2);
        assertNotEquals(ast, invocation3);

        try {
            ast.exec();
        } catch (NotImplementedError ignore) {
        }
    }
}
