package com.jiuwan.publication;

import java.util.List;

public class GoodsAndPrivacy {
    String pvy;

    String ptl;

    public String getPtl() {
        return ptl;
    }

    public void setPtl(String ptl) {
        this.ptl = ptl;
    }

    List<Good> goods;

    public List<Good> getGoods() {
        return goods;
    }

    public void setGoods(List<Good> goods) {
        this.goods = goods;
    }

    public String getPvy() {
        return pvy;
    }

    public void setPvy(String pvy) {
        this.pvy = pvy;
    }

    Boolean protocol_box;

    public Boolean getProtocol_box() {
        return protocol_box;
    }

    public void setProtocol_box(Boolean protocol_box) {
        this.protocol_box = protocol_box;
    }

    public static class Good{
        String cp_id;
        String c_id;
        String money;

        public String getCp_id() {
            return cp_id;
        }

        public void setCp_id(String cp_id) {
            this.cp_id = cp_id;
        }

        public String getC_id() {
            return c_id;
        }

        public void setC_id(String c_id) {
            this.c_id = c_id;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }
}
