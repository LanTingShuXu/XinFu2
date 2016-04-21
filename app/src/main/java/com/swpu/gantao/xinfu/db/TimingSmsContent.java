package com.swpu.gantao.xinfu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.bean.Linkman;

import java.util.ArrayList;

/**
 * 定时短信表
 *
 * @author 李长军
 */
public class TimingSmsContent {
    private SmsDbHelper smsDbHelper = null;
    private SmsGroupTable smsGroupTable;
    private SmsContactTable smsContactTable;

    public TimingSmsContent(Context context) {
        smsDbHelper = new SmsDbHelper(context);
        smsGroupTable = new SmsGroupTable(context);
        smsContactTable = new SmsContactTable(context);
    }

    /**
     * 添加一条定时短信到数据库
     *
     * @param groupId 组别号（如果组别号是-1，将会忽略此条记录）
     * @param content 内容
     * @param time    发送的设置时间(使用的是时间long值)
     * @return 距离当前最近的定时短信的时间
     */
    public long addTimingSmsContent(long groupId, String content, long time) {
        if (groupId < 0) {
            return -1;
        }
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("groupId", groupId);
        values.put("time", time);
        values.put("isDirty", 0);
        values.put("isSend", 0);
        sqLiteDatabase.insert("timingSmsContent", null, values);

        //标记失效了的定时短信
        sqLiteDatabase.execSQL("update timingSmsContent set isDirty = 1 where time < " + System.currentTimeMillis());
        Cursor cursor = sqLiteDatabase.query("timingSmsContent", null, "isDirty = 0", null, null, null, null);
        if (cursor.moveToFirst()) {
            time = cursor.getLong(cursor.getColumnIndex("time"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return time;
    }

    /**
     * 获取指定时间的定时短信
     *
     * @param time 定时短信的时间
     * @return 待发的短信联系人(如果size为0表示没有待发的短信)
     */
    public ArrayList<Linkman> getTimingSmsLinkmans(long time) {
        ArrayList<Linkman> linkmans = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        //标记失效了的定时短信
        sqLiteDatabase.execSQL("update timingSmsContent set isDirty = 1 where time < " + System.currentTimeMillis());
        Cursor cursor = sqLiteDatabase.query("timingSmsContent", null, "time = " + time, null, null, null, null);
        if (cursor.moveToFirst()) {
            long groupId = cursor.getLong(cursor.getColumnIndex("groupId"));
            String ids = smsGroupTable.getGroupMemberIds(groupId);
            linkmans = smsContactTable.querLinkmans(ids);
        }
        cursor.close();
        sqLiteDatabase.close();
        return linkmans;
    }

    /**
     * 获取定时短信的发送组id和内容
     *
     * @param time 定时短信的时间
     * @return 一个长度为2的数组，下标0表示组id，下标1表示内容
     */
    public String[] getTimingContentAndGroupId(long time) {
        String[] content = new String[2];
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("timingSmsContent", null, "time = " + time, null, null, null, null);
        if (cursor.moveToFirst()) {
            content[0] = cursor.getString(cursor.getColumnIndex("groupId"));
            content[1] = cursor.getString(cursor.getColumnIndex("content"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return content;
    }


    /**
     * 获取距离最近的待发送的短信的时间
     *
     * @return long值的时间（如果-1表示没有待发送的短信）
     */
    public long getLastTimingSmsTime() {
        long time = -1;
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        //标记失效了的定时短信
        sqLiteDatabase.execSQL("update timingSmsContent set isDirty = 1 where time < " + System.currentTimeMillis());
        Cursor cursor = sqLiteDatabase.query("timingSmsContent", null, "isDirty = 0", null, null, null, null);
        if (cursor.moveToFirst()) {
            time = cursor.getLong(cursor.getColumnIndex("time"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return time;
    }

    /**
     * 更新定时短信的发送状态。(更改为已发送)
     *
     * @param groupId 组别号
     */
    public void updateSendStatus(long groupId) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("update timingSmsContent set isSend = 1 where groupId = " + groupId);
        sqLiteDatabase.close();
    }

    /**
     * 获取待发送的短信
     *
     * @return 待发送短信集合(如果没有，返回的数据size为0)
     */
    public ArrayList<GroupMessage> getTimingSmsQueen() {
        ArrayList<GroupMessage> groupMessages = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        //获取发送时间大于当前时间的数据
        Cursor cursor = sqLiteDatabase.query("timingSmsContent", null, "time > " + System.currentTimeMillis(), null, null, null, "time");
        while (cursor.moveToNext()) {
            GroupMessage groupMessage = new GroupMessage();

            groupMessage.setTime(cursor.getLong(cursor.getColumnIndex("time")));
            groupMessage.setGroupId(cursor.getLong(cursor.getColumnIndex("groupId")));
            groupMessage.setContent(cursor.getString(cursor.getColumnIndex("content")));
            String member = "";
            for (Linkman linkman : smsContactTable.querLinkmans(smsGroupTable.getGroupMemberIds(groupMessage.getGroupId()))) {
                groupMessage.addLinkman(linkman);
                member += linkman.getName() + ";";
            }
            if (member.length() > 0) {
                member = member.substring(0, member.length() - 1);
            }
            groupMessage.setMember(member);
            groupMessages.add(groupMessage);
        }
        cursor.close();
        sqLiteDatabase.close();
        return groupMessages;
    }

    /**
     * 删除指定的定时短信
     *
     * @param id 定时短信的组id
     */
    public void deleteTimingSms(long id) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        sqLiteDatabase.delete("timingSmsContent", "groupId=" + id, null);
        sqLiteDatabase.close();
    }
}
