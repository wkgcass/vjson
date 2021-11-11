package vjson;

import kotlin.Unit;
import org.junit.Test;
import vjson.listener.AbstractParserListener;
import vjson.parser.*;
import vjson.util.AbstractUnsupportedParserListener;
import vjson.util.AppendableMap;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestListenerJavaObject {
    private int step = 0;

    @Test
    public void nullV() throws Exception {
        NullParser parser = new NullParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onNull(Unit n) {
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNull(parser.buildJavaObject(CharStream.from("null"), true));
        assertTrue(parser.completed());
        assertEquals(3, step);
    }

    @Test
    public void bool() throws Exception {
        BoolParser parser = new BoolParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onBool(boolean bool) {
                assertTrue(bool);
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from("true"), true));
        assertEquals(3, step);

        step = 0;
        parser = new BoolParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onBool(boolean bool) {
                assertFalse(bool);
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from("false"), true));
        assertEquals(3, step);
    }

    @Test
    public void string() throws Exception {
        String toParse = "\"abcd\\r\\n\"";
        StringParser parser = new StringParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onString(String string) {
                assertEquals("abcd\r\n", string);
                ++step;
                assertEquals(9, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(9, step);
    }

    @Test
    public void integer() throws Exception {
        String toParse = "3";
        NumberParser parser = new NumberParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onNumber(Number number) {
                assertEquals(3, number);
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(3, step);
    }

    @Test
    public void doubleV() throws Exception {
        String toParse = "3.14";
        NumberParser parser = new NumberParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
                assertEquals(14, number.getFraction());
                assertEquals(2, number.getFractionDivisorZeros());
                ++step;
                assertEquals(3, step);
            }

            @Override
            public void onNumber(Number number) {
                assertEquals(3.14D, number);
                ++step;
                assertEquals(4, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(4, step);
    }

    @Test
    public void integerExponent() throws Exception {
        String toParse = "3e2";
        NumberParser parser = new NumberParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
            public void onNumber(Number number) {
                assertEquals(300D, number);
                ++step;
                assertEquals(4, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(4, step);
    }

    @Test
    public void doubleExponent() throws Exception {
        String toParse = "3.14e2";
        NumberParser parser = new NumberParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
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
                assertEquals(14, number.getFraction());
                assertEquals(2, number.getFractionDivisorZeros());
                ++step;
                assertEquals(3, step);
            }

            @Override
            public void onNumberEnd(NumberParser number) {
                assertTrue(number.hasFraction());
                assertTrue(number.hasExponent());
                assertEquals(3L, number.getInteger());
                assertFalse(number.isNegative());
                assertEquals(14, number.getFraction());
                assertEquals(2, number.getFractionDivisorZeros());
                assertEquals(2, number.getExponent());
                assertFalse(number.isExponentNegative());
                ++step;
                assertEquals(4, step);
            }

            @Override
            public void onNumber(Number number) {
                assertEquals(314D, number);
                ++step;
                assertEquals(5, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(5, step);
    }

    @Test
    public void array() throws Exception {
        String toParse = "[]";
        ArrayParser parser = new ArrayParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onArrayBegin(ArrayParser array) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onArrayEnd(ArrayParser array) {
                assertTrue(array.getJavaList().isEmpty());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onArray(List<?> array) {
                assertEquals(0, array.size());
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(3, step);

        step = 0;
        toParse = "[1,2,3]";
        parser = new ArrayParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractParserListener() {
            @Override
            public void onArrayBegin(ArrayParser array) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onArrayValueJavaObject(ArrayParser array, Object value) {
                assertEquals(step, array.getJavaList().size());
                assertTrue(value instanceof Integer);
                assertEquals(step, (int) value);
                ++step;
            }

            @Override
            public void onArrayEnd(ArrayParser array) {
                ++step;
                assertEquals(5, step);
            }

            @Override
            public void onArray(List<?> array) {
                assertEquals(3, array.size());
                ++step;
                assertEquals(6, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(6, step);
    }

    @Test
    public void object() throws Exception {
        String toParse = "{}";
        ObjectParser parser = new ObjectParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractUnsupportedParserListener() {
            @Override
            public void onObjectBegin(ObjectParser obj) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onObjectEnd(ObjectParser obj) {
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void onObject(Map<String, ?> obj) {
                ++step;
                assertEquals(3, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(3, step);

        step = 0;
        toParse = "{\"a\":1,\"b\":2,\"c\":3}";
        parser = new ObjectParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractParserListener() {
            @Override
            public void onObjectBegin(ObjectParser obj) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onObjectKey(ObjectParser obj, String key) {
                assertEquals(key, obj.getCurrentKey());
                if (step == 1) {
                    assertEquals(0, obj.getJavaMap().size());
                    assertEquals("a", key);
                } else if (step == 3) {
                    assertEquals(1, obj.getJavaMap().size());
                    assertEquals("b", key);
                } else if (step == 5) {
                    assertEquals(2, obj.getJavaMap().size());
                    assertEquals("c", key);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectValueJavaObject(ObjectParser obj, String key, Object value) {
                assertNull(obj.getCurrentKey());
                if (step == 2) {
                    assertEquals(1, obj.getJavaMap().size());
                    assertEquals(1, (int) value);
                } else if (step == 4) {
                    assertEquals(2, obj.getJavaMap().size());
                    assertEquals(2, (int) value);
                } else if (step == 6) {
                    assertEquals(3, obj.getJavaMap().size());
                    assertEquals(3, (int) value);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectEnd(ObjectParser obj) {
                assertNull(obj.getCurrentKey());
                assertEquals(3, obj.getJavaMap().size());
                ++step;
                assertEquals(8, step);
            }

            @Override
            public void onObject(Map<String, ?> obj) {
                //noinspection AssertEqualsBetweenInconvertibleTypes
                assertEquals(new AppendableMap<>()
                        .append("a", 1)
                        .append("b", 2)
                        .append("c", 3)
                    , obj);
                ++step;
                assertEquals(9, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(9, step);
    }

    @Test
    public void objectWithoutValue() throws Exception {
        step = 0;
        String toParse = "{\"a\",\"b\":2,\"c\"}";
        ObjectParser parser = new ObjectParser(new ParserOptions()
            .setAllowObjectEntryWithoutValue(true)
            .setMode(ParserMode.JAVA_OBJECT).setListener(new AbstractParserListener() {
            @Override
            public void onObjectBegin(ObjectParser obj) {
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void onObjectKey(ObjectParser obj, String key) {
                assertEquals(key, obj.getCurrentKey());
                if (step == 1) {
                    assertEquals(0, obj.getJavaMap().size());
                    assertEquals("a", key);
                } else if (step == 3) {
                    assertEquals(1, obj.getJavaMap().size());
                    assertEquals("b", key);
                } else if (step == 5) {
                    assertEquals(2, obj.getJavaMap().size());
                    assertEquals("c", key);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectValueJavaObject(ObjectParser obj, String key, Object value) {
                assertNull(obj.getCurrentKey());
                if (step == 2) {
                    assertEquals(1, obj.getJavaMap().size());
                    assertNull(value);
                } else if (step == 4) {
                    assertEquals(2, obj.getJavaMap().size());
                    assertEquals(2, (int) value);
                } else if (step == 6) {
                    assertEquals(3, obj.getJavaMap().size());
                    assertNull(value);
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void onObjectEnd(ObjectParser obj) {
                assertNull(obj.getCurrentKey());
                assertEquals(3, obj.getJavaMap().size());
                ++step;
                assertEquals(8, step);
            }

            @Override
            public void onObject(Map<String, ?> obj) {
                //noinspection AssertEqualsBetweenInconvertibleTypes
                assertEquals(new AppendableMap<>()
                        .append("a", null)
                        .append("b", 2)
                        .append("c", null)
                    , obj);
                ++step;
                assertEquals(9, step);
            }
        }));
        assertNotNull(parser.buildJavaObject(CharStream.from(toParse), true));
        assertEquals(9, step);
    }
}
