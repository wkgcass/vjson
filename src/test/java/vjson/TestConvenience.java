package vjson;

import org.junit.Test;
import vjson.simple.*;
import vjson.util.AppendableMap;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestConvenience {
    @Test
    public void objectBuilder() throws Exception {
        long l = System.currentTimeMillis();
        JSON.Object result = new ObjectBuilder()
            .put("bool", true)
            .put("integer", 1)
            .put("long", l)
            .put("double", 3.14)
            .put("exp", 3.14, 5)
            .put("null", null)
            .put("string", "a")
            .putObject("object", o -> o.put("a", 1))
            .putArray("array", a -> a.add(1))
            .putInst("x", new SimpleInteger(1))
            .putNullableInst("nullable", true, () -> {
                throw new RuntimeException();
            })
            .putNullableInst("notnull", false, () -> new ObjectBuilder().put("a", "b").build())
            .build();
        assertEquals(new SimpleObject(
            new AppendableMap<>()
                .append("bool", new SimpleBool(true))
                .append("integer", new SimpleInteger(1))
                .append("long", new SimpleLong(l))
                .append("double", new SimpleDouble(3.14))
                .append("exp", new SimpleExp(3.14, 5))
                .append("null", new SimpleNull())
                .append("string", new SimpleString("a"))
                .append("object", new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))))
                .append("array", new SimpleArray(new SimpleInteger(1)))
                .append("x", new SimpleInteger(1))
                .append("nullable", new SimpleNull())
                .append("notnull", new SimpleObject(new AppendableMap<>().append("a", new SimpleString("b"))))
        ), result);
    }

    @Test
    public void arrayBuilder() throws Exception {
        long l = System.currentTimeMillis();
        JSON.Array result = new ArrayBuilder()
            .add(true)
            .add(1)
            .add(l)
            .add(3.14)
            .add(3.14, 5)
            .add(null)
            .add("a")
            .addObject(o -> o.put("a", 1))
            .addArray(a -> a.add(1))
            .addInst(new SimpleInteger(1))
            .addNullableInst(true, () -> {
                throw new RuntimeException();
            })
            .addNullableInst(false, () -> new SimpleInteger(123))
            .build();
        assertEquals(new SimpleArray(
            new SimpleBool(true),
            new SimpleInteger(1),
            new SimpleLong(l),
            new SimpleDouble(3.14),
            new SimpleExp(3.14, 5),
            new SimpleNull(),
            new SimpleString("a"),
            new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))),
            new SimpleArray(new SimpleInteger(1)),
            new SimpleInteger(1),
            new SimpleNull(),
            new SimpleInteger(123)
        ), result);
    }

    @Test
    public void arrayBuilderIterable() throws Exception {
        List<String> ls = Arrays.asList("a", "b", "c", "d");
        ArrayBuilder ab = new ArrayBuilder();
        ab.iterable(ls, ArrayBuilder::add);
        assertEquals(new SimpleArray(
            new SimpleString("a"),
            new SimpleString("b"),
            new SimpleString("c"),
            new SimpleString("d")
        ), ab.build());
    }

    @Test
    public void object() throws Exception {
        long l = System.currentTimeMillis();
        JSON.Object object = new ObjectBuilder()
            .put("bool", true)
            .put("integer", 1)
            .put("long", l)
            .put("double", 3.14)
            .put("exp", 3.14, 5)
            .put("null", null)
            .put("string", "a")
            .putObject("object", o -> o.put("a", 1))
            .putArray("array", a -> a.add(1)).build();
        assertTrue(object.getBool("bool"));
        assertEquals(1, object.getInt("integer"));
        assertEquals(1L, object.getLong("integer"));
        assertEquals(1D, object.getDouble("integer"), 0);
        assertEquals(l, object.getLong("long"));
        assertEquals((int) l, object.getInt("long"));
        assertEquals((double) l, object.getDouble("long"), 0);
        assertEquals(3.14, object.getDouble("double"), 0);
        assertEquals(3, object.getInt("double"));
        assertEquals(3L, object.getLong("double"));
        assertEquals(314000D, object.getDouble("exp"), 0);
        assertEquals(314000, object.getInt("exp"), 0);
        assertEquals(314000L, object.getLong("exp"), 0);
        assertNull(object.getNullableString("null"));
        assertNull(object.getNullableObject("null"));
        assertNull(object.getNullableArray("null"));
        assertEquals("a", object.getString("string"));
        assertEquals("a", object.getNullableString("string"));
        assertEquals(new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))), object.getObject("object"));
        assertEquals(new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))), object.getNullableObject("object"));
        assertEquals(new SimpleArray(new SimpleInteger(1)), object.getArray("array"));
        assertEquals(new SimpleArray(new SimpleInteger(1)), object.getNullableArray("array"));
    }

    @Test
    public void array() throws Exception {
        long l = System.currentTimeMillis();
        JSON.Array array = new ArrayBuilder()
            .add(true)
            .add(1)
            .add(l)
            .add(3.14)
            .add(3.14, 5)
            .add(null)
            .add("a")
            .addObject(o -> o.put("a", 1))
            .addArray(a -> a.add(1))
            .build();
        assertTrue(array.getBool(0));
        assertEquals(1, array.getInt(1));
        assertEquals(1L, array.getLong(1));
        assertEquals(1D, array.getDouble(1), 0);
        assertEquals(l, array.getLong(2));
        assertEquals((int) l, array.getInt(2));
        assertEquals((double) l, array.getDouble(2), 0);
        assertEquals(3.14, array.getDouble(3), 0);
        assertEquals(3, array.getInt(3));
        assertEquals(3L, array.getLong(3));
        assertEquals(314000D, array.getDouble(4), 0);
        assertEquals(314000, array.getInt(4), 0);
        assertEquals(314000L, array.getLong(4), 0);
        assertNull(array.getNullableString(5));
        assertNull(array.getNullableObject(5));
        assertNull(array.getNullableArray(5));
        assertEquals("a", array.getString(6));
        assertEquals("a", array.getNullableString(6));
        assertEquals(new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))), array.getObject(7));
        assertEquals(new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1))), array.getNullableObject(7));
        assertEquals(new SimpleArray(new SimpleInteger(1)), array.getArray(8));
        assertEquals(new SimpleArray(new SimpleInteger(1)), array.getNullableArray(8));
    }
}
