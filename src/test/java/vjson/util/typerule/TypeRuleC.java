package vjson.util.typerule;

import vjson.deserializer.rule.ObjectRule;
import vjson.util.SimpleObjectCase;

import java.util.Objects;

public class TypeRuleC extends TypeRuleBase {
    public static final ObjectRule<TypeRuleC> cRule = new ObjectRule<>(TypeRuleC::new, TypeRuleBase.baseRule)
        .put("c", TypeRuleC::setC, SimpleObjectCase.simpleObjectCaseRule);

    public SimpleObjectCase c;

    public TypeRuleC() {
    }

    public TypeRuleC(TypeRuleBase base, SimpleObjectCase c) {
        super(base);
        this.c = c;
    }

    public void setC(SimpleObjectCase c) {
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeRuleC typeRuleC = (TypeRuleC) o;
        return Objects.equals(c, typeRuleC.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), c);
    }

    @Override
    public String toString() {
        return "TypeRuleC{" +
            "x=" + x +
            ", y='" + y + '\'' +
            ", c=" + c +
            '}';
    }
}
