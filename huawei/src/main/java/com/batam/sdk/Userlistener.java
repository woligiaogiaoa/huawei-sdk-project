package com.batam.sdk;

public interface Userlistener {

    void onUserSwitchAccount();

    void onLoginSuccess(String uid);

    void onLoginError(String message);

    void onLogout();
}
