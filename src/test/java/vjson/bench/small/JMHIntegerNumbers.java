package vjson.bench.small;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import vjson.JSON;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleInteger;
import vjson.simple.SimpleLong;

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
public class JMHIntegerNumbers {
    private char[] intNumbersChars;
    private byte[] intNumbersBytes;
    private char[] longNumbersChars;
    private byte[] longNumbersBytes;

    @Setup
    public void setUp() {
        Random rnd = new Random(12345);
        List<JSON.Integer> intNumbers = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            intNumbers.add(new SimpleInteger(rnd.nextInt()));
        }
        String intNumbersString = new SimpleArray(intNumbers).stringify();
        this.intNumbersChars = intNumbersString.toCharArray();
        this.intNumbersBytes = intNumbersString.getBytes();
        List<JSON.Long> longNumbers = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            longNumbers.add(new SimpleLong(rnd.nextLong() + Integer.MAX_VALUE));
        }
        String longNumbersString = new SimpleArray(longNumbers).stringify();
        this.longNumbersChars = longNumbersString.toCharArray();
        this.longNumbersBytes = longNumbersString.getBytes();
    }

    @Benchmark
    public void test_int_numbers_vjson(Blackhole h) {
        h.consume(vjson(intNumbersChars));
    }

    @Benchmark
    public void test_long_numbers_vjson(Blackhole h) {
        h.consume(vjson(longNumbersChars));
    }

    @Benchmark
    public void test_int_numbers_jackson(Blackhole h) throws Exception {
        h.consume(jackson(intNumbersBytes));
    }

    @Benchmark
    public void test_long_numbers_jackson(Blackhole h) throws Exception {
        h.consume(jackson(longNumbersBytes));
    }

    @Benchmark
    public void test_int_numbers_gson(Blackhole h) {
        h.consume(gson(intNumbersChars));
    }

    @Benchmark
    public void test_long_numbers_gson(Blackhole h) {
        h.consume(gson(longNumbersChars));
    }

    @Benchmark
    public void test_int_numbers_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(intNumbersBytes));
    }

    @Benchmark
    public void test_long_numbers_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(longNumbersBytes));
    }
}
