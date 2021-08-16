package vjson.util.typerule;

import kotlin.jvm.internal.Reflection;
import vjson.deserializer.rule.IntRule;
import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.StringRule;
import vjson.deserializer.rule.TypeRule;

import java.util.Objects;

public class TypeRuleBase {
    public static final ObjectRule<TypeRuleBase> baseRule = new ObjectRule<>(TypeRuleBase::new)
        .put("x", TypeRuleBase::setX, IntRule.get())
        .put("y", TypeRuleBase::setY, StringRule.get());
    private static TypeRule<TypeRuleBase> typeBaseRule;

    public int x;
    public String y;

    public TypeRuleBase() {
    }

    public TypeRuleBase(int x, String y) {
        this.x = x;
        this.y = y;
    }

    public TypeRuleBase(TypeRuleBase base) {
        this(base.x, base.y);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeRuleBase that = (TypeRuleBase) o;
        return x == that.x && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @SuppressWarnings("unchecked")
    public static TypeRule<TypeRuleBase> getTypeRule() {
        if (typeBaseRule != null) {
            return typeBaseRule;
        }
        synchronized (TypeRuleBase.class) {
            if (typeBaseRule != null) {
                return typeBaseRule;
            }
            TypeRule<TypeRuleBase> rule = new TypeRule<>();
            typeBaseRule = rule;
            rule.type(Reflection.getOrCreateKotlinClass(TypeRuleBase.class), baseRule)
                .type(Reflection.getOrCreateKotlinClass(TypeRuleA.class), TypeRuleA.aRule)
                .type(Reflection.getOrCreateKotlinClass(TypeRuleB.class), TypeRuleB.bRule)
                .type(Reflection.getOrCreateKotlinClass(TypeRuleC.class), TypeRuleC.cRule)
                .type(Reflection.getOrCreateKotlinClass(TypeRuleD.class), TypeRuleD.dRule())
                .type(Reflection.getOrCreateKotlinClass(TypeRuleMap.class), TypeRuleMap.mapRule);
            return rule;
        }
    }

    @Override
    public String toString() {
        return "TypeRuleBase{" +
            "x=" + x +
            ", y='" + y + '\'' +
            '}';
    }
}
