package com.batam.sdk.http;

import java.io.Serializable;

public class LzyResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String message;
    public T data;
}