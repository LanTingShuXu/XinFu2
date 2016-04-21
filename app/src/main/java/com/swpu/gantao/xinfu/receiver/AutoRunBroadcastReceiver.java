package com.swpu.gantao.xinfu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.swpu.gantao.xinfu.service.SmsService;

/**
 * 监听系统事件，启动短信服务的广播接收者
 *
 * @author 李长军
 */
public class AutoRunBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SmsService.class);
        i.setAction("com.swpu.gantao.xinfu.SMS_SERVICE");
        context.startService(i);
    }
}
