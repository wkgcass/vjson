package vjson.bench;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import vjson.CharStream;
import vjson.JSON;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("DefaultAnnotationParam")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-XX:+UseG1GC", "-Xms2G", "-Xmx2G", "-XX:NewSize=1500M", "-XX:MaxNewSize=1500M"})
@Threads(1)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
public class JMHBenchmark {
    private static final String file = "tests.zip";
    private static final String[] tests = {
        "test1.json", "test2.json", "test3.json", "test4.json", "test5.json",
    };

    private byte[][] BYTES;
    private char[][] CHARS;

    private ObjectMapper mapper;
    private Gson gson;
    private static Type listType = new TypeReference<List>() {
    }.getType();

    private Object vjson(char[] chars) {
        return JSON.parseToJavaObject(CharStream.from(chars));
    }

    private Object jackson(byte[] bytes) throws Exception {
        return mapper.readValue(new ByteArrayInputStream(bytes), List.class);
    }

    private Object gson(char[] chars) {
        return gson.fromJson(new CharArrayReader(chars), List.class);
    }

    private Object fastjson(byte[] bytes) throws Exception {
        return com.alibaba.fastjson.JSON.parseObject(new ByteArrayInputStream(bytes), listType);
    }

    @Setup
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        gson = new Gson();
        byte[][] bytes = BYTES = new byte[tests.length][];
        char[][] chars = CHARS = new char[tests.length][];
        InputStream is = JMHBenchmark.class.getClassLoader().getResourceAsStream(file);
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
    }

    // test1

    @org.openjdk.jmh.annotations.Benchmark
    public void test1_vjson(Blackhole h) {
        h.consume(vjson(CHARS[0]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test1_jackson(Blackhole h) throws Exception {
        h.consume(jackson(BYTES[0]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test1_gson(Blackhole h) {
        h.consume(gson(CHARS[0]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test1_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(BYTES[0]));
    }

    // test2

    @org.openjdk.jmh.annotations.Benchmark
    public void test2_vjson(Blackhole h) {
        h.consume(vjson(CHARS[1]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test2_jackson(Blackhole h) throws Exception {
        h.consume(jackson(BYTES[1]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test2_gson(Blackhole h) {
        h.consume(gson(CHARS[1]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test2_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(BYTES[1]));
    }

    // test3

    @org.openjdk.jmh.annotations.Benchmark
    public void test3_vjson(Blackhole h) {
        h.consume(vjson(CHARS[2]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test3_jackson(Blackhole h) throws Exception {
        h.consume(jackson(BYTES[2]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test3_gson(Blackhole h) {
        h.consume(gson(CHARS[2]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test3_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(BYTES[2]));
    }

    // test4

    @org.openjdk.jmh.annotations.Benchmark
    public void test4_vjson(Blackhole h) {
        h.consume(vjson(CHARS[3]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test4_jackson(Blackhole h) throws Exception {
        h.consume(jackson(BYTES[3]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test4_gson(Blackhole h) {
        h.consume(gson(CHARS[3]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test4_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(BYTES[3]));
    }

    // test5

    @org.openjdk.jmh.annotations.Benchmark
    public void test5_vjson(Blackhole h) {
        h.consume(vjson(CHARS[4]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test5_jackson(Blackhole h) throws Exception {
        h.consume(jackson(BYTES[4]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test5_gson(Blackhole h) {
        h.consume(gson(CHARS[4]));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void test5_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(BYTES[4]));
    }
}
