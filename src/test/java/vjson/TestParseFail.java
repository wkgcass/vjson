package vjson;

import org.junit.Test;
import vjson.cs.UTF8ByteArrayCharStream;
import vjson.deserializer.rule.*;
import vjson.ex.JsonParseException;
import vjson.ex.ParserFinishedException;
import vjson.parser.*;
import vjson.util.typerule.TypeRuleBase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestParseFail {
    private void parseFail(String err, Runnable r) {
        try {
            r.run();
            fail();
        } catch (JsonParseException e) {
            assertEquals(err, e.getMessage());
        }
    }

    private void finish(Runnable r) {
        try {
            r.run();
            fail();
        } catch (ParserFinishedException ignore) {
        }
    }

    @Test
    public void nullV() throws Exception {
        parseFail("invalid character for `[n]ull`: a", () -> new NullParser().last("aull"));
        parseFail("invalid character for `n[u]ll`: a", () -> new NullParser().last("nall"));
        parseFail("invalid character for `nu[l]l`: a", () -> new NullParser().last("nual"));
        parseFail("invalid character for `nul[l]`: a", () -> new NullParser().last("nula"));
        parseFail("expecting more characters to build `null`", () -> new NullParser().last("nul"));
        finish(() -> {
            NullParser p = new NullParser();
            p.feed("null");
            p.feed("n");
        });
    }

    @Test
    public void boolV() throws Exception {
        parseFail("invalid character for [t]rue|[f]alse: a", () -> new BoolParser().last("arue"));
        parseFail("invalid character for t[r]ue: a", () -> new BoolParser().last("taue"));
        parseFail("invalid character for tr[u]e: a", () -> new BoolParser().last("trae"));
        parseFail("invalid character for tru[e]: a", () -> new BoolParser().last("trua"));
        parseFail("expecting more characters to build `true` or `false`", () -> new BoolParser().last("tru"));
        finish(() -> {
            BoolParser p = new BoolParser();
            p.feed("true");
            p.feed("t");
        });
        parseFail("invalid character for f[a]lse: b", () -> new BoolParser().last("fblse"));
        parseFail("invalid character for fa[l]se: b", () -> new BoolParser().last("fabse"));
        parseFail("invalid character for fal[s]e: b", () -> new BoolParser().last("falbe"));
        parseFail("invalid character for fals[e]: b", () -> new BoolParser().last("falsb"));
        parseFail("expecting more characters to build `true` or `false`", () -> new BoolParser().last("fals"));
        finish(() -> {
            BoolParser p = new BoolParser();
            p.feed("false");
            p.feed("f");
        });
    }

    @Test
    public void string() throws Exception {
        parseFail("invalid character for string: not starts with \": x", () -> new StringParser().last("xabc\""));
        parseFail("invalid character for string: not starts with \": '", () -> new StringParser().last("'abc\""));
        parseFail("invalid character in string: code is: " + ((int) '\t'), () -> new StringParser().last("\"\tabc\""));
        parseFail("invalid escape character: x", () -> new StringParser().last("\"abc\\x\""));
        parseFail("invalid escape character: '", () -> new StringParser().last("\"abc\\'\""));
        parseFail("invalid hex character in \\u[H]HHH: g", () -> new StringParser().last("\"abc\\ug012\""));
        parseFail("invalid hex character in \\u0[H]HH: +", () -> new StringParser().last("\"abc\\u0+12\""));
        parseFail("invalid hex character in \\u00[H]H: G", () -> new StringParser().last("\"abc\\u00G2\""));
        parseFail("invalid hex character in \\u001[H]: @", () -> new StringParser().last("\"abc\\u001@\""));
        parseFail("expecting more characters to build string", () -> new StringParser().last("\"abc"));
        finish(() -> {
            StringParser p = new StringParser();
            p.feed("\"abc\"");
            p.feed("\"");
        });
    }

    @Test
    public void number() throws Exception {
        parseFail("invalid digit in number: +", () -> new NumberParser().last("+1"));
        parseFail("invalid digit in number: -", () -> new NumberParser().last("--"));
        parseFail("invalid digit in fraction: .", () -> new NumberParser().last("0.."));
        parseFail("invalid digit in exponent: e", () -> new NumberParser().last("3.14ee"));
        parseFail("invalid digit in exponent: +", () -> new NumberParser().last("3.14e++"));
        parseFail("expecting more characters to build number", () -> new NumberParser().last("3."));
        parseFail("expecting more characters to build number", () -> new NumberParser().last("3.14e"));
        finish(() -> {
            NumberParser p = new NumberParser();
            p.last("1");
            p.feed("2");
        });
        parseFail("input stream contains extra characters other than number", () -> new NumberParser().last("3.14e+1+"));
    }

    @Test
    public void array() throws Exception {
        parseFail("invalid character for json array: not starts with `[`: a", () -> new ArrayParser().last("a1,2]"));
        parseFail("invalid character for json array, expecting `]` or `,`, but got a", () -> new ArrayParser().last("[1,2a"));
        parseFail("expecting more characters to build array", () -> new ArrayParser().last("[1,2"));
        parseFail("invalid json array: failed when parsing element: (invalid digit in exponent: ,)", () -> new ArrayParser().last("[1e,2]"));
        parseFail("invalid json array: failed when parsing element: (not valid json string)", () -> new ArrayParser().last("[:]"));
        parseFail("invalid json array: failed when parsing element: (not valid json string)", () -> new ArrayParser().last("['a']"));
        finish(() -> {
            ArrayParser p = new ArrayParser();
            p.feed("[]");
            p.feed("[");
        });
    }

    @Test
    public void object() throws Exception {
        parseFail("invalid json object: failed when parsing key: (invalid character in string: code is: " + ((int) '\t') + ")", () -> new ObjectParser().last("{\"\t\":1}"));
        parseFail("invalid json object: failed when parsing value: (invalid digit in exponent: })", () -> new ObjectParser().last("{\"a\":1e}"));
        parseFail("invalid character for json object: not starts with `{`: a", () -> new ObjectParser().last("a"));
        parseFail("invalid key-value separator for json object, expecting `:`, but got +", () -> new ObjectParser().last("{\"a\"+1}"));
        parseFail("invalid character for json object, expecting `}` or `,`, but got +", () -> new ObjectParser().last("{\"a\":1+\"b\":2}"));
        parseFail("expecting more characters to build object", () -> new ObjectParser().last("{"));
        parseFail("invalid json object: failed when parsing value: (not valid json string)", () -> new ObjectParser().last("{\"a\":+}"));
        parseFail("invalid json object: failed when parsing value: (not valid json string)", () -> new ObjectParser().last("{\"a\":'x'}"));
        parseFail("invalid character for json object key: a", () -> new ObjectParser().last("{a:1}"));
        parseFail("invalid character for json object key: b", () -> new ObjectParser().last("{\"a\":1,b:2}"));
        finish(() -> {
            ObjectParser p = new ObjectParser();
            p.feed("{}");
            p.feed("{");
        });
    }

    @Test
    public void utils() throws Exception {
        parseFail("empty input string", () -> JSON.parse(""));
        parseFail("empty input string", () -> ParserUtils.buildFrom(CharStream.from(""), new ParserOptions()));
        parseFail("empty input string", () -> JSON.parseToJavaObject(""));
        parseFail("empty input string", () -> ParserUtils.buildJavaObject(CharStream.from(""), new ParserOptions()));
        parseFail("not valid json string", () -> JSON.parse("e"));
        parseFail("not valid json string", () -> JSON.parse("+"));
        parseFail("not valid json string: stringSingleQuotes not enabled", () -> ParserUtils.buildFrom(CharStream.from("''"), new ParserOptions()));
        parseFail("not valid json string: stringSingleQuotes not enabled", () -> JSON.parse("''"));
        parseFail("not valid json string: stringSingleQuotes not enabled", () -> JSON.parseToJavaObject("''"));
    }

    @Test
    public void invalidUtf16Character() throws Exception {
        UTF8ByteArrayCharStream cs = new UTF8ByteArrayCharStream(
            new byte[]{
                (byte) 0b101_0_0000
            }
        );
        try {
            cs.hasNext(1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void deserializeErrors() throws Exception {
        try {
            JSON.deserialize("{}", new ArrayRule<>(ArrayList::new, List::add, IntRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("expect: array, actual: object"));
        }

        try {
            JSON.deserialize("[]", new ObjectRule<>(Object::new));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("expect: object, actual: array"));
        }

        JSON.deserialize("{\"a\":1}", new ObjectRule<>(Object::new));

        try {
            JSON.deserialize("[null]", new ArrayRule<>(ArrayList::new, List::add, StringRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: String, value=null(nil)"));
        }
        try {
            JSON.deserialize("[true]", new ArrayRule<>(ArrayList::new, List::add, StringRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: String, value=true(kotlin.Boolean)"));
        }
        try {
            JSON.deserialize("[true]", new ArrayRule<>(ArrayList::new, List::add, StringRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: String, value=true(kotlin.Boolean)"));
        }
        try {
            JSON.deserialize("[\"a\"]", new ArrayRule<>(ArrayList::new, List::add, DoubleRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: Double, value=a(kotlin.String)"));
        }
        try {
            JSON.deserialize("[1.0]", new ArrayRule<>(ArrayList::new, List::add, LongRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: Long, value=1.0(kotlin.Double)"));
        }
        try {
            JSON.deserialize("[\"a\"]", new ArrayRule<>(ArrayList::new, List::add, LongRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: Long, value=a(kotlin.String)"));
        }

        JSON.deserialize("[1]", new ArrayRule<>(ArrayList::new, List::add, LongRule.get()));

        try {
            JSON.deserialize("[1.0]", new ArrayRule<>(ArrayList::new, List::add, IntRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: Int, value=1.0(kotlin.Double)"));
        }
        try {
            JSON.deserialize("[\"a\"]", new ArrayRule<>(ArrayList::new, List::add, IntRule.get()));
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting: Int, value=a(kotlin.String)"));
        }

        try {
            JSON.deserialize("{\"@type\":1}", TypeRuleBase.getTypeRule());
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("invalid type: expecting type name for " + TypeRuleBase.getTypeRule().toString() + " but got " + 1));
        }
        try {
            JSON.deserialize("{}", TypeRuleBase.getTypeRule());
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("type for " + TypeRuleBase.getTypeRule() + " is still not determined when reaching the object end"));
        }
        try {
            JSON.deserialize("{\"@type\":\"xyz\"}", TypeRuleBase.getTypeRule());
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("cannot find type xyz in " + TypeRuleBase.getTypeRule()));
        }
        JSON.deserialize("{\"x\":1,\"y\":\"2\"}", TypeRuleBase.baseRule);
        try {
            JSON.deserialize("{\"x\":1,\"y\":\"2\"}", TypeRuleBase.getTypeRule());
            fail();
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().contains("cannot determine type for " + TypeRuleBase.getTypeRule()));
        }
    }
}
