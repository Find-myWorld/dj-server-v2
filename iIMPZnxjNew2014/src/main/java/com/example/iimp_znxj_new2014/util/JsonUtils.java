package com.example.iimp_znxj_new2014.util;

import android.annotation.SuppressLint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

@SuppressLint("SimpleDateFormat")
public class JsonUtils {

	//默认使用东八区时区（北京时间）
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT+8");
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        OBJECT_MAPPER.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_FORMAT);
        format.setTimeZone(DEFAULT_TIMEZONE);
        OBJECT_MAPPER.getSerializationConfig().setDateFormat(format);
    }
	
	/**
     * 将json字符串转换成相应的对象
     *
     * @param jsonString json字符串
     * @param clazz      对象类型的class
     * @param <E>        对象的类型
     * @return 转换后的对象
     * @throws IOException
     */
    public static <E> E decode(String jsonString, Class<E> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(jsonString, clazz);
    }

    /**
     * 将已经从json转换后的对象（通常是一个Map）转换成对应Bean的对象实例
     *
     * @param fromValue     一个转json换中的过程对象，通常是一个Map
     * @param typeReference 要转换的对象类型的Type描述
     * @param <E>           对象类型
     * @return 转换后的Bean的实例
     */
    public static <E> E decode(Object fromValue, TypeReference<E> typeReference) {
        return OBJECT_MAPPER.convertValue(fromValue, typeReference);
    }

    /**
     * 将json字符串转换成响应的对象
     *
     * @param fromValue     待转换的json字符串
     * @param typeReference 对象类型的描述
     * @param <E>           对象的类型
     * @return 转换后的对象
     * @throws IOException
     */
    public static <E> E decode(String fromValue, TypeReference<E> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(fromValue, typeReference);
    }

    /**
     * 将一个JavaBean转换成json字符串
     *
     * @param object 待转换的对象
     * @return json字符串
     * @throws IOException
     */
    public static String encode(Object object) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * @param jsonString
     * @param type
     * @param <E>
     * @return
     * @throws IOException
     */
    public static <E> E getObject(String jsonString, JavaType type) throws IOException {
        return OBJECT_MAPPER.readValue(jsonString, type);
    }
	
}
