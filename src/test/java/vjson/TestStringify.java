package vjson;

import org.junit.Test;
import vjson.simple.*;
import vjson.util.AppendableMap;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestStringify {
    private final Random random = new Random();

    @Test
    public void integer() throws Exception {
        JSON.Instance i;

        i = new SimpleInteger(1);
        assertEquals("1", i.stringify());
        assertEquals("1", i.pretty());
        i = new SimpleInteger(32);
        assertEquals("32", i.stringify());
        assertEquals("32", i.pretty());
        i = new SimpleInteger(-32);
        assertEquals("-32", i.stringify());
        assertEquals("-32", i.pretty());
        int rand = random.nextInt();
        i = new SimpleInteger(rand);
        assertEquals("" + rand, i.stringify());
        assertEquals("" + rand, i.pretty());
        // multiple times
        assertEquals("" + rand, i.stringify());
        assertEquals("" + rand, i.pretty());
    }

    @Test
    public void longV() throws Exception {
        JSON.Instance l;

        l = new SimpleLong(1L);
        assertEquals("1", l.stringify());
        assertEquals("1", l.pretty());
        l = new SimpleLong(32L);
        assertEquals("32", l.stringify());
        assertEquals("32", l.pretty());
        l = new SimpleLong(-32L);
        assertEquals("-32", l.stringify());
        assertEquals("-32", l.pretty());
        long rand = random.nextLong();
        l = new SimpleLong(rand);
        assertEquals("" + rand, l.stringify());
        assertEquals("" + rand, l.pretty());
        // multiple times
        assertEquals("" + rand, l.stringify());
        assertEquals("" + rand, l.pretty());
    }

    @Test
    public void doubleV() throws Exception {
        JSON.Instance d;

        d = new SimpleDouble(1);
        assertEquals("1.0", d.stringify());
        assertEquals("1.0", d.pretty());
        d = new SimpleDouble(3.2);
        assertEquals("3.2", d.stringify());
        assertEquals("3.2", d.pretty());
        d = new SimpleDouble(-3.2);
        assertEquals("-3.2", d.stringify());
        assertEquals("-3.2", d.pretty());
        double rand = random.nextDouble();
        d = new SimpleDouble(rand);
        assertEquals("" + rand, d.stringify());
        assertEquals("" + rand, d.pretty());
        // multiple times
        assertEquals("" + rand, d.stringify());
        assertEquals("" + rand, d.pretty());
    }

    @Test
    public void exp() throws Exception {
        JSON.Instance d;

        d = new SimpleExp(3, 5);
        assertEquals("3.0e5", d.stringify());
        assertEquals("3.0e5", d.pretty());
        d = new SimpleExp(3.2, 32);
        assertEquals("3.2e32", d.stringify());
        assertEquals("3.2e32", d.pretty());
        d = new SimpleExp(-3, 5);
        assertEquals("-3.0e5", d.stringify());
        assertEquals("-3.0e5", d.pretty());
        // multiple times
        assertEquals("-3.0e5", d.stringify());
        assertEquals("-3.0e5", d.pretty());
    }

    @Test
    public void bool() throws Exception {
        JSON.Instance b;

        b = new SimpleBool(true);
        assertEquals("true", b.stringify());
        assertEquals("true", b.pretty());
        // multiple times
        assertEquals("true", b.stringify());
        assertEquals("true", b.pretty());
        b = new SimpleBool(false);
        assertEquals("false", b.stringify());
        assertEquals("false", b.pretty());
        // multiple times
        assertEquals("false", b.stringify());
        assertEquals("false", b.pretty());
    }

    @Test
    public void nullV() throws Exception {
        JSON.Instance n;

        n = new SimpleNull();
        assertEquals("null", n.stringify());
        assertEquals("null", n.pretty());
        // multiple times
        assertEquals("null", n.stringify());
        assertEquals("null", n.pretty());
    }

    @Test
    public void string() throws Exception {
        JSON.Instance s;

        s = new SimpleString("a");
        assertEquals("\"a\"", s.stringify());
        assertEquals("\"a\"", s.pretty());
        s = new SimpleString("abc");
        assertEquals("\"abc\"", s.stringify());
        assertEquals("\"abc\"", s.pretty());
        s = new SimpleString("\"");
        assertEquals("\"\\\"\"", s.stringify());
        assertEquals("\"\\\"\"", s.pretty());
        s = new SimpleString("'");
        assertEquals("\"'\"", s.stringify());
        // omit the pretty() check, they should be the same
        s = new SimpleString("/");
        assertEquals("\"/\"", s.stringify());
        s = new SimpleString("\\");
        assertEquals("\"\\\\\"", s.stringify());
        s = new SimpleString("\b");
        assertEquals("\"\\b\"", s.stringify());
        s = new SimpleString("\f");
        assertEquals("\"\\f\"", s.stringify());
        s = new SimpleString("\n");
        assertEquals("\"\\n\"", s.stringify());
        s = new SimpleString("\r");
        assertEquals("\"\\r\"", s.stringify());
        s = new SimpleString("\t");
        assertEquals("\"\\t\"", s.stringify());
        s = new SimpleString("\u0002");
        assertEquals("\"\\u0002\"", s.stringify());
        s = new SimpleString("\u0012");
        assertEquals("\"\\u0012\"", s.stringify());
        s = new SimpleString("\u007f");
        assertEquals("\"\\u007f\"", s.stringify());
        s = new SimpleString("\u00ff");
        assertEquals("\"\\u00ff\"", s.stringify());
        s = new SimpleString("\u0234");
        assertEquals("\"\\u0234\"", s.stringify());
        s = new SimpleString("\u1234");
        assertEquals("\"\\u1234\"", s.stringify());
        assertEquals("\"\\u1234\"", s.pretty());
        // multiple times
        assertEquals("\"\\u1234\"", s.stringify());
        assertEquals("\"\\u1234\"", s.pretty());
    }

    @Test
    public void array() throws Exception {
        JSON.Instance a;

        a = new SimpleArray();
        assertEquals("[]", a.stringify());
        assertEquals("[]", a.pretty());
        a = new SimpleArray(new SimpleString("ab"));
        assertEquals("[\"ab\"]", a.stringify());
        assertEquals("[ \"ab\" ]", a.pretty());
        a = new SimpleArray(new SimpleString("ab"), new SimpleInteger(32));
        assertEquals("[\"ab\",32]", a.stringify());
        assertEquals("" +
            "[\n" +
            "    \"ab\",\n" +
            "    32\n" +
            "]", a.pretty());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1)));
        assertEquals("[[1]]", a.stringify());
        assertEquals("[ [ 1 ] ]", a.pretty());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1), new SimpleDouble(3.2)), new SimpleString("a"));
        assertEquals("[[1,3.2],\"a\"]", a.stringify());
        assertEquals("" +
            "[\n" +
            "    [\n" +
            "        1,\n" +
            "        3.2\n" +
            "    ],\n" +
            "    \"a\"\n" +
            "]", a.pretty());
        // multiple times
        assertEquals("[[1,3.2],\"a\"]", a.stringify());
        assertEquals("" +
            "[\n" +
            "    [\n" +
            "        1,\n" +
            "        3.2\n" +
            "    ],\n" +
            "    \"a\"\n" +
            "]", a.pretty());
    }

    @Test
    public void object() throws Exception {
        JSON.Instance o;

        o = new SimpleObject(new AppendableMap<>());
        assertEquals("{}", o.stringify());
        assertEquals("{}", o.pretty());
        o = new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1)));
        assertEquals("{\"a\":1}", o.stringify());
        assertEquals("{ \"a\": 1 }", o.pretty());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleString("a"))
            .append("b", new SimpleString("b")));
        assertEquals("{\"a\":\"a\",\"b\":\"b\"}", o.stringify());
        assertEquals("" +
            "{\n" +
            "    \"a\": \"a\",\n" +
            "    \"b\": \"b\"\n" +
            "}", o.pretty());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleObject(new AppendableMap<>()
                .append("x", new SimpleInteger(1))
                .append("y", new SimpleNull())))
            .append("b", new SimpleString("n")));
        assertEquals("{\"a\":{\"x\":1,\"y\":null},\"b\":\"n\"}", o.stringify());
        assertEquals("" +
            "{\n" +
            "    \"a\": {\n" +
            "        \"x\": 1,\n" +
            "        \"y\": null\n" +
            "    },\n" +
            "    \"b\": \"n\"\n" +
            "}", o.pretty());
        // multiple times
        assertEquals("{\"a\":{\"x\":1,\"y\":null},\"b\":\"n\"}", o.stringify());
        assertEquals("" +
            "{\n" +
            "    \"a\": {\n" +
            "        \"x\": 1,\n" +
            "        \"y\": null\n" +
            "    },\n" +
            "    \"b\": \"n\"\n" +
            "}", o.pretty());
    }
}
