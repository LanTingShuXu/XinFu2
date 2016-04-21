package com.swpu.gantao.xinfu.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 具体短信消息对象
 *
 * @auther 李长军
 */
public class SmsContent {
    private long _id;
    private String content;
    private long groupId;//对应的组id
    private long time;//发送消息的时间
    private String formarttedTime;//格式化后的时间

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        formarttedTime = simpleDateFormat.format(new Date(time));
    }

    public String getFormarttedTime() {
        return formarttedTime;
    }
}
