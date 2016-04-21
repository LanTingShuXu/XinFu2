package com.swpu.gantao.xinfu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swpu.gantao.xinfu.bean.Linkman;

import java.util.ArrayList;

/**
 * 短信通讯录表
 *
 * @author 李长军
 */
public class SmsContactTable {
    private SmsDbHelper smsDbHelper;

    public SmsContactTable(Context context) {
        smsDbHelper = new SmsDbHelper(context);
    }

    /**
     * 插入短信联系人
     *
     * @param linkman 联系人对象
     * @return 插入数据的id
     */
    private long saveSmsContact(Linkman linkman) {
        long index;
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", linkman.getName());
        values.put("phoneNum", linkman.getPhone());
        index = sqLiteDatabase.insert("smsContact", null, values);
        sqLiteDatabase.close();

        return index;
    }

    /**
     * 更新指定id的记录
     *
     * @param id      数据库的id
     * @param linkman 需要更新的对象
     */
    private void updateSmsContact(long id, Linkman linkman) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", linkman.getName());
        values.put("phoneNum", linkman.getPhone());
        sqLiteDatabase.update("smsContact", values, "_id=?", new String[]{id + ""});
        sqLiteDatabase.close();
    }

    /**
     * 是否含有相同的手机号的记录
     *
     * @param phoneNumber 手机号码
     * @return 相同记录的id。 -1表示没有相同的记录.
     */
    public long hasSameContact(String phoneNumber) {
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("smsContact", null, "phoneNum = ?", new String[]{phoneNumber}, null, null, null);
        long position = -1;
        if (cursor.moveToFirst()) {
            position = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        cursor.close();
        sqLiteDatabase.close();
        return position;
    }

    /**
     * 如果数据库中不存在指定联系人,我们将直接存储。如果存在，我们将更新联系人的姓名
     *
     * @param linkman 需要保存的对象
     * @return 联系人在数据库中的id
     */
    public long saveOrUpdateSmsContact(Linkman linkman) {
        long position = hasSameContact(linkman.getPhone());
        if (position == -1) {
            position = saveSmsContact(linkman);
        } else {
            updateSmsContact(position, linkman);
        }
        return position;
    }

    /**
     * 将指定集合中的联系人保存到数据库中。（如果指定的联系人存在，我们将直接更新联系人姓名）
     *
     * @param linkmans 保存后的联系人的id集合。
     */
    public ArrayList<Long> saveOrUpdateSmsContact(ArrayList<Linkman> linkmans) {
        ArrayList<Long> longs = new ArrayList<>();
        for (Linkman linkman : linkmans) {
            longs.add(saveOrUpdateSmsContact(linkman));
        }
        return longs;
    }

    /**
     * 查询指定id的联系人的姓名
     *
     * @param ids 联系人的id.多个id通过 ; 分割开
     * @return 姓名集合。顺序按照的id顺序
     */
    public ArrayList<String> querName(String ids) {
        ArrayList<String> strings = new ArrayList<>();
        String[] sId = ids.split(";");
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        for (String id : sId) {
            Cursor cursor = sqLiteDatabase.query("smsContact", null, "_id = ?", new String[]{id}, null, null, null);
            cursor.moveToFirst();
            strings.add(cursor.getString(cursor.getColumnIndex("name")));
            cursor.close();
        }
        sqLiteDatabase.close();
        return strings;
    }

    /**
     * 查询指定id的联系人的姓名
     *
     * @param ids 联系人的id.多个id通过 ; 分割开
     * @return 联系人集合。顺序按照的id顺序
     */
    public ArrayList<Linkman> querLinkmans(String ids) {
        ArrayList<Linkman> linkmans = new ArrayList<>();
        String[] sId = ids.split(";");
        SQLiteDatabase sqLiteDatabase = smsDbHelper.getReadableDatabase();
        for (String id : sId) {
            Linkman linkman = new Linkman();
            Cursor cursor = sqLiteDatabase.query("smsContact", null, "_id = ?", new String[]{id}, null, null, null);
            cursor.moveToFirst();
            linkman.setName(cursor.getString(cursor.getColumnIndex("name")));
            linkman.setPhone(cursor.getString(cursor.getColumnIndex("phoneNum")));
            linkmans.add(linkman);
            cursor.close();
        }
        sqLiteDatabase.close();
        return linkmans;
    }
}
