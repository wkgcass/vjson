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

Run `./gradlew clean coverage` to get the coverage report.

## use

### if you do no use kotlin

**gradle**

```groovy
// with module-info (also works for jdk8)
implementation 'io.vproxy:vjson:1.5.5-jdk9'
/* without module-info (if your toolchain doesn't work, use this version instead)
implementation 'io.vproxy:vjson:1.5.5'
*/
```

**maven**

```xml
<dependency>
  <groupId>io.vproxy</groupId>
  <artifactId>vjson</artifactId>
<!-- with module-info (also works for jdk8) -->
  <version>1.5.5-jdk9</version>
<!-- without module-info (if your toolchain doesn't work, use this version instead)
  <version>1.5.5</version>
-->
</dependency>
```

### if you are using kotlin

**gradle**

```groovy
implementation('io.vproxy:vjson:${vjsonVersion}') {
  exclude group: 'io.vproxy', module: 'kotlin-stdlib-lite'
}
```

**maven**

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.vproxy</groupId>
      <artifactId>vjson</artifactId>
      <version>${vjson.version}</version>
      <exclusions>
        <exclusion>
          <groupId>io.vproxy</groupId>
          <artifactId>kotlin-stdlib-lite</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</dependencyManagement>
```

## kotlin native

`vjson` is purely written in kotlin and only rely on kotlin stdlib, as a result it can be ported to kotlin native.

Run `./gradlew clean kotlinNative` to compile the source code to kotlin native version.

Also you can run the following commands to build a native executable:

```shell
./gradlew clean kotlinNative
rm -r misc/vjson_executable/src/nativeMain/kotlin/vjson/*
cp -r src/main/kotlin/vjson/* misc/vjson_executable/src/nativeMain/kotlin/vjson

# Then you can build kotlin native executable
cd misc/vjson_executable/
./gradlew clean nativeBinaries
```

Run with:

```shell
./build/bin/native/releaseExecutable/vjson_executable.kexe --help
```

## kotlin js

Kotlin JS has more restrictions than kotlin native, a standalone task is provided for kotlin js:

Run `./gradlew clean kotlinJs` to compile the source code to kotlin js version.

Also you can run the following commands to update the web interpreter code:

```shell
./gradlew clean kotlinJs
rm -r misc/online_vjson_script_interpreter/src/main/kotlin/vjson/*
cp -r src/main/kotlin/vjson/* misc/online_vjson_script_interpreter/src/main/kotlin/vjson

# Then you can run the interpreter:
cd misc/online_vjson_script_interpreter/
./gradlew clean run
```

## vpreprocessor

The same source code would be used on both jvm and kotlin native. Some jvm specific annotations and jdk classes are used to get better java interoperability. However they cannot be used when building kotlin native applications. To solve this problem, I developed a code preprocessor program which allow you to integrate `macro` into java/kotlin code using comments.

See [vpreprocessor/README.md](https://github.com/wkgcass/vjson/blob/master/src/main/kotlin/vpreprocessor/README.md) for more info.

Also note that the preprocessing directly rewrites source codes, so the building process requires you to keep git directory clean before doing preprocessing. A task `checkGit` is automatically invoked before `coverage`, `kotlinNative` and `kotlinJs`.

## example

**java**

```java
// basic
JSON.Instance result = JSON.parse("{\"hello\":\"world\"}");
String json = result.stringify();
String prettyJson = result.pretty();

// deserialize
Rule<Shop> shopRule = new ObjectRule<>(Shop::new)
    .put("name", Shop::setName, StringRule.get())
    .put("goods", Shop::setGoods, new ArrayRule<>(
        ArrayList::new,
        ArrayList::add,
        new ObjectRule<>(Good::new)
            .put("id", Good::setId, StringRule.get())
            .put("name", Good::setName, StringRule.get())
            .put("price", Good::setPrice, DoubleRule.get())
    ));
Shop shop = JSON.deserialize(jsonStr, shopRule);

// autotype
ObjectRule<Good> goodRule = new ObjectRule<>(Good::new)
    .put("id", Good::setId, StringRule.get())
    .put("name", Good::setName, StringRule.get())
    .put("price", Good::setPrice, DoubleRule.get());
ObjectRule<SpecialPriceGood> specialPriceGoodRule = new ObjectRule<>(SpecialPriceGood::new, goodRule)
    .put("originalPrice", SpecialPriceGood::setOriginalPrice, DoubleRule.get());
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

// advanced
new ObjectParser(new ParserOptions().setListener(...)); // hook points that the parsers will call
result.stringify(stringBuilder, stringifier); // customize the output format

// additional features
new ParserOptions().setStringSingleQuotes(true).setKeyNoQuotes(true);
                        // allow to parse strings like: {key: 'value'}
```

**kotlin**

```kotlin
// builder example with kotlin syntax
val shopRule = ObjectRule.builder(::ShopBuilder, { build() }) {
  put("name", StringRule) { name = it }
  put("goods", { goods = it }) {
    ArrayRule.builder({ ArrayList<Good>() }, { Collections.unmodifiableList(this) }, { add(it) }) {
      ObjectRule.builder(::GoodBuilder, { build() }) {
        put("id", StringRule) { id = it }
        put("name", StringRule) { name = it }
        put("price", DoubleRule) { price = it }
      }
    }
  }
}
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
     +----------------+-------------+---------------+----------------+-----------------------------+
     |                |             |               |                |                             |
     |                |             |               |                |                             |
 ObjectRule       ArrayRule     StringRule       BoolRule   +--------+--------+            NullableRule(Rule)
                                                            |        |        |                    |
                                                            |        |        |                    |
                                                         IntRule  LongRule DoubleRule              |
                                                                                                   |
                                                      NullAsFalseBoolRule NullAsZeroIntRule NullAsZeroLongRule NullAsZeroDoubleRule NullableStringRule
```
