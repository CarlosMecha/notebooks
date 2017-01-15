package com.carlosmecha.notebooks.utils;

/**
 * Base exception for services.
 *
 * Created by carlos on 15/01/17.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
