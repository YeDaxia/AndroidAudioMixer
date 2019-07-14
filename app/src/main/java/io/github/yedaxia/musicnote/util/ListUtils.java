package io.github.yedaxia.musicnote.util;

import java.util.Collections;
import java.util.List;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2017/1/16
 */

public class ListUtils {

    /**
     * 添加数组
     * @param list
     * @param array
     * @param <T>
     */
    public static<T> void addAll(List<T> list, T[] array){
        if(list != null){
            Collections.addAll(list,array);
        }
    }

    /**
     * 判断是否为Null或者空
     * @param list
     * @param <T>
     * @return
     */
    public static<T> boolean isEmpty(List<T> list){
        return list == null || list.isEmpty();
    }

    /**
     * 判断是否不为空
     * @param list
     * @param <T>
     * @return
     */
    public static<T> boolean isNotEmpty(List<T> list){
        return list != null && !list.isEmpty();
    }

    /**
     * 比较两个列表的内容是否一致
     *
     * @param list1
     * @param list2
     * @param <T>
     * @return
     */
    public static <T> boolean contentEquals(List<T> list1, List<T> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null) {
            return false;
        }
        int list1Size = list1.size();
        int list2Size = list2.size();
        if (list1Size != list2Size) {
            return false;
        }

        for (int i = 0; i != list1Size; i++) {
            if (list1.get(i) != list2.get(i)) {
                return false;
            }
        }

        return true;
    }
}
