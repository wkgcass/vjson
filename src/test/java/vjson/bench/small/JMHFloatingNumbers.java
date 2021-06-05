package vjson.bench.small;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import vjson.JSON;
import vjson.simple.SimpleArray;
import vjson.simple.SimpleDouble;

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
public class JMHFloatingNumbers {
    private char[] floatingNumbersChars;
    private byte[] floatingNumbersBytes;

    @Setup
    public void setUp() {
        Random rnd = new Random(12345);
        List<JSON.Double> floatingNumbers = new LinkedList<>();
        for (int i = 0; i < 100; ++i) {
            floatingNumbers.add(new SimpleDouble(rnd.nextDouble()));
        }
        String floatingNumbersString = new SimpleArray(floatingNumbers).stringify();
        this.floatingNumbersChars = floatingNumbersString.toCharArray();
        this.floatingNumbersBytes = floatingNumbersString.getBytes();
    }

    @Benchmark
    public void test_floating_numbers_vjson(Blackhole h) {
        h.consume(vjson(floatingNumbersChars));
    }

    @Benchmark
    public void test_floating_numbers_jackson(Blackhole h) throws Exception {
        h.consume(jackson(floatingNumbersBytes));
    }

    @Benchmark
    public void test_floating_numbers_gson(Blackhole h) {
        h.consume(gson(floatingNumbersChars));
    }

    @Benchmark
    public void test_floating_numbers_fastjson(Blackhole h) throws Exception {
        h.consume(fastjson(floatingNumbersBytes));
    }
}
