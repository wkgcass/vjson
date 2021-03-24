package vpreprocessor;

import org.junit.Test;
import vjson.CharStream;

import static org.junit.Assert.assertEquals;

public class TestPreprocessor {
    private String test(String input, String output, String... definitions) {
        PreprocessorContext ctx = new PreprocessorContext(PreprocessorOptions.KT);
        for (String def : definitions) {
            ctx.define(def);
        }
        return test(input, output, ctx);
    }

    private String test(String input, String output, PreprocessorContext ctx) {
        Preprocessor preprocessor = new Preprocessor(ctx);
        StringBuilder sb = new StringBuilder();
        preprocessor.process(CharStream.from(input), sb);
        assertEquals(output, sb.toString());
        return sb.toString();
    }

    @Test
    public void general() {
        String output = test("" +
                "/* #ifdef JVM {{*/@JvmStatic/*}} */\n" +
                "val a = 1\n",
            "" +
                "@JvmStatic\n" +
                "val a = 1\n",
            "JVM");
        System.out.println(output);

        output = test("" +
                "/* #ifdef JVM {{*/@JvmStatic/*}} */\n" +
                "val a = 1\n",
            "" +
                "\n" +
                "val a = 1\n"
        );
        System.out.println(output);
    }

    @Test
    public void plain() {
        test("", "");
        test("abc", "abc");
        test("/* # {{ abc }} */", " abc ");
    }

    @Test
    public void ifdef() {
        test("/* #ifdef X {{*/ abc /*}}*/", "");
        test("/* #ifdef X {{*/ abc /*}}*/", " abc ",
            "X");
        test("/* #ifdef X {{*/ abc /*}} else {{ def }} */", " def ");
        test("/* #ifdef X {{*/ abc /*}} else {{ def }} */", " abc ",
            "X");
    }

    @Test
    public void ifndef() {
        test("/* #ifndef X {{*/ abc /*}}*/", " abc ");
        test("/* #ifndef X {{*/ abc /*}}*/", "",
            "X");
        test("/* #ifndef X {{*/ abc /*}} else {{ def }} */", " abc ");
        test("/* #ifndef X {{*/ abc /*}} else {{ def }} */", " def ",
            "X");
    }

    @Test
    public void block() {
        test("/* #ifdef X { */ abc /* # } */", " abc ",
            "X");
        test("/* #ifdef X {{ */ abc /* }} */", "  abc  ",
            "X");
        test("/* #ifdef X {{ */ abc /* # }} */", "  abc  # ",
            "X");

        test("/* #ifdef X { */ abc /* xyz */ def /* # } */", " abc /* xyz */ def ",
            "X");
    }
}
