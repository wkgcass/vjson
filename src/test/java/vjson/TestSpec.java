package vjson;

import org.junit.Test;
import vjson.simple.*;
import vjson.util.AppendableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestSpec {
    private void testHashCode(@SuppressWarnings("TypeParameterExplicitlyExtendsObject") Supplier<? extends Object> o) {
        Set<Object> set = new HashSet<>();
        Object a = o.get();
        Object b = o.get();
        assertNotSame(a, b);
        set.add(a);
        set.add(b);
        assertEquals("hashCode is invalid", 1, set.size());
    }

    @Test
    public void array() throws Exception {
        JSON.Array array = new SimpleArray(
            new SimpleInteger(1),
            new SimpleNull(),
            new SimpleString("ab")
        );
        assertEquals(3, array.length());
        assertEquals(new SimpleInteger(1), array.get(0));

        assertEquals(array, array);
        assertEquals(new SimpleArray(
            new SimpleInteger(1),
            new SimpleNull(),
            new SimpleString("ab")
        ), array);
        assertEquals(array, array);

        assertNotEquals(new SimpleArray(
            new SimpleInteger(1),
            new SimpleNull(),
            new SimpleString("b")
        ), array);
        assertNotEquals(new SimpleArray(
            new SimpleInteger(1),
            new SimpleString("ab")
        ), array);
        assertNotEquals(new SimpleArray(), 1);

        testHashCode(() -> new SimpleArray(new SimpleInteger(1), new SimpleNull()));
    }

    @Test
    public void bool() throws Exception {
        JSON.Bool b = new SimpleBool(true);
        assertTrue(b.booleanValue());
        b = new SimpleBool(false);
        assertFalse(b.booleanValue());

        b = new SimpleBool(true);
        assertEquals(b, b);
        b = new SimpleBool(false);
        assertEquals(b, b);
        assertEquals(new SimpleBool(true), new SimpleBool(true));
        assertEquals(new SimpleBool(false), new SimpleBool(false));
        assertNotEquals(new SimpleBool(true), new SimpleBool(false));
        assertNotEquals(new SimpleBool(false), new SimpleBool(true));
        assertNotEquals(new SimpleBool(true), 1);
        assertNotEquals(new SimpleBool(false), 1);

        testHashCode(() -> new SimpleBool(true));
        testHashCode(() -> new SimpleBool(false));
    }

    @Test
    public void doubleV() throws Exception {
        JSON.Double d = new SimpleDouble(1.0);
        assertEquals(1.0, d.doubleValue(), 0);
        d = new SimpleDouble(3.2);
        assertEquals(3.2, d.doubleValue(), 0);

        assertEquals(d, d);
        assertEquals(new SimpleDouble(1.0), new SimpleDouble(1.0));
        assertEquals(new SimpleDouble(3.2), new SimpleDouble(3.2));

        assertNotEquals(new SimpleDouble(1.0), new SimpleDouble(0.1));
        assertNotEquals(new SimpleDouble(3.2), new SimpleDouble(3.0));
        assertNotEquals(new SimpleDouble(1.2), 1);

        testHashCode(() -> new SimpleDouble(3.2));
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void exp() throws Exception {
        JSON.Exp d = new SimpleExp(1.0, 3);
        assertEquals(1000.0, d.doubleValue(), 0);
        assertEquals(1.0, d.base(), 0);
        assertEquals(3, d.exponent());

        assertEquals(d, d);
        assertEquals(new SimpleExp(1.0, 3), new SimpleExp(1.0, 3));
        assertEquals(new SimpleDouble(1000.0), new SimpleExp(1.0, 3));
        assertEquals(new SimpleExp(1.0, 3), new SimpleDouble(1000.0));

        assertNotEquals(new SimpleExp(1.0, 3), new SimpleExp(1.0, 2));
        assertNotEquals(new SimpleExp(1.0, 3), new SimpleExp(2.0, 3));
        assertNotEquals(new SimpleExp(1.0, 3), new SimpleDouble(20));
        assertNotEquals(new SimpleExp(1.0, 3), 1);

        testHashCode(() -> new SimpleExp(1.0, 3));
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void integer() throws Exception {
        JSON.Integer i = new SimpleInteger(10);
        assertEquals(10, i.intValue());

        assertEquals(i, i);
        assertEquals(new SimpleInteger(10), new SimpleInteger(10));
        assertEquals(new SimpleInteger(10), new SimpleLong(10L));
        assertEquals(new SimpleLong(10L), new SimpleInteger(10));

        assertNotEquals(new SimpleInteger(10), new SimpleInteger(11));
        assertNotEquals(new SimpleInteger(10), new SimpleLong(11L));
        assertNotEquals(new SimpleInteger(10), 1);

        testHashCode(() -> new SimpleInteger(10));
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    @Test
    public void longV() throws Exception {
        JSON.Long i = new SimpleLong(10);
        assertEquals(10L, i.longValue());

        assertEquals(i, i);
        assertEquals(new SimpleLong(10), new SimpleLong(10));
        assertEquals(new SimpleInteger(10), new SimpleLong(10L));
        assertEquals(new SimpleLong(10L), new SimpleInteger(10));

        assertNotEquals(new SimpleLong(10), new SimpleLong(11));
        assertNotEquals(new SimpleLong(10), new SimpleInteger(11));
        assertNotEquals(new SimpleLong(10), 1);

        testHashCode(() -> new SimpleLong(10));
    }

    @Test
    public void nullV() throws Exception {
        JSON.Null n = new SimpleNull();

        assertEquals(n, n);
        assertEquals(new SimpleNull(), new SimpleNull());
        assertNotEquals(new SimpleNull(), null);
        assertNotEquals(new SimpleNull(), 1);

        testHashCode(SimpleNull::new);
    }

    @Test
    public void object() throws Exception {
        Supplier<JSON.Object> build = () -> new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleString("b"))
            .append("c", new SimpleNull()));
        JSON.Object o = build.get();
        assertEquals(3, o.size());
        assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), o.keySet());
        assertTrue(o.containsKey("a"));
        assertTrue(o.containsKey("b"));
        assertTrue(o.containsKey("c"));
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(new SimpleString("b"), o.get("b"));
        assertEquals(new SimpleNull(), o.get("c"));

        assertEquals(o, o);
        assertEquals(build.get(), build.get());

        assertNotEquals(build.get(), new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleString("b"))
            .append("c", new SimpleString("c"))));
        assertNotEquals(build.get(), new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleString("b"))
            .append("d", new SimpleNull())));
        assertNotEquals(build.get(), new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleString("b"))));
        assertNotEquals(build.get(), 1);

        testHashCode(build);
    }

    @Test
    public void string() throws Exception {
        JSON.String s = new SimpleString("abc");

        assertEquals(s, s);
        assertEquals(new SimpleString("abc"), new SimpleString("abc"));

        assertNotEquals(new SimpleString("abc"), new SimpleString("abcd"));
        assertNotEquals(new SimpleString("abc"), 1);

        testHashCode(() -> new SimpleString("abc"));
    }

    @Test
    public void multiObject() throws Exception {
        JSON.Object o = new SimpleObject(Arrays.asList(
            new SimpleObjectEntry<>("a", new SimpleInteger(1)),
            new SimpleObjectEntry<>("a", new SimpleInteger(2))
        ));
        assertEquals(2, o.size());
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(Collections.singleton("a"), o.keySet());
        assertEquals(Arrays.asList("a", "a"), o.keyList());
        assertEquals(Arrays.asList(
            new SimpleInteger(1), new SimpleInteger(2)
        ), o.getAll("a"));
    }
}
