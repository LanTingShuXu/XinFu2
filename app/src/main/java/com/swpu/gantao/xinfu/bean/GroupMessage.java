package com.swpu.gantao.xinfu.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 群消息类
 *
 * @author 李长军
 */
public class GroupMessage implements Serializable {

    private long groupId;//群组id
    private String member;//群发的成员
    private String content;//群发的内容
    private long time;//群发的时间
    private ArrayList<Linkman> linkmans = new ArrayList<>();//组中的联系人
    private String fomarttedTime;//格式化后的时间
    private String longFormarttedTime;//长格式的格式化的时间

    public ArrayList<Linkman> getLinkmans() {
        return linkmans;
    }

    public void addLinkman(Linkman linkman) {
        linkmans.add(linkman);
    }

    public void setLinkmans(ArrayList<Linkman> linkmans) {
        this.linkmans = linkmans;
    }

    public void setFomarttedTime(String fomarttedTime) {
        this.fomarttedTime = fomarttedTime;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd", Locale.getDefault());
        fomarttedTime = simpleDateFormat.format(new Date(time));
        simpleDateFormat.applyPattern("yyyy-MM-dd\nHH:mm");
        longFormarttedTime = simpleDateFormat.format(new Date(time));
    }

    public String getFomarttedTime() {
        return fomarttedTime;
    }

    /**
     * 获取长格式的时间
     *
     * @return 格式化后的时间
     */
    public String getLongFormarttedTime() {
        return longFormarttedTime;
    }

    /**
     * 获取群发的成员数目
     *
     * @return 数目
     */
    public int getMemberCount() {
        int count = 0;
        if (!TextUtils.isEmpty(member)) {
            String[] temp = member.split(";");
            count = temp.length;
        }
        return count;
    }
}
