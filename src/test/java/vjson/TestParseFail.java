package vjson;

import org.junit.Test;
import vjson.ex.JsonParseException;
import vjson.ex.ParserFinishedException;
import vjson.parser.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        parseFail("invalid character in string: code is: " + ((int) '\t'), () -> new StringParser().last("\"\tabc\""));
        parseFail("invalid escape character: x", () -> new StringParser().last("\"abc\\x\""));
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
        parseFail("input stream contain extra characters other than number", () -> new NumberParser().last("3.14e+1+"));
    }

    @Test
    public void array() throws Exception {
        parseFail("invalid character for json array: not starts with `[`: a", () -> new ArrayParser().last("a1,2]"));
        parseFail("invalid character for json array, expecting `]` or `,`, but got a", () -> new ArrayParser().last("[1,2a"));
        parseFail("expecting more characters to build array", () -> new ArrayParser().last("[1,2"));
        parseFail("invalid json array: failed when parsing element: (invalid digit in exponent: ,)", () -> new ArrayParser().last("[1e,2]"));
        parseFail("invalid json array: failed when parsing element: (not valid json string)", () -> new ArrayParser().last("[:]"));
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
        finish(() -> {
            ObjectParser p = new ObjectParser();
            p.feed("{}");
            p.feed("{");
        });
    }

    @Test
    public void utils() throws Exception {
        parseFail("empty input string", () -> JSON.parse(""));
        parseFail("not valid json string", () -> JSON.parse("e"));
        parseFail("not valid json string", () -> JSON.parse("+"));
    }
}
