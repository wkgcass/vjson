package vjson.util.typerule;

import vjson.deserializer.rule.ObjectRule;
import vjson.deserializer.rule.Rule;

import java.util.Objects;

public class Box {
    public static final Rule<Box> boxRule = new ObjectRule<>(Box::new)
        .put("typed", Box::setTyped, TypeRuleBase.getTypeRule());

    public TypeRuleBase typed;

    public Box() {
    }

    public Box(TypeRuleBase typed) {
        this.typed = typed;
    }

    public void setTyped(TypeRuleBase typed) {
        this.typed = typed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Objects.equals(typed, box.typed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typed);
    }
}
