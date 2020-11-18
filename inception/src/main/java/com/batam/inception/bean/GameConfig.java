package com.batam.inception.bean;

public class GameConfig {

    public static final GameConfig BLANK_OBJ = new GameConfig("", "", "","", "");

    public GameConfig(String channel_id, String game_id, String package_id, String plan_id, String site_id) {
        this.channel_id = channel_id;
        this.game_id = game_id;
        this.package_id = package_id;
        this.plan_id = plan_id;
        this.site_id = site_id;
    }

    /**
     * channel_id :
     * game_id : aafd3d2fa5d74e6ebd7dec56158017cc
     * package_id :
     * plan_id :
     * site_id :
     */

    private String channel_id;
    private String game_id;
    private String package_id;
    private String plan_id;
    private String site_id;

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
}