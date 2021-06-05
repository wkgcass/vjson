package vjson.bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import vjson.CharStream;
import vjson.JSON;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.lang.reflect.Type;
import java.util.List;

public class BenchHelper {
    private BenchHelper() {
    }

    public static final ObjectMapper mapper = new ObjectMapper();
    public static final Gson gson = new Gson();
    private static final Type fastJsonListType = new com.alibaba.fastjson.TypeReference<List>() {
    }.getType();

    public static Object vjson(char[] chars) {
        return JSON.parseToJavaObject(CharStream.from(chars));
    }

    public static Object jackson(byte[] bytes) throws Exception {
        return mapper.readValue(new ByteArrayInputStream(bytes), List.class);
    }

    public static Object gson(char[] chars) {
        return gson.fromJson(new CharArrayReader(chars), List.class);
    }

    public static Object fastjson(byte[] bytes) throws Exception {
        return com.alibaba.fastjson.JSON.parseObject(new ByteArrayInputStream(bytes), fastJsonListType);
    }
}
