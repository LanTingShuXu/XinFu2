package com.swpu.gantao.xinfu.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.bean.Linkman;
import com.swpu.gantao.xinfu.bean.SmsContent;
import com.swpu.gantao.xinfu.db.SmsContentTable;
import com.swpu.gantao.xinfu.db.SmsGroupTable;
import com.swpu.gantao.xinfu.db.TimingSmsContent;

import java.util.ArrayList;

/**
 * 发送短信的service
 */
public class SmsService extends Service {
    //发送短信的服务
    public static Service smsService = null;
    //短信服务对象
    private static SmsManager smsManager = null;
    private static SmsGroupTable smsGroupTable;//短信组别对象
    private static SmsContentTable smsContentTable;//短信记录对象
    private static TimingSmsContent timingSmsContent;//定时短信记录对象
    /**
     * intent中附带的日期(long型的时间)
     */
    public static final String INTENT_EXTRA_KEY = "time";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        smsService = this;
        smsManager = SmsManager.getDefault();
        smsGroupTable = new SmsGroupTable(this);
        smsContentTable = new SmsContentTable(this);
        timingSmsContent = new TimingSmsContent(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return 0;
        }
        //如果收到了intent时。判断是否是定时消息（判断extra，如果是，我们发送立即短信）
        long time = intent.getLongExtra(INTENT_EXTRA_KEY, -1);
        if (time != -1) {
            ArrayList<Linkman> linkmans = timingSmsContent.getTimingSmsLinkmans(time);
            if (linkmans.size() != 0) {//如果定时短信还存在。
                String[] content = timingSmsContent.getTimingContentAndGroupId(time);
                sendSms(Long.parseLong(content[0]), linkmans, content[1], true);
            }
        }
        //开启服务后，激发定时短信
        time = timingSmsContent.getLastTimingSmsTime();
        setTimingSms(time);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        smsService = null;
        smsManager = null;
        smsGroupTable = null;
        smsContentTable = null;
        timingSmsContent = null;
    }

    /**
     * 发送短信的静态方法
     *
     * @param groupId     组别号。如果是新建的传递-1
     * @param linkmans    待发送的联系人队列
     * @param content     发送消息内容
     * @param isTimingSms true表示定时短信发送的。存入数据库的时候，我们需要加上【通过定时短信发送】标示
     * @return 数据库中组别的id.如果发送失败。返回-1
     */
    public static long sendSms(long groupId, ArrayList<Linkman> linkmans, String content, boolean isTimingSms) {
        if (smsService == null) {
            return -1;
        }
        if (groupId == -1) {
            groupId = smsGroupTable.saveGroup(linkmans);
        }
        if (isTimingSms) {
            smsContentTable.addContent(groupId, content + "【通过定时短信发送】");
            timingSmsContent.updateSendStatus(groupId);
        } else {
            smsContentTable.addContent(groupId, content);
        }
        smsContentTable.clearDraft(groupId);//清空之前的草稿
        for (Linkman linkman : linkmans) {
            //创建pendingIntent对象
            PendingIntent pendingIntent = PendingIntent.getService(smsService, 0, new Intent(), 0);
            //发送短信
            smsManager.sendTextMessage(linkman.getPhone(), null, content, pendingIntent, null);
        }
        return groupId;
    }

    /**
     * 保存sms为草稿
     *
     * @param groupId
     * @param linkmans
     * @param content  如果content为null，只清空草稿
     * @return
     */
    public static long saveSms(long groupId, ArrayList<Linkman> linkmans, String content) {
        if (smsService == null) {
            return -1;
        }
        if (groupId == -1) {
            groupId = smsGroupTable.saveGroup(linkmans);
        }
        smsContentTable.clearDraft(groupId);//清空之前的草稿
        if (content != null) {
            smsContentTable.addDraft(groupId, content);//添加新的草稿
        }
        return groupId;
    }


    /**
     * 获取所有的分组消息。（最新的一条数据）
     *
     * @return 分组集合。按照时间先后顺序排序。如果查询失败。返回null
     */
    public static ArrayList<GroupMessage> getGroupMessages() {
        if (smsService == null) {
            return null;
        }
        ArrayList<GroupMessage> groupMessages = smsGroupTable.getRecentGroupMessages();
        return groupMessages;
    }

    /**
     * 获取指定组id的消息内容
     *
     * @param groupId 组id
     * @return 消息内容集合。如果分组不存在，返回集合size为0
     */
    public static ArrayList<SmsContent> getGroupChatContent(long groupId) {
        return smsContentTable.getSmsContent(groupId);
    }

    /**
     * 删除指定组
     *
     * @param groupId 组id 。-1表示删除所有
     */
    public static void deleteGroup(long groupId) {
        smsGroupTable.deleteGroup(groupId);
    }

    /**
     * 通过id删除指定的内容
     *
     * @param id id
     */
    public static void deleteContent(long id) {
        smsContentTable.deleteContentById(id);
    }

    /**
     * 发送定时短信
     *
     * @param groupId  组别号（如果是新建的传递-1即可；如果是修改之前的定时短信。需要传递组别号）
     * @param time     发送的时间。
     * @param linkmans 需要发送的联系人
     * @param content  短信内容
     */
    public static void sendTimingSms(long groupId, long time, ArrayList<Linkman> linkmans, String content) {
        groupId = smsGroupTable.saveGroup(linkmans);//保存组信息
        long recentTime = timingSmsContent.addTimingSmsContent(groupId, content, time);
        setTimingSms(recentTime);
    }

    /**
     * 设置定时任务
     *
     * @param time 任务时间.如果时间不合法，直接返回
     */
    private static void setTimingSms(long time) {
        if (time < 0) {
            return;
        }
        Intent intent = new Intent(smsService, SmsService.class);
        intent.setAction("com.swpu.gantao.xinfu.SMS_SERVICE");
        intent.putExtra(INTENT_EXTRA_KEY, time);
        PendingIntent pendingIntent = PendingIntent.getService(smsService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) smsService.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);//设置闹钟事件
    }

    /**
     * 获取待发短信
     *
     * @return 待发短信集合
     */
    public static ArrayList<GroupMessage> getTimingSmsQueen() {
        return timingSmsContent.getTimingSmsQueen();
    }

    /**
     * 删除指定的定时短信
     *
     * @param id 短信id
     */
    public static void deleteTimingSms(long id) {
        timingSmsContent.deleteTimingSms(id);
    }
}
