package com.carlosmecha.notebooks.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A bunch of list operations.
 *
 * Created by carlos on 15/01/17.
 */
public enum ListUtils {

    ;

    public static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new LinkedList<>();
        for(E e : iterable) {
            list.add(e);
        }
        return list;
    }

}
