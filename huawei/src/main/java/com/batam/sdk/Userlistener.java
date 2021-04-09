package com.batam.sdk;

public interface Userlistener {

    void onUserSwitchAccount();

    void onLoginSuccess(String userInfo);

    void onLoginError(String message);

    void onLogout();
}
