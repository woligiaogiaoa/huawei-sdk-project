package com.jiuwan.inception.bean;

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

    private HardwareInfo hardware;

    public HardwareInfo getHardware() {
        return hardware;
    }

    public void setHardware(HardwareInfo hardware) {
        this.hardware = hardware;
    }

    public static class HardwareInfo {
        private String hardware;
        private String flavor;
        private String model;
        private String manufacturer;
        private String board;
        private String platform;
        private String base_band;
        private int sensor_number;
        private int user_app_number;
        private boolean support_camera;
        private boolean support_camera_flash;
        private boolean support_bluetooth;
        private boolean has_light_sensor;
        private String cgroup_result;
        private int suspect_count;
        private boolean maybe_emulator;

        public String getHardware() {
            return hardware;
        }

        public void setHardware(String hardware) {
            this.hardware = hardware;
        }

        public String getFlavor() {
            return flavor;
        }

        public void setFlavor(String flavor) {
            this.flavor = flavor;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getBoard() {
            return board;
        }

        public void setBoard(String board) {
            this.board = board;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getBase_band() {
            return base_band;
        }

        public void setBase_band(String base_band) {
            this.base_band = base_band;
        }

        public int getSensor_number() {
            return sensor_number;
        }

        public void setSensor_number(int sensor_number) {
            this.sensor_number = sensor_number;
        }

        public int getUser_app_number() {
            return user_app_number;
        }

        public void setUser_app_number(int user_app_number) {
            this.user_app_number = user_app_number;
        }

        public boolean isSupport_camera() {
            return support_camera;
        }

        public void setSupport_camera(boolean support_camera) {
            this.support_camera = support_camera;
        }

        public boolean isSupport_camera_flash() {
            return support_camera_flash;
        }

        public void setSupport_camera_flash(boolean support_camera_flash) {
            this.support_camera_flash = support_camera_flash;
        }

        public boolean isSupport_bluetooth() {
            return support_bluetooth;
        }

        public void setSupport_bluetooth(boolean support_bluetooth) {
            this.support_bluetooth = support_bluetooth;
        }

        public boolean isHas_light_sensor() {
            return has_light_sensor;
        }

        public void setHas_light_sensor(boolean has_light_sensor) {
            this.has_light_sensor = has_light_sensor;
        }

        public String getCgroup_result() {
            return cgroup_result;
        }

        public void setCgroup_result(String cgroup_result) {
            this.cgroup_result = cgroup_result;
        }

        public int getSuspect_count() {
            return suspect_count;
        }

        public void setSuspect_count(int suspect_count) {
            this.suspect_count = suspect_count;
        }

        public boolean isMaybe_emulator() {
            return maybe_emulator;
        }

        public void setMaybe_emulator(boolean maybe_emulator) {
            this.maybe_emulator = maybe_emulator;
        }
    }

}
