package vjson;

import org.junit.Test;
import vjson.parser.*;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleNull;
import vjson.simple.SimpleString;
import vjson.util.AppendableMap;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestParse {
    private <T extends JSON.Instance> T parse(Supplier<Parser<T>> parserSupplier,
                                              Supplier<Parser<T>> javaObjectParserSupplier,
                                              String... jsons) {
        //noinspection unchecked
        T inst = (T) JSON.parse(jsons[0]);
        for (String json : jsons) {
            T x = parse(json, parserSupplier, javaObjectParserSupplier);
            assertEquals(inst, x);
        }
        return inst;
    }

    private <T extends JSON.Instance> T parse(String json,
                                              Supplier<Parser<T>> parserSupplier,
                                              Supplier<Parser<T>> javaObjectParserSupplier) {
        //noinspection unchecked
        T inst = (T) JSON.parse(json);
        assertNotNull(inst);
        {
            Object foo = ParserUtils.buildFrom(CharStream.from(json), new ParserOptions());
            assertEquals(inst, foo);
        }
        {
            assertEquals(inst.toJavaObject(), JSON.parseToJavaObject(json));
        }
        {
            assertEquals(inst.toJavaObject(), ParserUtils.buildJavaObject(CharStream.from(json), new ParserOptions()));
        }
        // test partial
        char[] chars = ("   " + json + "   ").toCharArray();
        for (int step = 1; step < chars.length; ++step) {
            Parser<T> parser = parserSupplier.get();
            T tmpResult;
            T result = null;
            boolean finished = false;
            for (int i = 0; i < chars.length; i += step) {
                for (int j = 0; j < 5; ++j) {
                    tmpResult = parser.feed(""); // feed empty character
                    assertNull(tmpResult);
                }

                char[] foo = new char[Math.min(step, chars.length - i)];
                System.arraycopy(chars, i, foo, 0, foo.length);
                CharStream cs = CharStream.from(foo);
                boolean isComplete = i + step >= chars.length;
                if (isComplete) {
                    tmpResult = parser.last(cs);
                } else {
                    tmpResult = parser.feed(cs);
                }
                if (finished) {
                    for (char c : foo) {
                        assertEquals(' ', c);
                    }
                    assertNull(tmpResult);
                } else {
                    if (tmpResult == null) {
                        assertFalse(parser.completed());
                    } else {
                        finished = true;
                        result = tmpResult;
                    }
                }
            }
            assertNotNull(result);
            assertEquals(inst, result);
        }
        for (int step = 1; step < chars.length; ++step) {
            Parser parser = javaObjectParserSupplier.get();
            Object tmpResult;
            Object result = null;
            boolean finished = false;
            for (int i = 0; i < chars.length; i += step) {
                for (int j = 0; j < 5; ++j) {
                    tmpResult = parser.buildJavaObject(CharStream.from(""), false); // feed empty character
                    assertNull(tmpResult);
                }

                char[] foo = new char[Math.min(step, chars.length - i)];
                System.arraycopy(chars, i, foo, 0, foo.length);
                CharStream cs = CharStream.from(foo);
                boolean isComplete = i + step >= chars.length;

                tmpResult = parser.buildJavaObject(cs, isComplete);

                if (finished) {
                    for (char c : foo) {
                        assertEquals(' ', c);
                    }
                    assertNull(tmpResult);
                } else {
                    if (parser.completed()) {
                        finished = true;
                        result = tmpResult;
                    }
                }
            }
            assertTrue(parser.completed());
            assertEquals(inst.toJavaObject(), result);
        }
        // test full expression but with an end
        // only for numbers
        Parser<T> parser = parserSupplier.get();
        if (parser instanceof NumberParser) {
            assertNull(parser.feed(CharStream.from(json)));
            assertEquals(inst, parser.last(CharStream.from(new char[0])));
        }
        return inst;
    }

    @Test
    public void nullV() throws Exception {
        JSON.Instance inst = parse("null", NullParser::new, () -> new NullParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT)));
        //noinspection ConstantConditions
        assertTrue(inst instanceof JSON.Null);
    }

    @Test
    public void bool() throws Exception {
        JSON.Bool inst = parse("true", BoolParser::new, () -> new BoolParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT)));
        assertTrue(inst.booleanValue());

        inst = parse("false", BoolParser::new, () -> new BoolParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT)));
        assertFalse(inst.booleanValue());
    }

    @Test
    public void string() throws Exception {
        JSON.String inst = parse("\"a012\\\"\\\\\\/\\b\\f\\n\\r\\t\\u0020\\u1ABC\\u0abc\\u00aB\\u000C中文\"", StringParser::new, () -> new StringParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT)));
        assertEquals("" +
            "a" +
            "0" +
            "1" +
            "2" +
            "\"" +
            "\\" +
            "/" +
            "\b" +
            "\f" +
            "\n" +
            "\r" +
            "\t" +
            "\u0020" +
            "\u1abc" +
            "\u0abc" +
            "\u00ab" +
            "\u000c" +
            "中" +
            "文" +
            "", inst.toJavaObject());
    }

    @Test
    public void number() throws Exception {
        Supplier<Parser<JSON.Number>> sup = NumberParser::new;
        Supplier<Parser<JSON.Number>> sup2 = () -> new NumberParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT));
        Map<String, Integer> intTests = new AppendableMap<>()
            .append("0", 0)
            .append("1", 1)
            .append("12", 12)
            .append("123", 123)
            .append("1234", 1234)
            .append("10", 10)
            .append("100", 100)
            .append("1200", 1200)
            .append("102", 102)
            .append("1002", 1002);
        for (Map.Entry<String, Integer> entry : intTests.entrySet()) {
            JSON.Integer i = (JSON.Integer) parse(entry.getKey(), sup, sup2);
            assertEquals(entry.getValue().intValue(), i.intValue());
            i = (JSON.Integer) parse("-" + entry.getKey(), sup, sup2);
            assertEquals(entry.getValue().intValue(), -i.intValue());
            for (int x = 0; x < 3; ++x) {
                JSON.Exp e = (JSON.Exp) parse(entry.getKey() + "e" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0);

                // exponent symbol +
                e = (JSON.Exp) parse(entry.getKey() + "e+" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e+" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0);
                // exponent symbol -
                e = (JSON.Exp) parse(entry.getKey() + "e-" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, -x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e-" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, -x), -e.doubleValue(), 0);
            }
        }
        {
            int v = Integer.MAX_VALUE;
            JSON.Integer i = (JSON.Integer) parse("" + v, sup, sup2);
            assertEquals(v, i.intValue());
            v = Integer.MIN_VALUE;
            i = (JSON.Integer) parse("" + v, sup, sup2);
            assertEquals(v, i.intValue());
        }
        Map<String, Long> longTests = new AppendableMap<>()
            .append("3000000000", 3000000000L)
            .append("3210000000", 3210000000L)
            .append("9876543210", 9876543210L)
            .append("3000200010", 3000200010L)
            .append("3000200001", 3000200001L);
        for (Map.Entry<String, Long> entry : longTests.entrySet()) {
            JSON.Long i = (JSON.Long) parse(entry.getKey(), sup, sup2);
            assertEquals(entry.getValue().longValue(), i.longValue());
            i = (JSON.Long) parse("-" + entry.getKey(), sup, sup2);
            assertEquals(entry.getValue().longValue(), -i.longValue());

            for (int x = 0; x < 3; ++x) {
                JSON.Exp e = (JSON.Exp) parse(entry.getKey() + "e" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0);

                // exponent symbol +
                e = (JSON.Exp) parse(entry.getKey() + "e+" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e+" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0);
                // exponent symbol -
                e = (JSON.Exp) parse(entry.getKey() + "e-" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, -x), e.doubleValue(), 0);
                e = (JSON.Exp) parse("-" + entry.getKey() + "e-" + x, sup, sup2);
                assertEquals(entry.getValue() * Math.pow(10, -x), -e.doubleValue(), 0);
            }
        }
        {
            long v = ((long) Integer.MAX_VALUE) + 1;
            JSON.Long i = (JSON.Long) parse("" + v, sup, sup2);
            assertEquals(v, i.longValue());
            v = ((long) Integer.MIN_VALUE) - 1;
            i = (JSON.Long) parse("" + v, sup, sup2);
            assertEquals(v, i.longValue());
        }
        Map<String, Double> floatTests = new AppendableMap<>()
            .append("0.0", 0D)
            .append("0.1", 0.1)
            .append("0.01", 0.01)
            .append("0.001", 0.001)
            .append("0.012", 0.012)
            .append("0.123", 0.123)
            .append("0.00123", 0.00123)
            .append("1.0", 1D)
            .append("10.0", 10D)
            .append("100.0", 100D)
            .append("12.0", 12D)
            .append("123.0", 123D)
            .append("12300.0", 12300D)
            .append("1.2", 1.2)
            .append("1.23", 1.23)
            .append("1.234", 1.234)
            .append("1.00023", 1.00023)
            .append("123.004", 123.004);
        for (Map.Entry<String, Double> entry : floatTests.entrySet()) {
            for (int i = 0; i < 3; ++i) {
                String suffix = "";
                for (int j = 0; j < i; ++j) {
                    //noinspection StringConcatenationInLoop
                    suffix += "0";
                }
                JSON.Double d = (JSON.Double) parse(entry.getKey() + suffix, sup, sup2);
                assertEquals(entry.getValue(), d.doubleValue(), 0.000001);
                d = (JSON.Double) parse("-" + entry.getKey() + suffix, sup, sup2);
                assertEquals(entry.getValue(), -d.doubleValue(), 0.000001);

                for (int x = 0; x < 11; ++x) {
                    if (x > 3 && x < 10) {
                        continue; // skip unnecessary tests
                    }
                    JSON.Exp e = (JSON.Exp) parse(entry.getKey() + suffix + "e" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0.000001);
                    e = (JSON.Exp) parse("-" + entry.getKey() + suffix + "e" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0.000001);

                    // exponent symbol +
                    e = (JSON.Exp) parse(entry.getKey() + suffix + "e+" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, x), e.doubleValue(), 0.000001);
                    e = (JSON.Exp) parse("-" + entry.getKey() + suffix + "e+" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, x), -e.doubleValue(), 0.000001);
                    // exponent symbol -
                    e = (JSON.Exp) parse(entry.getKey() + suffix + "e-" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, -x), e.doubleValue(), 0.000001);
                    e = (JSON.Exp) parse("-" + entry.getKey() + suffix + "e-" + x, sup, sup2);
                    assertEquals(entry.getValue() * Math.pow(10, -x), -e.doubleValue(), 0.000001);
                }
            }
        }
    }

    @Test
    public void array() throws Exception {
        Supplier<Parser<JSON.Array>> sup = ArrayParser::new;
        Supplier<Parser<JSON.Array>> sup2 = () -> new ArrayParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT));

        JSON.Array a = parse(sup, sup2, "[]", "[   ]");
        assertEquals(0, a.length());

        a = parse(sup, sup2, "[1]", "[  1   ]");
        assertEquals(1, a.length());
        assertEquals(new SimpleInteger(1), a.get(0));

        a = parse(sup, sup2, "[1,2]", "[   1   ,   2   ]");
        assertEquals(2, a.length());
        assertEquals(new SimpleInteger(1), a.get(0));
        assertEquals(new SimpleInteger(2), a.get(1));

        a = parse(sup, sup2, "[1,null,\"hello\"]", "[   1   ,   null   ,    \"hello\"   ]");
        assertEquals(3, a.length());
        assertEquals(new SimpleInteger(1), a.get(0));
        assertEquals(new SimpleNull(), a.get(1));
        assertEquals(new SimpleString("hello"), a.get(2));

        a = parse(sup, sup2, "[[1]]", "[    [    1   ]    ]");
        assertEquals(1, a.length());
        assertEquals(new SimpleArray(new SimpleInteger(1)), a.get(0));
    }

    @Test
    public void object() throws Exception {
        Supplier<Parser<JSON.Object>> sup = ObjectParser::new;
        Supplier<Parser<JSON.Object>> sup2 = () -> new ObjectParser(new ParserOptions().setMode(ParserMode.JAVA_OBJECT));

        JSON.Object o = parse(sup, sup2, "{}", "{   }");
        assertEquals(0, o.size());

        o = parse(sup, sup2, "{\"a\":1}", "{    \"a\"    :    1    }");
        assertEquals(1, o.size());
        assertEquals(new SimpleInteger(1), o.get("a"));

        o = parse(sup, sup2, "{\"a\":1,\"b\":2}", "{    \"a\"    :    1   ,    \"b\"    :     2    }");
        assertEquals(2, o.size());
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(new SimpleInteger(2), o.get("b"));

        o = parse(sup, sup2, "{\"a\":1,\"b\":null,\"c\":\"hello\"}", "{   \"a\"   :    1    ,   \"b\"   :   null   ,   \"c\"   :    \"hello\"     }");
        assertEquals(3, o.size());
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(new SimpleNull(), o.get("b"));
        assertEquals(new SimpleString("hello"), o.get("c"));

        o = parse(sup, sup2, "{\"a\":{\"b\":1}}", "{   \"a\"   :    {   \"b\"   :   1   }   }");
        assertEquals(1, o.size());
        {
            JSON.Object o2 = (JSON.Object) o.get("a");
            assertEquals(1, o2.size());
            assertEquals(new SimpleInteger(1), o2.get("b"));
        }
    }

    @Test
    public void general() throws Exception {
        // https://www.json-generator.com/#
        String json = "[\n" +
            "  {\n" +
            "    \"_id\": \"5d49bb155d8e0f9ccbe21356\",\n" +
            "    \"index\": 0,\n" +
            "    \"guid\": \"4957233d-8526-4949-9498-2d6644ee1a67\",\n" +
            "    \"isActive\": false,\n" +
            "    \"balance\": \"$3,286.34\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 33,\n" +
            "    \"eyeColor\": \"blue\",\n" +
            "    \"name\": \"Robyn Moody\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"INSOURCE\",\n" +
            "    \"email\": \"robynmoody@insource.com\",\n" +
            "    \"phone\": \"+1 (824) 452-2508\",\n" +
            "    \"address\": \"728 Ide Court, Sisquoc, Alaska, 8569\",\n" +
            "    \"about\": \"Non laboris eu duis ad voluptate eiusmod ipsum do. Aute esse irure cillum veniam minim minim nisi dolor sit qui labore deserunt elit. Sint adipisicing deserunt sint eiusmod fugiat voluptate laborum nostrud anim dolor do.\\r\\n\",\n" +
            "    \"registered\": \"2016-12-27T09:40:55 -08:00\",\n" +
            "    \"latitude\": -41.779924,\n" +
            "    \"longitude\": -13.753001,\n" +
            "    \"tags\": [\n" +
            "      \"laborum\",\n" +
            "      \"nisi\",\n" +
            "      \"elit\",\n" +
            "      \"Lorem\",\n" +
            "      \"mollit\",\n" +
            "      \"exercitation\",\n" +
            "      \"minim\"\n" +
            "    ],\n" +
            "    \"friends\": [\n" +
            "      {\n" +
            "        \"id\": 0,\n" +
            "        \"name\": \"Sondra English\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Neal Vaughan\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"name\": \"Porter Durham\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"greeting\": \"Hello, Robyn Moody! You have 9 unread messages.\",\n" +
            "    \"favoriteFruit\": \"banana\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"_id\": \"5d49bb155b7fbc211d88ce5a\",\n" +
            "    \"index\": 1,\n" +
            "    \"guid\": \"568a54c2-c837-4950-8835-0c2a45a4d7ab\",\n" +
            "    \"isActive\": false,\n" +
            "    \"balance\": \"$2,436.59\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 39,\n" +
            "    \"eyeColor\": \"brown\",\n" +
            "    \"name\": \"Danielle Stokes\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"BEDDER\",\n" +
            "    \"email\": \"daniellestokes@bedder.com\",\n" +
            "    \"phone\": \"+1 (816) 500-3218\",\n" +
            "    \"address\": \"335 Drew Street, Avoca, Texas, 9518\",\n" +
            "    \"about\": \"Labore duis exercitation proident exercitation. Nostrud do eu culpa eiusmod nisi ipsum labore velit. Sunt sint aute excepteur nulla velit consectetur proident anim officia. Ad culpa sit elit Lorem laboris laborum Lorem amet exercitation ad incididunt in excepteur commodo.\\r\\n\",\n" +
            "    \"registered\": \"2018-04-04T12:33:35 -08:00\",\n" +
            "    \"latitude\": 49.795819,\n" +
            "    \"longitude\": -87.54978,\n" +
            "    \"tags\": [\n" +
            "      \"ad\",\n" +
            "      \"est\",\n" +
            "      \"do\",\n" +
            "      \"amet\",\n" +
            "      \"et\",\n" +
            "      \"velit\",\n" +
            "      \"aliqua\"\n" +
            "    ],\n" +
            "    \"friends\": [\n" +
            "      {\n" +
            "        \"id\": 0,\n" +
            "        \"name\": \"Bass Mcclure\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Byers Baird\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"name\": \"Elsa Thornton\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"greeting\": \"Hello, Danielle Stokes! You have 8 unread messages.\",\n" +
            "    \"favoriteFruit\": \"banana\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"_id\": \"5d49bb15c4dcd05807c49712\",\n" +
            "    \"index\": 2,\n" +
            "    \"guid\": \"af17f570-f684-4361-8b6a-740fa717638d\",\n" +
            "    \"isActive\": false,\n" +
            "    \"balance\": \"$1,971.71\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 29,\n" +
            "    \"eyeColor\": \"brown\",\n" +
            "    \"name\": \"Ofelia Pearson\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"SONIQUE\",\n" +
            "    \"email\": \"ofeliapearson@sonique.com\",\n" +
            "    \"phone\": \"+1 (808) 580-3320\",\n" +
            "    \"address\": \"133 Clifton Place, Saranap, North Dakota, 1882\",\n" +
            "    \"about\": \"Eu sit ullamco nostrud excepteur aliquip nostrud esse sunt nostrud enim labore eiusmod fugiat. Cillum ea dolor est laborum duis aliqua eu eiusmod esse ipsum in qui. Cupidatat proident ipsum veniam pariatur ad laboris. Exercitation anim sunt nulla nostrud duis aute labore nulla quis pariatur. Minim cillum incididunt quis et eu eiusmod dolor reprehenderit ullamco excepteur. Consequat aute velit ut deserunt cupidatat esse eiusmod occaecat veniam elit ex.\\r\\n\",\n" +
            "    \"registered\": \"2015-06-15T03:27:45 -08:00\",\n" +
            "    \"latitude\": -66.190943,\n" +
            "    \"longitude\": -179.18901,\n" +
            "    \"tags\": [\n" +
            "      \"excepteur\",\n" +
            "      \"duis\",\n" +
            "      \"Lorem\",\n" +
            "      \"duis\",\n" +
            "      \"aute\",\n" +
            "      \"esse\",\n" +
            "      \"cupidatat\"\n" +
            "    ],\n" +
            "    \"friends\": [\n" +
            "      {\n" +
            "        \"id\": 0,\n" +
            "        \"name\": \"Walsh Cole\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Howe Gonzalez\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"name\": \"Dona Robinson\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"greeting\": \"Hello, Ofelia Pearson! You have 4 unread messages.\",\n" +
            "    \"favoriteFruit\": \"strawberry\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"_id\": \"5d49bb15a7cf7c40cb8d8f39\",\n" +
            "    \"index\": 3,\n" +
            "    \"guid\": \"9a132e7d-81a4-4082-93a8-61e5c1a112a4\",\n" +
            "    \"isActive\": false,\n" +
            "    \"balance\": \"$1,176.81\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 36,\n" +
            "    \"eyeColor\": \"brown\",\n" +
            "    \"name\": \"Blanca Talley\",\n" +
            "    \"gender\": \"female\",\n" +
            "    \"company\": \"NAMEGEN\",\n" +
            "    \"email\": \"blancatalley@namegen.com\",\n" +
            "    \"phone\": \"+1 (941) 555-3324\",\n" +
            "    \"address\": \"624 Bartlett Place, Barstow, Marshall Islands, 1439\",\n" +
            "    \"about\": \"Anim irure magna irure eiusmod deserunt in. Esse qui eu commodo adipisicing elit magna minim aliquip. Aliquip velit occaecat excepteur exercitation anim. Officia reprehenderit laboris est deserunt.\\r\\n\",\n" +
            "    \"registered\": \"2018-11-14T11:22:58 -08:00\",\n" +
            "    \"latitude\": 89.880848,\n" +
            "    \"longitude\": -174.703971,\n" +
            "    \"tags\": [\n" +
            "      \"sit\",\n" +
            "      \"et\",\n" +
            "      \"qui\",\n" +
            "      \"labore\",\n" +
            "      \"aliquip\",\n" +
            "      \"enim\",\n" +
            "      \"incididunt\"\n" +
            "    ],\n" +
            "    \"friends\": [\n" +
            "      {\n" +
            "        \"id\": 0,\n" +
            "        \"name\": \"Iris Gutierrez\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Susan Curtis\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"name\": \"Franco Moon\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"greeting\": \"Hello, Blanca Talley! You have 8 unread messages.\",\n" +
            "    \"favoriteFruit\": \"apple\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"_id\": \"5d49bb15c40a6fbda7f6191f\",\n" +
            "    \"index\": 4,\n" +
            "    \"guid\": \"e895f94d-8662-405d-a7da-157ff87977a8\",\n" +
            "    \"isActive\": true,\n" +
            "    \"balance\": \"$1,019.61\",\n" +
            "    \"picture\": \"http://placehold.it/32x32\",\n" +
            "    \"age\": 28,\n" +
            "    \"eyeColor\": \"blue\",\n" +
            "    \"name\": \"Wyatt Barnett\",\n" +
            "    \"gender\": \"male\",\n" +
            "    \"company\": \"STEELFAB\",\n" +
            "    \"email\": \"wyattbarnett@steelfab.com\",\n" +
            "    \"phone\": \"+1 (824) 509-3652\",\n" +
            "    \"address\": \"357 Village Road, Deputy, Nebraska, 9145\",\n" +
            "    \"about\": \"Ut sunt proident minim dolor dolore duis cupidatat ullamco ut sunt voluptate adipisicing tempor deserunt. Dolore eu ad non amet officia. Minim ullamco nisi proident laborum enim esse sit magna ullamco mollit officia excepteur.\\r\\n\",\n" +
            "    \"registered\": \"2014-05-31T09:29:39 -08:00\",\n" +
            "    \"latitude\": 9.619312,\n" +
            "    \"longitude\": 78.440152,\n" +
            "    \"tags\": [\n" +
            "      \"ad\",\n" +
            "      \"eiusmod\",\n" +
            "      \"irure\",\n" +
            "      \"labore\",\n" +
            "      \"esse\",\n" +
            "      \"ullamco\",\n" +
            "      \"cillum\"\n" +
            "    ],\n" +
            "    \"friends\": [\n" +
            "      {\n" +
            "        \"id\": 0,\n" +
            "        \"name\": \"Avery Brown\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"name\": \"Isabel Davidson\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 2,\n" +
            "        \"name\": \"Oneil Acosta\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"greeting\": \"Hello, Wyatt Barnett! You have 4 unread messages.\",\n" +
            "    \"favoriteFruit\": \"strawberry\"\n" +
            "  }\n" +
            "]";
        // stringify by nodejs v8.11.3
        String oneLine = "[{\"_id\":\"5d49bb155d8e0f9ccbe21356\",\"index\":0,\"guid\":\"4957233d-8526-4949-9498-2d6644ee1a67\",\"isActive\":false,\"balance\":\"$3,286.34\",\"picture\":\"http://placehold.it/32x32\",\"age\":33,\"eyeColor\":\"blue\",\"name\":\"Robyn Moody\",\"gender\":\"female\",\"company\":\"INSOURCE\",\"email\":\"robynmoody@insource.com\",\"phone\":\"+1 (824) 452-2508\",\"address\":\"728 Ide Court, Sisquoc, Alaska, 8569\",\"about\":\"Non laboris eu duis ad voluptate eiusmod ipsum do. Aute esse irure cillum veniam minim minim nisi dolor sit qui labore deserunt elit. Sint adipisicing deserunt sint eiusmod fugiat voluptate laborum nostrud anim dolor do.\\r\\n\",\"registered\":\"2016-12-27T09:40:55 -08:00\",\"latitude\":-41.779924,\"longitude\":-13.753001,\"tags\":[\"laborum\",\"nisi\",\"elit\",\"Lorem\",\"mollit\",\"exercitation\",\"minim\"],\"friends\":[{\"id\":0,\"name\":\"Sondra English\"},{\"id\":1,\"name\":\"Neal Vaughan\"},{\"id\":2,\"name\":\"Porter Durham\"}],\"greeting\":\"Hello, Robyn Moody! You have 9 unread messages.\",\"favoriteFruit\":\"banana\"},{\"_id\":\"5d49bb155b7fbc211d88ce5a\",\"index\":1,\"guid\":\"568a54c2-c837-4950-8835-0c2a45a4d7ab\",\"isActive\":false,\"balance\":\"$2,436.59\",\"picture\":\"http://placehold.it/32x32\",\"age\":39,\"eyeColor\":\"brown\",\"name\":\"Danielle Stokes\",\"gender\":\"female\",\"company\":\"BEDDER\",\"email\":\"daniellestokes@bedder.com\",\"phone\":\"+1 (816) 500-3218\",\"address\":\"335 Drew Street, Avoca, Texas, 9518\",\"about\":\"Labore duis exercitation proident exercitation. Nostrud do eu culpa eiusmod nisi ipsum labore velit. Sunt sint aute excepteur nulla velit consectetur proident anim officia. Ad culpa sit elit Lorem laboris laborum Lorem amet exercitation ad incididunt in excepteur commodo.\\r\\n\",\"registered\":\"2018-04-04T12:33:35 -08:00\",\"latitude\":49.795819,\"longitude\":-87.54978,\"tags\":[\"ad\",\"est\",\"do\",\"amet\",\"et\",\"velit\",\"aliqua\"],\"friends\":[{\"id\":0,\"name\":\"Bass Mcclure\"},{\"id\":1,\"name\":\"Byers Baird\"},{\"id\":2,\"name\":\"Elsa Thornton\"}],\"greeting\":\"Hello, Danielle Stokes! You have 8 unread messages.\",\"favoriteFruit\":\"banana\"},{\"_id\":\"5d49bb15c4dcd05807c49712\",\"index\":2,\"guid\":\"af17f570-f684-4361-8b6a-740fa717638d\",\"isActive\":false,\"balance\":\"$1,971.71\",\"picture\":\"http://placehold.it/32x32\",\"age\":29,\"eyeColor\":\"brown\",\"name\":\"Ofelia Pearson\",\"gender\":\"female\",\"company\":\"SONIQUE\",\"email\":\"ofeliapearson@sonique.com\",\"phone\":\"+1 (808) 580-3320\",\"address\":\"133 Clifton Place, Saranap, North Dakota, 1882\",\"about\":\"Eu sit ullamco nostrud excepteur aliquip nostrud esse sunt nostrud enim labore eiusmod fugiat. Cillum ea dolor est laborum duis aliqua eu eiusmod esse ipsum in qui. Cupidatat proident ipsum veniam pariatur ad laboris. Exercitation anim sunt nulla nostrud duis aute labore nulla quis pariatur. Minim cillum incididunt quis et eu eiusmod dolor reprehenderit ullamco excepteur. Consequat aute velit ut deserunt cupidatat esse eiusmod occaecat veniam elit ex.\\r\\n\",\"registered\":\"2015-06-15T03:27:45 -08:00\",\"latitude\":-66.190943,\"longitude\":-179.18901,\"tags\":[\"excepteur\",\"duis\",\"Lorem\",\"duis\",\"aute\",\"esse\",\"cupidatat\"],\"friends\":[{\"id\":0,\"name\":\"Walsh Cole\"},{\"id\":1,\"name\":\"Howe Gonzalez\"},{\"id\":2,\"name\":\"Dona Robinson\"}],\"greeting\":\"Hello, Ofelia Pearson! You have 4 unread messages.\",\"favoriteFruit\":\"strawberry\"},{\"_id\":\"5d49bb15a7cf7c40cb8d8f39\",\"index\":3,\"guid\":\"9a132e7d-81a4-4082-93a8-61e5c1a112a4\",\"isActive\":false,\"balance\":\"$1,176.81\",\"picture\":\"http://placehold.it/32x32\",\"age\":36,\"eyeColor\":\"brown\",\"name\":\"Blanca Talley\",\"gender\":\"female\",\"company\":\"NAMEGEN\",\"email\":\"blancatalley@namegen.com\",\"phone\":\"+1 (941) 555-3324\",\"address\":\"624 Bartlett Place, Barstow, Marshall Islands, 1439\",\"about\":\"Anim irure magna irure eiusmod deserunt in. Esse qui eu commodo adipisicing elit magna minim aliquip. Aliquip velit occaecat excepteur exercitation anim. Officia reprehenderit laboris est deserunt.\\r\\n\",\"registered\":\"2018-11-14T11:22:58 -08:00\",\"latitude\":89.880848,\"longitude\":-174.703971,\"tags\":[\"sit\",\"et\",\"qui\",\"labore\",\"aliquip\",\"enim\",\"incididunt\"],\"friends\":[{\"id\":0,\"name\":\"Iris Gutierrez\"},{\"id\":1,\"name\":\"Susan Curtis\"},{\"id\":2,\"name\":\"Franco Moon\"}],\"greeting\":\"Hello, Blanca Talley! You have 8 unread messages.\",\"favoriteFruit\":\"apple\"},{\"_id\":\"5d49bb15c40a6fbda7f6191f\",\"index\":4,\"guid\":\"e895f94d-8662-405d-a7da-157ff87977a8\",\"isActive\":true,\"balance\":\"$1,019.61\",\"picture\":\"http://placehold.it/32x32\",\"age\":28,\"eyeColor\":\"blue\",\"name\":\"Wyatt Barnett\",\"gender\":\"male\",\"company\":\"STEELFAB\",\"email\":\"wyattbarnett@steelfab.com\",\"phone\":\"+1 (824) 509-3652\",\"address\":\"357 Village Road, Deputy, Nebraska, 9145\",\"about\":\"Ut sunt proident minim dolor dolore duis cupidatat ullamco ut sunt voluptate adipisicing tempor deserunt. Dolore eu ad non amet officia. Minim ullamco nisi proident laborum enim esse sit magna ullamco mollit officia excepteur.\\r\\n\",\"registered\":\"2014-05-31T09:29:39 -08:00\",\"latitude\":9.619312,\"longitude\":78.440152,\"tags\":[\"ad\",\"eiusmod\",\"irure\",\"labore\",\"esse\",\"ullamco\",\"cillum\"],\"friends\":[{\"id\":0,\"name\":\"Avery Brown\"},{\"id\":1,\"name\":\"Isabel Davidson\"},{\"id\":2,\"name\":\"Oneil Acosta\"}],\"greeting\":\"Hello, Wyatt Barnett! You have 4 unread messages.\",\"favoriteFruit\":\"strawberry\"}]";
        assertEquals(oneLine, JSON.parse(json).stringify());
        assertEquals(oneLine, JSON.parse(oneLine).stringify());
    }

    @Test
    public void utils() throws Exception {
        CharStream cs = CharStream.from("truefalse");
        JSON.Bool bool = (JSON.Bool) ParserUtils.buildFrom(cs, new ParserOptions().setEnd(false));
        assertTrue(bool.booleanValue());
        assertEquals("CharStream(tru[e]false)", cs.toString());
        bool = (JSON.Bool) ParserUtils.buildFrom(cs, new ParserOptions().setEnd(true));
        assertFalse(bool.booleanValue());
    }
}
