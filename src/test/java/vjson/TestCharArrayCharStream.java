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
}
