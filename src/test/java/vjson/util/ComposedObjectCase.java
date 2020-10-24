package vjson.util;

import vjson.deserializer.rule.ArrayRule;
import vjson.deserializer.rule.ObjectRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComposedObjectCase extends SimpleObjectCase {
    public static final ObjectRule<ComposedObjectCase> composedObjectCaseRule = new ObjectRule<>(ComposedObjectCase::new, simpleObjectCaseRule);

    static {
        ComposedObjectCase.composedObjectCaseRule
            .put("object1", (o, v) -> o.object1 = v, SimpleObjectCase.simpleObjectCaseRule)
            .put("object2", (o, v) -> o.object2 = v, SimpleObjectCase.simpleObjectCaseRule)
            .put("nullObject", (o, v) -> o.nullObject = v, SimpleObjectCase.simpleObjectCaseRule)
            .put("array1", (o, v) -> o.array1 = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, SimpleObjectCase.simpleObjectCaseRule))
            .put("array2", (o, v) -> o.array2 = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, SimpleObjectCase.simpleObjectCaseRule))
            .put("nullArray", (o, v) -> o.nullArray = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, SimpleObjectCase.simpleObjectCaseRule))
            .put("arrayWithNull", (o, v) -> o.arrayWithNull = v, new ArrayRule<List<SimpleObjectCase>, SimpleObjectCase>(ArrayList::new, List::add, SimpleObjectCase.simpleObjectCaseRule))
            .put("nullableMoreObject", (o, v) -> o.nullableMoreObject = v, ComposedObjectCase.composedObjectCaseRule)
            .put("nullableMoreList", (o, v) -> o.nullableMoreList = v, new ArrayRule<List<ComposedObjectCase>, ComposedObjectCase>(ArrayList::new, List::add, ComposedObjectCase.composedObjectCaseRule));
    }

    public SimpleObjectCase object1;
    public SimpleObjectCase object2;
    public SimpleObjectCase nullObject;
    public List<SimpleObjectCase> array1;
    public List<SimpleObjectCase> array2;
    public List<SimpleObjectCase> nullArray;
    public List<SimpleObjectCase> arrayWithNull;
    public ComposedObjectCase nullableMoreObject;
    public List<ComposedObjectCase> nullableMoreList;

    public ComposedObjectCase() {
    }

    public ComposedObjectCase(SimpleObjectCase o,
                              SimpleObjectCase object1,
                              SimpleObjectCase object2,
                              SimpleObjectCase nullObject,
                              List<SimpleObjectCase> array1,
                              List<SimpleObjectCase> array2,
                              List<SimpleObjectCase> nullArray,
                              List<SimpleObjectCase> arrayWithNull,
                              ComposedObjectCase nullableMoreObject,
                              List<ComposedObjectCase> nullableMoreList) {
        super(o);
        this.object1 = object1;
        this.object2 = object2;
        this.nullObject = nullObject;
        this.array1 = array1;
        this.array2 = array2;
        this.nullArray = nullArray;
        this.arrayWithNull = arrayWithNull;
        this.nullableMoreObject = nullableMoreObject;
        this.nullableMoreList = nullableMoreList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComposedObjectCase that = (ComposedObjectCase) o;
        return Objects.equals(object1, that.object1) &&
            Objects.equals(object2, that.object2) &&
            Objects.equals(nullObject, that.nullObject) &&
            Objects.equals(array1, that.array1) &&
            Objects.equals(array2, that.array2) &&
            Objects.equals(nullArray, that.nullArray) &&
            Objects.equals(arrayWithNull, that.arrayWithNull) &&
            Objects.equals(nullableMoreObject, that.nullableMoreObject) &&
            Objects.equals(nullableMoreList, that.nullableMoreList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), object1, object2, nullObject, array1, array2, nullArray, arrayWithNull, nullableMoreObject, nullableMoreList);
    }

    @Override
    public String toString() {
        return "ComposedObjectCase{" +
            "intValue=" + intValue +
            ", longValue=" + longValue +
            ", doubleValue=" + doubleValue +
            ", boolValue=" + boolValue +
            ", stringValue='" + stringValue + '\'' +
            ", nullValue=" + nullValue +
            ", object1=" + object1 +
            ", object2=" + object2 +
            ", nullObject=" + nullObject +
            ", array1=" + array1 +
            ", array2=" + array2 +
            ", nullArray=" + nullArray +
            ", arrayWithNull=" + arrayWithNull +
            ", nullableMoreObject=" + nullableMoreObject +
            ", nullableMoreList=" + nullableMoreList +
            '}';
    }
}
