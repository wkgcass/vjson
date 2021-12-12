package vjson;

import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
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
}
