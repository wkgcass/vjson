package vjson;

import org.junit.Test;
import vjson.cs.CharArrayCharStream;

import static org.junit.Assert.*;

@SuppressWarnings("RedundantThrows")
public class TestCharArrayCharStream {
    @Test
    public void testToString() throws Exception {
        String s = "abcde";
        CharArrayCharStream cs = (CharArrayCharStream) CharStream.from(s);
        assertEquals("CharStream([]abcde)", cs.toString());
        assertTrue(cs.hasNext());
        assertEquals('a', cs.moveNextAndGet());
        assertEquals("CharStream([a]bcde)", cs.toString());
        assertTrue(cs.hasNext());
        assertEquals('b', cs.moveNextAndGet());
        assertEquals("CharStream(a[b]cde)", cs.toString());
        assertTrue(cs.hasNext());
        assertEquals('c', cs.moveNextAndGet());
        assertEquals("CharStream(ab[c]de)", cs.toString());
        assertTrue(cs.hasNext());
        assertEquals('d', cs.moveNextAndGet());
        assertEquals("CharStream(abc[d]e)", cs.toString());
        assertTrue(cs.hasNext());
        assertEquals('e', cs.moveNextAndGet());
        assertEquals("CharStream(abcd[e])", cs.toString());
        assertFalse(cs.hasNext());

        s = "";
        cs = (CharArrayCharStream) CharStream.from(s);
        assertEquals("CharStream([])", cs.toString());
    }

    @Test
    public void iterable() throws Exception {
        String s = "abcde";
        CharArrayCharStream cs = (CharArrayCharStream) CharStream.from(s);
        int idx = 0;
        for (char c : cs) {
            char expected = (char) ('a' + (idx++));
            assertEquals(expected, c);
        }
    }

    @Test
    public void skipBlank() throws Exception {
        CharStream cs = CharStream.from(" ab c d");
        cs.skipBlank();
        assertEquals('a', cs.moveNextAndGet());
        assertEquals('b', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('c', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
    }

    @Test
    public void skipComment() throws Exception {
        CharStream cs = CharStream.from("//xxxxx\nab//yyyyyy\rc/*zzzzzz*/d#xxxxxxx\nef");
        cs.skipBlank();
        assertEquals('a', cs.moveNextAndGet());
        assertEquals('b', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('c', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('d', cs.moveNextAndGet());
        cs.skipBlank();
        assertEquals('e', cs.moveNextAndGet());
        assertEquals('f', cs.moveNextAndGet());

        cs = CharStream.from("/**/");
        assertTrue(cs.hasNext());
        cs.skipBlank();
        assertFalse(cs.hasNext());
    }

    @Test
    public void doNotSkipComment() throws Exception {
        CharStream cs = CharStream.from("//x");
        cs.skipBlank(false);
        assertEquals('/', cs.moveNextAndGet());
        assertEquals('/', cs.moveNextAndGet());
        assertEquals('x', cs.moveNextAndGet());
    }
}
