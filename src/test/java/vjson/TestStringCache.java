package vjson;

import org.junit.Test;
import vjson.util.StringDictionary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestStringCache {
    private void cache(StringDictionary.Traveler traveler, String s) {
        for (char c : s.toCharArray()) {
            traveler.next(c);
        }
    }

    private String cacheAndDone(StringDictionary.Traveler traveler, String s) {
        cache(traveler, s);
        return traveler.done();
    }

    @Test
    public void cache() {
        StringDictionary dictionary = new StringDictionary(16);
        StringDictionary.Traveler traveler = dictionary.traveler();

        cache(traveler, "hell");
        String hell = traveler.done();
        assertEquals("hell", hell);
        assertSame(hell, cacheAndDone(traveler, "hell"));

        cache(traveler, "hello");
        String hello = traveler.done();
        assertEquals("hello", hello);
        assertSame(hello, cacheAndDone(traveler, "hello"));

        cache(traveler, "hey");
        String hey = traveler.done();
        assertEquals("hey", hey);
        assertSame(hey, cacheAndDone(traveler, "hey"));

        assertEquals("" +
                "h/1/114 -> e/1/111 -> l/2/131 -> l/2/118 -> o/1/121 -> \"hello\"/1\n" +
                "h/1/114 -> e/1/111 -> l/2/131 -> l/2/118 -> \"hell\"/0\n" +
                "h/1/114 -> e/1/111 -> y/2/131 -> \"hey\"/2\n" +
                "",
            dictionary.toString());
    }

    @Test
    public void cannotHandle() {
        StringDictionary dictionary = new StringDictionary(16);
        StringDictionary.Traveler traveler = dictionary.traveler();

        cache(traveler, "a你好");
        String anihao = traveler.done();
        assertEquals("a你好", anihao);
        assertNotSame(anihao, cacheAndDone(traveler, "a你好"));

        cache(traveler, "你好");
        String nihao = traveler.done();
        assertEquals("你好", nihao);
        assertNotSame(nihao, cacheAndDone(traveler, "你好"));

        cache(traveler, "abcdefghijklmnopqrstuvwxyz");
        String a2z = traveler.done();
        assertEquals("abcdefghijklmnopqrstuvwxyz", a2z);
        assertNotSame(a2z, cacheAndDone(traveler, "abcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void concurrent() throws Exception {
        List<Thread> threads = new ArrayList<>();
        List<StringDictionary.Traveler> travelers = new ArrayList<>();
        List<String> results = new ArrayList<>();
        StringDictionary dictionary = new StringDictionary(16);

        int threadCnt = 8000;

        for (int i = 0; i < threadCnt; ++i) {
            StringDictionary.Traveler traveler = dictionary.traveler();
            travelers.add(traveler);
            threads.add(new Thread(() -> cache(traveler, "abcde")));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        threads.clear();
        for (StringDictionary.Traveler t : travelers) {
            threads.add(new Thread(() -> {
                String res = t.done();
                synchronized (results) {
                    results.add(res);
                }
            }));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < threadCnt; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("abcde");
        }
        sb.append("]");

        assertEquals(sb.toString(), results.toString());
    }
}
