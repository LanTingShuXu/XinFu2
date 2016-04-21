package com.swpu.gantao.xinfu.bean;

/**
 * 短信组对象
 * Created by 李长军 on 16/01/26.
 */
public class SmsGroup {
    private String _id;//主键(组id)
    //接收者的手机号码（可能有多个。我们记录的是联系人的id，通过;隔开。）例如：张三的id为1，李四的id为2，那么接收者的字段为1;2;
    private String recivers;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRecivers() {
        return recivers;
    }

    public void setRecivers(String recivers) {
        this.recivers = recivers;
    }
}
