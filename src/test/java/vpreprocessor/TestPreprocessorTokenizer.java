package vpreprocessor;

import org.junit.Test;
import vjson.CharStream;
import vjson.ex.ParserException;
import vpreprocessor.token.EOFToken;
import vpreprocessor.token.Macro;
import vpreprocessor.token.Plain;
import vpreprocessor.token.Token;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class TestPreprocessorTokenizer {
    private void testPlain(String input) {
        test(input, new Plain(input));
    }

    private void testPlain(PreprocessorOptions opts, String input) {
        test(opts, input, new Plain(input));
    }

    private void test(String input, Token... tokens) {
        test(PreprocessorOptions.KT, input, tokens);
    }

    private void test(PreprocessorOptions opts, String input, Token... tokens) {
        Tokenizer tokenizer = new Tokenizer(opts);
        CharStream cs = CharStream.from(input);

        for (int i = 0; i < tokens.length; ++i) {
            Token expected = tokens[i];
            Token actual = tokenizer.feed(cs);
            assertEquals("mismatch at index " + i, expected, actual);
        }
        if (cs.hasNext()) {
            Token got = tokenizer.feed(cs);
            assertTrue(got instanceof EOFToken);
        }
        // test stats
        assertEquals("", tokenizer.getTexts());
        assertEquals("", tokenizer.getPendingTexts());
        assertEquals(0, tokenizer.getCommentStackDepth());
        assertEquals(Tokenizer.TextState.INIT, tokenizer.getTextState());
        assertEquals(Tokenizer.MacroState.INIT, tokenizer.getMacroState());
        // test eof
        assertEquals(new EOFToken(), tokenizer.feed(cs));
        assertFalse(cs.hasNext());
    }

    private Tokenizer testPartial(Tokenizer.TextState textState,
                                  String input, Token... tokens) {
        return testPartial(textState, Tokenizer.MacroState.INIT, input, tokens);
    }

    private Tokenizer testPartial(Tokenizer.TextState textState, Tokenizer.MacroState macroState,
                                  String input, Token... tokens) {
        return testPartial(PreprocessorOptions.KT, textState, macroState, input, tokens);
    }

    private Tokenizer testPartial(PreprocessorOptions opts,
                                  Tokenizer.TextState textState, Tokenizer.MacroState macroState,
                                  String input, Token... tokens) {
        Tokenizer tokenizer = new Tokenizer(opts);
        CharStream cs = CharStream.from(input);

        for (int i = 0; i < tokens.length; ++i) {
            Token expected = tokens[i];
            Token actual = tokenizer.feed(cs);
            assertEquals("mismatch at index " + i, expected, actual);
        }
        // test stats
        assertEquals(textState, tokenizer.getTextState());
        assertEquals(macroState, tokenizer.getMacroState());
        // test eof
        assertFalse(cs.hasNext());
        assertEquals(new EOFToken(), tokenizer.feed(cs));
        return tokenizer;
    }

    private void testFail(String input, Consumer<ParserException> check) {
        testFail(PreprocessorOptions.KT, input, check);
    }

    private void testFail(PreprocessorOptions opts, String input, Consumer<ParserException> check) {
        Tokenizer tokenizer = new Tokenizer(opts);
        CharStream cs = CharStream.from(input);

        while (true) {
            Token token;
            try {
                token = tokenizer.feed(cs);
            } catch (ParserException t) {
                check.accept(t);
                return;
            }
            if (token instanceof EOFToken) {
                break;
            }
        }
        fail();
    }

    @Test
    public void general() {
        test("abc def", new Plain("abc def"));
        test("abc /* def */", new Plain("abc /* def */"));
        test("abc /* #ifdef DEF {{ */ ghi /* }} */",
            new Plain("abc "),
            new Macro("ifdef"),
            new Macro("DEF"),
            new Macro("{{"),
            new Plain("  ghi  "),
            new Macro("}}"));
        test("/* #ifndef ABC {{ def }} */",
            new Macro("ifndef"),
            new Macro("ABC"),
            new Macro("{{"),
            new Plain(" def "),
            new Macro("}}"));
    }

    @Test
    public void nothing() {
        Tokenizer tokenizer = new Tokenizer(PreprocessorOptions.KT);
        assertEquals(new EOFToken(), tokenizer.feed(CharStream.from("")));
    }

    @Test
    public void plain() {
        testPlain("abc def ghi");
        testPlain("abc #def ghi");
        testPlain("abc / def");
    }

    @Test
    public void comment() {
        testPlain("/* abc def */");
        testPlain("/* abc #def */");
        testPlain("/* abc\n #def */");
        testPlain("// abc def\n");

        testPlain("abc /* def ghi */ jkl");
        testPlain("// abc def\nghi");

        testPlain(PreprocessorOptions.JAVA, "/* abc /* def /* ghi */");
    }

    @Test
    public void partialComment() {
        testPartial(Tokenizer.TextState.INIT, "/",
            new Plain("/"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_COMMENT, "//",
            new Plain("//"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "// abc",
            new Plain("// abc"));
        testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT, "/*",
            new Plain("/*"));
        testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* abc",
            new Plain("/* abc"));

        Tokenizer tokenizer = testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* /* /* */",
            new Plain("/* /* /* */"));
        assertEquals(2, tokenizer.getCommentStackDepth());

        testPartial(PreprocessorOptions.JAVA, Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* /",
            new Plain("/* /"));
        testPartial(PreprocessorOptions.JAVA, Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* /a",
            new Plain("/* /a"));
        testPartial(PreprocessorOptions.KT, Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* /",
            new Plain("/* /"));
        testPartial(PreprocessorOptions.KT, Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* /a",
            new Plain("/* /a"));
    }

    @Test
    public void nestedComment() {
        testPlain("/* abc /* def ghi */ jkl */");
        testPlain("/* abc /* #def ghi */ jkl */");
        testPlain(PreprocessorOptions.JAVA, "/* abc /* def */");
    }

    @Test
    public void partialNestedComment() {
        testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.IGNORE, "/* abc /* def */ ghi",
            new Plain("/* abc /* def */ ghi"));
    }

    @Test
    public void string() throws Exception {
        testPlain("\"\"");

        testPlain("\"\" abc");

        testPlain("\"abc def\"");
        testPlain("\"abc /* def\"");
        testPlain("\"abc /* #def\"");
        testPlain("\"abc // def\"");
        testPlain("\"abc // #def\"");
        testPlain("\"abc \\\" def\"");
        testPlain("'a'");
        testPlain("'\\''");
        testPlain("'\\t'");

        testPlain("\"\"\"abc\ndef/* #ghi  \n \"\"\"");
        testPlain("\"\"\"abc\"\"def\"\"\"");

        testPlain("'\\\\'");
        testPlain("\"\\\\\\\\\"");
    }

    @Test
    public void partialString() {
        testPartial(Tokenizer.TextState.SINGLE_LINE_STRING, "\"",
            new Plain("\""));
        testPartial(Tokenizer.TextState.SINGLE_LINE_STRING, "'",
            new Plain("'"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_STRING, "\"abc\\",
            new Plain("\"abc\\"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_STRING, "\"\\",
            new Plain("\"\\"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_STRING, "'\\",
            new Plain("'\\"));
        testPartial(Tokenizer.TextState.MULTI_LINE_STRING, "\"\"\"abc",
            new Plain("\"\"\"abc"));
        testPartial(Tokenizer.TextState.MULTI_LINE_STRING, "\"\"\"abc\"",
            new Plain("\"\"\"abc\""));
        testPartial(Tokenizer.TextState.MULTI_LINE_STRING, "\"\"\"abc\"\"",
            new Plain("\"\"\"abc\"\""));
    }

    @Test
    public void macro() {
        test("/* #abc */",
            new Macro("abc"));
        test("abc /* #def ghi */ jkl",
            new Plain("abc "),
            new Macro("def"),
            new Macro("ghi"),
            new Plain(" jkl"));
        test("abc /* #def ghi {{ */ jkl /* }} */ mno",
            new Plain("abc "),
            new Macro("def"),
            new Macro("ghi"),
            new Macro("{{"),
            new Plain("  jkl  "),
            new Macro("}}"),
            new Plain(" mno"));
        test("/* #abc */ def /* ghi */",
            new Macro("abc"),
            new Plain(" def /* ghi */"));
        test("// #abc \ndef",
            new Macro("abc"),
            new Plain("\ndef"));
        test("" +
                "// #abc {{\n" +
                "// }}\n",
            new Macro("abc"),
            new Macro("{{"),
            new Plain("\n "),
            new Macro("}}"),
            new Plain("\n"));
        test("/* #abc {{ } }} */",
            new Macro("abc"),
            new Macro("{{"),
            new Plain(" } "),
            new Macro("}}"));
    }

    @Test
    public void partialMacro() {
        testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT,
            Tokenizer.MacroState.READING_VAR, "/* #abc def",
            new Macro("abc"),
            new Macro("def"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_COMMENT,
            Tokenizer.MacroState.TEXT, "// #abc {{ def",
            new Macro("abc"),
            new Macro("{{"),
            new Plain(" def"));
        testPartial(Tokenizer.TextState.SINGLE_LINE_COMMENT,
            Tokenizer.MacroState.TEXT, "// #abc {{ }",
            new Macro("abc"),
            new Macro("{{"),
            new Plain(" }"));
    }

    @Test
    public void failedMacro() {
        testFail("/* #def *", ex -> assertEquals("unexpected character *", ex.getMessage()));
    }

    @Test
    public void codeBlock() {
        test("/* # { */", new Macro("{"));
        test("/* # } */", new Macro("}"));
        test("/* # { } */", new Macro("{"), new Macro("}"));
        test("/* # { abc } */", new Macro("{"), new Macro("abc"), new Macro("}"));
    }

    @Test
    public void partialCodeBlock() throws Exception {
        testPartial(Tokenizer.TextState.MULTI_LINE_COMMENT, Tokenizer.MacroState.READING, "/* #abc {",
            new Macro("abc"), new Macro("{"));
    }

    @Test
    public void tokenHolder() {
        TokenSeq tokenSeq = new TokenSeq(CharStream.from(
            "abc /* #def {{*/ ghi /*}} jkl */"
        ), new Tokenizer(PreprocessorOptions.KT));
        assertEquals(new Plain("abc "), tokenSeq.peek());
        assertEquals(new Plain("abc "), tokenSeq.peek());
        assertEquals(new Plain("abc "), tokenSeq.moveNextAndGet());

        assertEquals(new Macro("def"), tokenSeq.peek());
        assertEquals(new Macro("{{"), tokenSeq.peek(2));
        assertEquals(new Plain(" ghi "), tokenSeq.peek(3));

        assertEquals(new Macro("def"), tokenSeq.moveNextAndGet());
        assertEquals(new Macro("{{"), tokenSeq.moveNextAndGet());
        assertEquals(new Plain(" ghi "), tokenSeq.moveNextAndGet());

        assertEquals(new Macro("}}"), tokenSeq.peek());
        assertEquals(new Macro("jkl"), tokenSeq.peek(2));
        assertEquals(new EOFToken(), tokenSeq.peek(3));
        assertEquals(new EOFToken(), tokenSeq.peek(4));

        assertEquals(new Macro("}}"), tokenSeq.moveNextAndGet());
        assertEquals(new Macro("jkl"), tokenSeq.moveNextAndGet());
        assertEquals(new EOFToken(), tokenSeq.moveNextAndGet());
        assertEquals(new EOFToken(), tokenSeq.moveNextAndGet());
        assertEquals(new EOFToken(), tokenSeq.moveNextAndGet());
    }
}
