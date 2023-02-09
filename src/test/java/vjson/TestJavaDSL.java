package vjson;

import kotlin.jvm.internal.Reflection;
import org.junit.Test;
import vjson.cs.LineCol;
import vjson.deserializer.rule.*;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleInteger;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class TestJavaDSL {
    static class Xxx {
        private final int x;
        private final String y;
        private final Aaa z;

        Xxx(int x, String y, Aaa z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        protected ObjectBuilder toJsonObject(Consumer<ObjectBuilder> f) {
            ObjectBuilder builder = new ObjectBuilder()
                .put("x", x)
                .put("y", y)
                .putInst("z", z.toJson());
            f.accept(builder);
            return builder;
        }

        JSON.Object toJson() {
            return toJsonObject(x -> {
            }).build();
        }
    }

    static class XxxBuilder {
        int x = 0;
        String y = "";
        Aaa z = new Aaa();

        public void setX(int x) {
            this.x = x;
        }

        public void setY(String y) {
            this.y = y;
        }

        public void setZ(Aaa z) {
            this.z = z;
        }

        Xxx build() {
            return new Xxx(x, y, z);
        }
    }

    static class Yyy extends Xxx {
        private final long a;

        Yyy(int x, String y, Aaa z, long a) {
            super(x, y, z);
            this.a = a;
        }

        @Override
        JSON.Object toJson() {
            return toJsonObject(o -> o.put("a", a))
                .build();
        }

        JSON.Object toJson2() {
            return new ObjectBuilder(super.toJson())
                .put("a", a)
                .build();
        }
    }

    static class YyyBuilder extends XxxBuilder {
        private long a = 0;

        public void setA(long a) {
            this.a = a;
        }

        Yyy build() {
            return new Yyy(x, y, z, a);
        }
    }

    static class Aaa implements JSONObject {
        private int a = 0;
        private String b = "";

        public Aaa() {
        }

        public Aaa(int a, String b) {
            this.a = a;
            this.b = b;
        }

        public void setA(int a) {
            this.a = a;
        }

        public void setB(String b) {
            this.b = b;
        }

        protected ObjectBuilder toJsonObject(Consumer<ObjectBuilder> f) {
            ObjectBuilder builder = new ObjectBuilder()
                .type("aaa")
                .put("a", a)
                .put("b", b);
            f.accept(builder);
            return builder;
        }

        @Override
        public JSON.Object toJson() {
            return toJsonObject(x -> {
            }).build();
        }
    }

    static class Bbb extends Aaa {
        private long c = 0;

        public Bbb() {
        }

        public Bbb(int a, String b, long c) {
            super(a, b);
            this.c = c;
        }

        public void setC(long c) {
            this.c = c;
        }

        @Override
        public JSON.Object toJson() {
            return toJsonObject(x -> x
                .type(Reflection.getOrCreateKotlinClass(Bbb.class))
                .put("c", c)).build();
        }
    }

    static class Mmm {
        private int m;
        private String n = "";
        List<Aaa> o = Collections.emptyList();

        public Mmm() {
        }

        public Mmm(int m, String n, List<Aaa> o) {
            this.m = m;
            this.n = n;
            this.o = o;
        }

        public void setM(int m) {
            this.m = m;
        }

        public void setN(String n) {
            this.n = n;
        }

        public void setO(List<Aaa> o) {
            this.o = o;
        }

        JSON.Object toJson() {
            return new ObjectBuilder()
                .put("m", m)
                .put("n", n)
                .putInst("o", JSONObject.listToJson(o))
                .build();
        }
    }

    static class Ls<E> {
        private final ArrayList<E> ls = new ArrayList<>();

        void add(E e) {
            ls.add(e);
        }

        List<E> build() {
            return ls;
        }
    }

    private final ObjectRule<Aaa> aaaRule = new ObjectRule<>(Aaa::new)
        .put("a", Aaa::setA, IntRule.get())
        .put("b", Aaa::setB, StringRule.get());

    private final ObjectRule<Bbb> bbbRule = new ObjectRule<>(Bbb::new, aaaRule)
        .put("c", Bbb::setC, LongRule.get());

    @SuppressWarnings("unchecked")
    private final TypeRule<Aaa> aaaTypeRule = new TypeRule<Aaa>()
        .type("aaa", aaaRule)
        .type(Reflection.getOrCreateKotlinClass(Bbb.class), bbbRule);

    private final ObjectRule.BuilderRule<Xxx> xxxRule = ObjectRule.builder(XxxBuilder::new, XxxBuilder::build,
        it -> it
            .put("x", XxxBuilder::setX, IntRule.get())
            .put("y", XxxBuilder::setY, StringRule.get())
            .put("z", XxxBuilder::setZ, aaaRule)
    );

    private final ObjectRule.BuilderRule<Yyy> yyyRule = ObjectRule.builder(YyyBuilder::new, xxxRule, YyyBuilder::build,
        it -> it
            .put("a", YyyBuilder::setA, LongRule.get()));

    private final ArrayRule<List<Aaa>, Aaa> lsRule = JSONObject.buildArrayRule(aaaTypeRule);

    private final ArrayRule<List<Aaa>, Aaa> lsBRule = ArrayRule.<List<Aaa>, Ls<Aaa>, Aaa>builder(Ls::new, Ls::build, Ls::add, aaaTypeRule);

    private final ObjectRule<Mmm> mmmRule = new ObjectRule<>(Mmm::new)
        .put("m", Mmm::setM, IntRule.get())
        .put("n", Mmm::setN, StringRule.get())
        .put("o", Mmm::setO, lsRule);

    private final ObjectRule<Mmm> mmmBRule = new ObjectRule<>(Mmm::new)
        .put("m", Mmm::setM, IntRule.get())
        .put("n", Mmm::setN, StringRule.get())
        .put("o", Mmm::setO, lsBRule);


    @Test
    public void simpleObject() {
        Aaa aaa = new Aaa(1, "2");
        Aaa aaa2 = JSON.deserialize(aaa.toJson().stringify(), aaaRule);
        assertEquals(aaa.toJson(), aaa2.toJson());
    }

    @Test
    public void inherit() {
        Bbb bbb = new Bbb(1, "2", 3);
        Bbb bbb2 = JSON.deserialize(bbb.toJson().stringify(), bbbRule);
        assertEquals(bbb.toJson(), bbb2.toJson());
    }

    @Test
    public void objectBuilder() {
        Xxx xxx = new Xxx(1, "2", new Aaa(3, "4"));
        Xxx xxx2 = JSON.deserialize(xxx.toJson().stringify(), xxxRule);
        assertEquals(xxx.toJson(), xxx2.toJson());
    }

    @Test
    public void builderInherit() {
        Yyy yyy = new Yyy(1, "2", new Aaa(3, "4"), 5);
        Yyy yyy2 = JSON.deserialize(yyy.toJson().stringify(), yyyRule);
        assertEquals(yyy.toJson(), yyy2.toJson());
        assertEquals(yyy.toJson2(), yyy2.toJson2());
        assertEquals(yyy.toJson(), yyy2.toJson2());
        assertEquals(yyy.toJson2(), yyy2.toJson());
    }

    @Test
    public void simpleArray() {
        Mmm mmm = new Mmm(1, "2", Arrays.asList(new Bbb(4, "5", 6), new Bbb(7, "8", 9), new Aaa(10, "11"), new Aaa(12, "13")));
        Mmm mmm2 = JSON.deserialize(mmm.toJson().stringify(), mmmRule);
        assertEquals(mmm.toJson(), mmm2.toJson());
    }

    @Test
    public void arrayBuilder() {
        Mmm mmm = new Mmm(1, "2", Arrays.asList(new Bbb(4, "5", 6), new Bbb(7, "8", 9), new Aaa(10, "11"), new Aaa(12, "13")));
        Mmm mmm2 = JSON.deserialize(mmm.toJson().stringify(), mmmBRule);
        assertEquals(mmm.toJson(), mmm2.toJson());
    }

    @Test
    public void useSimpleArrayDirectlyWithLineCol() {
        SimpleArray arr = new SimpleArray(new LineCol("", 0, 0), new SimpleInteger(1), new SimpleInteger(2), new SimpleInteger(3));
        assertEquals(new ArrayBuilder().add(1).add(2).add(3).build(), arr);
    }
}
