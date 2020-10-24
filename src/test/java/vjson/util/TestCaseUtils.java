package vjson.util;

import vjson.JSON;
import vjson.simple.SimpleNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestCaseUtils {
    private TestCaseUtils() {
    }

    public static String randomString() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int len = random.nextInt(10) + 5;
        char[] c = new char[len];
        for (int i = 0; i < c.length; ++i) {
            c[i] = (char) (random.nextInt('z' - 'a' + 1) + 'a');
        }
        return new String(c);
    }

    public static SimpleObjectCase randomSimpleObjectCase() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new SimpleObjectCase(random.nextInt(), random.nextLong(), random.nextDouble(), random.nextBoolean(), randomString(), null);
    }

    private static ObjectBuilder getSimpleObjectCaseObjectBuilder(SimpleObjectCase o) {
        return new ObjectBuilder()
            .put("intValue", o.intValue)
            .put("longValue", o.longValue)
            .put("doubleValue", o.doubleValue)
            .put("boolValue", o.boolValue)
            .put("stringValue", o.stringValue)
            .put("nullValue", null);
    }

    public static JSON.Instance getSimpleObjectCaseJSON(SimpleObjectCase o) {
        if (o == null) {
            return new SimpleNull();
        }
        return getSimpleObjectCaseObjectBuilder(o).build();
    }

    public static List<SimpleObjectCase> randomSimpleObjectCaseList() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int len = random.nextInt(3) + 1;
        List<SimpleObjectCase> ret = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            ret.add(randomSimpleObjectCase());
        }
        return ret;
    }

    public static JSON.Instance getSimpleObjectCaseListJSON(List<SimpleObjectCase> ls) {
        if (ls == null) {
            return new SimpleNull();
        }
        Iterator<SimpleObjectCase> ite = ls.iterator();
        ArrayBuilder ret = new ArrayBuilder();
        while (ite.hasNext()) {
            SimpleObjectCase simple = ite.next();
            ret.addInst(getSimpleObjectCaseJSON(simple));
        }
        return ret.build();
    }

    public static ComposedObjectCase randomComposedObjectCase(int recurse) {
        if (recurse >= 5) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (recurse >= 3) {
            if (random.nextDouble() < 0.5) {
                return null;
            }
        }
        return new ComposedObjectCase(randomSimpleObjectCase(),
            randomSimpleObjectCase(), randomSimpleObjectCase(), null,
            randomSimpleObjectCaseList(), randomSimpleObjectCaseList(), null, Arrays.asList(null, null),
            randomComposedObjectCase(recurse + 1), randomComposedObjectCaseList(recurse + 1));
    }

    public static JSON.Instance getComposedObjectCaseJSON(ComposedObjectCase o) {
        if (o == null) {
            return new SimpleNull();
        }
        return getSimpleObjectCaseObjectBuilder(o)
            .putInst("object1", getSimpleObjectCaseJSON(o.object1))
            .putInst("object2", getSimpleObjectCaseJSON(o.object2))
            .putInst("nullObject", getSimpleObjectCaseJSON(o.nullObject))
            .putInst("array1", getSimpleObjectCaseListJSON(o.array1))
            .putInst("array2", getSimpleObjectCaseListJSON(o.array2))
            .putInst("nullArray", getSimpleObjectCaseListJSON(o.nullArray))
            .putInst("arrayWithNull", getSimpleObjectCaseListJSON(o.arrayWithNull))
            .putInst("nullableMoreObject", getComposedObjectCaseJSON(o.nullableMoreObject))
            .putInst("nullableMoreList", getComposedObjectCaseListJSON(o.nullableMoreList))
            .build();
    }

    public static List<ComposedObjectCase> randomComposedObjectCaseList(int recurse) {
        if (recurse >= 5) {
            return null;
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (recurse >= 3) {
            if (random.nextDouble() < 0.5) {
                return null;
            }
        }
        int len = random.nextInt(3) + 1;
        List<ComposedObjectCase> ret = new ArrayList<>(len);
        for (int i = 0; i < len; ++i) {
            ret.add(randomComposedObjectCase(recurse + 1));
        }
        return ret;
    }

    public static JSON.Instance getComposedObjectCaseListJSON(List<ComposedObjectCase> ls) {
        if (ls == null) {
            return new SimpleNull();
        }
        Iterator<ComposedObjectCase> ite = ls.iterator();
        ArrayBuilder builder = new ArrayBuilder();
        while (ite.hasNext()) {
            ComposedObjectCase o = ite.next();
            builder.addInst(getComposedObjectCaseJSON(o));
        }
        return builder.build();
    }
}
