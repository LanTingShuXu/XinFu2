package com.swpu.gantao.xinfu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ev.contactsmultipicker.ContactPickerActivity;
import com.ev.contactsmultipicker.ContactResult;
import com.swpu.gantao.xinfu.bean.Linkman;
import com.swpu.gantao.xinfu.service.SmsService;
import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 定时短信界面
 */
public class TimingSmsActivity extends AppCompatActivity {
    private final int REQUEST_SELECT_CONTACT = 1;//启动联系人的请求码

    //显示日期和时间的TextView
    private TextView tv_date, tv_time;
    private TextView tv_name;//显示接收者的TextView
    private EditText edt_content;//短信内容

    private Calendar calendar = Calendar.getInstance(Locale.getDefault());//日历对象,用于存储定时时间
    private Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());//当前时间的日历对象
    private ArrayList<Linkman> smsQueen = new ArrayList<>();//短信队列
    //日期格式化对象
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    //时间格式化对象
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_sms);
        initToolBar();
        viewFind();
        initDate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取来自通讯录的联系人返回uri
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            smsQueen = processContacts((ArrayList<ContactResult>)
                    data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT));
            for (Linkman linkman : smsQueen) {
                tv_name.setText(tv_name.getText().toString() + linkman.getName() + ";");
            }
        }
    }

    /**
     * 初始化时间。
     */
    private void initDate() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        currentCalendar.add(Calendar.MINUTE, 10);
        calendar.add(Calendar.MINUTE, 10);//十分钟之后
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());
        setDateAndTime(date, time);

    }

    /**
     * 集中使用findViewById()
     */
    private void viewFind() {
        tv_date = (TextView) findViewById(R.id.aty_timing_sms_date);
        tv_time = (TextView) findViewById(R.id.aty_timing_sms_time);
        tv_name = (TextView) findViewById(R.id.aty_timing_sms_name);
        edt_content = (EditText) findViewById(R.id.aty_timing_sms_content);
    }


    /**
     * 给TextView分别设置日期和时间
     *
     * @param date 日期
     * @param time 时间
     */
    public void setDateAndTime(String date, String time) {
        tv_date.setText(date);
        tv_time.setText(time);
    }

    /**
     * 初始化ActionBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimingSmsActivity.this.finish();
                Anim_BetweenActivity.leftIn_rightOut(TimingSmsActivity.this);
            }
        });
    }

    /**
     * 处理来自联系人选择界面的返回信息
     *
     * @param contacts 联系人集合信息
     */
    private ArrayList<Linkman> processContacts(ArrayList<ContactResult> contacts) {
        ArrayList<Linkman> linkmans = new ArrayList<>();
        for (ContactResult contactResult : contacts) {
            for (ContactResult.ResultItem item : contactResult.getResults()) {
                Linkman linkman = new Linkman();
                linkman.setName(contactResult.getName());
                linkman.setPhone(item.getResult());
                linkmans.add(linkman);
            }
        }
        return linkmans;
    }

    /**
     * 选择联系人
     */
    private void selectContact() {
        startActivityForResult(new Intent(this, ContactPickerActivity.class), REQUEST_SELECT_CONTACT);
        Anim_BetweenActivity.leftOut_rightIn(this);
    }

    /**
     * 点击添加联系人的按钮
     *
     * @param v 被点击的按钮对象（ImageView）
     */
    public void clk_addContact(View v) {
        selectContact();
    }

    /**
     * 点击日期按钮的监听事件
     *
     * @param v 按钮对象
     */
    public void clk_date(View v) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance(Locale.getDefault());
                c.set(year, monthOfYear, dayOfMonth);
                if (currentCalendar.after(c)) {//如果选中的日期是在当前日期之前。我们应该提示不合法
                    AlertDialog.Builder builder = new AlertDialog.Builder(TimingSmsActivity.this);
                    builder.setMessage("  \n您设置的日期不能小于当前日期\n  ").create().show();
                } else {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    tv_date.setText(dateFormat.format(calendar.getTime()));
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 点击设置时间的监听器
     *
     * @param v 被点击的对象
     */
    public void clk_time(View v) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance(Locale.getDefault());
                //获取用户设置的时间
                c.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                if (currentCalendar.after(c)) {//如果选择的时间在当前时间之后十分钟内。我们提示不合法
                    AlertDialog.Builder builder = new AlertDialog.Builder(TimingSmsActivity.this);
                    builder.setMessage("  \n您设置的时间不能小于当前时间十分钟之后\n  ").create().show();
                } else {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                    tv_time.setText(timeFormat.format(calendar.getTime()));
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                //true表示24小时制
                true).show();
    }

    /**
     * 点击发送定时短信的监听事件
     *
     * @param v 被点击的按钮
     */
    public void clk_sendTimingSms(View v) {
        if (smsQueen.size() == 0) {//未选择联系人
            AlertDialog.Builder builder = new AlertDialog.Builder(TimingSmsActivity.this);
            builder.setMessage("  \n您还没选择联系人呢\n  ").create().show();
        } else if (TextUtils.isEmpty(edt_content.getText().toString().trim())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TimingSmsActivity.this);
            builder.setMessage("  \n您还没填写短信内容呢\n  ").create().show();
        } else {
            //提示确定框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")//
                    .setIcon(android.R.drawable.stat_sys_warning)//
                    .setMessage("您的短信将在" + tv_date.getText().toString() + " " + tv_time.getText().toString() + "发送，您确定吗？")//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendTimingSms();
                        }
                    }).setNegativeButton("取消", null).create().show();

        }
    }

    /**
     * 执行发送定时短信
     */
    public void sendTimingSms() {
        SmsService.sendTimingSms(-1, calendar.getTimeInMillis(), smsQueen, edt_content.getText().toString());
        //提示确定框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")//
                .setIcon(android.R.drawable.stat_sys_warning)//
                .setMessage("定时短信设置成功！")//
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TimingSmsActivity.this.finish();
                        Anim_BetweenActivity.leftIn_rightOut(TimingSmsActivity.this);
                    }
                }).setCancelable(false).create().show();
    }

    @Override
    public void onBackPressed() {
        finish();
        Anim_BetweenActivity.leftIn_rightOut(this);
    }
}
