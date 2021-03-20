package vpreprocessor;

import org.junit.Test;
import vjson.CharStream;
import vjson.ex.ParserException;
import vpreprocessor.ast.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestPreprocessorParser {
    private PreprocessorContext dummyContext() {
        return new PreprocessorContext(PreprocessorOptions.KT);
    }

    private void test(String input, Statement... statements) {
        Parser parser = new Parser(new PreprocessorContext(PreprocessorOptions.KT));
        StatementSeq actual = parser.parse(CharStream.from(input));
        StatementSeq expected = new StatementSeq(dummyContext(), Arrays.asList(statements));
        assertEquals(expected, actual);
    }

    private void testFail(String input, Consumer<ParserException> f) {
        try {
            new Parser(new PreprocessorContext(PreprocessorOptions.KT)).parse(CharStream.from(input));
            fail();
        } catch (ParserException e) {
            f.accept(e);
        }
    }

    @Test
    public void general() {
        Parser parser = new Parser(new PreprocessorContext(PreprocessorOptions.KT));
        StatementSeq seq = parser.parse(CharStream.from("" +
            "/* #ifdef JVM {{*/@JvmStatic/*}} */\n" +
            "val a = 1\n"
        ));
        System.out.println(seq);

        assertEquals(
            new StatementSeq(dummyContext(), Arrays.asList(
                new If(dummyContext(), new IfDef(dummyContext(), "JVM"),
                    new StatementSeq(dummyContext(), Collections.singletonList(
                        new PlainText(dummyContext(), "@JvmStatic")
                    ))),
                new PlainText(dummyContext(), "\n" +
                    "val a = 1\n")
            ))
            ,
            seq);
    }

    @Test
    public void plain() {
        test("abc", new PlainText(dummyContext(), "abc"));
        test("/* abc */", new PlainText(dummyContext(), "/* abc */"));
        test("// abc", new PlainText(dummyContext(), "// abc"));
        test("/* #{{abc}} */", new PlainText(dummyContext(), "abc"));
    }

    @Test
    public void ifdef() {
        test("/* #ifdef XXX {{ }}",
            new If(dummyContext(), new IfDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " ")
                ))
            ));
        test("/* #ifdef XXX {{ abc }} {{ def }}",
            new If(dummyContext(), new IfDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " abc ")
                ))
            ),
            new PlainText(dummyContext(), " def "));
        test("/* #ifdef XXX {{ abc }} else {{ def }}",
            new If(dummyContext(), new IfDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " abc ")
                )),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " def ")
                ))
            ));
    }

    @Test
    public void ifndef() {
        test("/* #ifndef XXX {{ }}",
            new If(dummyContext(), new IfNotDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " ")
                ))
            ));
        test("/* #ifndef XXX {{ abc }} else {{ def }}",
            new If(dummyContext(), new IfNotDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " abc ")
                )),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " def ")
                ))
            ));
    }

    @Test
    public void codeBlock() {
        test("/* # {}", new StatementSeq(dummyContext(), Collections.emptyList()));
        test("/* #ifndef XXX { {{ abc }} {{ def }} {{ ghi }} }",
            new If(dummyContext(), new IfNotDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Arrays.asList(
                    new PlainText(dummyContext(), " abc "),
                    new PlainText(dummyContext(), " def "),
                    new PlainText(dummyContext(), " ghi ")
                ))
            ));
        test("/* #ifndef XXX {} else { {{ abc }} {{ def }} {{ ghi }} }",
            new If(dummyContext(), new IfNotDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.emptyList()),
                new StatementSeq(dummyContext(), Arrays.asList(
                    new PlainText(dummyContext(), " abc "),
                    new PlainText(dummyContext(), " def "),
                    new PlainText(dummyContext(), " ghi ")
                ))
            ));
        test("/* #ifndef XXX { {{ abc }} } else { {{ def }} }",
            new If(dummyContext(), new IfNotDef(dummyContext(), "XXX"),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " abc ")
                )),
                new StatementSeq(dummyContext(), Collections.singletonList(
                    new PlainText(dummyContext(), " def ")
                ))
            ));
    }

    @Test
    public void stmtSeqFail() {
        testFail("/* # abc */", e -> assertEquals("unexpected token for statement: Macro(abc)", e.getMessage()));
    }

    @Test
    public void ifFail() {
        testFail("/* #ifdef {} */", e -> assertEquals("expecting variable name, but got Macro({)", e.getMessage()));
        testFail("/* #ifdef xxx {} else yyy */", e -> assertEquals("expecting {{ or { for else branch code, but got Macro(yyy)", e.getMessage()));
        testFail("/* # ifdef xxx yyy */", e -> assertEquals("expecting {{ or { for if code, but got Macro(yyy)", e.getMessage()));
        testFail("/* # ifdef */ aaa", e -> assertEquals("expecting variable name, but got Plain( aaa)", e.getMessage()));
        testFail("/* # ifdef */", e -> assertEquals("expecting variable name, but got EOF", e.getMessage()));
        testFail("/* # ifdef xxx */", e -> assertEquals("expecting {{ or { for if code, but got EOF", e.getMessage()));
        testFail("/* # ifdef xxx */ aaa", e -> assertEquals("expecting {{ or { for if code, but got Plain( aaa)", e.getMessage()));
    }

    @Test
    public void statementSeq() {
        testFail("/* # { */", e -> assertEquals("missing ending symbol }", e.getMessage()));
        testFail("/* # {{ */", e -> assertEquals("missing ending symbol }}", e.getMessage()));
    }
}
