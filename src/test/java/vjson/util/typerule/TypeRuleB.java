package vjson.util.typerule;

import vjson.deserializer.rule.LongRule;
import vjson.deserializer.rule.ObjectRule;

import java.util.Objects;

public class TypeRuleB extends TypeRuleBase {
    public static final ObjectRule<TypeRuleB> bRule = new ObjectRule<>(TypeRuleB::new, TypeRuleBase.baseRule)
        .put("b", TypeRuleB::setB, LongRule.get());

    public long b;

    public TypeRuleB() {
    }

    public TypeRuleB(TypeRuleBase base, long b) {
        super(base);
        this.b = b;
    }

    public void setB(long b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeRuleB typeRuleB = (TypeRuleB) o;
        return b == typeRuleB.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), b);
    }

    @Override
    public String toString() {
        return "TypeRuleB{" +
            "b=" + b +
            ", x=" + x +
            ", y='" + y + '\'' +
            '}';
    }
}
