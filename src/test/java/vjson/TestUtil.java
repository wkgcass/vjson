package vjson;

import org.junit.Test;
import vjson.util.AppendableMap;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestUtil {
    @Test
    public void appendAll() {
        AppendableMap<String, String> map = new AppendableMap<>();
        map.append("0", "1").append("2", "3");
        map.appendAll(new HashMap<String, String>() {{
            put("a", "b");
            put("c", "d");
            put("e", "f");
            put("g", "h");
        }});
        assertEquals(new HashMap<String, String>() {{
            put("0", "1");
            put("2", "3");
            put("a", "b");
            put("c", "d");
            put("e", "f");
            put("g", "h");
        }}, map);
    }
}
