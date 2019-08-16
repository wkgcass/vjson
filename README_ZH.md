# vjson

中文 | [English](https://github.com/wkgcass/vjson/blob/master/README.md)

## 简介

vjson是一个轻量级的json解析和序列化库。

vjson致力于用java对象还原最原始的json结构，并支持你仅调用几个java方法就能构造任何json字符串。

注意：这只是一个json解析库，并_不是_一个json反序列化库！在做解析时，它仅会将输入的json流转换为vjson内置对象，在此之后，你可以通过调用`.toJavaObject()`方法来获取Map/List/String/基本类型的值。不过话说回来，你也可以通过ParserListener接口构造你自己的反序列化库。

## 性能

可以查看`src/test/java/vjson/bench`以获取更多信息。你可以执行jmh测试，或者直接通过一个主函数运行简易的测试。

## 可靠性

现在`vjson`有100%的行覆盖率和分支覆盖率。

执行`src/test/java/vjson/Suite.java`可以跑测试用例。

## 用法

将`src/main/java/vjson`复制粘贴到你的源码目录即可。`vjson`设计原则就是小且零依赖，并且，复制粘贴就是本lib的推荐用法。

```java
// 基础用法
JSON.Instance result = JSON.parse("{\"hello\":\"world\"}");
String json = result.stringify();
String prettyJson = result.pretty();

// 解析
ObjectParser parser = new ObjectParser();
parser.feed("{\"hel");    // 这里返回null
parser.feed("lo\":\"wo"); // 这里返回null
parser.feed("rld\"}");    // 这里返回JSON.Object
                          // 你也可以用CharStream对象调用feed而不是例子中的String
    // 如果是最后一片要喂给解析器的数据:
    parser.last("rld\"}");

// 获取值
String value = ((JSON.Object) result).getString("hello"); // world
Map<String, Object> map = ((JSON.Object) result).toJavaObject();

// 序列化
new SimpleInteger(1).stringify(); // 1
new SimpleString("hello\nworld").stringify(); // "hello\nworld"

// 构造复杂的对象
new ObjectBuilder().put("id", 1).put("name", "pizza").build(); // JSON.Object
new ArrayBuilder().add(3.14).addObject(o -> ...).addArray(a -> ...).build(); // JSON.Array

// 高级用法
new ObjectParser(new ParserOptions().setListener(...)); // 解析器会调用这些回调点
result.stringify(stringBuilder, stringifier); // 自定义输出格式
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
                                             |
                                    CharArrayCharStream
```
