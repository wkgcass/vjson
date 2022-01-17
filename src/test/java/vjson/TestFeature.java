package vjson;

import org.junit.Test;
import vjson.ex.JsonParseException;
import vjson.ex.ParserException;
import vjson.parser.*;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleNull;
import vjson.simple.SimpleObject;
import vjson.simple.SimpleString;
import vjson.util.AppendableMap;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("RedundantThrows")
public class TestFeature {
    @Test
    public void stringSingleQuote() throws Exception {
        {
            StringParser parser = new StringParser(new ParserOptions().setStringSingleQuotes(true));
            assertEquals(new SimpleString("abc\'\""), parser.last("'abc\\'\\\"'"));
        }

        {
            // normal string should pass
            StringParser parser = new StringParser(new ParserOptions().setStringSingleQuotes(true));
            assertEquals(new SimpleString("abc\'\""), parser.last("\"abc\\'\\\"\""));
        }

        {
            ArrayParser parser = new ArrayParser(new ParserOptions().setStringSingleQuotes(true));
            assertEquals(new SimpleArray(
                new SimpleString("a"),
                new SimpleString("b"),
                new SimpleString("c")
            ), parser.last("['a',\"b\",'c']"));
        }

        {
            ObjectParser parser = new ObjectParser(new ParserOptions().setStringSingleQuotes(true));
            assertEquals(new SimpleObject(new AppendableMap<>()
                .append("a", new SimpleString("aa"))
                .append("b", new SimpleString("bb"))
                .append("c", new SimpleString("cc"))), parser.last("{'a':'aa','b':\"bb\",\"c\":'cc'}"));
        }

        {
            assertEquals(
                new SimpleString("a"),
                ParserUtils.buildFrom(CharStream.from("'a'"), new ParserOptions().setStringSingleQuotes(true))
            );
        }
    }

    @Test
    public void keyNoQuotes() throws Exception {
        {
            ObjectParser parser = new ObjectParser(new ParserOptions().setKeyNoQuotes(true));
            assertEquals(new SimpleObject(new AppendableMap<>()
                    .append("a", new SimpleString("char"))
                    .append("A", new SimpleString("cap"))
                    .append("_", new SimpleString("underline"))
                    .append("0", new SimpleString("numbers"))
                    .append("$", new SimpleString("allowed symbol"))
                    .append("normal", new SimpleString("supported"))),
                parser.last("{" +
                    "a:\"char\"," +
                    "A:\"cap\"," +
                    "_:\"underline\"," +
                    "0:\"numbers\"," +
                    "$:\"allowed symbol\"," +
                    "\"normal\":\"supported\"" +
                    "}"));
        }

        {
            assertEquals(new SimpleObject(new AppendableMap<>()
                    .append("a", new SimpleString("aa"))
                    .append("b", new SimpleString("bb"))
                    .append("c", new SimpleString("cc"))),
                ParserUtils.buildFrom(CharStream.from("{a:\"aa\",b:\"bb\",c:\"cc\"}"), new ParserOptions().setKeyNoQuotes(true)));
        }
    }

    @Test
    public void keyNoQuotesAnyChar() throws Exception {
        try {
            ParserUtils.buildFrom(CharStream.from("{a.b:\"ab\"}"), new ParserOptions().setKeyNoQuotes(true));
        } catch (ParserException e) {
            assertEquals("invalid character for json object key without quotes: .", e.getMessage());
        }
        {
            assertEquals(new SimpleObject(new AppendableMap<>()
                    .append("a.b", new SimpleString("ab"))
                    .append("c.d", new SimpleString("cd"))),
                ParserUtils.buildFrom(CharStream.from("{a.b:\"ab\",c.d:\"cd\"}"), new ParserOptions()
                    .setKeyNoQuotes(true)
                    .setKeyNoQuotesAnyChar(true)));
        }
    }

    @Test
    public void allowSkippingComma() throws Exception {
        {
            ObjectParser parser = new ObjectParser(new ParserOptions().setAllowSkippingCommas(true));
            assertEquals(new SimpleObject(new AppendableMap<>()
                    .append("a", new SimpleString("b"))
                    .append("c", new SimpleString("d"))),
                parser.last("{" +
                    "\"a\":\"b\" \"c\":\"d\"" +
                    "}"));
        }
        {
            ArrayParser parser = new ArrayParser(new ParserOptions().setAllowSkippingCommas(true));
            assertEquals(new SimpleArray(Arrays.asList(new SimpleString("a"), new SimpleString("b"), new SimpleString("c"))),
                parser.last("[\"a\" \"b\" \"c\"]"));
        }
    }

