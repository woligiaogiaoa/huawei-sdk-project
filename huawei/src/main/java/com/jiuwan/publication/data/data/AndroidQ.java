package com.jiuwan.publication.data.data;

public class AndroidQ {
    private String oaid;
    private String vaid;
    private String aaid;

    public AndroidQ(String oaid, String vaid, String aaid) {
        this.oaid = oaid;
        this.vaid = vaid;
        this.aaid = aaid;
    }

    public AndroidQ() {
    }

    @Override
    public String toString() {
        return "AndroidQ{" +
                "oaid='" + oaid + '\'' +
                ", vaid='" + vaid + '\'' +
                ", aaid='" + aaid + '\'' +
                '}';
    }

    public String getOaid() {
        return oaid;
    }

    public void setOaid(String oaid) {
        this.oaid = oaid;
    }

    public String getVaid() {
        return vaid;
    }

    public void setVaid(String vaid) {
        this.vaid = vaid;
    }

    public String getAaid() {
        return aaid;
    }

    public void setAaid(String aaid) {
        this.aaid = aaid;
    }
}