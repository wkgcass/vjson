package vjson;

import org.junit.Test;
import vjson.util.TextBuilder;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestTextBuilder {
    @Test
    public void append() throws Exception {
        TextBuilder b = new TextBuilder(4);
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("", b.toString());
        b.append('a').append('b'); // into buffer
        assertEquals(2, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("ab", b.toString());
        assertEquals("ab", b.toString()); // multiple times
        b.clear();
        assertEquals("", b.toString());
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        b.append('c').append('d').append('e'); // into buffer
        assertEquals(3, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("cde", b.toString());
        b.append('f'); // into string builder
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(4, b.getCurrentBuilderLen());
        assertEquals("cdef", b.toString());
        b.append('g'); // into string builder
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(5, b.getCurrentBuilderLen());
        assertEquals("cdefg", b.toString());
        b.append('h'); // into string builder
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(6, b.getCurrentBuilderLen());
        assertEquals("cdefgh", b.toString());
        b.clear();
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("", b.toString());
        b.append('x').append('y').append('z'); // into buffer
        assertEquals(3, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("xyz", b.toString());
        b.append('a'); // into string builder
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(4, b.getCurrentBuilderLen());
        assertEquals("xyza", b.toString());
    }

    @Test
    public void removeLast() throws Exception {
        TextBuilder b = new TextBuilder(4);
        b.removeLast();
        assertEquals("", b.toString());
        b.append('a').append('b').removeLast(); // remove from buf
        assertEquals(1, b.getCurrentBufLen());
        assertEquals(0, b.getCurrentBuilderLen());
        assertEquals("a", b.toString());
        b.append('b').append('c').append('d').removeLast(); // remove from string builder
        assertEquals(0, b.getCurrentBufLen());
        assertEquals(3, b.getCurrentBuilderLen());
        assertEquals("abc", b.toString());
        b.clear();
        b.removeLast();
        assertEquals("", b.toString());
    }
}
