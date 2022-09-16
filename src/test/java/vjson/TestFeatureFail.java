package vjson;

import org.junit.Test;
import vjson.cs.LineColCharStream;
import vjson.ex.JsonParseException;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.parser.StringParser;

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
        parseFail("invalid character for json object key without quotes: }",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{a}"));
        parseFail("invalid character after json object key without quotes: +",
            () -> new ObjectParser(new ParserOptions().setKeyNoQuotes(true)).last("{a +:\"a\"}"));
    }

    @Test
    public void stringValueNoQuotes() throws Exception {
        parseFail("unexpected char code=93, expecting }",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last("a{b]c"));
        parseFail("unexpected char code=93, expecting }, reading noQuotesString starting from file(1:1) at file(1:5)",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last(new LineColCharStream(CharStream.from("a{b]c"), "file")));
        parseFail("expecting more characters to build string",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last("abc'def"));
        parseFail("expecting more characters to build string, reading noQuotesString starting from file(1:1) at file(1:8)",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last(new LineColCharStream(CharStream.from("abc'def"), "file")));
        parseFail("unexpected eof, expecting symbols: [}]",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last("abc{"));
        parseFail("unexpected eof, expecting symbols: [}], reading noQuotesString starting from file(1:1)",
            () -> new StringParser(new ParserOptions().setStringValueNoQuotes(true)).last(new LineColCharStream(CharStream.from("abc{"), "file")));
    }
}
