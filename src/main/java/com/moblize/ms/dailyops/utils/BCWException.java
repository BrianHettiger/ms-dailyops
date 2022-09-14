package com.moblize.ms.dailyops.utils;

public class BCWException extends Exception {

    public BCWException() {
        super();
    }

    public BCWException(String message) {
        super(message);
    }

    public BCWException(String message, Throwable cause) {
        super(message, cause);
    }

    public BCWException(Throwable cause) {
        super(cause);
    }

    protected BCWException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
