package vjson.bench;

import vjson.parser.ArrayParser;
import vjson.parser.ObjectParser;
import vjson.parser.ParserCacheHolder;
import vjson.parser.StringParser;
import vjson.util.StringDictionary;

public class VJsonThread extends Thread {
    private ArrayParser threadLocalArrayParser;
    private ObjectParser threadLocalObjectParser;
    private StringParser threadLocalStringParser;

    private ArrayParser threadLocalArrayParserJavaObject;
    private ObjectParser threadLocalObjectParserJavaObject;
    private StringParser threadLocalStringParserJavaObject;

    private StringDictionary threadLocalKeyDictionary;

    public VJsonThread(Runnable r) {
        super(r);
    }

    public static class P implements ParserCacheHolder {
        @Override
        public ArrayParser threadLocalArrayParser() {
            return ((VJsonThread) Thread.currentThread()).threadLocalArrayParser;
        }

        @Override
        public void threadLocalArrayParser(ArrayParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalArrayParser = parser;
        }

        @Override
        public ObjectParser threadLocalObjectParser() {
            return ((VJsonThread) Thread.currentThread()).threadLocalObjectParser;
        }

        @Override
        public void threadLocalObjectParser(ObjectParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalObjectParser = parser;
        }

        @Override
        public StringParser threadLocalStringParser() {
            return ((VJsonThread) Thread.currentThread()).threadLocalStringParser;
        }

        @Override
        public void threadLocalStringParser(StringParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalStringParser = parser;
        }

        @Override
        public ArrayParser threadLocalArrayParserJavaObject() {
            return ((VJsonThread) Thread.currentThread()).threadLocalArrayParserJavaObject;
        }

        @Override
        public void threadLocalArrayParserJavaObject(ArrayParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalArrayParserJavaObject = parser;
        }

        @Override
        public ObjectParser threadLocalObjectParserJavaObject() {
            return ((VJsonThread) Thread.currentThread()).threadLocalObjectParserJavaObject;
        }

        @Override
        public void threadLocalObjectParserJavaObject(ObjectParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalObjectParserJavaObject = parser;
        }

        @Override
        public StringParser threadLocalStringParserJavaObject() {
            return ((VJsonThread) Thread.currentThread()).threadLocalStringParserJavaObject;
        }

        @Override
        public void threadLocalStringParserJavaObject(StringParser parser) {
            ((VJsonThread) Thread.currentThread()).threadLocalStringParserJavaObject = parser;
        }

        @Override
        public StringDictionary threadLocalKeyDictionary() {
            return ((VJsonThread) Thread.currentThread()).threadLocalKeyDictionary;
        }

        @Override
        public void threadLocalKeyDictionary(StringDictionary dic) {
            ((VJsonThread) Thread.currentThread()).threadLocalKeyDictionary = dic;
        }
    }
}
