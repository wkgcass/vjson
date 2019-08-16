package vjson;

import org.junit.Test;
import vjson.listener.AbstractParserListener;
import vjson.listener.AbstractUnsupportedParserListener;
import vjson.parser.*;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleObject;
import vjson.util.AppendableMap;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestListener {
    private int step = 0;

    @Test
    public void nullV() throws Exception {
        NullParser parser = new NullParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onNullBegin(NullParser n) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onNullEnd(NullParser n) {
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onNull(JSON.Null n) {
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from("null")));
        assertEquals(3, step);
    }

    @Test
    public void bool() throws Exception {
        BoolParser parser = new BoolParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onBoolBegin(BoolParser bool) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onBoolEnd(BoolParser bool) {
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onBool(JSON.Bool bool) {
                assertTrue(bool.booleanValue());
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from("true")));
        assertEquals(3, step);

        step = 0;
        parser = new BoolParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onBoolBegin(BoolParser bool) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onBoolEnd(BoolParser bool) {
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onBool(JSON.Bool bool) {
                assertFalse(bool.booleanValue());
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from("false")));
        assertEquals(3, step);
    }

    @Test
    public void string() throws Exception {
        String toParse = "\"abcd\\r\\n\"";
        StringParser parser = new StringParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onStringBegin(StringParser string) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onStringChar(StringParser string, char c) {
                if (1 <= step && step <= 4) {
                    assertEquals((char) ('a' + step - 1), c);
                } else if (step == 5) {
                    assertEquals('\r', c);
                } else if (step == 6) {
                    assertEquals('\n', c);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onStringEnd(StringParser string) {
                ++step;
                assertEquals(8, step);
            }

            @Override
            public void onString(JSON.String string) {
                assertEquals("abcd\r\n", string.toJavaObject());
                ++step;
                assertEquals(9, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(9, step);
    }

    @Test
    public void integer() throws Exception {
        String toParse = "3";
        NumberParser parser = new NumberParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onNumberBegin(NumberParser number) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onNumberEnd(NumberParser number) {
                assertFalse(number.hasFraction());
                assertFalse(number.hasExponent());
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onNumber(JSON.Number number) {
                assertEquals(3, number.toJavaObject());
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(3, step);
    }

    @Test
    public void doubleV() throws Exception {
        String toParse = "3.14";
        NumberParser parser = new NumberParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onNumberBegin(NumberParser number) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onNumberFractionBegin(NumberParser number, long integer) {
                assertTrue(number.hasFraction());
                assertFalse(number.hasExponent());
                assertEquals(3L, integer);
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onNumberEnd(NumberParser number) {
                assertTrue(number.hasFraction());
                assertFalse(number.hasExponent());
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                assertEquals(0.14, number.getFraction(), 0.0000001);
                assertEquals(1000, number.getFractionNextMulti(), 0);
                ++step;
                assertEquals(3, step);
            }

            @Override
            public void onNumber(JSON.Number number) {
                assertEquals(3.14D, number.toJavaObject());
                ++step;
                assertEquals(4, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(4, step);
    }

    @Test
    public void integerExponent() throws Exception {
        String toParse = "3e2";
        NumberParser parser = new NumberParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onNumberBegin(NumberParser number) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onNumberExponentBegin(NumberParser number, double base) {
                assertFalse(number.hasFraction());
                assertTrue(number.hasExponent());
                assertEquals(3D, base, 0);
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onNumberEnd(NumberParser number) {
                assertFalse(number.hasFraction());
                assertTrue(number.hasExponent());
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                assertEquals(2, number.getExponent());
                assertFalse(number.isExponentNegative());
                ++step;
                assertEquals(3, step);
            }

            @Override
            public void onNumber(JSON.Number number) {
                assertEquals(300D, number.toJavaObject());
                ++step;
                assertEquals(4, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(4, step);
    }

    @Test
    public void doubleExponent() throws Exception {
        String toParse = "3.14e2";
        NumberParser parser = new NumberParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onNumberBegin(NumberParser number) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onNumberFractionBegin(NumberParser number, long integer) {
                assertTrue(number.hasFraction());
                assertFalse(number.hasExponent());
                assertEquals(3L, integer);
                assertFalse(number.isNegative());
                assertEquals(3L, number.getInteger());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onNumberExponentBegin(NumberParser number, double base) {
                assertTrue(number.hasFraction());
                assertTrue(number.hasExponent());
                assertEquals(3.14D, base, 0.000000001);
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                assertEquals(0.14, number.getFraction(), 0.000000001);
                assertEquals(1000, number.getFractionNextMulti(), 0);
                ++step;
                assertEquals(3, step);
            }

            @Override
            public void onNumberEnd(NumberParser number) {
                assertTrue(number.hasFraction());
                assertTrue(number.hasExponent());
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                assertEquals(0.14, number.getFraction(), 0.000000001);
                assertEquals(1000, number.getFractionNextMulti(), 0);
                assertEquals(2, number.getExponent());
                assertFalse(number.isExponentNegative());
                ++step;
                assertEquals(4, step);
            }

            @Override
            public void onNumber(JSON.Number number) {
                assertEquals(314D, number.toJavaObject());
                ++step;
                assertEquals(5, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(5, step);
    }

    @Test
    public void array() throws Exception {
        String toParse = "[]";
        ArrayParser parser = new ArrayParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onArrayBegin(ArrayParser array) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onArrayEnd(ArrayParser array) {
                assertTrue(array.getList().isEmpty());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onArray(JSON.Array array) {
                assertEquals(0, array.length());
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(3, step);

        step = 0;
        toParse = "[1,2,3]";
        parser = new ArrayParser(new ParserOptions().setListener(new AbstractParserListener() {
            @Override
            public void onArrayBegin(ArrayParser array) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onArrayValue(ArrayParser array, JSON.Instance value) {
                assertEquals(step, array.getList().size());
                assertTrue(value instanceof JSON.Integer);
                assertEquals(step, value.toJavaObject());
                ++step;
            }

            @Override
            public void onArrayEnd(ArrayParser array) {
                ++step;
                assertEquals(5, step);
            }

            @Override
            public void onArray(JSON.Array array) {
                ++step;
                assertEquals(6, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(6, step);
    }

    @Test
    public void object() throws Exception {
        String toParse = "{}";
        ObjectParser parser = new ObjectParser(new ParserOptions().setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onObjectBegin(ObjectParser object) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onObjectEnd(ObjectParser object) {
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onObject(JSON.Object object) {
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(3, step);

        step = 0;
        toParse = "{\"a\":1,\"b\":2,\"c\":3}";
        parser = new ObjectParser(new ParserOptions().setListener(new AbstractParserListener() {
            @Override
            public void onObjectBegin(ObjectParser object) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onObjectKey(ObjectParser object, String key) {
                assertEquals(key, object.getCurrentKey());
                if (step == 1) {
                    assertEquals(0, object.getMap().size());
                    assertEquals("a", key);
                } else if (step == 3) {
                    assertEquals(1, object.getMap().size());
                    assertEquals("b", key);
                } else if (step == 5) {
                    assertEquals(2, object.getMap().size());
                    assertEquals("c", key);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectValue(ObjectParser object, String key, JSON.Instance value) {
                assertNull(object.getCurrentKey());
                if (step == 2) {
                    assertEquals(1, object.getMap().size());
                    assertEquals(1, value.toJavaObject());
                } else if (step == 4) {
                    assertEquals(2, object.getMap().size());
                    assertEquals(2, value.toJavaObject());
                } else if (step == 6) {
                    assertEquals(3, object.getMap().size());
                    assertEquals(3, value.toJavaObject());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectEnd(ObjectParser object) {
                assertNull(object.getCurrentKey());
                assertEquals(3, object.getMap().size());
                ++step;
                assertEquals(8, step);
            }

            @Override
            public void onObject(JSON.Object object) {
                assertEquals(new SimpleObject(new AppendableMap<>()
                    .append("a", new SimpleInteger(1))
                    .append("b", new SimpleInteger(2))
                    .append("c", new SimpleInteger(3))
                ), object);
                ++step;
                assertEquals(9, step);
            }
        }));
        assertNotNull(parser.last(CharStream.from(toParse)));
        assertEquals(9, step);
    }
}
