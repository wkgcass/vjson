package vjson.util.typerule;

import vjson.deserializer.rule.DoubleRule;
import vjson.deserializer.rule.ObjectRule;

import java.util.Objects;

public class TypeRuleA extends TypeRuleBase {
    public static final ObjectRule<TypeRuleA> aRule = new ObjectRule<>(TypeRuleA::new, TypeRuleBase.baseRule)
        .put("a", TypeRuleA::setA, DoubleRule.get());

    public double a;

    public TypeRuleA() {
    }

    public TypeRuleA(TypeRuleBase base, double a) {
        super(base);
        this.a = a;
    }

    public void setA(double a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeRuleA typeRuleA = (TypeRuleA) o;
        return Math.abs(typeRuleA.a - a) < 0.0000000001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), a);
    }

    @Override
    public String toString() {
        return "TypeRuleA{" +
            "a=" + a +
            ", x=" + x +
            ", y='" + y + '\'' +
            '}';
    }
}
