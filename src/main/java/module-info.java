module vjson {
    requires kotlin.stdlib;

    exports vjson;
    exports vjson.cs;
    exports vjson.deserializer;
    exports vjson.deserializer.rule;
    exports vjson.ex;
    exports vjson.listener;
    exports vjson.parser;
    exports vjson.pl;
    exports vjson.pl.ast;
    exports vjson.pl.inst;
    exports vjson.pl.token;
    exports vjson.pl.type;
    exports vjson.pl.type.lang;
    exports vjson.simple;
    exports vjson.stringifier;
    exports vjson.util;
    exports vjson.util.collection;
    exports vjson.util.functional;
    exports vpreprocessor;
    exports vpreprocessor.ast;
    exports vpreprocessor.semantic;
    exports vpreprocessor.token;
}
