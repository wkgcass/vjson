package vjson;

import org.junit.Test;
import vjson.listener.AbstractParserListener;
import vjson.parser.*;
import vjson.simple.*;
import vjson.util.AppendableMap;
import vjson.util.TextBuilder;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestUpdateParser {
    @Test
    public void string() throws Exception {
        StringParser parser = new StringParser(new ParserOptions().setListener(new AbstractParserListener() {
            @Override
            public void onStringChar(StringParser string, char c) {
                if (c == 'a') {
                    TextBuilder sb = string.getBuilder();
                    sb.removeLast().append('b');
                }
            }
        }));
        JSON.String result = parser.last(CharStream.from("\"an apple\""));
        assertEquals("bn bpple", result.toJavaObject());
    }

    @Test
    public void number() throws Exception {
        NumberParser parser = new NumberParser();
        parser.feed(CharStream.from("3"));
        parser.setFraction(0.14, 2);
        parser.setExponent(2);
        JSON.Number num = parser.end();
        assertEquals(new SimpleExp(3.14, 2), num);

        parser = new NumberParser();
        parser.feed(CharStream.from("3.14"));
        parser.clearFraction();
        num = parser.end();
        assertEquals(new SimpleInteger(3), num);

        parser = new NumberParser();
        parser.feed(CharStream.from("3.14e2"));
        parser.clearExponent();
        num = parser.end();
        assertEquals(new SimpleDouble(3.14), num);

        parser = new NumberParser();
        parser.feed(CharStream.from("3.14e2"));
        parser.setInteger(5);
        num = parser.end();
        assertEquals(new SimpleExp(5.14, 2), num);

        parser = new NumberParser();
        parser.feed(CharStream.from("3.14e2"));
        parser.setNegative(true);
        num = parser.end();
        assertEquals(new SimpleExp(-3.14, 2), num);

        parser = new NumberParser();
        parser.feed(CharStream.from("3.14e2"));
        parser.setExponentNegative(true);
        num = parser.end();
        assertEquals(new SimpleExp(3.14, -2), num);
    }

    @Test
    public void array() throws Exception {
        ArrayParser parser = new ArrayParser();
        parser.feed("[1,2,");
        LinkedList<JSON.Instance> list = parser.getList();
        assertEquals(2, list.size());
        list.removeLast();
        list.add(new SimpleInteger(4));
        JSON.Array array = parser.last("3]");
        assertEquals(new SimpleArray(
            new SimpleInteger(1),
            new SimpleInteger(4),
            new SimpleInteger(3)
        ), array);
    }

    @Test
    public void object() throws Exception {
        ObjectParser parser = new ObjectParser();
        parser.feed("{\"b\":");
        assertEquals("b", parser.getCurrentKey());
        parser.setCurrentKey("a");
        JSON.Object object = parser.last("1}");
        assertEquals(new SimpleObject(new AppendableMap<>()
                .append("a", new SimpleInteger(1))),
            object);
    }
}
