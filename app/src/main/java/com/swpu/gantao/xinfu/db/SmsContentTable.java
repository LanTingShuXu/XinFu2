package com.swpu.gantao.xinfu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.swpu.gantao.xinfu.bean.SmsContent;

import java.util.ArrayList;

/**
 * 短信内容记录表
 *
 * @author 李长军
 */
public class SmsContentTable {
    private SmsDbHelper smsDbHelper;

    public SmsContentTable(Context context) {
        smsDbHelper = new SmsDbHelper(context);
    }

    /**
     * 向数据库中添加发送内容。
     *
     * @param groupId 组id(如果id为-1将会丢弃此记录)
     * @param content 发送内容
     */
    public void addContent(long groupId, String content) {
        if (groupId < 0) {
            return;
        }
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("groupId", groupId);
        values.put("time", System.currentTimeMillis());
        sqLiteDatabase.insert("smsContent", null, values);
        sqLiteDatabase.close();
    }

    /**
     * 添加草稿
     *
     * @param groupId
     * @param content
     */
    public void addDraft(long groupId, String content) {
        if (groupId < 0) {
            return;
        }
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("groupId", groupId);
        values.put("time", -1);
        sqLiteDatabase.insert("smsContent", null, values);
        sqLiteDatabase.close();
    }

    /**
     * 清空草稿
     *
     * @param groupId
     */
    public void clearDraft(long groupId) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        sqLiteDatabase.delete("smsContent", "groupId = " + groupId + " and time = -1", null);
        sqLiteDatabase.close();
    }

    /**
     * 通过id删除指定的消息
     *
     * @param id id(如果是-1表示删除所有)
     */
    public void deleteContentById(long id) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        if (id < 0) {
            sqLiteDatabase.delete("smsContent", null, null);
        } else {
            sqLiteDatabase.delete("smsContent", "_id=?", new String[]{id + ""});
        }
        sqLiteDatabase.close();
    }

    /**
     * 获取指定群组的短信消息
     *
     * @param groupId 组id
     * @return 消息集合
     */
    public ArrayList<SmsContent> getSmsContent(long groupId) {
        ArrayList<SmsContent> smsContents = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("smsContent", null, "groupId = " + groupId, null, null, null, null);
        while (cursor.moveToNext()) {
            //如果不是草稿信息
            if (cursor.getLong(cursor.getColumnIndex("time")) > 0) {
                SmsContent smsContent = new SmsContent();
                smsContent.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
                smsContent.setContent(cursor.getString(cursor.getColumnIndex("content")));
                smsContent.setTime(cursor.getLong(cursor.getColumnIndex("time")));
                smsContent.setGroupId(cursor.getLong(cursor.getColumnIndex("groupId")));
                smsContents.add(smsContent);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return smsContents;
    }
}
