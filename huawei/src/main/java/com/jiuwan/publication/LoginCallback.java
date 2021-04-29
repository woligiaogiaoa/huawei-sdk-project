package com.jiuwan.publication;

public interface LoginCallback {


    void onSuccess(String user);

    void onFailure(String message, int code);

}
