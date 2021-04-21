package com.jiuwan.publication.callback;

public interface ReportCallback {
    void onSuccess(String result);

    void onFailure(String msg, int code);
}