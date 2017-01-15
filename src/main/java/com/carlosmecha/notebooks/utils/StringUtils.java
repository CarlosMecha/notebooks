package com.carlosmecha.notebooks.utils;

/**
 * String utils.
 *
 * Created by carlos on 15/01/17.
 */
public enum StringUtils {

    ;

    public static String nameToCode(String name) {
        String normalized = name.toLowerCase();
        return normalized.replaceAll("(\\s|\\.|_|-)", "");
    }

}
