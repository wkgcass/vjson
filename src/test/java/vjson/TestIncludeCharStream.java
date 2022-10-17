package vjson;

import org.junit.Test;
import vjson.cs.IncludeCharStream;
import vjson.ex.ParserException;

import static org.junit.Assert.*;

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

    @Test
    public void skipBlankWithoutComment() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a \t #include \"b\"";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank(false);
        for (int i = 0; i < "#include \"b\"".length(); ++i) {
            assertEquals("#include \"b\"".charAt(i), cs.moveNextAndGet());
        }
    }

    @Test
    public void skipNested() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\"        f";
            if (name.equals("sub1")) return () -> "    b #include \"sub2\"        ";
            if (name.equals("sub2")) return () -> "     #include \"sub3\"       ";
            if (name.equals("sub3")) return () -> "  #include \"sub4\"  e  ";
            if (name.equals("sub4")) return () -> "   c #include \"sub5\"        ";
            if (name.equals("sub5")) return () -> "  d        ";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('b', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('c', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('e', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('f', cs.moveNextAndGet());
    }

    @Test
    public void skipNestedButWithoutComment() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\"        f  ";
            if (name.equals("sub1")) return () -> "    b #include \"sub2\"        ";
            if (name.equals("sub2")) return () -> "     #include \"sub3\"       ";
            if (name.equals("sub3")) return () -> "  #include \"sub4\"  e  ";
            if (name.equals("sub4")) return () -> "   c #include \"sub5\"        ";
            if (name.equals("sub5")) return () -> "  d        ";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('b', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('c', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
        cs.skipBlank(false);
        assertEquals('e', cs.moveNextAndGet());
        cs.skipBlank(false);
        assertEquals('f', cs.moveNextAndGet());
        cs.skipBlank(false);
        assertFalse(cs.hasNext());
    }

    @Test
    public void includeMoreThanOnce() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include \"sub1\" #include \"sub2\"";
            if (name.equals("sub1")) return () -> "b #include \"sub3\"";
            if (name.equals("sub2")) return () -> "c #include \"sub3\"";
            if (name.equals("sub3")) return () -> "d";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('b', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('c', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
    }

    @Test
    public void failedRecursiveInclude() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("a")) return () -> "a #include \"b\"";
            if (name.equals("b")) return () -> "b #include \"a\"";
            return null;
        }, "a");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('b', cs.moveNextAndGet());
        try {
            cs.skipBlank();
            fail();
        } catch (ParserException e) {
            assertEquals("recursive include: a at b(1:3)", e.getMessage());
        }
    }

    @Test
    public void failedNoSuchCharStream() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a \t #include \"b\"";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        try {
            cs.skipBlank();
            fail();
        } catch (ParserException e) {
            assertEquals("unable to #include \"b\": char stream not found at main(1:5)", e.getMessage());
        }
    }

    @Test
    public void failedInvalidStatement() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include b";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        try {
            cs.skipBlank();
            fail();
        } catch (ParserException e) {
            assertEquals("invalid #include statement: invalid character for string: not starts with \": b at main(1:12) at main(1:3)", e.getMessage());
        }
    }

    @Test
    public void failedEof() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        try {
            cs.skipBlank();
            fail();
        } catch (ParserException e) {
            assertEquals("invalid #include statement: reaches eof at main(1:3)", e.getMessage());
        }
    }

    @Test
    public void failedMissingName() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #include ";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        try {
            cs.skipBlank();
            fail();
        } catch (ParserException e) {
            assertEquals("invalid #include statement: missing char stream name to be included at main(1:3)", e.getMessage());
        }
    }

    @Test
    public void failedOutOfBounds() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        try {
            cs.moveNextAndGet();
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
        try {
            cs.peekNext();
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void corner() {
        IncludeCharStream cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a";
            return null;
        }, "main");
        cs.skipBlank();
        assertEquals('a', cs.moveNextAndGet());

        cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #inc";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertFalse(cs.hasNext());

        cs = new IncludeCharStream(name -> {
            if (name.equals("main")) return () -> "a #inc\nb";
            return null;
        }, "main");
        assertEquals('a', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('b', cs.moveNextAndGet());
    }
}
