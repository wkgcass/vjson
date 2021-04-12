package vjson.util;

import vjson.deserializer.rule.*;

import java.util.Objects;

public class SimpleObjectCase {
    public static final ObjectRule<SimpleObjectCase> simpleObjectCaseRule = new ObjectRule<>(SimpleObjectCase::new)
        .put("intValue", (o, v) -> o.intValue = v, IntRule.get())
        .put("longValue", (o, v) -> o.longValue = v, LongRule.get())
        .put("doubleValue", (o, v) -> o.doubleValue = v, DoubleRule.get())
        .put("boolValue", (o, v) -> o.boolValue = v, BoolRule.get())
        .put("stringValue", (o, v) -> o.stringValue = v, StringRule.get())
        .put("nullValue", (o, v) -> o.nullValue = v, NullableStringRule.get());

    public int intValue;
    public long longValue;
    public double doubleValue;
    public boolean boolValue;
    public String stringValue;
    public Object nullValue;

    public SimpleObjectCase() {
    }

    public SimpleObjectCase(int intValue, long longValue, double doubleValue, boolean boolValue, String stringValue, Object nullValue) {
        this.intValue = intValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.boolValue = boolValue;
        this.stringValue = stringValue;
        this.nullValue = nullValue;
    }

    public SimpleObjectCase(SimpleObjectCase o) {
        this(o.intValue, o.longValue, o.doubleValue, o.boolValue, o.stringValue, o.nullValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleObjectCase that = (SimpleObjectCase) o;
        return intValue == that.intValue &&
            longValue == that.longValue &&
            Math.abs(that.doubleValue - doubleValue) < 0.0000000001 &&
            boolValue == that.boolValue &&
            Objects.equals(stringValue, that.stringValue) &&
            Objects.equals(nullValue, that.nullValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue, longValue, doubleValue, boolValue, stringValue, nullValue);
    }

    @Override
    public String toString() {
        return "SimpleObjectCase{" +
            "intValue=" + intValue +
            ", longValue=" + longValue +
            ", doubleValue=" + doubleValue +
            ", boolValue=" + boolValue +
            ", stringValue='" + stringValue + '\'' +
            ", nullValue=" + nullValue +
            '}';
    }
}
