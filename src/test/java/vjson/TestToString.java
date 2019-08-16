package vjson;

import org.junit.Test;
import vjson.simple.*;
import vjson.util.AppendableMap;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestToString {
    private final Random random = new Random();

    @Test
    public void integer() throws Exception {
        JSON.Instance i;

        i = new SimpleInteger(1);
        assertEquals("Integer(1)", i.toString());
        i = new SimpleInteger(32);
        assertEquals("Integer(32)", i.toString());
        i = new SimpleInteger(-32);
        assertEquals("Integer(-32)", i.toString());
        int rand = random.nextInt();
        i = new SimpleInteger(rand);
        assertEquals("Integer(" + rand + ")", i.toString());
        // multiple times
        assertEquals("Integer(" + rand + ")", i.toString());
    }

    @Test
    public void longV() throws Exception {
        JSON.Instance l;

        l = new SimpleLong(1L);
        assertEquals("Long(1)", l.toString());
        l = new SimpleLong(32L);
        assertEquals("Long(32)", l.toString());
        l = new SimpleLong(-32L);
        assertEquals("Long(-32)", l.toString());
        long rand = random.nextLong();
        l = new SimpleLong(rand);
        assertEquals("Long(" + rand + ")", l.toString());
        // multiple times
        assertEquals("Long(" + rand + ")", l.toString());
    }

    @Test
    public void doubleV() throws Exception {
        JSON.Instance d;

        d = new SimpleDouble(1);
        assertEquals("Double(1.0)", d.toString());
        d = new SimpleDouble(3.2);
        assertEquals("Double(3.2)", d.toString());
        d = new SimpleDouble(-3.2);
        assertEquals("Double(-3.2)", d.toString());
        double rand = random.nextDouble();
        d = new SimpleDouble(rand);
        assertEquals("Double(" + rand + ")", d.toString());
        // multiple times
        assertEquals("Double(" + rand + ")", d.toString());
    }

    @Test
    public void exp() throws Exception {
        JSON.Instance d;

        d = new SimpleExp(3, 5);
        assertEquals("Exp(3.0e5=" + 3e5 + ")", d.toString());
        d = new SimpleExp(3.2, 32);
        assertEquals("Exp(3.2e32=" + 3.2 * Math.pow(10, 32) + ")", d.toString());
        d = new SimpleExp(-3, 5);
        assertEquals("Exp(-3.0e5=" + -3.0e5 + ")", d.toString());
        // multiple times
        assertEquals("Exp(-3.0e5=" + -3.0e5 + ")", d.toString());
    }

    @Test
    public void bool() throws Exception {
        JSON.Instance b;

        b = new SimpleBool(true);
        assertEquals("Bool(true)", b.toString());
        // multiple times
        assertEquals("Bool(true)", b.toString());
        b = new SimpleBool(false);
        assertEquals("Bool(false)", b.toString());
        // multiple times
        assertEquals("Bool(false)", b.toString());
    }

    @Test
    public void nullV() throws Exception {
        JSON.Instance n;

        n = new SimpleNull();
        assertEquals("Null", n.toString());
        // multiple times
        assertEquals("Null", n.toString());
    }

    @Test
    public void string() throws Exception {
        JSON.Instance s;

        s = new SimpleString("a");
        assertEquals("String(a)", s.toString());
        s = new SimpleString("abc");
        assertEquals("String(abc)", s.toString());
        s = new SimpleString("\"");
        assertEquals("String(\")", s.toString());
        s = new SimpleString("'");
        assertEquals("String(')", s.toString());
        s = new SimpleString("/");
        assertEquals("String(/)", s.toString());
        s = new SimpleString("\\");
        assertEquals("String(\\)", s.toString());
        s = new SimpleString("\b");
        assertEquals("String(\b)", s.toString());
        s = new SimpleString("\f");
        assertEquals("String(\f)", s.toString());
        s = new SimpleString("\n");
        assertEquals("String(\n)", s.toString());
        s = new SimpleString("\r");
        assertEquals("String(\r)", s.toString());
        s = new SimpleString("\t");
        assertEquals("String(\t)", s.toString());
        s = new SimpleString("\u0002");
        assertEquals("String(\u0002)", s.toString());
        s = new SimpleString("\u0012");
        assertEquals("String(\u0012)", s.toString());
        s = new SimpleString("\u007f");
        assertEquals("String(\u007f)", s.toString());
        s = new SimpleString("\u00ff");
        assertEquals("String(\u00ff)", s.toString());
        s = new SimpleString("\u0234");
        assertEquals("String(\u0234)", s.toString());
        s = new SimpleString("\u1234");
        assertEquals("String(\u1234)", s.toString());
        // multiple times
        assertEquals("String(\u1234)", s.toString());
    }

    @Test
    public void array() throws Exception {
        JSON.Instance a;

        a = new SimpleArray();
        assertEquals("Array[]", a.toString());
        a = new SimpleArray(new SimpleString("ab"));
        assertEquals("Array[String(ab)]", a.toString());
        a = new SimpleArray(new SimpleString("ab"), new SimpleInteger(32));
        assertEquals("Array[String(ab), Integer(32)]", a.toString());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1)));
        assertEquals("Array[Array[Integer(1)]]", a.toString());
        a = new SimpleArray(new SimpleArray(new SimpleInteger(1), new SimpleDouble(3.2)), new SimpleString("a"));
        assertEquals("Array[Array[Integer(1), Double(3.2)], String(a)]", a.toString());
        // multiple times
        assertEquals("Array[Array[Integer(1), Double(3.2)], String(a)]", a.toString());
    }

    @Test
    public void object() throws Exception {
        JSON.Instance o;

        o = new SimpleObject(new AppendableMap<>());
        assertEquals("Object{}", o.toString());
        o = new SimpleObject(new AppendableMap<>().append("a", new SimpleInteger(1)));
        assertEquals("Object{a:Integer(1)}", o.toString());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleString("a"))
            .append("b", new SimpleString("b")));
        assertEquals("Object{a:String(a), b:String(b)}", o.toString());
        o = new SimpleObject(new AppendableMap<>()
            .append("a", new SimpleObject(new AppendableMap<>()
                .append("x", new SimpleInteger(1))
                .append("y", new SimpleNull())))
            .append("b", new SimpleString("n")));
        assertEquals("Object{a:Object{x:Integer(1), y:Null}, b:String(n)}", o.toString());
        // multiple times
        assertEquals("Object{a:Object{x:Integer(1), y:Null}, b:String(n)}", o.toString());
    }
}