    @Test
    public void allowObjectEntryWithoutValue() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions().setAllowObjectEntryWithoutValue(true));
        assertEquals(new SimpleObject(new AppendableMap<>()
                .append("a", new SimpleString("b"))
                .append("c", new SimpleNull())
                .append("e", new SimpleString("f"))),
            parser.last("{" +
                "\"a\":\"b\"," +
                "\"c\"," +
                "\"e\":\"f\"" +
                "}"));
    }

    @Test
    public void entryWithoutValueAndKeyNoQuotes() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setAllowObjectEntryWithoutValue(true)
            .setKeyNoQuotes(true)
            .setKeyNoQuotesAnyChar(true));
        assertEquals(new ObjectBuilder()
            .put("a", null)
            .put("b", null)
            .put("c", null)
            .build(), parser.last("{a, b, c}"));
    }

    @Test
    public void allowOmittingColonBeforeBraces() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setAllowOmittingColonBeforeBraces(true));
        assertEquals(new ObjectBuilder()
            .putObject("a", o -> o.put("b", "c"))
            .build(), parser.last("{\"a\" {\"b\":\"c\"}}"));
    }

    @Test
    public void allowOmittingColonBeforeBracesAndKeyNoQuotes() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setAllowOmittingColonBeforeBraces(true)
            .setKeyNoQuotes(true));
        assertEquals(new ObjectBuilder()
            .putObject("a", o -> o.put("b", "c"))
            .build(), parser.last("{a {b:\"c\"}}"));
    }

    @Test
    public void equalAsColon() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions().setEqualAsColon(true));
        assertEquals(new SimpleObject(new AppendableMap<>()
                .append("a", new SimpleString("b"))
                .append("c", new SimpleNull())
                .append("e", new SimpleString("f"))),
            parser.last("{" +
                "\"a\"=\"b\"," +
                "\"c\"=null," +
                "\"e\":\"f\"}"));
    }

    @Test
    public void semicolonAsComma() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions().setSemicolonAsComma(true));
        assertEquals(new SimpleObject(new AppendableMap<>()
                .append("a", new SimpleString("b"))
                .append("c", new SimpleNull())
                .append("e", new SimpleString("f"))),
            parser.last("{" +
                "\"a\":\"b\";" +
                "\"c\":null;" +
                "\"e\":\"f\"}"));
        ArrayParser parser2 = new ArrayParser(new ParserOptions().setSemicolonAsComma(true));
        assertEquals(new ArrayBuilder()
                .add(1).add(2).add("abc")
                .build(),
            parser2.last("[1;2;\"abc\"]"));
    }

    @Test
    public void stringValueNoQuotes() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions().setStringValueNoQuotes(true));
        assertEquals(new ObjectBuilder()
                .put("a", "b")
                .put("c", "d")
                .put("e", "f")
                .put("g", "")
                .put("h", "")
                .build(),
            parser.last("{\"a\":b,\"c\":d,\"e\":\"f\",\"g\": ,\"h\":}"));
        ArrayParser parser2 = new ArrayParser(new ParserOptions().setStringValueNoQuotes(true));
        assertEquals(new ArrayBuilder()
                .add("a")
                .add("b")
                .add("")
                .add("c")
                .build(),
            parser2.last("[\"a\",\"b\", ,\"c\"]"));

        assertEquals(new SimpleString("hello(\r\nworld)"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello(\r\nworld)"));
        assertEquals(new SimpleString("hello(\r\nworld)zzz"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello(\r\nworld)zzz"));
        assertEquals(new SimpleString("hello[\r\nworld]"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello[\r\nworld]"));
        assertEquals(new SimpleString("hello{\r\nworld}"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello{\r\nworld}"));
        assertEquals(new SimpleString("hello"), new StringParser(new ParserOptions().setStringValueNoQuotes(true).setEnd(false))
            .last("hello\r\nworld"));
    }

    @Test
    public void stringValueNoQuotesWithStringLiteral() throws Exception {
        assertEquals(new SimpleString("hello\"xyz\"world"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello\"xyz\"world"));
        assertEquals(new SimpleString("hello'xyz'world"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello'xyz'world"));
        assertEquals(new SimpleString("hello\"[java/lang/Object;\"world"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello\"[java/lang/Object;\"world"));

        try {
            new StringParser(new ParserOptions().setStringValueNoQuotes(true))
                .last("hello(xyz)world\nabc\n)def");
            fail();
        } catch (JsonParseException e) {
            assertEquals("input stream contains extra characters other than string", e.getMessage());
        }
        assertEquals(new SimpleString("hello(xyz\")\"world\nabc\n)def"), new StringParser(new ParserOptions().setStringValueNoQuotes(true))
            .last("hello(xyz\")\"world\nabc\n)def"));
    }

    @Test
    public void all() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setStringSingleQuotes(true)
            .setKeyNoQuotes(true)
            .setKeyNoQuotesAnyChar(true)
            .setAllowSkippingCommas(true)
            .setAllowObjectEntryWithoutValue(true)
            .setAllowOmittingColonBeforeBraces(true)
            .setEqualAsColon(true)
            .setSemicolonAsComma(true)
            .setStringValueNoQuotes(true));
        assertEquals(new ObjectBuilder()
                .put("function", null)
                .putObject("a", o -> o
                    .put("x", "int")
                    .put("y", "string"))
                .putObject("void", o -> o
                    .put("while", "b.c > 1")
                    .putObject("do", oo -> oo
                        .put("break", null)
                    )
                )
                .build(),
            parser.last("{\n" +
                "  function a: {x: \"int\", y: \"string\"} void {\n" +
                "    while: b.c > 1; do {\n" +
                "      break\n" +
                "    }\n" +
                "  }\n" +
                "}"));
    }

    public static final String TEST_PROG = "{\n" +
        "function printPrimes { searchRange: int } void {\n" +
        "  var notPrime = { new (bool[searchRange + 1]) }\n" +
        "  for: [ { var i = 2 }; i <= searchRange; i += 1] do {\n" +
        "    if: !notPrime[i]; then {\n" +
        "      var j = 2\n" +
        "      while: true; do {\n" +
        "        var n = i * j\n" +
        "        if: n > searchRange; then {\n" +
        "          break\n" +
        "        }\n" +
        "        notPrime[n] = true\n" +
        "        j = j + 1\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "  std.console.log: [ ('primes:') ]\n" +
        "  for: [ { var i = 2 }; i < notPrime.length; i += 1] do {\n" +
        "    if: !notPrime[i]; then {\n" +
        "      std.console.log: [ ('' + i) ]\n" +
        "    }\n" +
        "  }\n" +
        "}\n" +
        "printPrimes: [ 10 ]\n" +
        "}";

    @Test
    public void pass() throws Exception {
        ObjectParser parser = new ObjectParser(ParserOptions.allFeatures());
        JSON.Object obj = parser.last(TEST_PROG);
        System.out.println(obj);
    }
}
