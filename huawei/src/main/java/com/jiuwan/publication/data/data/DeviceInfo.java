package com.jiuwan.publication.data.data;

import java.util.List;

public class DeviceInfo {

    /**
     * channel_id : 08e965eba20944dda47346c43c20107b
     * game_id : aafd3d2fa5d74e6ebd7dec56158017cc
     * package_id : 7d47ed037589411b9d4a8551bb496165
     * plan_id : e3c4578ecd3d480ba862f45d8f9bd38a
     * site_id : 32765d4a18744626b14fecacc4b03ea1
     * device : {"os":"Android","android":{"imei":["imei1","imei2"],"android_id":"bcbc00f09479aa5b","sim_serial":["sim1","sim2"],"imsi":"460017932859596","version":"8.0.1","brand":"Huawei","model":"HUAWEI G750-T01","id":"HUAWEITAG-TLOO","product":"G750-T01","serial":"YGKBBBB5C1711949","sdk_package_name":"com.xxx.xxx.www","sdk_version":"19.2.4","game_package_name":"com.xxx.xxx.www","game_version":"1.0.0"},"network":{"code":46001,"name":"中国联通(wifi名称)","type":"wifi","intranet_ip":"192.168.1.145","mac":"a8:a6:68:a3:d9:ef"}}
     */

    private String channel_id;
    private String game_id;
    private String package_id;
    private String plan_id;
    private String site_id;
    private String user_id = "";
    private String type = "normal";
    private String domain = "";
    private String user_pact_url = "";
    private String h5_apk_url = "";
    private String customer_service_url = "";
    private DeviceBean device;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser_pact_url() {
        return user_pact_url;
    }

    public void setUser_pact_url(String user_pact_url) {
        this.user_pact_url = user_pact_url;
    }

    public String getH5_apk_url() {
        return h5_apk_url;
    }

    public void setH5_apk_url(String h5_apk_url) {
        this.h5_apk_url = h5_apk_url;
    }

    public String getCustomer_service_url() {
        return customer_service_url;
    }

    public void setCustomer_service_url(String customer_service_url) {
        this.customer_service_url = customer_service_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public static class DeviceBean {
        /**
         * os : Android
         * android : {"imei":["imei1","imei2"],"android_id":"bcbc00f09479aa5b","sim_serial":["sim1","sim2"],"imsi":"460017932859596","version":"8.0.1","brand":"Huawei","model":"HUAWEI G750-T01","id":"HUAWEITAG-TLOO","product":"G750-T01","serial":"YGKBBBB5C1711949","sdk_package_name":"com.xxx.xxx.www","sdk_version":"19.2.4","game_package_name":"com.xxx.xxx.www","game_version":"1.0.0"}
         * network : {"code":46001,"name":"中国联通(wifi名称)","type":"wifi","intranet_ip":"192.168.1.145","mac":"a8:a6:68:a3:d9:ef"}
         */

        private String os;
        private AndroidBean android;
        private NetworkBean network;

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public AndroidBean getAndroid() {
            return android;
        }

        public void setAndroid(AndroidBean android) {
            this.android = android;
        }

        public NetworkBean getNetwork() {
            return network;
        }

        public void setNetwork(NetworkBean network) {
            this.network = network;
        }

        public static class AndroidBean {
            /**
             * imei : ["imei1","imei2"]
             * android_id : bcbc00f09479aa5b
             * sim_serial : ["sim1","sim2"]
             * imsi : 460017932859596
             * version : 8.0.1
             * brand : Huawei
             * model : HUAWEI G750-T01
             * id : HUAWEITAG-TLOO
             * product : G750-T01
             * serial : YGKBBBB5C1711949
             * sdk_package_name : com.xxx.xxx.www
             * sdk_version : 19.2.4
             * game_package_name : com.xxx.xxx.www
             * game_version : 1.0.0
             */

            private String android_id;
            private String imsi;
            private String version;
            private String brand;
            private String model;
            private String id;
            private String product;
            private String serial;
            private String sdk_package_name;
            private String sdk_version;
            private String game_package_name;
            private String game_version;
            private List<String> imei;
            private List<String> sim_serial;
            private AndroidQ android_q;

            public String getAndroid_id() {
                return android_id;
            }

            public void setAndroid_id(String android_id) {
                this.android_id = android_id;
            }

            public String getImsi() {
                return imsi;
            }

            public void setImsi(String imsi) {
                this.imsi = imsi;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getProduct() {
                return product;
            }

            public void setProduct(String product) {
                this.product = product;
            }

            public String getSerial() {
                return serial;
            }

            public void setSerial(String serial) {
                this.serial = serial;
            }

            public String getSdk_package_name() {
                return sdk_package_name;
            }

            public void setSdk_package_name(String sdk_package_name) {
                this.sdk_package_name = sdk_package_name;
            }

            public String getSdk_version() {
                return sdk_version;
            }

            public void setSdk_version(String sdk_version) {
                this.sdk_version = sdk_version;
            }

            public String getGame_package_name() {
                return game_package_name;
            }

            public void setGame_package_name(String game_package_name) {
                this.game_package_name = game_package_name;
            }

            public String getGame_version() {
                return game_version;
            }

            public void setGame_version(String game_version) {
                this.game_version = game_version;
            }

            public List<String> getImei() {
                return imei;
            }

            public void setImei(List<String> imei) {
                this.imei = imei;
            }

            public List<String> getSim_serial() {
                return sim_serial;
            }

            public AndroidQ getAndroid_q() {
                return android_q;
            }

            public void setAndroid_q(AndroidQ android_q) {
                this.android_q = android_q;
            }

            public void setSim_serial(List<String> sim_serial) {
                this.sim_serial = sim_serial;
            }
        }

        public static class NetworkBean {
            /**
             * code : 46001
             * name : 中国联通(wifi名称)
             * type : wifi
             * intranet_ip : 192.168.1.145
             * mac : a8:a6:68:a3:d9:ef
             */

            private int code;
            private String name;
            private String type;
            private String intranet_ip;
            private String mac;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getIntranet_ip() {
                return intranet_ip;
            }

            public void setIntranet_ip(String intranet_ip) {
                this.intranet_ip = intranet_ip;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }
        }
    }
}
