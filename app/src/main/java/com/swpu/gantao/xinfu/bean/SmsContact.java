package com.swpu.gantao.xinfu.bean;

/**
 * 短信联系人对象
 *
 * @auther 李长军
 */
public class SmsContact {
    private int _id;//联系人id
    private String name;//联系人姓名
    private String phoneNum;//联系人手机号码

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
