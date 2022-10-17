package vjson;

import org.junit.Test;
import vjson.cs.IncludeCharStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestIncludeCharStream {
    @Test
    public void simple() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a b c";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('b', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('c', cs.moveNextAndGet());
        assertFalse(cs.hasNext());
    }

    @Test
    public void simplePeek() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a b c";
            return null;
        }, "main");
        assertEquals('a', cs.peekNext());
        assertEquals(' ', cs.peekNext(2));
        assertEquals('b', cs.peekNext(3));
        assertEquals(' ', cs.peekNext(4));
        assertEquals('c', cs.peekNext(5));
        assertFalse(cs.hasNext(6));
        assertEquals('a', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('b', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('c', cs.moveNextAndGet());
        assertFalse(cs.hasNext());
    }

    @Test
    public void simpleInclude() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\" c";
            if (name.equals("sub1")) return () -> "x y z";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('x', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('y', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('z', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('c', cs.moveNextAndGet());
    }

    @Test
    public void nestedInclude() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\" b";
            if (name.equals("sub1")) return () -> "x #include \"sub2\" y";
            if (name.equals("sub2")) return () -> "m #include \"sub3\" n";
            if (name.equals("sub3")) return () -> "o p q";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('x', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('m', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('o', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('p', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('q', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('n', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('y', cs.moveNextAndGet());
        assertEquals(' ', cs.moveNextAndGet());
        assertEquals('b', cs.moveNextAndGet());
    }

    @Test
    public void nestedIncludePeek() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\" b";
            if (name.equals("sub1")) return () -> "x #include \"sub2\" y";
            if (name.equals("sub2")) return () -> "m #include \"sub3\" n";
            if (name.equals("sub3")) return () -> "o p q";
            return null;
        }, "main");
        assertEquals('a', cs.peekNext(1));
        assertEquals(' ', cs.peekNext(2));
        assertEquals('#', cs.peekNext(3));
        assertEquals('i', cs.peekNext(4));
        cs.skip(2);
        cs.skipBlank();
        assertEquals('x', cs.peekNext(1));
        assertEquals(' ', cs.peekNext(2));
        assertEquals('#', cs.peekNext(3));
        assertEquals('i', cs.peekNext(4));
        cs.skip(2);
        cs.skipBlank();
        assertEquals('m', cs.peekNext(1));
        assertEquals(' ', cs.peekNext(2));
        assertEquals('#', cs.peekNext(3));
        assertEquals('i', cs.peekNext(4));
        cs.skip(2);
        cs.skipBlank();
        assertEquals('o', cs.peekNext(1));
        assertEquals(' ', cs.peekNext(2));
        assertEquals('p', cs.peekNext(3));
        assertEquals(' ', cs.peekNext(4));
        assertEquals('q', cs.peekNext(5));
        assertEquals(' ', cs.peekNext(6));
        assertEquals('n', cs.peekNext(7));
        assertEquals(' ', cs.peekNext(8));
        assertEquals('y', cs.peekNext(9));
        assertEquals(' ', cs.peekNext(10));
        assertEquals('b', cs.peekNext(11));
    }
}
