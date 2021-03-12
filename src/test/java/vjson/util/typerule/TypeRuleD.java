package vjson.util.typerule;

import vjson.deserializer.rule.ObjectRule;

import java.util.Objects;

public class TypeRuleD extends TypeRuleBase {
    private static ObjectRule<TypeRuleD> dRule;

    public TypeRuleBase d;

    public TypeRuleD() {
    }

    public TypeRuleD(TypeRuleBase base, TypeRuleBase d) {
        super(base);
        this.d = d;
    }

    public void setD(TypeRuleBase d) {
        this.d = d;
    }

    public static ObjectRule<TypeRuleD> dRule() {
        if (dRule != null) {
            return dRule;
        }
        synchronized (TypeRuleD.class) {
            if (dRule != null) {
                return dRule;
            }
            dRule = new ObjectRule<>(TypeRuleD::new, TypeRuleBase.baseRule)
                .put("d", TypeRuleD::setD, TypeRuleBase.getTypeRule());
            return dRule;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeRuleD typeRuleD = (TypeRuleD) o;
        return Objects.equals(d, typeRuleD.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), d);
    }

    @Override
    public String toString() {
        return "TypeRuleD{" +
            "x=" + x +
            ", y='" + y + '\'' +
            ", d=" + d +
            '}';
    }
}
