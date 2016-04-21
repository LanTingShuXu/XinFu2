package com.swpu.gantao.xinfu.bean;

import java.io.Serializable;

/**
 * 联系人对象。包括了常用的姓名、联系电话信息等
 * Created by 李长军 on 16/01/23.
 */
public class Linkman implements Serializable {
    private String name = "";
    private String phone = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
