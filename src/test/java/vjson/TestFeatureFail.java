package vjson;

import org.junit.Test;
import vjson.ex.JsonParseException;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("RedundantThrows")
public class TestFeatureFail {
    private void parseFail(String err, Runnable r) {
        try {
            r.run();
            fail();
        } catch (JsonParseException e) {
            assertEquals(err, e.getMessage());
        }
    }

    @Test
    public void keyNoQuotes() throws Exception {
        parseFail("empty key is not allowed when parsing object key without quotes",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{:\"a\"}"));
        parseFail("invalid character for json object key without quotes: +",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{+:\"a\"}"));
        parseFail("invalid character for json object key without quotes: =",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{=:\"a\"}"));
        parseFail("invalid character for json object key without quotes: ^",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{^:\"a\"}"));
        parseFail("invalid character for json object key without quotes: {",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{{:\"a\"}"));
        parseFail("invalid character after json object key without quotes: +",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{a +:\"a\"}"));
    }
}
