package vjson;

import org.junit.Test;
import vjson.util.TextBuilder;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("RedundantThrows")
public class TestTextBuilder {
    @Test
    public void append() throws Exception {
        TextBuilder b = new TextBuilder(4);
        assertEquals("", b.toString());
        b.append('a').append('b'); // into buffer
        assertEquals("ab", b.toString());
        assertEquals("ab", b.toString()); // multiple times
        b.clear();
        assertEquals("", b.toString());
        b.append('c').append('d').append('e'); // into buffer
        assertEquals("cde", b.toString());
        b.append('f'); // into string builder
        assertEquals("cdef", b.toString());
        b.append('g'); // into string builder
        assertEquals("cdefg", b.toString());
        b.append('h'); // into buffer
        assertEquals("cdefgh", b.toString());
        b.clear();
        assertEquals("", b.toString());
        b.append('x').append('y').append('z'); // into string builder
        assertEquals("xyz", b.toString());
    }

    @Test
    public void removeLast() throws Exception {
        TextBuilder b = new TextBuilder(4);
        b.removeLast();
        assertEquals("", b.toString());
        b.append('a').append('b').removeLast(); // remove from buf
        assertEquals("a", b.toString());
        b.append('b').append('c').append('d').removeLast(); // remove from string builder
        assertEquals("abc", b.toString());
        b.clear();
        b.removeLast();
        assertEquals("", b.toString());
    }
}
