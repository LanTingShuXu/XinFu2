package com.swpu.gantao.xinfu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.bean.Linkman;

import java.util.ArrayList;

/**
 * 短信组表
 *
 * @author 李长军
 */
public class SmsGroupTable {
    private SmsDbHelper smsDbHelper = null;
    private SmsContactTable smsContactTable = null;

    public SmsGroupTable(Context context) {
        smsDbHelper = new SmsDbHelper(context);
        smsContactTable = new SmsContactTable(context);
    }

    /**
     * 插入一条新的组别信息
     *
     * @param ids 组别中对应的所有联系人的id
     * @return 插入的组别的id
     */
    private long insertGroup(ArrayList<Long> ids) {
        StringBuilder stringBuilder = new StringBuilder();
        for (long id : ids) {
            stringBuilder.append(id + ";");
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);//删除最后一个;
        }
        long index;
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recivers", stringBuilder.toString());
        index = sqLiteDatabase.insert("smsGroup", null, values);
        sqLiteDatabase.close();
        return index;
    }

    /**
     * 将指定的组别添加到数据库中
     *
     * @param linkmans 一组的联系人
     * @return 插入的组别的id
     */
    public long saveGroup(ArrayList<Linkman> linkmans) {
        ArrayList<Long> ids = smsContactTable.saveOrUpdateSmsContact(linkmans);
        return insertGroup(ids);
    }

    /**
     * 通过组别id，获取成员的id。以；分开 例如：1；2；3
     *
     * @param groupId 组别号
     * @return 如果组别号不合法。返回“”
     */
    public String getGroupMemberIds(long groupId) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("smsGroup", null, "_id=?", new String[]{groupId + ""}, null, null, null);
        String ids = "";
        if (cursor.moveToFirst()) {
            ids = cursor.getString(cursor.getColumnIndex("recivers"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return ids;
    }

    /**
     * 根据groupid删除指定组
     *
     * @param groupId 组id（-1表示删除所有）
     */
    public void deleteGroup(long groupId) {
        String groupSelection = "";
        String contentSelection = "";
        if (groupId != -1) {
            groupSelection = "_id = " + groupId;
            contentSelection = "groupId = " + groupId;
        }
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        sqLiteDatabase.delete("smsContent", contentSelection, null);//删除指定group的内容
        sqLiteDatabase.delete("smsGroup", groupSelection, null);//删除指定group
        sqLiteDatabase.close();
    }

    /**
     * 获取最近的消息
     *
     * @return 消息集合
     */
    public ArrayList<GroupMessage> getRecentGroupMessages() {
        ArrayList<GroupMessage> groupMessages = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("smsContent", null, null, null, "groupId", null, "time desc");

        while (cursor.moveToNext()) {
            GroupMessage groupMessage = new GroupMessage();

            String str_draft = "【草稿】";
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            if (time > 0) {
                str_draft = "";
            }
            groupMessage.setTime(time);
            groupMessage.setContent(str_draft + cursor.getString(cursor.getColumnIndex("content")));
            long groupId = cursor.getLong(cursor.getColumnIndex("groupId"));
            groupMessage.setGroupId(groupId);

            Cursor cursor1 = sqLiteDatabase.query("smsGroup", null, "_id = ?", new String[]{groupId + ""}, null, null, null);
            cursor1.moveToFirst();
            String memberIds = cursor1.getString(cursor1.getColumnIndex("recivers"));
            String member = "";
            for (Linkman linkman : smsContactTable.querLinkmans(memberIds)) {
                groupMessage.addLinkman(linkman);
                member += linkman.getName() + ";";
            }
            if (member.length() > 1) {
                member = member.substring(0, member.length() - 1);
            }
            groupMessage.setMember(member);
            groupMessages.add(groupMessage);
            cursor1.close();
        }
        cursor.close();
        sqLiteDatabase.close();
        return groupMessages;
    }

}
