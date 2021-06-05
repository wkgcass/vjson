package vjson.bench.small;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import vjson.JSON;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleString;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static vjson.bench.BenchHelper.*;

@SuppressWarnings("DefaultAnnotationParam")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, jvmArgs = {"-XX:+UseG1GC", "-Xms2G", "-Xmx2G", "-XX:NewSize=1500M", "-XX:MaxNewSize=1500M"})
@Threads(1)
@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
public class JMHStrings {
    private static final char[] availableChars = ("" +
        "abcdefghijklmnopqrstuvwxyz01234567ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        "").toCharArray();

    private char[] stringsChars;
    private byte[] stringsBytes;

    @Setup
    public void setUp() {
        Random rnd = new Random(12345);
        List<JSON.String> strings = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            int len = rnd.nextInt(40) + 80;
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

    @Benchmark
    public void test_strings_vjson(Blackhole h) {
        h.consume(vjson(stringsChars));
    }

    @Benchmark
    public void test_strings_jackson(Blackhole h) throws Exception {
        h.consume(jackson(stringsBytes));
    }

    @Benchmark
    public void test_strings_gson(Blackhole h) {
        h.consume(gson(stringsChars));
    }

    @Benchmark
    public void test_strings_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(stringsBytes));
    }
}
