# vjson

[中文](https://github.com/wkgcass/vjson/blob/master/README_ZH.md) | English

## intro

vjson is a light weight json parser/deserializer/builder lib built for java/kotlin and kotlin native.

The lib focuses on providing the original json representation in java object form, and letting you build any json string using only java method invocations, or deserialize into java objects without reflection, which is a great feature for building `native-image`s.

## performance

Check `src/test/java/vjson/bench` for more info. You may run the jmh tests or a simple test directly with the main method.

## reliability

`vjson` has 100% line + branch coverage.

Run `src/test/java/vjson/Suite.java` to test the lib.

## use

Copy and paste `src/main/java/kotlin` to your source directory, and enjoy.

If you are not using kotlin, you need to add the following code snippet to your `build.gradle` configuration.

```groovy
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.4.31'
}
dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
}
compileKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = ['-Xjvm-default=enable']
    }
}
```

>If you want to use vjson without kotlin runtime, you may checkout to commit `00577677156cd9394ea2a32028f684cbce178065`, which is the last java version.

## example

```java
// basic
JSON.Instance result = JSON.parse("{\"hello\":\"world\"}");
String json = result.stringify();
String prettyJson = result.pretty();

// deserialize
Rule<Shop> shopRule = new ObjectRule<>(Shop::new)
    .put("name", Shop::setName, new StringRule())
    .put("goods", Shop::setGoods, new ArrayRule<>(
        ArrayList::new,
        ArrayList::add,
        new ObjectRule<>(Good::new)
            .put("id", Good::setId, new StringRule())
            .put("name", Good::setName, new StringRule())
            .put("price", Good::setPrice, new DoubleRule())
    ));
Shop shop = JSON.deserialize(jsonStr, shopRule);

// autotype
ObjectRule<Good> goodRule = new ObjectRule<>(Good::new)
    .put("id", Good::setId, new StringRule())
    .put("name", Good::setName, new StringRule())
    .put("price", Good::setPrice, new DoubleRule());
ObjectRule<SpecialPriceGood> specialPriceGoodRule = new ObjectRule<>(SpecialPriceGood::new, goodRule)
    .put("originalPrice", SpecialPriceGood::setOriginalPrice, new DoubleRule());
TypeRule<Good> typeGoodRule = new TypeRule<>(Good.class, goodRule)
    .type("special", specialPriceGoodRule);
    // or .type(SpecialPriceGood.class, specialPriceGoodRule);
// the TypeRule<> can also be constructed without arguments
// deserializes json like: {"@type": "...", ...}

// retrieve
javaObject = result.toJavaObject(); // List,Map,String or primitive boxing types
javaObject = JSON.parseToJavaObject("{\"hello\":\"world\"}");
String value = ((JSON.Object) result).getString("hello"); // world

// parse
ObjectParser parser = new ObjectParser();
parser.feed("{\"hel");    // return null here
parser.feed("lo\":\"wo"); // return null here
parser.feed("rld\"}");    // return JSON.Object here
                          // you may use CharStream instead of String to feed the parser
    // if it's the last piece to feed the parser:
    parser.last("rld\"}");

// construct complex objects
new ObjectBuilder().put("id", 1).put("name", "pizza").build(); // JSON.Object
new ArrayBuilder().add(3.14).addObject(o -> ...).addArray(a -> ...).build(); // JSON.Array

// serialize
new SimpleInteger(1).stringify(); // 1
new SimpleString("hello\nworld").stringify(); // "hello\nworld"
Transformer tf = new Transformer();
tf.transform(javaObject).stringify();

// advanced
new ObjectParser(new ParserOptions().setListener(...)); // hook points that the parsers will call
result.stringify(stringBuilder, stringifier); // customize the output format
tf.addRule(MyBean.class, bean -> ...); // customize transforming rules

// additional features
new ParserOptions().setStringSingleQuotes(true).setKeyNoQuotes(true);
                        // allow to parse strings like: {key: 'value'}
```

## lib

```
                                     JSON.Instance
                                           ^
                                           |
     +----------------+-------------+------+--------+----------------+----------------+
     |                |             |               |                |                |
 JSON.Object      JSON.Array   JSON.String      JSON.Bool       JSON.Number       JSON.Null
     ^                ^             ^               ^                ^                ^
     |                |             |               |                |                |
     |                |             |               |                |                |
SimpleObject     SimpleArray   SimpleString     SimpleBool           |            SimpleNull
                                                                     |
                                                +--------------------+---------------------+
                                                |                    |                     |
                                           JSON.Integer          JSON.Long            JSON.Double <---- SimpleDouble
                                                ^                    ^                     ^
                                                |                    |                     |
                                                |                    |                     |
                                           SimpleInteger         SimpleLong           JSON.Exp <-------- SimpleExp


                                          Parser
                                             ^
                                             |
     +----------------+-------------+--------+------+----------------+--------------+
     |                |             |               |                |              |
     |                |             |               |                |              |
ObjectParser     ArrayParser   StringParser     BoolParser      NumberParser    NullParser


                                         CharStream
                                             ^
                                             |
                                +------------+--------------+
                                |                           |
                        CharArrayCharStream      UTF8ByteArrayCharStream


                                     Rule ----------------------------> DeserializeParserListener
                                       ^
                                       |
                                       |
     +----------------+-------------+---------------+----------------+
     |                |             |               |                |
     |                |             |               |                |
 ObjectRule       ArrayRule    +----+----+       BoolRule   +--------+--------+
                               |         |                  |        |        |
                               |         |                  |        |        |
                         StringRule NullableStringRule   IntRule  LongRule DoubleRule
```
