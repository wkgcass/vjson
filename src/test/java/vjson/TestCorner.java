package vjson;

import org.junit.Test;
import vjson.listener.AbstractUnsupportedParserListener;
import vjson.listener.EmptyParserListener;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleNull;
import vjson.simple.SimpleObject;
import vjson.simple.SimpleObjectEntry;
import vjson.util.AppendableMap;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;
import vjson.util.VERSION;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestCorner {
    @Test
    public void numberExponent() throws Exception {
        assertEquals(JSON.parse("3e4"), JSON.parse("3E4"));
        assertEquals(JSON.parse("0e1"), JSON.parse("0E1"));
        assertEquals(JSON.parse("3.14e2"), JSON.parse("3.14E2"));
    }

    @Test
    public void parserOptions() throws Exception {
        ParserOptions opts = new ParserOptions();
        opts.setListener(new AbstractUnsupportedParserListener() {
        });
        opts.setListener(null);
        assertEquals(EmptyParserListener.INSTANCE, opts.getListener());
        opts.setBufLen(1);
        assertEquals(1, opts.getBufLen());
    }

    @Test
    public void whiteSpace() throws Exception {
        assertTrue(ParserUtils.isWhiteSpace(' '));
        assertTrue(ParserUtils.isWhiteSpace('\t'));
        assertTrue(ParserUtils.isWhiteSpace('\r'));
        assertTrue(ParserUtils.isWhiteSpace('\n'));
    }

    @Test
    public void version() throws Exception {
        System.out.println("Current version is: " + VERSION.VERSION);
        new VERSION();
    }

    @Test
    public void simpleObjectEntry() throws Exception {
        SimpleObjectEntry<Integer> entry1 = new SimpleObjectEntry<>("a", 1);
        assertEquals(entry1, entry1);

        SimpleObjectEntry<Integer> entry2 = new SimpleObjectEntry<>("a", 1);
        assertEquals(entry1, entry2);

        SimpleObjectEntry<Integer> entry3 = new SimpleObjectEntry<>("b", 2);
        assertNotEquals(entry1, entry3);

        SimpleObjectEntry<Integer> entry4 = new SimpleObjectEntry<>("a", 2);
        assertNotEquals(entry1, entry4);

        SimpleObjectEntry<Integer> entry5 = new SimpleObjectEntry<>("b", 1);
        assertNotEquals(entry1, entry5);

        assertNotEquals(entry1, new Object());
        assertNotEquals(entry1, null);
    }

    @Test
    public void object() throws Exception {
        SimpleObject o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleInteger(2))
        );
        assertEquals(Collections.singletonList(new SimpleInteger(1)), o.getAll("a"));
        assertEquals(Collections.singletonList(new SimpleInteger(1)), o.getAll("a"));
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(new SimpleInteger(1), o.get("a"));

        assertEquals(Arrays.asList("a", "b"), o.keyList());
        assertEquals(Arrays.asList("a", "b"), o.keyList());
    }

    @Test
    public void reuseNullParser() throws Exception {
        JSON.Object o = new ObjectParser().last("{\"a\":null,\"b\":null}");
        assertEquals(new SimpleNull(), o.get("a"));
        assertEquals(new SimpleNull(), o.get("b"));
    }

    @Test
    public void customNumber() throws Exception {
        JSON.Number<Integer> n = new JSON.Number<Integer>() {
            @Override
            public Integer toJavaObject() {
                return 1;
            }

            @Override
            public String stringify() {
                return "1";
            }

            @Override
            public String pretty() {
                return "1";
            }

            @Override
            public void stringify(StringBuilder builder, Stringifier sfr) {
                builder.append(stringify());
            }
        };
        JSON.Object o = new ObjectBuilder().putInst("a", n).build();
        assertEquals(1, o.getInt("a"));
        assertEquals(1L, o.getLong("a"));
        assertEquals(1D, o.getDouble("a"), 0);

        JSON.Array a = new ArrayBuilder().addInst(n).build();
        assertEquals(1, a.getInt(0));
        assertEquals(1L, a.getLong(0));
        assertEquals(1D, a.getDouble(0), 0);
    }
}
