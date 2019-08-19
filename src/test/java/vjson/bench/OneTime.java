package vjson.bench;

import vjson.CharStream;
import vjson.JSON;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OneTime {
    private static final String file = "tests.zip";
    private static final String[] tests = {
        "test1.json", "test2.json", "test3.json", "test4.json", "test5.json",
    };

    private static void vjson(char[] chars) {
        JSON.parseToJavaObject(CharStream.from(chars));
    }

    public static void main(String[] args) throws Exception {
        char[][] chars = new char[tests.length][];
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
                    chars[i] = s.toCharArray();
                    found = true;
                    break;
                }
            }
            assert found;
        }
        for (int i = 0; i < tests.length; ++i) {
            vjson(chars[i]);
        }
    }
}
