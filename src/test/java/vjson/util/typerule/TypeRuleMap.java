package vjson.util.typerule;

import vjson.deserializer.rule.ObjectRule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class TypeRuleMap extends TypeRuleBase {
    public static final ObjectRule<TypeRuleMap> mapRule = new ObjectRule<>(TypeRuleMap::new)
        .put("map", TypeRuleMap::setMap, new ObjectRule<Map<String, String>>(LinkedHashMap::new)
            .addExtraRule((o, key, value) -> o.put(key, (String) value))
        );

    public Map<String, String> map;

    public TypeRuleMap() {
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public TypeRuleMap put(String key, String value) {
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        map.put(key, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TypeRuleMap that = (TypeRuleMap) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), map);
    }
}
