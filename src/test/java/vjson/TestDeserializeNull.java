package vjson;

import org.junit.Test;
import vjson.deserializer.rule.*;
import vjson.util.ArrayBuilder;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestDeserializeNull {
    private static class A {
        private boolean aBool;
        private double aDouble;
        private int anInt;
        private long aLong;
        private String string;

        static final Rule<A> rule = new ObjectRule<>(A::new)
            .put("aBool", (o, v) -> o.aBool = v, NullAsFalseBoolRule.get())
            .put("aDouble", (o, v) -> o.aDouble = v, NullAsZeroDoubleRule.get())
            .put("anInt", (o, v) -> o.anInt = v, NullAsZeroIntRule.get())
            .put("aLong", (o, v) -> o.aLong = v, NullAsZeroLongRule.get())
            .put("string", (o, v) -> o.string = v, NullableStringRule.get());
        static final Rule<A> customRule = new ObjectRule<>(A::new)
            .put("aBool", (o, v) -> o.aBool = v, new NullableRule<>(BoolRule.get(), () -> true))
            .put("aDouble", (o, v) -> o.aDouble = v, new NullableRule<>(DoubleRule.get(), () -> 1.2))
            .put("anInt", (o, v) -> o.anInt = v, new NullableRule<>(IntRule.get(), () -> 3))
            .put("aLong", (o, v) -> o.aLong = v, new NullableRule<>(LongRule.get(), () -> 4L))
            .put("string", (o, v) -> o.string = v, new NullableRule<>(StringRule.get(), () -> "abc"));

        public JSON.Object toJson() {
            return new ObjectBuilder()
                .put("aBool", aBool)
                .put("aDouble", aDouble)
                .put("anInt", anInt)
                .put("aLong", aLong)
                .put("string", string)
                .build();
        }

        @Override
        public String toString() {
            return toJson().stringify();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            if (aBool != a.aBool) return false;
            if (Double.compare(a.aDouble, aDouble) != 0) return false;
            if (anInt != a.anInt) return false;
            if (aLong != a.aLong) return false;
            return string != null ? string.equals(a.string) : a.string == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = (aBool ? 1 : 0);
            temp = Double.doubleToLongBits(aDouble);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + anInt;
            result = 31 * result + (int) (aLong ^ (aLong >>> 32));
            result = 31 * result + (string != null ? string.hashCode() : 0);
            return result;
        }
    }

    private static class B {
        private A a;
        private List<A> ls;

        static final Rule<B> rule = new ObjectRule<>(B::new)
            .put("a", (o, v) -> o.a = v, A.rule)
            .put("ls", (o, v) -> o.ls = v, new ArrayRule<ArrayList<A>, A>(ArrayList::new, List::add, A.rule));
        static final Rule<B> customRule = new ObjectRule<>(B::new)
            .put("a", (o, v) -> o.a = v,
                new NullableRule<>(A.customRule, () -> {
                    A a = new A();
                    a.anInt = 1234;
                    return a;
                }))
            .put("ls", (o, v) -> o.ls = v,
                new NullableRule<>(new ArrayRule<ArrayList<A>, A>(ArrayList::new, List::add, A.customRule), () -> {
                    ArrayList<A> ls = new ArrayList<>();
                    ls.add(null);
                    ls.add(new A());
                    ls.add(null);
                    return ls;
                }));

        @SuppressWarnings("ConstantConditions")
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .putNullableInst("a", a == null, () -> a.toJson())
                .putNullableInst("ls", ls == null, () ->
                    new ArrayBuilder().iterable(ls, (arr, e) -> arr.addNullableInst(e == null, () -> e.toJson())).build())
                .build();
        }

        @Override
        public String toString() {
            return toJson().stringify();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            B b = (B) o;

            if (a != null ? !a.equals(b.a) : b.a != null) return false;
            return ls != null ? ls.equals(b.ls) : b.ls == null;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (ls != null ? ls.hashCode() : 0);
            return result;
        }
    }

    @Test
    public void nullableDeserialize() {
        Rule<ArrayList<A>> ruleLs = new NullableRule<>(new ArrayRule<>(ArrayList::new, List::add, A.rule));
        assertEquals(Collections.emptyList(), JSON.deserialize("[]", ruleLs));
        assertEquals(Collections.singletonList(null), JSON.deserialize("[null]", ruleLs));

        Rule<A> ruleA = new NullableRule<>(A.rule);
        A a = new A();
        assertEquals(a, JSON.deserialize(a.toJson().stringify(), ruleA));
    }

    @Test
    public void builtInNullFieldsHandle() {
        A a = new A();
        assertEquals(a, JSON.deserialize(new ObjectBuilder()
            .put("aBool", null)
            .put("aDouble", null)
            .put("anInt", null)
            .put("aLong", null)
            .put("string", null)
            .build().stringify(), A.rule));
        B b = new B();
        assertEquals(b, JSON.deserialize(new ObjectBuilder()
            .put("a", null)
            .put("ls", null)
            .build().stringify(), B.rule));
    }

    @Test
    public void customNullFieldsHandle() {
        A a = new A();
        a.aBool = true;
        a.aDouble = 1.2;
        a.anInt = 3;
        a.aLong = 4L;
        a.string = "abc";
        assertEquals(a, JSON.deserialize(new ObjectBuilder()
            .put("aBool", null)
            .put("aDouble", null)
            .put("anInt", null)
            .put("aLong", null)
            .put("string", null)
            .build().stringify(), A.customRule));

        B b = new B();
        b.a = new A();
        b.a.anInt = 1234;
        b.ls = Arrays.asList(null, new A(), null);
        assertEquals(b, JSON.deserialize(new ObjectBuilder()
            .put("a", null)
            .put("ls", null)
            .build().stringify(), B.customRule));
    }
}
