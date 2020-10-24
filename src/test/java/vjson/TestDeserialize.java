package vjson;

import org.junit.Test;
import vjson.deserializer.DeserializeParserListener;
import vjson.deserializer.rule.*;
import vjson.parser.*;
import vjson.util.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;
import static vjson.util.TestCaseUtils.*;

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

    @Test
    public void simpleObject() {
        for (int i = 0; i < 10; ++i) {
            SimpleObjectCase o = randomSimpleObjectCase();
            test(SimpleObjectCase.simpleObjectCaseRule, o, getSimpleObjectCaseJSON(o).stringify());
        }
    }

    @Test
    public void simpleArrays() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
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

    @Test
    public void composedObject() {
        for (int i = 0; i < 10; ++i) {
            ComposedObjectCase o = randomComposedObjectCase(1);
            test(ComposedObjectCase.composedObjectCaseRule, o, getComposedObjectCaseJSON(o).stringify());
        }
    }

    @Test
    public void composedArray() {
        for (int i = 0; i < 10; ++i) {
            List<ComposedObjectCase> o = randomComposedObjectCaseList(1);
            test(new ArrayRule<>(ArrayList::new, ArrayList::add, ComposedObjectCase.composedObjectCaseRule),
                o, getComposedObjectCaseListJSON(o).stringify());
        }
    }

    @Test
    public void deserializeParserListenerTakeMultiCharStreamToComplete() {
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
