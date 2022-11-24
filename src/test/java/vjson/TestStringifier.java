package vjson;

import org.junit.Test;
import vjson.simple.SimpleString;
import vjson.stringifier.AbstractStringifier;
import vjson.util.AbstractUnsupportedStringifier;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;
import vjson.util.PrintableChars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("RedundantThrows")
public class TestStringifier {
    private int step = 0;

    @Test
    public void arrayFlow() throws Exception {
        JSON.Array array = new ArrayBuilder().add(1).add(2).add(3).build();
        StringBuilder sb = new StringBuilder();
        array.stringify(sb, new AbstractUnsupportedStringifier() {
            @Override
            public void beforeArrayBegin(StringBuilder sb, JSON.Array array) {
                assertEquals("", sb.toString());
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void afterArrayBegin(StringBuilder sb, JSON.Array array) {
                assertEquals("[", sb.toString());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void beforeArrayValue(StringBuilder sb, JSON.Array array, JSON.Instance value) {
                if (step == 2) {
                    assertEquals("[", sb.toString());
                } else if (step == 6) {
                    assertEquals("[1,", sb.toString());
                } else if (step == 10) {
                    assertEquals("[1,2,", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterArrayValue(StringBuilder sb, JSON.Array array, JSON.Instance value) {
                if (step == 3) {
                    assertEquals("[1", sb.toString());
                } else if (step == 7) {
                    assertEquals("[1,2", sb.toString());
                } else if (step == 11) {
                    assertEquals("[1,2,3", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeArrayComma(StringBuilder sb, JSON.Array array) {
                if (step == 4) {
                    assertEquals("[1", sb.toString());
                } else if (step == 8) {
                    assertEquals("[1,2", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterArrayComma(StringBuilder sb, JSON.Array array) {
                if (step == 5) {
                    assertEquals("[1,", sb.toString());
                } else if (step == 9) {
                    assertEquals("[1,2,", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeArrayEnd(StringBuilder sb, JSON.Array array) {
                assertEquals("[1,2,3", sb.toString());
                ++step;
                assertEquals(13, step);
            }

            @Override
            public void afterArrayEnd(StringBuilder sb, JSON.Array array) {
                assertEquals("[1,2,3]", sb.toString());
                ++step;
                assertEquals(14, step);
            }
        });
        assertEquals("[1,2,3]", sb.toString());
        assertEquals(14, step);
    }

    @Test
    public void objectFlow() throws Exception {
        JSON.Object o = new ObjectBuilder().put("a", 1).put("b", 2).put("c", 3).build();
        StringBuilder sb = new StringBuilder();
        o.stringify(sb, new AbstractUnsupportedStringifier() {
            @Override
            public void beforeObjectBegin(StringBuilder sb, JSON.Object obj) {
                assertEquals("", sb.toString());
                ++step;
                assertEquals(1, step);
            }

            @Override
            public void afterObjectBegin(StringBuilder sb, JSON.Object obj) {
                assertEquals("{", sb.toString());
                ++step;
                assertEquals(2, step);
            }

            @Override
            public void beforeObjectKey(StringBuilder sb, JSON.Object obj, String key) {
                if (step == 2) {
                    assertEquals("{", sb.toString());
                } else if (step == 10) {
                    assertEquals("{\"a\":1,", sb.toString());
                } else if (step == 18) {
                    assertEquals("{\"a\":1,\"b\":2,", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterObjectKey(StringBuilder sb, JSON.Object obj, String key) {
                if (step == 3) {
                    assertEquals("{\"a\"", sb.toString());
                } else if (step == 11) {
                    assertEquals("{\"a\":1,\"b\"", sb.toString());
                } else if (step == 19) {
                    assertEquals("{\"a\":1,\"b\":2,\"c\"", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeObjectColon(StringBuilder sb, JSON.Object obj) {
                if (step == 4) {
                    assertEquals("{\"a\"", sb.toString());
                } else if (step == 12) {
                    assertEquals("{\"a\":1,\"b\"", sb.toString());
                } else if (step == 20) {
                    assertEquals("{\"a\":1,\"b\":2,\"c\"", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterObjectColon(StringBuilder sb, JSON.Object obj) {
                if (step == 5) {
                    assertEquals("{\"a\":", sb.toString());
                } else if (step == 13) {
                    assertEquals("{\"a\":1,\"b\":", sb.toString());
                } else if (step == 21) {
                    assertEquals("{\"a\":1,\"b\":2,\"c\":", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeObjectValue(StringBuilder sb, JSON.Object obj, String key, JSON.Instance value) {
                if (step == 6) {
                    assertEquals("{\"a\":", sb.toString());
                } else if (step == 14) {
                    assertEquals("{\"a\":1,\"b\":", sb.toString());
                } else if (step == 22) {
                    assertEquals("{\"a\":1,\"b\":2,\"c\":", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterObjectValue(StringBuilder sb, JSON.Object obj, String key, JSON.Instance value) {
                if (step == 7) {
                    assertEquals("{\"a\":1", sb.toString());
                } else if (step == 15) {
                    assertEquals("{\"a\":1,\"b\":2", sb.toString());
                } else if (step == 23) {
                    assertEquals("{\"a\":1,\"b\":2,\"c\":3", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeObjectComma(StringBuilder sb, JSON.Object obj) {
                if (step == 8) {
                    assertEquals("{\"a\":1", sb.toString());
                } else if (step == 16) {
                    assertEquals("{\"a\":1,\"b\":2", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void afterObjectComma(StringBuilder sb, JSON.Object obj) {
                if (step == 9) {
                    assertEquals("{\"a\":1,", sb.toString());
                } else if (step == 17) {
                    assertEquals("{\"a\":1,\"b\":2,", sb.toString());
                } else {
                    fail();
                }
                ++step;
            }

            @Override
            public void beforeObjectEnd(StringBuilder sb, JSON.Object obj) {
                assertEquals("{\"a\":1,\"b\":2,\"c\":3", sb.toString());
                ++step;
                assertEquals(25, step);
            }

            @Override
            public void afterObjectEnd(StringBuilder sb, JSON.Object obj) {
                assertEquals("{\"a\":1,\"b\":2,\"c\":3}", sb.toString());
                ++step;
                assertEquals(26, step);
            }
        });
        assertEquals("{\"a\":1,\"b\":2,\"c\":3}", sb.toString());
        assertEquals(26, step);
    }

    @Test
    public void nonAsciiChars() {
        assertEquals("\"\\u4f60\\u597d\"", new SimpleString("你好").stringify());

        Stringifier.StringOptions.Builder builder = new Stringifier.StringOptions.Builder();
        assertEquals("\"\\u4f60\\u597d\"", new SimpleString("你好").stringify(builder.build()));

        builder.setPrintableChar(PrintableChars.EveryCharExceptKnownUnprintable);

        assertEquals("\"你好\"", new SimpleString("你好").stringify(builder.build()));
        assertEquals("\"¡\"", new SimpleString("¡").stringify(builder.build()));
        assertEquals("\"abc\"", new SimpleString("abc").stringify(builder.build()));
        assertEquals("\"\\u0001\"", new SimpleString("\u0001").stringify(builder.build()));
        assertEquals("\"\\u001f\"", new SimpleString("\u001f").stringify(builder.build()));

        StringBuilder sb = new StringBuilder();
        new ObjectBuilder().put("你好", "世界").build().stringify(sb, new AbstractStringifier() {
            private final StringOptions strOpts = builder.build();

            @Override
            public StringOptions stringOptions() {
                return strOpts;
            }
        });
        assertEquals("{\"你好\":\"世界\"}", sb.toString());
    }
}
