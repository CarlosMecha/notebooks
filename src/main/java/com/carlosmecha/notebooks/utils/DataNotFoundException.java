package com.carlosmecha.notebooks.utils;


/**
 * When some data is not found.
 * Created by carlos on 15/01/17.
 */
public class DataNotFoundException extends ServiceException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
