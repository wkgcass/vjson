package vjson;

import org.junit.Test;
import vjson.util.TextBuilder;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestTextBuilder {
    @Test
    public void append() throws Exception {
        TextBuilder b = new TextBuilder(4);
        assertEquals(0, b.getBufLen());
        assertEquals(4, b.getBufCap());
        assertEquals("", b.toString());
        b.append('a').append('b');
        assertEquals(2, b.getBufLen());
        assertEquals(4, b.getBufCap());
        assertEquals("ab", b.toString());
        assertEquals("ab", b.toString());
        b.clear();
        assertEquals("", b.toString());
        assertEquals(0, b.getBufLen());
        assertEquals(4, b.getBufCap());
        b.append('c').append('d').append('e');
        assertEquals(3, b.getBufLen());
        assertEquals(4, b.getBufCap());
        assertEquals("cde", b.toString());
        b.append('f');
        assertEquals(4, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("cdef", b.toString());
        b.append('g');
        assertEquals(5, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("cdefg", b.toString());
        b.append('h');
        assertEquals(6, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("cdefgh", b.toString());
        b.clear();
        assertEquals(0, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("", b.toString());
        b.append('x').append('y').append('z');
        assertEquals(3, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("xyz", b.toString());
        b.append('a').append('b').append('c').append('d').append('e').append('f')
            .append('g').append('h').append('i').append('j').append('k').append('l')
            .append('m');
        assertEquals(16, b.getBufLen());
        assertEquals(64, b.getBufCap());
        assertEquals("xyzabcdefghijklm", b.toString());
    }

    @Test
    public void removeLast() throws Exception {
        TextBuilder b = new TextBuilder(4);
        b.removeLast();
        assertEquals("", b.toString());
        b.append('a').append('b').removeLast();
        assertEquals(1, b.getBufLen());
        assertEquals(4, b.getBufCap());
        assertEquals("a", b.toString());
        b.append('b').append('c').append('d').removeLast();
        assertEquals(3, b.getBufLen());
        assertEquals(16, b.getBufCap());
        assertEquals("abc", b.toString());
        b.clear();
        b.removeLast();
        assertEquals("", b.toString());
    }
}
