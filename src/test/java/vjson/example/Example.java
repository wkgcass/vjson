package vjson.example;

import kotlin.jvm.internal.Reflection;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ArrayBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Example {
    public static void main(String[] args) {
        JSON.Array result = (JSON.Array) JSON.parse("[{\"_id\":\"5d562114640da8b667376ca6\",\"index\":0,\"guid\":\"1e5619f2-900a-48c5-8515-35d65d71279b\",\"isActive\":false,\"balance\":\"$1,594.13\",\"picture\":\"http://placehold.it/32x32\",\"age\":22,\"eyeColor\":\"brown\",\"name\":\"Turner Beach\",\"gender\":\"male\",\"company\":\"PATHWAYS\",\"email\":\"turnerbeach@pathways.com\",\"phone\":\"+1 (988) 479-2037\",\"address\":\"874 Billings Place, Cloverdale, Montana, 2838\",\"about\":\"Est culpa esse aliqua ut. Ad dolor non incididunt labore ad Lorem. Duis nulla in mollit magna. Occaecat minim incididunt nisi incididunt dolor ex tempor exercitation eiusmod esse dolore sint eiusmod. Elit dolor quis proident aliquip elit. Ea sunt veniam in ipsum.\\r\\n\",\"registered\":\"2017-04-12T11:58:33 -08:00\",\"latitude\":44.592993,\"longitude\":164.884327,\"tags\":[\"esse\",\"nisi\",\"ut\",\"ut\",\"irure\",\"esse\",\"ex\"],\"friends\":[{\"id\":0,\"name\":\"Alexis Levy\"},{\"id\":1,\"name\":\"Lucile House\"},{\"id\":2,\"name\":\"Rivera Morrow\"}],\"greeting\":\"Hello, Turner Beach! You have 4 unread messages.\",\"favoriteFruit\":\"banana\"}]");
        System.out.println("result.getClass() == " + result.getClass());
        System.out.println("result.toString() == " + result);
        System.out.println("result.stringify() == " + result.stringify());
        System.out.println("result.pretty() == " + result.pretty());
        System.out.println("result.toJavaObject() == " + result.toJavaObject());
        System.out.println("result.getObject(0).getString(\"_id\") == " + result.getObject(0).getString("_id"));

        JSON.Array array = new ArrayBuilder()
            .addObject(o -> o
                .put("id", UUID.randomUUID().toString())
                .put("name", "pizza")
                .put("price", 5.12))
            .addObject(o -> o
                .put("id", UUID.randomUUID().toString())
                .put("name", "milk")
                .put("price", 1.28)).build();
        System.out.println("build result == " + array);
        System.out.println("build result pretty() == " + array.pretty());

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
        Shop shop = JSON.deserialize("{\"name\":\"HuaLian\",\"goods\":" + array.stringify() + "}", shopRule);
        System.out.println("deserialize result: " + shop);

        ObjectRule<Good> goodRule = new ObjectRule<>(Good::new)
            .put("id", Good::setId, new StringRule())
            .put("name", Good::setName, new StringRule())
            .put("price", Good::setPrice, new DoubleRule());
        ObjectRule<SpecialPriceGood> specialPriceGoodRule = new ObjectRule<>(SpecialPriceGood::new, goodRule)
            .put("originalPrice", SpecialPriceGood::setOriginalPrice, new DoubleRule());
        TypeRule<Good> typeGoodRule = new TypeRule<>(Reflection.getOrCreateKotlinClass(Good.class), goodRule)
            .type("special", specialPriceGoodRule);
        ArrayRule<List<Good>, Good> goodsRule = new ArrayRule<>(ArrayList::new, List::add, typeGoodRule);
        List<Good> goods = JSON.deserialize("[" +
            /**/ "{" +
            /*----*/ "\"id\":\"" + UUID.randomUUID().toString() + "\"," +
            /*----*/ "\"name\":\"pizza\"," +
            /*----*/ "\"price\":5.12" +
            /**/ "}," +
            /**/ "{" +
            /*----*/ "\"@type\":\"special\"," +
            /*----*/ "\"id\":\"" + UUID.randomUUID().toString() + "\"," +
            /*----*/ "\"name\":\"milk\"," +
            /*----*/ "\"price\":1.28," +
            /*----*/ "\"originalPrice\":2.56" +
            /**/ "}" +
            "]", goodsRule);
        System.out.println("deserialize result: " + goods);
    }

    public static class Shop {
        private String name;
        private List<Good> goods;

        public void setName(String name) {
            this.name = name;
        }

        public void setGoods(List<Good> goods) {
            this.goods = goods;
        }

        @Override
        public String toString() {
            return "Shop{" +
                "name='" + name + '\'' +
                ", goods=" + goods +
                '}';
        }
    }

    public static class Good {
        String id;
        String name;
        double price;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "Good{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
        }
    }

    public static class SpecialPriceGood extends Good {
        double originalPrice;

        public void setOriginalPrice(double originalPrice) {
            this.originalPrice = originalPrice;
        }

        @Override
        public String toString() {
            return "SpecialPriceGood{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", originalPrice=" + originalPrice +
                '}';
        }
    }
}
