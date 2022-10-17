package vjson;

import org.junit.Test;
import vjson.cs.LineCol;
import vjson.cs.LineColCharStream;
import vjson.cs.PeekCharStream;

import static org.junit.Assert.assertEquals;

public class TestPeekCharStream {
    @Test
    public void simpleLineCol() {
        PeekCharStream cs = new PeekCharStream(
            new LineColCharStream(CharStream.from("" +
                "a\n" +
                "b\r" +
                "c\r\n" +
                "d\r"), "file"), 0);
        TestJsonLineCol.assertLineCol(new LineCol("file", 1, 1), cs.lineCol());

        assertEquals('a', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 1, 2), cs.lineCol());
        assertEquals('\n', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 2, 1), cs.lineCol());
        assertEquals('b', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 2, 2), cs.lineCol());
        assertEquals('\r', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 3, 1), cs.lineCol());
        assertEquals('c', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 3, 2), cs.lineCol());
        assertEquals('\r', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 3, 3), cs.lineCol());
        assertEquals('\n', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 4, 1), cs.lineCol());
        assertEquals('d', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 4, 2), cs.lineCol());
        assertEquals('\r', cs.moveNextAndGet());
        TestJsonLineCol.assertLineCol(new LineCol("file", 5, 1), cs.lineCol());
    }
}
