package vjson;

import org.junit.Test;
import vjson.parser.ArrayParser;
import vjson.parser.KotlinNativeParserCacheHolder;
import vjson.parser.ObjectParser;
import vjson.parser.StringParser;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class TestKNParserCacheHolder {
    private final KotlinNativeParserCacheHolder holder = new KotlinNativeParserCacheHolder();

    @Test
    public void arrayParser() {
        ArrayParser arrayParser = new ArrayParser();
        assertNull(holder.threadLocalArrayParser());
        holder.threadLocalArrayParser(arrayParser);
        assertSame(arrayParser, holder.threadLocalArrayParser());
    }

    @Test
    public void objectParser() {
        ObjectParser objectParser = new ObjectParser();
        assertNull(holder.threadLocalObjectParser());
        holder.threadLocalObjectParser(objectParser);
        assertSame(objectParser, holder.threadLocalObjectParser());
    }

    @Test
    public void stringParser() {
        StringParser stringParser = new StringParser();
        assertNull(holder.threadLocalStringParser());
        holder.threadLocalStringParser(stringParser);
        assertSame(stringParser, holder.threadLocalStringParser());
    }

    @Test
    public void arrayParserJavaObject() {
        ArrayParser arrayParser = new ArrayParser();
        assertNull(holder.threadLocalArrayParserJavaObject());
        holder.threadLocalArrayParserJavaObject(arrayParser);
        assertSame(arrayParser, holder.threadLocalArrayParserJavaObject());
    }

    @Test
    public void objectParserJavaObject() {
        ObjectParser objectParser = new ObjectParser();
        assertNull(holder.threadLocalObjectParserJavaObject());
        holder.threadLocalObjectParserJavaObject(objectParser);
        assertSame(objectParser, holder.threadLocalObjectParserJavaObject());
    }

    @Test
    public void stringParserJavaObject() {
        StringParser stringParser = new StringParser();
        assertNull(holder.threadLocalStringParserJavaObject());
        holder.threadLocalStringParserJavaObject(stringParser);
        assertSame(stringParser, holder.threadLocalStringParserJavaObject());
    }
}
