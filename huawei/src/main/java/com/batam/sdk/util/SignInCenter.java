package com.batam.sdk.util;

import com.huawei.hms.support.hwid.result.AuthHuaweiId;

public class SignInCenter {

    private static SignInCenter INS = new SignInCenter();

    private static AuthHuaweiId currentAuthHuaweiId;

    public static SignInCenter get() {
        return INS;
    }

    public void updateAuthHuaweiId(AuthHuaweiId AuthHuaweiId) {
        currentAuthHuaweiId = AuthHuaweiId;
    }

    public AuthHuaweiId getAuthHuaweiId() {
        return currentAuthHuaweiId;
    }
}