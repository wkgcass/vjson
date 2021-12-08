package vjson;

import kotlin.jvm.internal.Reflection;
import org.junit.Test;
import vjson.deserializer.DeserializeParserListener;
import vjson.deserializer.rule.*;
import vjson.listener.EmptyParserListener;
import vjson.parser.ObjectParser;
import vjson.parser.ParserOptions;
import vjson.parser.ParserUtils;
import vjson.simple.*;
import vjson.util.*;
import vjson.util.typerule.TypeRuleA;
import vjson.util.typerule.TypeRuleBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestCorner {
    @Test
    public void numberExponent() throws Exception {
        assertEquals(JSON.parse("3e4"), JSON.parse("3E4"));
        assertEquals(JSON.parse("0e1"), JSON.parse("0E1"));
        assertEquals(JSON.parse("3.14e2"), JSON.parse("3.14E2"));
    }

    @Test
    public void parserOptions() throws Exception {
        ParserOptions opts = new ParserOptions();
        opts.setListener(new AbstractUnsupportedParserListener() {
        });
        opts.setListener(null);
        assertEquals(EmptyParserListener.INSTANCE, opts.getListener());
        opts.setBufLen(1);
        assertEquals(1, opts.getBufLen());
    }

    @Test
    public void whiteSpace() throws Exception {
        assertTrue(ParserUtils.isWhiteSpace(' '));
        assertTrue(ParserUtils.isWhiteSpace('\t'));
        assertTrue(ParserUtils.isWhiteSpace('\r'));
        assertTrue(ParserUtils.isWhiteSpace('\n'));
    }

    @Test
    public void varName() throws Exception {
        assertTrue(ParserUtils.isInitialVarName('a'));
        assertTrue(ParserUtils.isInitialVarName('z'));
        assertTrue(ParserUtils.isInitialVarName('A'));
        assertTrue(ParserUtils.isInitialVarName('Z'));
        assertTrue(ParserUtils.isInitialVarName('_'));
        assertTrue(ParserUtils.isInitialVarName('$'));
        assertTrue(ParserUtils.isVarName('a'));
        assertTrue(ParserUtils.isVarName('z'));
        assertTrue(ParserUtils.isVarName('A'));
        assertTrue(ParserUtils.isVarName('Z'));
        assertTrue(ParserUtils.isVarName('_'));
        assertTrue(ParserUtils.isVarName('$'));
        assertTrue(ParserUtils.isVarName('0'));
        assertTrue(ParserUtils.isVarName('9'));
    }

    @Test
    public void version() throws Exception {
        System.out.println("Current version is: " + VERSION.VERSION);
    }

    @Test
    public void simpleObjectEntry() throws Exception {
        SimpleObjectEntry<Integer> entry1 = new SimpleObjectEntry<>("a", 1);
        assertEquals(entry1, entry1);

        SimpleObjectEntry<Integer> entry2 = new SimpleObjectEntry<>("a", 1);
        assertEquals(entry1, entry2);

        SimpleObjectEntry<Integer> entry3 = new SimpleObjectEntry<>("b", 2);
        assertNotEquals(entry1, entry3);

        SimpleObjectEntry<Integer> entry4 = new SimpleObjectEntry<>("a", 2);
        assertNotEquals(entry1, entry4);

        SimpleObjectEntry<Integer> entry5 = new SimpleObjectEntry<>("b", 1);
        assertNotEquals(entry1, entry5);

        assertNotEquals(entry1, new Object());
        assertNotEquals(entry1, null);

        assertEquals(3007, new SimpleObjectEntry<>("a", null).hashCode());
    }

    @Test
    public void object() throws Exception {
        SimpleObject o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleInteger(1))
            .append("b", new SimpleInteger(2))
        );
        assertEquals(Collections.singletonList(new SimpleInteger(1)), o.getAll("a"));
        assertEquals(Collections.singletonList(new SimpleInteger(1)), o.getAll("a"));
        assertEquals(new SimpleInteger(1), o.get("a"));
        assertEquals(new SimpleInteger(1), o.get("a"));

        assertEquals(Arrays.asList("a", "b"), o.keyList());
        assertEquals(Arrays.asList("a", "b"), o.keyList());

        assertEquals(Arrays.asList(
                new JSON.ObjectEntry("a", new SimpleInteger(1)),
                new JSON.ObjectEntry("b", new SimpleInteger(2))),
            o.entryList());
        assertEquals(Arrays.asList(
                new JSON.ObjectEntry("a", new SimpleInteger(1)),
                new JSON.ObjectEntry("b", new SimpleInteger(2))),
            o.entryList());
    }

    @Test
    public void reuseNullParser() throws Exception {
        JSON.Object o = new ObjectParser().last("{\"a\":null,\"b\":null}");
        assertEquals(new SimpleNull(), o.get("a"));
        assertEquals(new SimpleNull(), o.get("b"));
    }

    @Test
    public void customNumber() throws Exception {
        JSON.Number<Integer> n = new JSON.Number<Integer>() {
            @Override
            public Integer toJavaObject() {
                return 1;
            }

            @Override
            public String stringify() {
                return "1";
            }

            @Override
            public String pretty() {
                return "1";
            }

            @Override
            public void stringify(StringBuilder builder, Stringifier sfr) {
                builder.append(stringify());
            }
        };
        JSON.Object o = new ObjectBuilder().putInst("a", n).build();
        assertEquals(1, o.getInt("a"));
        assertEquals(1L, o.getLong("a"));
        assertEquals(1D, o.getDouble("a"), 0);

        JSON.Array a = new ArrayBuilder().addInst(n).build();
        assertEquals(1, a.getInt(0));
        assertEquals(1L, a.getLong(0));
        assertEquals(1D, a.getDouble(0), 0);
    }

    @Test
    public void threadlocal() throws Exception {
        assertEquals(JSON.parse("\"a\""), JSON.parse("\"a\""));
        assertEquals(JSON.parseToJavaObject("\"a\""), JSON.parseToJavaObject("\"a\""));
    }

    @Test
    public void keyNoQuotes() throws Exception {
        ObjectParser parser = new ObjectParser(new ParserOptions().setKeyNoQuotes(true));
        parser.feed("   {     ");
        parser.feed("");
        parser.feed("  ab");
        parser.feed("");
        parser.feed("c   ");
        parser.feed("");
        parser.feed(":  \"x\"");
        parser.feed("");
        JSON.Object o = parser.last("  }  ");
        assertEquals(new SimpleObject(new AppendableMap<>()
            .append("abc", new SimpleString("x"))), o);
    }

    @Test
    public void objectRulePutFail() throws Exception {
        ObjectRule<Object> rule = new ObjectRule<>(Object::new);
        rule.put("a", (o, v) -> {
        }, rule);
        try {
            rule.put("a", (o, v) -> {
            }, rule);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void ruleToString() throws Exception {
        assertEquals("Int", IntRule.get().toString());
        assertEquals("Long", LongRule.get().toString());
        assertEquals("Double", DoubleRule.get().toString());
        assertEquals("Bool", BoolRule.get().toString());
        assertEquals("String?", NullableStringRule.get().toString());
        assertEquals("String", StringRule.get().toString());

        TypeRule<Object> typeRule = new TypeRule<>()
            .type("base", TypeRuleBase.baseRule)
            .type(Reflection.getOrCreateKotlinClass(TypeRuleA.class), TypeRuleA.aRule);
        assertEquals("TypeRule{" +
            "@type[base]=>Object{x=>Int,y=>String}," +
            "@type[" + TypeRuleA.class.getName() + "]=>Object{x=>Int,y=>String,a=>Double}" +
            "}", typeRule.toString());
        TypeRule<Object> typeRuleConstructor = new TypeRule<Object>("base", TypeRuleBase.baseRule)
            .type(Reflection.getOrCreateKotlinClass(TypeRuleA.class), TypeRuleA.aRule);
        assertEquals("TypeRule{" +
            "@type[base*]=>Object{x=>Int,y=>String}," +
            "@type[" + TypeRuleA.class.getName() + "]=>Object{x=>Int,y=>String,a=>Double}" +
            "}", typeRuleConstructor.toString());

        TypeRule<Object> typeRuleRec = new TypeRule<>();
        typeRuleRec.type("a", new ObjectRule<>(Object::new)
            .put("rec", (o, v) -> {
            }, typeRuleRec));
        assertEquals("TypeRule{" +
            "@type[a]=>Object{rec=>TypeRule{...recursive...}}" +
            "}", typeRuleRec.toString());

        ObjectRule<Object> objRule = new ObjectRule<>(Object::new);
        objRule
            .put("int", (o, v) -> {
            }, IntRule.get())
            .put("long", (o, v) -> {
            }, LongRule.get())
            .put("double", (o, v) -> {
            }, DoubleRule.get())
            .put("bool", (o, v) -> {
            }, BoolRule.get())
            .put("nullableStr", (o, v) -> {
            }, NullableStringRule.get())
            .put("string", (o, v) -> {
            }, StringRule.get())
            .put("typed", (o, v) -> {
            }, typeRuleRec);
        objRule.put("self", (o, v) -> {
        }, objRule);
        assertEquals("Object{" +
                "int=>Int," +
                "long=>Long," +
                "double=>Double," +
                "bool=>Bool," +
                "nullableStr=>String?," +
                "string=>String," +
                "typed=>TypeRule{@type[a]=>Object{rec=>TypeRule{...recursive...}}}," +
                "self=>Object{...recursive...}" +
                "}",
            objRule.toString());

        ArrayRule<List<Object>, Object> arrRule = new ArrayRule<>(ArrayList::new, List::add, objRule);
        assertEquals("Array[" + objRule.toString() + "]", arrRule.toString());

        objRule.put("arr", (o, v) -> {
        }, arrRule);

        assertEquals("Object{" +
                "int=>Int," +
                "long=>Long," +
                "double=>Double," +
                "bool=>Bool," +
                "nullableStr=>String?," +
                "string=>String," +
                "typed=>TypeRule{@type[a]=>Object{rec=>TypeRule{...recursive...}}}," +
                "self=>Object{...recursive...}," +
                "arr=>Array[Object{...recursive...}]" +
                "}",
            objRule.toString());
        assertEquals("Array[" + "Object{" +
            "int=>Int," +
            "long=>Long," +
            "double=>Double," +
            "bool=>Bool," +
            "nullableStr=>String?," +
            "string=>String," +
            "typed=>TypeRule{@type[a]=>Object{rec=>TypeRule{...recursive...}}}," +
            "self=>Object{...recursive...}," +
            "arr=>Array[...recursive...]" +
            "}" + "]", arrRule.toString());
    }

    @Test
    public void deserializeParserListenerInitWithWrongRule() throws Exception {
        try {
            new DeserializeParserListener<>(IntRule.get());
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void veryLongFraction() throws Exception {
        double res = ((JSON.Double) JSON.parse("0" +
            ".123456789" +
            "0123456789" +
            "0123456789" +
            "0123456789" +
            "0123456789" +
            "0123456789" +
            "0123456789" +
            "0123456789")).doubleValue();
        assertEquals(
            0.12345678901234567, res,
            0.0000000000000001);
    }

    @Test
    public void shortFraction() throws Exception {
        double res = ((JSON.Double) JSON.parse("1.23")).doubleValue();
        assertEquals(1.23, res, 0.000000001);
    }

    @Test
    public void objectEntryToString() throws Exception {
        JSON.ObjectEntry entry = new JSON.ObjectEntry("a", new SimpleInteger(1));
        assertEquals("(a: Integer(1))", entry.toString());
    }

    @Test
    public void parserOptionsSetKeyNoQuotesAnyChar() throws Exception {
        ParserOptions opts = new ParserOptions();
        assertFalse(opts.isKeyNoQuotes());
        assertFalse(opts.isKeyNoQuotesAnyChar());
        opts.setKeyNoQuotesAnyChar(true);
        assertTrue(opts.isKeyNoQuotes());
        assertTrue(opts.isKeyNoQuotesAnyChar());

        opts.setKeyNoQuotesAnyChar(false);
        assertTrue(opts.isKeyNoQuotes());
        assertFalse(opts.isKeyNoQuotesAnyChar());

        opts.setKeyNoQuotesAnyChar(true);
        assertTrue(opts.isKeyNoQuotes());
        assertTrue(opts.isKeyNoQuotesAnyChar());
        opts.setKeyNoQuotes(false);
        assertFalse(opts.isKeyNoQuotes());
        assertFalse(opts.isKeyNoQuotesAnyChar());

        opts.setKeyNoQuotes(true);
        assertTrue(opts.isKeyNoQuotes());
        assertFalse(opts.isKeyNoQuotesAnyChar());
    }

    @Test
    public void emptyStringDictionary() throws Exception {
        StringDictionary strDic = new StringDictionary(0);
        assertEquals("", strDic.toString());
    }
}
