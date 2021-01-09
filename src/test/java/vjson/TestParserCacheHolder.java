package vjson;

import org.junit.Test;
import vjson.parser.*;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestParserCacheHolder {
    @Test
    public void setParserCacheHolder() throws Exception {
        try {
            ParserUtils.setParserCacheHolder(null);
            fail();
        } catch (NullPointerException ignore) {
        }
        ParserUtils.setParserCacheHolder(new H());
        ParserCacheHolder holder = new H();
        ParserUtils.setParserCacheHolder(holder); // pass because not started
        assertEquals(1, ((JSON.Array) JSON.parse("[1]")).getInt(0));

        try {
            ParserUtils.setParserCacheHolder(new H());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("parser cache holder already set", e.getMessage());
        }
        Field holderField = ParserUtils.class.getDeclaredField("holder");
        holderField.setAccessible(true);
        holderField.set(null, new H2());
        try {
            ParserUtils.setParserCacheHolder(new H2());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("parser cache holder already set", e.getMessage());
        }

        // must set back
        holderField.set(null, holder);
    }
}

class H extends DefaultParserCacheHolder {
}

class H2 implements ParserCacheHolder {
    @Override
    public ArrayParser threadLocalArrayParser() {
        return null;
    }

    @Override
    public void threadLocalArrayParser(ArrayParser parser) {

    }

    @Override
    public ObjectParser threadLocalObjectParser() {
        return null;
    }

    @Override
    public void threadLocalObjectParser(ObjectParser parser) {

    }

    @Override
    public StringParser threadLocalStringParser() {
        return null;
    }

    @Override
    public void threadLocalStringParser(StringParser parser) {

    }

    @Override
    public ArrayParser threadLocalArrayParserJavaObject() {
        return null;
    }

    @Override
    public void threadLocalArrayParserJavaObject(ArrayParser parser) {

    }

    @Override
    public ObjectParser threadLocalObjectParserJavaObject() {
        return null;
    }

    @Override
    public void threadLocalObjectParserJavaObject(ObjectParser parser) {

    }

    @Override
    public StringParser threadLocalStringParserJavaObject() {
        return null;
    }

    @Override
    public void threadLocalStringParserJavaObject(StringParser parser) {

    }
}