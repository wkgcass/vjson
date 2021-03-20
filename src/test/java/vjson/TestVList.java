package vjson;

import org.junit.Test;
import vjson.util.collection.VList;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class TestVList {
    private void testNoSuchElement(Runnable r) {
        try {
            r.run();
            fail();
        } catch (NoSuchElementException ignore) {
        }
    }

    private void testIndexOutOfBounds(Runnable r) {
        try {
            r.run();
            fail();
        } catch (IndexOutOfBoundsException ignore) {
        }
    }

    @Test
    public void simple() throws Exception {
        VList<String> ls = new VList<>();
        assertEquals(0, ls.size());
        assertTrue(ls.isEmpty());
        testNoSuchElement(ls::first);
        testNoSuchElement(ls::last);
        testNoSuchElement(ls::removeLast);
        testNoSuchElement(ls::removeFirst);
        testIndexOutOfBounds(() -> ls.get(0));
        testIndexOutOfBounds(() -> ls.get(-1));
        testIndexOutOfBounds(() -> ls.get(1));

        ls.add("a");
        assertEquals(1, ls.size());
        assertFalse(ls.isEmpty());
        assertEquals("a", ls.first());
        assertEquals("a", ls.last());
        assertEquals("a", ls.get(0));
        testIndexOutOfBounds(() -> ls.get(-1));
        testIndexOutOfBounds(() -> ls.get(1));

        ls.add("b");
        assertEquals(2, ls.size());
        assertFalse(ls.isEmpty());
        assertEquals("a", ls.first());
        assertEquals("b", ls.last());
        assertEquals("a", ls.get(0));
        assertEquals("b", ls.get(1));
        testIndexOutOfBounds(() -> ls.get(2));

        ls.add("c");
        assertEquals(3, ls.size());
        assertEquals("a", ls.first());
        assertEquals("c", ls.last());
        assertEquals("a", ls.get(0));
        assertEquals("b", ls.get(1));
        assertEquals("c", ls.get(2));
        testIndexOutOfBounds(() -> ls.get(3));

        ls.removeFirst();
        assertEquals(2, ls.size());
        assertEquals("b", ls.first());
        assertEquals("c", ls.last());
        assertEquals("b", ls.get(0));
        assertEquals("c", ls.get(1));
        testIndexOutOfBounds(() -> ls.get(2));

        ls.removeLast();
        assertEquals(1, ls.size());
        assertEquals("b", ls.first());
        assertEquals("b", ls.last());
        assertEquals("b", ls.get(0));
        testIndexOutOfBounds(() -> ls.get(1));

        ls.addFirst("a");
        assertEquals(2, ls.size());
        assertEquals("a", ls.first());
        assertEquals("b", ls.last());
        assertEquals("a", ls.get(0));
        assertEquals("b", ls.get(1));
        testIndexOutOfBounds(() -> ls.get(2));

        ls.removeLast();
        ls.removeLast();
        assertEquals(0, ls.size());
        assertTrue(ls.isEmpty());
        testNoSuchElement(ls::first);
        testNoSuchElement(ls::last);
        testNoSuchElement(ls::removeLast);
        testNoSuchElement(ls::removeFirst);
        testIndexOutOfBounds(() -> ls.get(0));
        testIndexOutOfBounds(() -> ls.get(-1));
        testIndexOutOfBounds(() -> ls.get(1));
    }

    @Test
    public void insert() {
        VList<String> ls = new VList<>();
        ls.add("a");
        ls.add("b");
        ls.add("c");

        ls.add(2, "d");
        assertEquals("a", ls.get(0));
        assertEquals("b", ls.get(1));
        assertEquals("d", ls.get(2));
        assertEquals("c", ls.get(3));

        ls.add(4, "e");
        assertEquals("a", ls.get(0));
        assertEquals("b", ls.get(1));
        assertEquals("d", ls.get(2));
        assertEquals("c", ls.get(3));
        assertEquals("e", ls.get(4));

        ls.add(0, "f");
        assertEquals("f", ls.get(0));
        assertEquals("a", ls.get(1));
        assertEquals("b", ls.get(2));
        assertEquals("d", ls.get(3));
        assertEquals("c", ls.get(4));
        assertEquals("e", ls.get(5));

        testIndexOutOfBounds(() -> ls.add(-1, ""));
        testIndexOutOfBounds(() -> ls.add(7, ""));
    }

    @Test
    public void addFirstInitially() {
        VList<String> ls = new VList<>();
        ls.addFirst("a");
        assertEquals("a", ls.get(0));
    }
}
