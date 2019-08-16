package vjson;

import org.junit.Test;
import vjson.cs.CharArrayCharStream;
import vjson.listener.AbstractUnsupportedParserListener;
import vjson.parser.*;
import vjson.simple.*;
import vjson.util.AppendableMap;
import vjson.util.ObjectBuilder;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

@SuppressWarnings("RedundantThrows")
public class TestParamValidationReport {
    @Test
    public void array() throws Exception {
        try {
            new SimpleArray((List<JSON.Instance>) null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new SimpleArray(Arrays.asList(new SimpleInteger(1), null));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleArray(new SimpleInteger(1), null);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleArray(new SimpleInteger(1)).get(1);
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
        class T extends SimpleArray {
            private T(List<JSON.Instance> list, TrustedFlag flag) {
                super(list, flag);
            }
        }
        try {
            new T(Collections.emptyList(), null);
            fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    @Test
    public void object() throws Exception {
        try {
            new SimpleObject((Map<String, JSON.Instance>) null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new SimpleObject(new AppendableMap<>()
                .append(null, new SimpleInteger(1)));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleObject(new AppendableMap<>()
                .append("a", null));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleObject((List<SimpleObjectEntry<JSON.Instance>>) null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new SimpleObject(Collections.singletonList(null));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleObject(Collections.singletonList(new SimpleObjectEntry<>(null, new SimpleNull())));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleObject(Collections.singletonList(new SimpleObjectEntry<>("a", null)));
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new SimpleObject(new LinkedHashMap<>()).get("a");
            fail();
        } catch (NoSuchElementException ignore) {
        }
        try {
            new SimpleObject(new LinkedHashMap<>()).getAll("a");
            fail();
        } catch (NoSuchElementException ignore) {
        }
        try {
            new SimpleObject(new LinkedHashMap<>()).get(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new SimpleObject(new LinkedHashMap<>()).getAll(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        class T extends SimpleObject {
            private T(List<SimpleObjectEntry<JSON.Instance>> initMap, TrustedFlag flag) {
                super(initMap, flag);
            }
        }
        try {
            new T(new LinkedList<>(), null);
            fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    @Test
    public void string() throws Exception {
        try {
            new SimpleString(null);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void charArrayCharStream() throws Exception {
        try {
            new CharArrayCharStream(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            //noinspection deprecation
            new CharArrayCharStream(new char[]{'a'}).remove();
            fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    @Test
    public void arrayParser() throws Exception {
        try {
            new ArrayParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new ArrayParser().build(null, false);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void boolParser() throws Exception {
        try {
            new BoolParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new BoolParser().build(null, false);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void nullParser() throws Exception {
        try {
            new NullParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new NullParser().feed((CharStream) null);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void numberParser() throws Exception {
        try {
            new NumberParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new NumberParser().build(null, false);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new NumberParser().setInteger(-1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new NumberParser().setFraction(-0.1, 1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new NumberParser().setFraction(0.1, -1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new NumberParser().setFraction(1.1, 1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
        try {
            new NumberParser().setExponent(-1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void objectParser() throws Exception {
        try {
            new ObjectParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new ObjectParser().build(null, false);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new ObjectParser().setCurrentKey(null);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void stringParser() throws Exception {
        try {
            new StringParser(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            new StringParser().build(null, false);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void unsupportedParserListener() throws Exception {
        ParserListener l = new AbstractUnsupportedParserListener() {
        };
        Consumer<Runnable> test = r -> {
            try {
                r.run();
                fail();
            } catch (UnsupportedOperationException ignore) {
            }
        };

        test.accept(() -> l.onArrayBegin(null));
        test.accept(() -> l.onArrayEnd(null));
        test.accept(() -> l.onArrayValue(null, null));
        test.accept(() -> l.onArray(null));

        test.accept(() -> l.onError(null));

        test.accept(() -> l.onBoolBegin(null));
        test.accept(() -> l.onBoolEnd(null));
        test.accept(() -> l.onBool(null));

        test.accept(() -> l.onNullBegin(null));
        test.accept(() -> l.onNullEnd(null));
        test.accept(() -> l.onNull(null));

        test.accept(() -> l.onNumberBegin(null));
        test.accept(() -> l.onNumberFractionBegin(null, 0));
        test.accept(() -> l.onNumberExponentBegin(null, 0));
        test.accept(() -> l.onNumberEnd(null));
        test.accept(() -> l.onNumber(null));

        test.accept(() -> l.onObjectBegin(null));
        test.accept(() -> l.onObjectKey(null, null));
        test.accept(() -> l.onObjectValue(null, null, null));
        test.accept(() -> l.onObjectEnd(null));
        test.accept(() -> l.onObject(null));

        test.accept(() -> l.onStringBegin(null));
        test.accept(() -> l.onStringChar(null, 'a'));
        test.accept(() -> l.onStringEnd(null));
        test.accept(() -> l.onString(null));
    }

    @Test
    public void parserUtils() throws Exception {
        try {
            ParserUtils.buildFrom(null, new ParserOptions());
            fail();
        } catch (NullPointerException ignore) {
        }
        try {
            ParserUtils.buildFrom(CharStream.from(""), null);
            fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void convenience() throws Exception {
        JSON.Object object = new ObjectBuilder().put("a", "str").build();
        try {
            object.getInt("a");
            fail();
        } catch (ClassCastException ignore) {
        }
        try {
            object.getLong("a");
            fail();
        } catch (ClassCastException ignore) {
        }
        try {
            object.getDouble("a");
            fail();
        } catch (ClassCastException ignore) {
        }

        JSON.Array array = new SimpleArray(new SimpleString("a"));
        try {
            array.getInt(0);
            fail();
        } catch (ClassCastException ignore) {
        }
        try {
            array.getLong(0);
            fail();
        } catch (ClassCastException ignore) {
        }
        try {
            array.getDouble(0);
            fail();
        } catch (ClassCastException ignore) {
        }
    }
}
