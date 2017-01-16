package com.carlosmecha.notebooks.utils;

import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * String utils.
 *
 * Created by carlos on 15/01/17.
 */
public enum StringUtils {

    ;

    private final static Map<String, DateFormat> formatters = new HashMap<>();

    public static String nameToCode(String name) {
        String normalized = name.toLowerCase();
        return normalized.replaceAll("(\\s|\\.|_|-)", "");
    }

    public static Set<String> split(String text, String regex) {
        Set<String> set = new HashSet<>();
        if(text != null && !text.isEmpty()) {
            Collections.addAll(set, text.split(regex));
        }
        return set;
    }

    public static Date unsafeToDate(String text, String format, Logger logger) {
        if(!formatters.containsKey(format)) {
            formatters.put(format, new SimpleDateFormat(format));
        }

        try {
            return formatters.get(format).parse(text);
        } catch (ParseException e) {
            logger.warn("Date invalid {}", text);
            return new Date();
        }
    }



}
