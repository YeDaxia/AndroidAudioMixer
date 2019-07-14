package io.github.yedaxia.musicnote.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/1/16
 */

public class ArrayUtils {

    /**
     * 判断是否为Null或者空
     * @param array
     * @param <T>
     * @return
     */
    public static<T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }

    /**
     * 判断是否不为空
     * @param array
     * @param <T>
     * @return
     */
    public static<T> boolean isNotEmpty(T[] array){
        return array != null && array.length > 0;
    }


    /**
     * 移除Null元素
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> List<T> removeNullObject(T[] array) {
        List<T> list = new ArrayList<>();
        if (array == null){
            return list;
        }
        for (T tmp : array){
            if(tmp != null){
                list.add(tmp);
            }
        }
        return list;
    }

    /**
     * 拼接数组字符串.
     *
     * @param array
     * @return
     */
    public static String toArrayString(Object[] array) {
        if (isEmpty(array)) {
            return "";
        }
        StringBuilder arrayBuilder = new StringBuilder();
        final int downIndex = array.length - 1;
        for (int i = 0; i != array.length; i++) {
            arrayBuilder.append(array[i]);
            if (i != downIndex) {
                arrayBuilder.append(',');
            }
        }
        return arrayBuilder.toString();
    }
}
