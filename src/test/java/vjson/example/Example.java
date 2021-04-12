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

        Rule<Shop> shopRule = ObjectRule.builder(ShopB::new, ShopB::build, obud -> obud
            .put("name", ShopB::setName, StringRule.get())
            .put("goods", ShopB::setGoods, new ArrayRule<>(
                ArrayList::new,
                ArrayList::add,
                ObjectRule.builder(GoodB::new, GoodB::build, obd2 -> obd2
                    .put("id", GoodB::setId, StringRule.get())
                    .put("name", GoodB::setName, StringRule.get())
                    .put("price", GoodB::setPrice, DoubleRule.get()))
            ))
        );
        Shop shop = JSON.deserialize("{\"name\":\"HuaLian\",\"goods\":" + array.stringify() + "}", shopRule);
        System.out.println("deserialize result: " + shop);

        ObjectRule.BuilderRule<Good> goodRule = ObjectRule.builder(GoodB::new, GoodB::build, obud -> obud
            .put("id", GoodB::setId, StringRule.get())
            .put("name", GoodB::setName, StringRule.get())
            .put("price", GoodB::setPrice, DoubleRule.get())
        );
        ObjectRule<SpecialPriceGood> specialPriceGoodRule = ObjectRule.builder(
            SpecialPriceGoodB::new,
            goodRule,
            SpecialPriceGoodB::build,
            obud -> obud.put("originalPrice", SpecialPriceGoodB::setOriginalPrice, DoubleRule.get())
        );
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
        final String name;
        final List<Good> goods;

        public Shop(String name, List<Good> goods) {
            this.name = name;
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

    public static class ShopB {
        private String name;
        private List<Good> goods;

        public void setName(String name) {
            this.name = name;
        }

        public void setGoods(List<Good> goods) {
            this.goods = goods;
        }

        public Shop build() {
            return new Shop(name, goods);
        }
    }

    public static class Good {
        final String id;
        final String name;
        final double price;

        public Good(String id, String name, double price) {
            this.id = id;
            this.name = name;
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

    public static class GoodB {
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

        public Good build() {
            return new Good(id, name, price);
        }
    }

    public static class SpecialPriceGood extends Good {
        final double originalPrice;

        public SpecialPriceGood(String id, String name, double price, double originalPrice) {
            super(id, name, price);
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

    public static class SpecialPriceGoodB extends GoodB {
        double originalPrice;

        public void setOriginalPrice(double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public SpecialPriceGood build() {
            return new SpecialPriceGood(id, name, price, originalPrice);
        }
    }
}
