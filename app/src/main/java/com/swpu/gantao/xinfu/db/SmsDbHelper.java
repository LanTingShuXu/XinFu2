package com.swpu.gantao.xinfu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 短信记录数据库的OpenHelper
 *
 * @author 李长军
 */
public class SmsDbHelper extends SQLiteOpenHelper {

    private static final String dbName = "smsLog";//数据库名称

    public SmsDbHelper(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建短信组表.包括：_id，recivers
        db.execSQL("create table smsGroup (_id integer primary key autoincrement , recivers)");
        //创建短信联系人表，包括：_id，name，phoneNum
        db.execSQL("create table smsContact (_id integer primary key autoincrement , name , phoneNum)");
        //创建短信内容记录表,包括：_id,groupId，content，time(短信发送的时间)
        db.execSQL("create table smsContent (_id integer primary key autoincrement , groupId , content , time)");
        //创建定时短信队列表,包括：_id,groupId，content，time(定时短信的时间)，isDirty（是否失效了。1表示失效了,0表示没失效），isSend(是否发送，1表示已发送，0表示未发送)
        db.execSQL("create table timingSmsContent (_id integer primary key autoincrement , groupId , content , time , isDirty,isSend)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
