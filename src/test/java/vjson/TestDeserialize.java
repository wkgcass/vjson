package vjson;

import kotlin.jvm.internal.Reflection;
import org.junit.Test;
import vjson.deserializer.DeserializeParserListener;
import vjson.deserializer.rule.*;
import vjson.parser.ArrayParser;
import vjson.parser.ParserMode;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;
import vjson.simple.SimpleObject;
import vjson.util.ArrayBuilder;
import vjson.util.ComposedObjectCase;
import vjson.util.ObjectBuilder;
import vjson.util.SimpleObjectCase;
import vjson.util.typerule.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    public void objectEntryWithoutValue() {
        String jsonStr = "{" +
            "\"nullValue\"" +
            "}";
        SimpleObjectCase o = new SimpleObjectCase();
        o.nullValue = null;
        DeserializeParserListener<SimpleObjectCase> lsn = new DeserializeParserListener<>(SimpleObjectCase.simpleObjectCaseRule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions()
            .setAllowObjectEntryWithoutValue(true)
            .setMode(ParserMode.JAVA_OBJECT).setNullArraysAndObjects(true).setListener(lsn));
        assertTrue(lsn.completed());
        assertEquals(o, lsn.get());
    }

    @Test
    public void simpleArrays() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 10; ++i) {
            {
                List<Integer> ls = Arrays.asList(random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, IntRule.get()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Long> ls = Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong(), random.nextLong());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, LongRule.get()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Double> ls = Arrays.asList(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, DoubleRule.get()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<Boolean> ls = Arrays.asList(random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, BoolRule.get()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<String> ls = Arrays.asList(randomString(), randomString(), randomString(), randomString());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, StringRule.get()), ls,
                    new ArrayBuilder().add(ls.get(0)).add(ls.get(1)).add(ls.get(2)).add(ls.get(3)).build().stringify());
            }
            {
                List<String> ls = Arrays.asList(randomString(), null, null, randomString());
                test(new ArrayRule<>(ArrayList::new, ArrayList::add, NullableStringRule.get()), ls,
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
    public void typeObject() {
        for (int i = 0; i < 10; ++i) {
            TypeRuleBase base = randomTypeRuleBase();
            TypeRuleA a = randomTypeRuleA();
            TypeRuleB b = randomTypeRuleB();
            TypeRuleC c = randomTypeRuleC();
            TypeRuleD d = randomTypeRuleD();
            TypeRuleMap map = randomTypeRuleMap();

            test(TypeRuleBase.getTypeRule(), base, getTypeRuleBaseJSON(base).stringify());
            test(TypeRuleBase.getTypeRule(), a, getTypeRuleBaseJSON(a).stringify());
            test(TypeRuleBase.getTypeRule(), b, getTypeRuleBaseJSON(b).stringify());
            test(TypeRuleBase.getTypeRule(), c, getTypeRuleBaseJSON(c).stringify());
            test(TypeRuleBase.getTypeRule(), d, getTypeRuleBaseJSON(d).stringify());
            test(TypeRuleBase.getTypeRule(), map, getTypeRuleBaseJSON(map).stringify());

            Box bbase = new Box(base);
            Box ba = new Box(a);
            Box bb = new Box(b);
            Box bc = new Box(c);
            Box bd = new Box(d);
            Box bmap = new Box(map);

            test(Box.boxRule, bbase, getBoxJSON(bbase).stringify());
            test(Box.boxRule, ba, getBoxJSON(ba).stringify());
            test(Box.boxRule, bb, getBoxJSON(bb).stringify());
            test(Box.boxRule, bc, getBoxJSON(bc).stringify());
            test(Box.boxRule, bd, getBoxJSON(bd).stringify());
            test(Box.boxRule, bmap, getBoxJSON(bmap).stringify());
        }
    }

    @Test
    public void typeObjectDefaultRule() {
        Rule<TypeRuleBase> rule = new TypeRule<>(Reflection.getOrCreateKotlinClass(TypeRuleBase.class), TypeRuleBase.baseRule);
        TypeRuleBase base = randomTypeRuleBase();
        JSON.Object o = new ObjectBuilder()
            .put("x", base.x)
            .put("y", base.y)
            .build();
        test(rule, base, o.stringify());

        base = new TypeRuleBase();
        o = new SimpleObject(Collections.emptyMap());
        test(rule, base, o.stringify());
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
        DeserializeParserListener<List<Integer>> lsn = new DeserializeParserListener<>(new ArrayRule<>(ArrayList::new, List::add, IntRule.get()));
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

    @Test
    public void deserializeWithNotRegisteredFields() {
        class A {
            public String a;
            public List<String> b;
            public int c;
        }
        class Container {
            public List<A> list;
        }
        Rule<A> ruleA = new ObjectRule<>(A::new)
            .put("a", (o, v) -> o.a = v, StringRule.get())
            .put("c", (o, v) -> o.c = v, IntRule.get());
        Rule<Container> ruleContainer = new ObjectRule<>(Container::new)
            .put("list", (o, v) -> o.list = v, new ArrayRule<>(() -> new ArrayList<A>(), ArrayList::add, ruleA));
        Container c = JSON.deserialize("{\"list\":" +
            "[" +
            "{\"a\":\"xxx\",\"c\":1,\"b\":[" +
            "\"zzz\",1,1.0,1e2,true,false,null,[1],{\"x\":\"y\"}" +
            "]}," +
            "{\"a\":\"yyy\"}" +
            "]" +
            "}", ruleContainer);
        assertEquals("xxx", c.list.get(0).a);
        assertEquals(1, c.list.get(0).c);
        assertEquals("yyy", c.list.get(1).a);
    }

    @Test
    public void deserializeWithNotRegisteredFields2() {
        String jsonStr = new ObjectBuilder()
            .putArray("xx", ab -> ab.addObject(ob -> ob
                .put("a1111111", true)
                .put("b2222222", 1)
                .put("c3333333", ((long) Integer.MAX_VALUE) * 3)
                .put("d4444444", 1.2)
                .put("e5555555", "eee")
                .put("f6666666", null)))
            .build().stringify();
        Rule<ArrayList<SimpleObjectCase>> listRule = new ArrayRule<>(
            ArrayList::new, ArrayList::add, SimpleObjectCase.simpleObjectCaseRule
        );
        class Holder {
            List<SimpleObjectCase> ls;
        }
        Rule<Holder> holderRule = new ObjectRule<>(Holder::new)
            .put("ls", (o, v) -> o.ls = v, listRule);
        DeserializeParserListener<Holder> listener = new DeserializeParserListener<>(holderRule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setListener(listener));
        //noinspection ConstantConditions
        assertNull(listener.get().ls);
    }

    @Test
    public void deserializeWithNotRegisteredFields3() {
        class Test2 {
            int id;
        }
        class Test1 {
            Test2 user;
            String body;
        }
        Rule<Test2> rule2 = new ObjectRule<>(Test2::new)
            .put("id", (o, it) -> o.id = it, IntRule.get());
        Rule<Test1> rule1 = new ObjectRule<>(Test1::new)
            .put("user", (o, it) -> o.user = it, rule2)
            .put("body", (o, it) -> o.body = it, StringRule.get());

        String jsonStr = "[{\"url\":\"aaa\",\"user\":{\"login\":\"xx\",\"id\":123,\"node_id\":\"zzzz\"},\"created_at\":\"2022\",\"body\":\"bbbb\",\"reactions\":{\"url\":\"uuu\"},\"perform\": null}]";
        Rule<List<Test1>> holderRule = new ArrayRule<>(
            ArrayList::new, List::add, rule1
        );
        DeserializeParserListener<List<Test1>> listener = new DeserializeParserListener<>(holderRule);
        ParserUtils.buildFrom(CharStream.from(jsonStr), new ParserOptions().setListener(listener));
        List<Test1> o = listener.get();
        assertNotNull(o);
        assertEquals(123, o.get(0).user.id);
        assertEquals("bbbb", o.get(0).body);
    }
}
