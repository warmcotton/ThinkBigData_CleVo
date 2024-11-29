package com.thinkbigdata.clevo.exception;

public class AsyncException extends RuntimeException{
    public AsyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
