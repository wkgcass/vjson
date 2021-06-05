package vjson.bench;

import org.junit.Before;
import org.junit.Test;
import vjson.JSON;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleString;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static vjson.bench.BenchHelper.vjson;

public class X {
    private static final char[] availableChars = ("" +
        "abcdefghijklmnopqrstuvwxyz01234567ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "").toCharArray();

    private char[] stringsChars;
    private byte[] stringsBytes;

    @Before
    public void setUp() {
        Random rnd = new Random(12345);
        List<JSON.String> strings = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            int len = rnd.nextInt(30) + 20;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; ++j) {
                sb.append(availableChars[rnd.nextInt(availableChars.length)]);
            }
            strings.add(new SimpleString(sb.toString()));
        }
        String stringsString = new SimpleArray(strings).stringify();
        this.stringsChars = stringsString.toCharArray();
        this.stringsBytes = stringsString.getBytes();
    }

    @Test
    public void vjsonx() {
        for (int i = 0; i< 1_000_000; ++i) {
            vjson(stringsChars);
        }
    }
}
