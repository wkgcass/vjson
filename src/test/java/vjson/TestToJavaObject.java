package vjson;

import org.junit.Test;
import vjson.simple.*;
import vjson.util.AppendableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings("RedundantThrows")
public class TestToJavaObject {
    private final Random random = new Random();

    @Test
    public void integer() throws Exception {
        SimpleInteger i;

        i = new SimpleInteger(1);
        assertEquals((Integer) 1, i.toJavaObject());
        i = new SimpleInteger(32);
        assertEquals((Integer) 32, i.toJavaObject());
        i = new SimpleInteger(-32);
        assertEquals((Integer) (-32), i.toJavaObject());
        int rand = random.nextInt();
        i = new SimpleInteger(rand);
        assertEquals((Integer) rand, i.toJavaObject());
        // multiple times
        assertEquals((Integer) rand, i.toJavaObject());
    }

    @Test
    public void longV() throws Exception {
        JSON.Long l;

        l = new SimpleLong(1L);
        assertEquals((Long) 1L, l.toJavaObject());
        l = new SimpleLong(32L);
        assertEquals((Long) 32L, l.toJavaObject());
        l = new SimpleLong(-32L);
        assertEquals((Long) (-32L), l.toJavaObject());
        long rand = random.nextLong();
        l = new SimpleLong(rand);
        assertEquals((Long) rand, l.toJavaObject());
        // multiple times
        assertEquals((Long) rand, l.toJavaObject());
    }

    @Test
    public void doubleV() throws Exception {
        JSON.Double d;

        d = new SimpleDouble(1);
        assertEquals((Double) 1.0, d.toJavaObject());
        d = new SimpleDouble(3.2);
        assertEquals((Double) 3.2, d.toJavaObject());
        d = new SimpleDouble(-3.2);
        assertEquals((Double) (-3.2), d.toJavaObject());
        double rand = random.nextDouble();
        d = new SimpleDouble(rand);
        assertEquals((Double) rand, d.toJavaObject());
        // multiple times
        assertEquals((Double) rand, d.toJavaObject());
    }

    @Test
    public void exp() throws Exception {
        JSON.Exp d;

        d = new SimpleExp(3, 5);
        assertEquals(3e5, d.toJavaObject(), 0.1);
        d = new SimpleExp(3.2, 32);
        assertEquals(3.2 * Math.pow(10, 32), d.toJavaObject(), 1);
        d = new SimpleExp(-3, 5);
        assertEquals(-3.0e5, d.toJavaObject(), 1);
        // multiple times
        assertEquals(-3.0e5, d.toJavaObject(), 1);
    }

    @Test
    public void bool() throws Exception {
        JSON.Bool b;

        b = new SimpleBool(true);
        assertEquals(true, b.toJavaObject());
        // multiple times
        assertEquals(true, b.toJavaObject());
        b = new SimpleBool(false);
        assertEquals(false, b.toJavaObject());
        // multiple times
        assertEquals(false, b.toJavaObject());
    }

    @Test
    public void nullV() throws Exception {
        JSON.Null n;

        n = new SimpleNull();
        assertNull(n.toJavaObject());
        // multiple times
        assertNull(n.toJavaObject());
    }

    @Test
    public void string() throws Exception {
        JSON.String s;

        s = new SimpleString("a");
        assertEquals("a", s.toJavaObject());
        s = new SimpleString("abc");
        assertEquals("abc", s.toJavaObject());
        s = new SimpleString("\"");
        assertEquals("\"", s.toJavaObject());
        s = new SimpleString("'");
        assertEquals("'", s.toJavaObject());
        s = new SimpleString("/");
        assertEquals("/", s.toJavaObject());
        s = new SimpleString("\\");
        assertEquals("\\", s.toJavaObject());
        s = new SimpleString("\b");
        assertEquals("\b", s.toJavaObject());
        s = new SimpleString("\f");
        assertEquals("\f", s.toJavaObject());
        s = new SimpleString("\n");
        assertEquals("\n", s.toJavaObject());
        s = new SimpleString("\r");
        assertEquals("\r", s.toJavaObject());
        s = new SimpleString("\t");
        assertEquals("\t", s.toJavaObject());
        s = new SimpleString("\u0002");
        assertEquals("\u0002", s.toJavaObject());
        s = new SimpleString("\u0012");
        assertEquals("\u0012", s.toJavaObject());
        s = new SimpleString("\u007f");
        assertEquals("\u007f", s.toJavaObject());
        s = new SimpleString("\u00ff");
        assertEquals("\u00ff", s.toJavaObject());
        s = new SimpleString("\u0234");
        assertEquals("\u0234", s.toJavaObject());
        s = new SimpleString("\u1234");
        assertEquals("\u1234", s.toJavaObject());
        // multiple times
        assertEquals("\u1234", s.toJavaObject());
    }

    @Test
    public void array() throws Exception {
        JSON.Instance a;

        a = new SimpleArray();
        assertEquals(Collections.emptyList(), a.toJavaObject());
        a = new SimpleArray(new SimpleString("ab"));
        assertEquals(Collections.singletonList("ab"), a.toJavaObject());
        a = new SimpleArray(new SimpleString("ab"), new SimpleInteger(32));
        assertEquals(Arrays.asList("ab", 32), a.toJavaObject());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1)));
        assertEquals(Collections.singletonList(Collections.singletonList(1)), a.toJavaObject());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1), new SimpleDouble(3.2)), new SimpleString("a"));
        assertEquals(Arrays.asList(Arrays.asList(1, 3.2), "a"), a.toJavaObject());
        // multiple times
        assertEquals(Arrays.asList(Arrays.asList(1, 3.2), "a"), a.toJavaObject());
    }

    @Test
    public void object() throws Exception {
        JSON.Instance o;

        o = new SimpleObject(new AppendableMap<>());
        assertEquals(new AppendableMap<>(), o.toJavaObject());
        o = new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1)));
        assertEquals(new AppendableMap<>().append("a", 1), o.toJavaObject());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleString("a"))
            .append("b", new SimpleString("b")));
        assertEquals(new AppendableMap<>()
            .append("a", "a")
            .append("b", "b"), o.toJavaObject());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleObject(new AppendableMap<>()
                .append("x", new SimpleInteger(1))
                .append("y", new SimpleNull())))
            .append("b", new SimpleString("n")));
        assertEquals(new AppendableMap<>()
            .append("a", new AppendableMap<>()
                .append("x", 1)
                .append("y", null))
            .append("b", "n"), o.toJavaObject());
        // multiple times
        assertEquals(new AppendableMap<>()
            .append("a", new AppendableMap<>()
                .append("x", 1)
                .append("y", null))
            .append("b", "n"), o.toJavaObject());
    }
}
