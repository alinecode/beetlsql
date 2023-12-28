package org.beetl.sql.clazz.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 集合工具类
 */
public class ListUtil {

    /**
     * 判断是否为空.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断是否为不为空.
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !(isEmpty(collection));
    }

    public static <E> List<E> newArrayList() {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * 将list分割为等长的子list
     *
     * @param source 原list
     * @param size   子list长度
     * @param <T>    元素
     * @return List
     */
    public static <T> List<List<T>> partition(List<T> source, int size) {
        List<List<T>> result = new ArrayList<>();
        //余数
        int remainder = source.size() % size;
        //商
        int number = source.size() / size;
        //偏移量
        int offset;
        if (number != 0) {
            for (int i = 0; i < number; i++) {
                offset = i * size;
                List<T> value = source.subList(offset, (i + 1) * size);
                result.add(value);
            }
        }
        if (remainder != 0) {
            offset = number * size;
            List<T> value = source.subList(offset, offset + remainder);
            result.add(value);
        }
        return result;
    }
}
