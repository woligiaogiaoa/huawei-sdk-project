package com.batam.sdk.data.pay;

public class PlatformPayParam {
    private String gameOrderNum;
    private String price;
    private String productName;
    private String callbackUrl;
    private String serverID;
    private String serverName;
    private String roleID;
    private String roleName;
    private String extendData;

    public PlatformPayParam() {
    }

    @Override
    public String toString() {
        return "PlatformPayParam{" +
                "gameOrderNum='" + gameOrderNum + '\'' +
                ", price='" + price + '\'' +
                ", productName='" + productName + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", serverID='" + serverID + '\'' +
                ", serverName='" + serverName + '\'' +
                ", roleID='" + roleID + '\'' +
                ", roleName='" + roleName + '\'' +
                ", extendData='" + extendData + '\'' +
                '}';
    }

    public String getGameOrderNum() {
        return this.gameOrderNum;
    }

    public void setGameOrderNum(String gameOrderNum) {
        this.gameOrderNum = gameOrderNum;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getServerID() {
        return this.serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleID() {
        return this.roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getExtendData() {
        return this.extendData;
    }

    public void setExtendData(String extendData) {
        this.extendData = extendData;
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

        public Builder() {
        }

        public PlatformPayParam.Builder gameOrderNum(String gameOrderNum) {
            this.gameOrderNum = gameOrderNum;
            return this;
        }

        public PlatformPayParam.Builder price(String price) {
            this.price = price;
            return this;
        }

        public PlatformPayParam.Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public PlatformPayParam.Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public PlatformPayParam.Builder serverID(String serverID) {
            this.serverID = serverID;
            return this;
        }

        public PlatformPayParam.Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public PlatformPayParam.Builder roleID(String roleID) {
            this.roleID = roleID;
            return this;
        }

        public PlatformPayParam.Builder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public PlatformPayParam.Builder extendData(String extendData) {
            this.extendData = extendData;
            return this;
        }

        public PlatformPayParam build() {
            PlatformPayParam platformPayParam = new PlatformPayParam();
            platformPayParam.setGameOrderNum(this.gameOrderNum);
            platformPayParam.setPrice(this.price);
            platformPayParam.setProductName(this.productName);
            platformPayParam.setCallbackUrl(this.callbackUrl);
            platformPayParam.setServerID(this.serverID);
            platformPayParam.setServerName(this.serverName);
            platformPayParam.setRoleID(this.roleID);
            platformPayParam.setRoleName(this.roleName);
            platformPayParam.setExtendData(this.extendData);
            return platformPayParam;
        }
    }
}
