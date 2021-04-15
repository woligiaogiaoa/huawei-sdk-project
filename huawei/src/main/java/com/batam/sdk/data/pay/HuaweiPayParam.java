package com.batam.sdk.data.pay;

public class HuaweiPayParam extends PlatformPayParam {

    public String roleLevel = "1";
    public String productId;

    @Override
    public String toString() {
        return super.toString();
    }

    public static final class Builder {
        private String gameOrderNum;
        private String price;
        private String productName;
        private String callbackUrl;
        private String serverID;
        private String serverName;
        private String roleID;
        private String roleName;
        private String extendData;
        private String roleLevel = "1";
        private String productId;

        public Builder() {
        }

        public Builder gameOrderNum(String gameOrderNum) {
            this.gameOrderNum = gameOrderNum;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder serverID(String serverID) {
            this.serverID = serverID;
            return this;
        }

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Builder roleID(String roleID) {
            this.roleID = roleID;
            return this;
        }

        public Builder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder extendData(String extendData) {
            this.extendData = extendData;
            return this;
        }

        public Builder roleLevel(String roleLevel) {
            this.roleLevel = roleLevel;
            return this;
        }

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public HuaweiPayParam build() {
           HuaweiPayParam huaweiPayParam = new HuaweiPayParam();
            huaweiPayParam.setGameOrderNum(this.gameOrderNum);
            huaweiPayParam.setPrice(this.price);
            huaweiPayParam.setProductName(this.productName);
            huaweiPayParam.setCallbackUrl(this.callbackUrl);
            huaweiPayParam.setServerID(this.serverID);
            huaweiPayParam.setServerName(this.serverName);
            huaweiPayParam.setRoleID(this.roleID);
            huaweiPayParam.setRoleName(this.roleName);
            huaweiPayParam.setExtendData(this.extendData);
            huaweiPayParam.roleLevel = this.roleLevel;
            huaweiPayParam.productId = this.productId;
            return huaweiPayParam;
        }
    }
}