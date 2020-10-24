package vjson;

import org.junit.Test;
import vjson.deserializer.DeserializeParserListener;
import vjson.deserializer.rule.*;
import vjson.parser.ArrayParser;
import vjson.parser.ParserMode;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;
import vjson.simple.SimpleNull;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;

import java.util.*;

import static org.junit.Assert.*;

public class TestDeserialize {
    @SuppressWarnings({"unchecked"})
    private void test(Rule rule, Object expected, String jsonStr) {
        System.out.println("input json: " + jsonStr);

        assertEquals(expected, JSON.deserialize(jsonStr, rule));

        DeserializeParserListener lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected, lsn.get());

        lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected, lsn.get());

        lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setNullArraysAndObjects(true).setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected, lsn.get());

        System.out.println("deserialize result: " + lsn.get());
    }

    @SuppressWarnings("unchecked")
    private void test(Rule rule, List<Double> expected, String jsonStr) {
        DeserializeParserListener lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected.size(), ((List) lsn.get()).size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), (double) ((List) lsn.get()).get(i), 0.0000000001);
        }

        lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected.size(), ((List) lsn.get()).size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), (double) ((List) lsn.get()).get(i), 0.0000000001);
        }

        lsn = new DeserializeParserListener(rule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setMode(ParserMode.JAVA_OBJECT).setNullArraysAndObjects(true).setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(expected.size(), ((List) lsn.get()).size());
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), (double) ((List) lsn.get()).get(i), 0.0000000001);
        }
    }

    static class SimpleObjectCase {
        public int intValue;
        public long longValue;
        public double doubleValue;
        public boolean boolValue;
        public String stringValue;
        public Object nullValue;

        public SimpleObjectCase() {
        }

        public SimpleObjectCase(int intValue, long longValue, double doubleValue, boolean boolValue, String stringValue, Object nullValue) {
            this.intValue = intValue;
            this.longValue = longValue;
            this.doubleValue = doubleValue;
            this.boolValue = boolValue;
            this.stringValue = stringValue;
            this.nullValue = nullValue;
        }

        public SimpleObjectCase(SimpleObjectCase o) {
            this(o.intValue, o.longValue, o.doubleValue, o.boolValue, o.stringValue, o.nullValue);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SimpleObjectCase that = (SimpleObjectCase) o;
            return intValue == that.intValue &&
                longValue == that.longValue &&
                Math.abs(that.doubleValue - doubleValue) < 0.0000000001 &&
                boolValue == that.boolValue &&
                Objects.equals(stringValue, that.stringValue) &&
                Objects.equals(nullValue, that.nullValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(intValue, longValue, doubleValue, boolValue, stringValue, nullValue);
        }

        @Override
        public String toString() {
            return "SimpleObjectCase{" +
                "intValue=" + intValue +
                ", longValue=" + longValue +
                ", doubleValue=" + doubleValue +
                ", boolValue=" + boolValue +
                ", stringValue='" + stringValue + '\'' +
                ", nullValue=" + nullValue +
                '}';
        }
    }

    static final ObjectRule<SimpleObjectCase> simpleObjectCaseRule = new ObjectRule<>(SimpleObjectCase::new)
        .put("intValue", (o, v) -> o.intValue = v, new IntRule())
        .put("longValue", (o, v) -> o.longValue = v, new LongRule())
        .put("doubleValue", (o, v) -> o.doubleValue = v, new DoubleRule())
        .put("boolValue", (o, v) -> o.boolValue = v, new BoolRule())
        .put("stringValue", (o, v) -> o.stringValue = v, new StringRule())
        .put("nullValue", (o, v) -> o.nullValue = v, new NullableStringRule());

    private final Random random = new Random();

    private String randomString() {
        int len = random.nextInt(10) + 5;
        char[] c = new char[len];
        for (int i = 0; i < c.length; ++i) {
            c[i] = (char) (random.nextInt('z' - 'a' + 1) + 'a');
        }
        return new String(c);
    }

    private SimpleObjectCase randomSimpleObjectCase() {
        return new SimpleObjectCase(random.nextInt(), random.nextLong(), random.nextDouble(), random.nextBoolean(), randomString(), null);
    }

    private ObjectBuilder getSimpleObjectCaseObjectBuilder(SimpleObjectCase o) {
        return new ObjectBuilder()
            .put("intValue", o.intValue)
            .put("longValue", o.longValue)
            .put("doubleValue", o.doubleValue)
            .put("boolValue", o.boolValue)
            .put("stringValue", o.stringValue)
            .put("nullValue", null);
    }

    private JSON.Instance getSimpleObjectCaseJSON(SimpleObjectCase o) {
        if (o == null) {
            return new SimpleNull();
        }
        return getSimpleObjectCaseObjectBuilder(o).build();
    }

    private List<SimpleObjectCase> randomSimpleObjectCaseList() {
        int len = random.nextInt(3) + 1;
        List<SimpleObjectCase> ret = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            ret.add(randomSimpleObjectCase());
        }
        return ret;
    }

    private JSON.Instance getSimpleObjectCaseListJSON(List<SimpleObjectCase> ls) {
        if (ls == null) {
            return new SimpleNull();
        }
        Iterator<SimpleObjectCase> ite = ls.iterator();
        ArrayBuilder ret = new ArrayBuilder();
        while (ite.hasNext()) {
            SimpleObjectCase simple = ite.next();
            ret.addInst(getSimpleObjectCaseJSON(simple));
        }
        return ret.build();
    }

    @Test
    public void simpleObject() {
        for (int i = 0; i < 10; ++i) {
            SimpleObjectCase o = randomSimpleObjectCase();
            test(simpleObjectCaseRule, o, getSimpleObjectCaseJSON(o).stringify());
        }
    }

    @Test
    public void simpleArrays() {
        for (int i = 0; i < 10; ++i) {
            {
                List<Integer> ls = Arrays.asList(random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new IntRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Long> ls = Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new LongRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Double> ls = Arrays.asList(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new DoubleRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Boolean> ls = Arrays.asList(random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new BoolRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<String> ls = Arrays.asList(randomString(), randomString(), randomString(), randomString());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new StringRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<String> ls = Arrays.asList(randomString(), null, null, randomString());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, new NullableStringRule()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
        }
    }

    static class ComposedObjectCase extends SimpleObjectCase {
        public SimpleObjectCase object1;
        public SimpleObjectCase object2;
        public SimpleObjectCase nullObject;
        public List<SimpleObjectCase> array1;
        public List<SimpleObjectCase> array2;
        public List<SimpleObjectCase> nullArray;
        public List<SimpleObjectCase> arrayWithNull;
        public ComposedObjectCase nullableMoreObject;
        public List<ComposedObjectCase> nullableMoreList;

        public ComposedObjectCase() {
        }

        public ComposedObjectCase(SimpleObjectCase o,
                                  SimpleObjectCase object1,
                                  SimpleObjectCase object2,
                                  SimpleObjectCase nullObject,
                                  List<SimpleObjectCase> array1,
                                  List<SimpleObjectCase> array2,
                                  List<SimpleObjectCase> nullArray,
                                  List<SimpleObjectCase> arrayWithNull,
                                  ComposedObjectCase nullableMoreObject,
                                  List<ComposedObjectCase> nullableMoreList) {
            super(o);
            this.object1 = object1;
            this.object2 = object2;
            this.nullObject = nullObject;
            this.array1 = array1;
            this.array2 = array2;
            this.nullArray = nullArray;
            this.arrayWithNull = arrayWithNull;
            this.nullableMoreObject = nullableMoreObject;
            this.nullableMoreList = nullableMoreList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            ComposedObjectCase that = (ComposedObjectCase) o;
            return Objects.equals(object1, that.object1) &&
                Objects.equals(object2, that.object2) &&
                Objects.equals(nullObject, that.nullObject) &&
                Objects.equals(array1, that.array1) &&
                Objects.equals(array2, that.array2) &&
                Objects.equals(nullArray, that.nullArray) &&
                Objects.equals(arrayWithNull, that.arrayWithNull) &&
                Objects.equals(nullableMoreObject, that.nullableMoreObject) &&
                Objects.equals(nullableMoreList, that.nullableMoreList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), object1, object2, nullObject, array1, array2, nullArray, arrayWithNull, nullableMoreObject, nullableMoreList);
        }

        @Override
        public String toString() {
            return "ComposedObjectCase{" +
                "intValue=" + intValue +
                ", longValue=" + longValue +
                ", doubleValue=" + doubleValue +
                ", boolValue=" + boolValue +
                ", stringValue='" + stringValue + '\'' +
                ", nullValue=" + nullValue +
                ", object1=" + object1 +
                ", object2=" + object2 +
                ", nullObject=" + nullObject +
                ", array1=" + array1 +
                ", array2=" + array2 +
                ", nullArray=" + nullArray +
                ", arrayWithNull=" + arrayWithNull +
                ", nullableMoreObject=" + nullableMoreObject +
                ", nullableMoreList=" + nullableMoreList +
                '}';
        }
    }

    static final ObjectRule<ComposedObjectCase> composedObjectCaseRule = new ObjectRule<>(ComposedObjectCase::new, simpleObjectCaseRule);

    static {
        composedObjectCaseRule
            .put("object1", (o, v) -> o.object1 = v, simpleObjectCaseRule)
            .put("object2", (o, v) -> o.object2 = v, simpleObjectCaseRule)
            .put("nullObject", (o, v) -> o.nullObject = v, simpleObjectCaseRule)
            .put("array1", (o, v) -> o.array1 = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, simpleObjectCaseRule))
            .put("array2", (o, v) -> o.array2 = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, simpleObjectCaseRule))
            .put("nullArray", (o, v) -> o.nullArray = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, simpleObjectCaseRule))
            .put("arrayWithNull", (o, v) -> o.arrayWithNull = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, simpleObjectCaseRule))
            .put("nullableMoreObject", (o, v) -> o.nullableMoreObject = v, composedObjectCaseRule)
            .put("nullableMoreList", (o, v) -> o.nullableMoreList = v, new ArrayRule<List<ComposedObjectCase>, ComposedObjectCase>(ArrayList::new, List::add, composedObjectCaseRule));
    }

    private ComposedObjectCase randomComposedObjectCase(int recurse) {
        if (recurse >= 5) {
            return null;
        }
        if (recurse >= 3) {
            if (random.nextDouble() < 0.5) {
                return null;
            }
        }
        return new ComposedObjectCase(randomSimpleObjectCase(),
            randomSimpleObjectCase(), randomSimpleObjectCase(), null,
            randomSimpleObjectCaseList(), randomSimpleObjectCaseList(), null, Arrays.asList(null, null),
            randomComposedObjectCase(recurse + 1), randomComposedObjectCaseList(recurse + 1));
    }

    private JSON.Instance getComposedObjectCaseJSON(ComposedObjectCase o) {
        if (o == null) {
            return new SimpleNull();
        }
        return getSimpleObjectCaseObjectBuilder(o)
            .putInst("object1", getSimpleObjectCaseJSON(o.object1))
            .putInst("object2", getSimpleObjectCaseJSON(o.object2))
            .putInst("nullObject", getSimpleObjectCaseJSON(o.nullObject))
            .putInst("array1", getSimpleObjectCaseListJSON(o.array1))
            .putInst("array2", getSimpleObjectCaseListJSON(o.array2))
            .putInst("nullArray", getSimpleObjectCaseListJSON(o.nullArray))
            .putInst("arrayWithNull", getSimpleObjectCaseListJSON(o.arrayWithNull))
            .putInst("nullableMoreObject", getComposedObjectCaseJSON(o.nullableMoreObject))
            .putInst("nullableMoreList", getComposedObjectCaseListJSON(o.nullableMoreList))
            .build();
    }

    private List<ComposedObjectCase> randomComposedObjectCaseList(int recurse) {
        if (recurse >= 5) {
            return null;
        }
        if (recurse >= 3) {
            if (random.nextDouble() < 0.5) {
                return null;
            }
        }
        int len = random.nextInt(3) + 1;
        List<ComposedObjectCase> ret = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            ret.add(randomComposedObjectCase(recurse + 1));
        }
        return ret;
    }

    private JSON.Instance getComposedObjectCaseListJSON(List<ComposedObjectCase> ls) {
        if (ls == null) {
            return new SimpleNull();
        }
        Iterator<ComposedObjectCase> ite = ls.iterator();
        ArrayBuilder builder = new ArrayBuilder();
        while (ite.hasNext()) {
            ComposedObjectCase o = ite.next();
            builder.addInst(getComposedObjectCaseJSON(o));
        }
        return builder.build();
    }

    @Test
    public void composedObject() {
        for (int i = 0; i < 10; ++i) {
            ComposedObjectCase o = randomComposedObjectCase(1);
            test(composedObjectCaseRule, o, getComposedObjectCaseJSON(o).stringify());
        }
    }

    @Test
    public void composedArray() {
        for (int i = 0; i < 10; ++i) {
            List<ComposedObjectCase> o = randomComposedObjectCaseList(1);
            test(new ArrayRule<>(ArrayList::new, ArrayList::add, composedObjectCaseRule),
                o, getComposedObjectCaseListJSON(o).stringify());
        }
    }

    @Test
    public void deserializeParserListenerTakeMultiCharStreamToComplete() throws Exception {
        DeserializeParserListener<List<Integer>> lsn = new DeserializeParserListener<>(new ArrayRule<>(ArrayList::new, List::add, new IntRule()));
        assertFalse(lsn.completed());

        ArrayParser ap = new ArrayParser(new ParserOptions().setListener(lsn));
        ap.build(CharStream.from("[1,"), false);
        assertFalse(lsn.completed());
        try {
            lsn.get();
        } catch (IllegalStateException ignore) {
        }

        ap.build(CharStream.from("2]"), true);
        assertTrue(lsn.completed());
        assertEquals(Arrays.asList(1, 2), lsn.get());
    }
}
