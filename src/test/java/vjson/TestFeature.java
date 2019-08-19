package vjson;

import org.junit.Test;
import vjson.parser.*;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleObject;
import vjson.simple.SimpleString;
import vjson.util.AppendableMap;

import static org.junit.Assert.assertEquals;

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
}
