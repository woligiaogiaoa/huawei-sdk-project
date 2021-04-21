package com.jiuwan.publication;

public interface LoginCallback {


    void onLoginSuccess(String uid);

    void onLoginError(String message,int code);

}
