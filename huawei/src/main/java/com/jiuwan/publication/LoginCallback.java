package com.jiuwan.publication;

public interface LoginCallback {


    void onLoginSuccess(String user);

    void onLoginError(String message,int code);

}
