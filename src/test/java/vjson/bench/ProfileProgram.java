package vjson.bench;

import vjson.CharStream;
import vjson.parser.ArrayParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ProfileProgram {
    private static final String file = "tests.zip";
    private static final String[] tests = {
        "test1.json", "test2.json", "test3.json", "test4.json", "test5.json",
    };
    private static final int WARM_TIME = 10;
    private static final int TEST_LOOP = 4;
    private static final int TEST_ROUND = 20;

    private static void vjson(char[] chars) {
        new ArrayParser().last(CharStream.from(chars));
    }

    public static void main(String[] args) throws Exception {
        // init
        byte[][] bytes = new byte[tests.length][];
        char[][] chars = new char[tests.length][];
        int[] size = new int[tests.length];
        InputStream is = ProfileProgram.class.getClassLoader().getResourceAsStream(file);
        assert is != null;
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            boolean found = false;
            for (int i = 0; i < tests.length; ++i) {
                if (entry.getName().equals(tests[i])) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(zis));
                    StringBuilder sb = new StringBuilder();
                    String s;
                    while ((s = br.readLine()) != null) {
                        sb.append(s).append("\n");
                    }
                    s = sb.toString();
                    bytes[i] = s.getBytes();
                    chars[i] = s.toCharArray();
                    found = true;
                    break;
                }
            }
            assert found;
        }
        final int[] REAL_ROUNDS = new int[tests.length];
        for (int idx = 0; idx < tests.length; ++idx) {
            size[idx] = bytes[idx].length;
            System.out.println("size of test" + (idx + 1) + " is: " + size[idx]);
            REAL_ROUNDS[idx] = (int) (TEST_ROUND * Math.ceil(Math.pow(10, tests.length - idx - 1)));
        }

        // warm
        System.out.println("warm starts, takes " + WARM_TIME + " seconds");
        long beforeWarm = System.currentTimeMillis();
        for (int idx = 0; idx < tests.length; ++idx) {
            while (System.currentTimeMillis() - beforeWarm <= WARM_TIME * 1000) {
                vjson(chars[idx]);
            }
        }
        System.out.println("warm finishes");
        System.out.println();

        // start
        long start;
        long end;
        long[][] full = new long[tests.length][1];
        for (int idx = 0; idx < tests.length; ++idx) {
            final int round = REAL_ROUNDS[idx];
            final int per = round / 10;
            for (int l = 0; l < TEST_LOOP; ++l) {
                for (int i = 0; i < round; i += per) {
                    start = System.currentTimeMillis();
                    for (int j = 0; j < per; ++j) {
                        vjson(chars[idx]);
                    }
                    end = System.currentTimeMillis();
                    full[idx][0] += (end - start);
                }

                double time;
                time = full[idx][0] * 1_000D / round;
                System.out.println("test" + (idx + 1) + " loop" + (l + 1) + " vjson   : " + time + "us/json, " + ((int) (time * 1_000D / size[idx])) + "ns/byte");
            }
            System.out.println();
        }
    }
}
