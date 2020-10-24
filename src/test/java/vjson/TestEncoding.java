package vjson;

import org.junit.Test;
import vjson.cs.UTF8ByteArrayCharStream;
import vjson.simple.SimpleString;
import vjson.util.ObjectBuilder;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestEncoding {
    @Test
    public void utf8() {
        UTF8ByteArrayCharStream cs = new UTF8ByteArrayCharStream(("" +
            "你好，世界" +
            "\uD808\uDC16\uD808\uDC17" +
            "こんにちは" +
            "♥" +
            "¡¢£" +
            "").getBytes(StandardCharsets.UTF_8));
        for (int i = 1; i <= 18; ++i) {
            assertTrue("cs.hasNext(" + i + ")", cs.hasNext(i));
        }
        // System.out.println("\uD808\uDC16".getBytes()[0]); =-16  = 1111_0_000
        // System.out.println("\uD808\uDC16".getBytes()[1]); =-110 = 10_01_0010
        // System.out.println("\uD808\uDC16".getBytes()[2]); =-128 = 10_00_0000
        // System.out.println("\uD808\uDC16".getBytes()[3]); =-106 = 10_01_0110
        // ----------------------------------------- 0_0001_0010_0000_0001_0110 unicode from utf8
        // ---------------------------- 1101_1000_0100_1000_1101_1100_0001_0110 utf16 from unicode without -0x10000
        // ---------------------------- 1101_1000_0000_1000_1101_1100_0001_0110 expand from input utf16
        // ------------------------------------------- 0000_0010_0000_0001_0110 unicode from utf16
        // ---------------------------- 1111_0000_1000_0010_1000_0000_1001_0110 utf8 from unicode from utf16
        assertFalse(cs.hasNext(19));
        assertEquals('你', cs.peekNext(1));
        assertEquals('好', cs.peekNext(2));
        assertEquals('，', cs.peekNext(3));
        assertEquals('世', cs.peekNext(4));
        assertEquals('界', cs.peekNext(5));
        assertEquals('\uD808', cs.peekNext(6));
        assertEquals('\uDC16', cs.peekNext(7));
        assertEquals('\uD808', cs.peekNext(8));
        assertEquals('\uDC17', cs.peekNext(9));
        assertEquals('こ', cs.peekNext(10));
        assertEquals('ん', cs.peekNext(11));
        assertEquals('に', cs.peekNext(12));
        assertEquals('ち', cs.peekNext(13));
        assertEquals('は', cs.peekNext(14));
        assertEquals('♥', cs.peekNext(15));
        assertEquals('¡', cs.peekNext(16));
        assertEquals('¢', cs.peekNext(17));
        assertEquals('£', cs.peekNext(18));

        assertEquals('你', cs.moveNextAndGet());
        assertEquals('好', cs.moveNextAndGet());

        assertEquals('，', cs.peekNext(1));
        assertEquals('世', cs.peekNext(2));
        assertEquals('界', cs.peekNext(3));
        assertEquals('\uD808', cs.peekNext(4));
        assertEquals('\uDC16', cs.peekNext(5));

        assertEquals('，', cs.moveNextAndGet());
        assertEquals('世', cs.moveNextAndGet());
        assertEquals('界', cs.moveNextAndGet());
        assertEquals('\uD808', cs.moveNextAndGet());

        assertEquals('\uDC16', cs.peekNext(1));
        assertEquals('\uD808', cs.peekNext(2));
        assertEquals('\uDC17', cs.peekNext(3));
        assertEquals('こ', cs.peekNext(4));
        assertTrue(cs.hasNext(1));
        assertTrue(cs.hasNext(2));
        assertFalse(cs.hasNext(14));
        assertFalse(cs.hasNext(15));

        assertEquals('\uDC16', cs.moveNextAndGet());
        assertEquals('\uD808', cs.moveNextAndGet());

        assertEquals('\uDC17', cs.peekNext(1));
        assertEquals('こ', cs.peekNext(2));
        assertEquals('ん', cs.peekNext(3));
        assertTrue(cs.hasNext(1));
        assertTrue(cs.hasNext(2));
        assertFalse(cs.hasNext(11));
        assertFalse(cs.hasNext(12));

        assertEquals('\uDC17', cs.moveNextAndGet());
        assertEquals('こ', cs.moveNextAndGet());
        assertEquals('ん', cs.moveNextAndGet());
        assertEquals('に', cs.moveNextAndGet());
        assertEquals('ち', cs.moveNextAndGet());
        assertEquals('は', cs.moveNextAndGet());
        assertEquals('♥', cs.moveNextAndGet());
        assertEquals('¡', cs.moveNextAndGet());
        assertEquals('¢', cs.moveNextAndGet());
        assertEquals('£', cs.moveNextAndGet());

        assertFalse(cs.hasNext(1));
        assertFalse(cs.hasNext(2));
    }

    @Test
    public void utf8Json() {
        JSON.Instance inst = JSON.parse(new UTF8ByteArrayCharStream(("" +
            "{" +
            "  \"键\":\"值\"," +
            "  \"键2\":123," +
            "  \"\uD808\uDC16\":\"\uD808\uDC17\"," +
            "  \"key3\":\"值3\"" +
            "}" +
            "").getBytes(StandardCharsets.UTF_8)));
        assertEquals(new ObjectBuilder()
            .put("键", "值")
            .put("键2", 123)
            .put("\uD808\uDC16", "\uD808\uDC17")
            .put("key3", "值3")
            .build(), inst);

        inst = JSON.parse(new UTF8ByteArrayCharStream("\"\uD808\uDC16\uD808\uDC17\"".getBytes(StandardCharsets.UTF_8)));
        assertEquals(new SimpleString("\uD808\uDC16\uD808\uDC17"), inst);
    }
}
