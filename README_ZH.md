# vjson

中文 | [English](https://github.com/wkgcass/vjson/blob/master/README.md)

## 简介

vjson是一个轻量级的json parser/deserializer/builder 库，可以运行于java/kotlin以及kotlin native上。

vjson致力于用java对象还原最原始的json结构。你可以通过简单的java方法调用构建任意json字符串。你也可以不借助反射功能将json反序列化为java对象，在构建`native-image`时这个特性非常实用。

## 性能

可以查看`src/test/java/vjson/bench`以获取更多信息。你可以执行jmh测试，或者直接通过一个主函数运行简易的测试。

## 可靠性

`vjson`有100%的行覆盖率和分支覆盖率。

执行`src/test/java/vjson/Suite.java`可以跑测试用例。

执行`./gradlew clean coverage`来获取覆盖率报告。

## 用法

### 如果你不使用kotlin

**gradle**

```groovy
implementation 'io.vproxy:vjson:1.5.2'
```

**maven**

```xml
<dependency>
  <groupId>io.vproxy</groupId>
  <artifactId>vjson</artifactId>
  <version>1.5.2</version>
</dependency>
```

### 如果你正在使用kotlin

**gradle**

```groovy
implementation('io.vproxy:vjson:1.5.2') {
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
      <version>1.5.2</version>
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

### 对于 jdk < 9 额外的依赖

如果你不使用kotlin，但是正在使用 jdk < 9，那么除了上述的`exclusion`之外，你还需要添加如下依赖：

**gradle**

```groovy
implementation 'io.vproxy:kotlin-stdlib-lite:1.0.1'
```

**maven**

```xml
<dependency>
  <groupId>io.vproxy</groupId>
  <artifactId>kotlin-stdlib-lite</artifactId>
  <version>1.0.1</version>
</dependency>
```

## kotlin native

`vjson`完全由kotlin编写，并且仅使用kotlin标准库，所以`vjson`可以用于kotlin native。

执行`./gradlew clean kotlinNative`，将源代码编译到kotlin native的版本。

你可以按照如下命令编译native可执行文件：

```shell
./gradlew clean kotlinNative
rm -r misc/vjson_executable/src/nativeMain/kotlin/vjson/*
cp -r src/main/kotlin/vjson/* misc/vjson_executable/src/nativeMain/kotlin/vjson

# Then you can build kotlin native executable
cd misc/vjson_executable/
./gradlew clean nativeBinaries
```

执行如下命令运行：

```shell
./build/bin/native/releaseExecutable/vjson_executable.kexe --help
```

## kotlin js

Kotlin JS 的限制比 Kotlin Native 更多，所以额外提供了一个task用于Kotlin JS：

执行`./gradlew clean kotlinJs`，将源代码编译到kotlin js的版本。

你还可以执行如下命令，来更新web版本的解释器代码：

```shell
./gradlew clean kotlinJs
rm -r misc/online_vjson_script_interpreter/src/main/kotlin/vjson/*
cp -r src/main/kotlin/vjson/* misc/online_vjson_script_interpreter/src/main/kotlin/vjson

# 然后你可以运行这个web版本的解释器:
cd misc/online_vjson_script_interpreter/
./gradlew clean run
```

## vpreprocessor

在jvm和kotlin native上，`vjson`使用相同的源代码。为了实现更好的java互操作性，`vjson`中使用了一些jvm特有的注解和jdk类库。但是在kotlin native中并没有提供这些类。为了解决这个问题，我开发了一个代码预处理器，可以将“宏”通过注释插入java/kotlin代码中。

查看[vpreprocessor/README.md](https://github.com/wkgcass/vjson/blob/master/src/main/kotlin/vpreprocessor/README.md)获取更多信息。

注意，预处理会直接覆盖源代码，所以在执行预处理前，你需要保证当前git目录是clean的。在执行`coverage`、`kotlinNative`和`kotlinJs`前，会自动先执行`checkGit`任务。

## 示例

**java**

```java
// 基础用法
JSON.Instance result = JSON.parse("{\"hello\":\"world\"}");
String json = result.stringify();
String prettyJson = result.pretty();

// 反序列化
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

// 自动类型
ObjectRule<Good> goodRule = new ObjectRule<>(Good::new)
    .put("id", Good::setId, StringRule.get())
    .put("name", Good::setName, StringRule.get())
    .put("price", Good::setPrice, DoubleRule.get());
ObjectRule<SpecialPriceGood> specialPriceGoodRule = new ObjectRule<>(SpecialPriceGood::new, goodRule)
    .put("originalPrice", SpecialPriceGood::setOriginalPrice, DoubleRule.get());
TypeRule<Good> typeGoodRule = new TypeRule<>(Good.class, goodRule)
    .type("special", specialPriceGoodRule);
    // 或者 .type(SpecialPriceGood.class, specialPriceGoodRule);
// TypeRule<> 也可以不传参构造
// 可用于反序列化这样的json： {"@type": "...", ...}

// 获取值
javaObject = result.toJavaObject(); // List,Map,String 或者 基本类型的包装类型
javaObject = JSON.parseToJavaObject("{\"hello\":\"world\"}");
String value = ((JSON.Object) result).getString("hello"); // world

// 解析
ObjectParser parser = new ObjectParser();
parser.feed("{\"hel");    // 这里返回null
parser.feed("lo\":\"wo"); // 这里返回null
parser.feed("rld\"}");    // 这里返回JSON.Object
                          // 你也可以用CharStream对象调用feed而不是例子中的String
    // 如果是最后一片要喂给解析器的数据:
    parser.last("rld\"}");

// 构造复杂的对象
new ObjectBuilder().put("id", 1).put("name", "pizza").build(); // JSON.Object
new ArrayBuilder().add(3.14).addObject(o -> ...).addArray(a -> ...).build(); // JSON.Array

// 序列化
new SimpleInteger(1).stringify(); // 1
new SimpleString("hello\nworld").stringify(); // "hello\nworld"

// 高级用法
new ObjectParser(new ParserOptions().setListener(...)); // 解析器会调用这些回调点
result.stringify(stringBuilder, stringifier); // 自定义输出格式

// 附加特性
new ParserOptions().setStringSingleQuotes(true).setKeyNoQuotes(true);
                        // 允许解析类似这样的字符串: {key: 'value'}
```

**kotlin**

```kotlin
// 使用kotlin语法的builder例子
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
