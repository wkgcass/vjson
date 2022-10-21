package vjson;

import org.junit.Test;
import vjson.ex.ParserException;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;
import vjson.util.ObjectBuilder;

import static org.junit.Assert.assertEquals;

public class TestIssues {
    @Test
    public void case1() {
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setKeyNoQuotesAnyChar(true)
            .setStringValueNoQuotes(true)
            .setEqualAsColon(true)
            .setAllowSkippingCommas(true)
            .setAllowObjectEntryWithoutValue(true));
        assertEquals(new ObjectBuilder()
                .putArray("for", arr ->
                    arr.addObject(o -> o.put("var", null).put("i", 2))
                        .add("; i <= searchRange")
                        .add("i += 1")
                )
                .build(),
            parser.last("{for: [ { var i = 2 }; i <= searchRange, i += 1 ]}"));
    }

    @Test
    public void case2() {
        ObjectParser parser = new ObjectParser(ParserOptions.allFeatures());
        System.out.println(parser.last("{\n" +
            "  location '~ \\\\.php$' {\n" +
            "    fastcgi_pass = 127.0.0.1:1025\n" +
            "  }\n" +
            "}\n"));
    }

    @Test
    public void case3() {
        try {
            ParserUtils.buildFrom(CharStream.from("{\n" +
                "\"a\":[1,\n" +
                "}"), new ParserOptions()
                .setStringValueNoQuotes(true)
                .setAllowSkippingCommas(true));
        } catch (ParserException e) {
            assertEquals("unexpected token } when trying to read string no quotes", e.getMessage());
        }
    }
}
