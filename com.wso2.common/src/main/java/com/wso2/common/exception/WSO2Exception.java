package com.wso2.common.exception;

/**
 * Custom exception class defined to be used within the WSO2 modules.
 */
public class WSO2Exception extends Exception {

    private String errorCode;
    private String message;
    private Throwable throwable;

    public WSO2Exception(String message, String errorCode) {

        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public WSO2Exception(String message, String errorCode, Throwable throwable) {

        super(message, throwable);
        this.errorCode = errorCode;
        this.throwable = throwable;
        this.message = message;
    }

    public String getErrorCode() {

        return errorCode;
    }

    public void setErrorCode(String errorCode) {

        this.errorCode = errorCode;
    }

    public Throwable getThrowable() {

        return throwable;
    }

    public void setThrowable(Throwable throwable) {

        this.throwable = throwable;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }
}
