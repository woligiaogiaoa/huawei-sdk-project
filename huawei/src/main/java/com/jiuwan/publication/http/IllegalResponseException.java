package com.jiuwan.publication.http;

import java.io.IOException;

public class IllegalResponseException extends IOException {
    private int code;
    public static final String SPLIT_SEPARATOR="###";

    public IllegalResponseException(String message) {
        super(message);
    }

    public IllegalResponseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + SPLIT_SEPARATOR + this.code;
    }
}