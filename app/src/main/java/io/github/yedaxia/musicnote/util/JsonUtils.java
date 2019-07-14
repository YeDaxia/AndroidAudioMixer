package io.github.yedaxia.musicnote.util;

import com.alibaba.fastjson.JSON;

/**
 * Json实用类
 *
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/4/21.
 */

public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 把对象转化成Json字符串
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 把Json字符串转化成对象
     *
     * @param jsonString
     * @param objectClazz
     * @return
     */
    public static <T> T parseObject(String jsonString, Class<T> objectClazz) {
        return JSON.parseObject(jsonString, objectClazz);
    }
}
