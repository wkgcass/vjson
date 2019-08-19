package vjson;

import org.junit.Test;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.simple.*;
import vjson.util.AppendableMap;
import vjson.util.Transformer;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@SuppressWarnings("RedundantThrows")
public class TestTransformer {
    private Transformer tf = new Transformer();

    @Test
    public void jsonInst() throws Exception {
        JSON.Instance x = new SimpleNull();
        assertSame(x, tf.transform(x));
    }

    @Test
    public void nullV() throws Exception {
        assertEquals(new SimpleNull(), tf.transform(null));
    }

    @Test
    public void boolV() throws Exception {
        assertEquals(new SimpleBool(true), tf.transform(true));
        assertEquals(new SimpleBool(false), tf.transform(false));
    }

    @Test
    public void integer() throws Exception {
        assertEquals(new SimpleInteger(1), tf.transform(1));
    }

    @Test
    public void longV() throws Exception {
        assertEquals(new SimpleLong(1L), tf.transform(1L));
    }

    @Test
    public void doubleV() throws Exception {
        assertEquals(new SimpleDouble(3.14), tf.transform(3.14D));
    }

    @Test
    public void floatV() throws Exception {
        assertEquals(new SimpleDouble(3.14F), tf.transform(3.14F));
    }

    @Test
    public void number() throws Exception {
        assertEquals(new SimpleInteger(1), tf.transform((short) 1));
    }

    @Test
    public void string() throws Exception {
        assertEquals(new SimpleString("a"), tf.transform("a"));
    }

    @Test
    public void charV() throws Exception {
        assertEquals(new SimpleString("a"), tf.transform('a'));
    }

    @Test
    public void collection() throws Exception {
        assertEquals(new SimpleArray(
            new SimpleInteger(1),
            new SimpleString("a"),
            new SimpleBool(true)
        ), tf.transform(Arrays.asList(1, "a", true)));
    }

    @Test
    public void map() throws Exception {
        assertEquals(new SimpleObject(
                new AppendableMap<>()
                    .append("a", new SimpleInteger(1))
                    .append("b", new SimpleString("s"))
                    .append("c", new SimpleBool(true))
            ),
            tf.transform(new AppendableMap<>().append("a", 1).append("b", "s").append("c", true)));
    }

    @Test
    public void composite() throws Exception {
        JSON.Object object = new ObjectParser(new ParserOptions().setKeyNoQuotes(true).setStringSingleQuotes(true)).last(
            "{integer: 1, string: 's', 'bool true': true, 'bool false': false, 'null': null, 'double': 3.14, " +
                "list: ['str', 1, {a: 1, b: 2}]," +
                "map: {a: 1, b: 2, ls: [1, 'a', true]}}"
        );
        assertEquals(object, tf.transform(
            new AppendableMap<>()
                .append("integer", 1)
                .append("string", "s")
                .append("bool true", true)
                .append("bool false", false)
                .append("null", null)
                .append("double", 3.14)
                .append("list", Arrays.asList(
                    "str",
                    1,
                    new AppendableMap<>()
                        .append("a", 1)
                        .append("b", 2)
                ))
                .append("map", new AppendableMap<>()
                    .append("a", 1)
                    .append("b", 2)
                    .append("ls", Arrays.asList(1, "a", true)))
        ));
    }
}
