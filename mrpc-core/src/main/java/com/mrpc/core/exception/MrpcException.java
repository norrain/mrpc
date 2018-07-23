package com.mrpc.core.exception;

/**
 * @author mark.z
 */
public class MrpcException extends RuntimeException {
    public MrpcException() {
    }

    public MrpcException(String message) {
        super(message);
    }

    public MrpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public MrpcException(Throwable cause) {
        super(cause);
    }

    public MrpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
