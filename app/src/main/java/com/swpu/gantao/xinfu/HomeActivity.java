package com.swpu.gantao.xinfu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.swpu.gantao.xinfu.adapter.HomeListAdapter;
import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.service.SmsService;
import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

import java.util.ArrayList;


/**
 * 信符 主界面
 */
public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static class MyHandler extends Handler {
        public static boolean isExit = false;//为点击第二次退出程序做的标记

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://退出程序消息
                    isExit = false;
                    break;
            }
        }

    }

    private DrawerLayout drawerLayout;//抽屉布局
    private LinearLayout navigationMenu;//右边的菜单
    private View lstEmptyView;//ListView为空的时候显示的view
    private ListView lstSmsLog;//发送历史ListView
    private HomeListAdapter adapter;//适配器
    private MyHandler myHandler = new MyHandler();

    private ArrayList<GroupMessage> groupMessages;//最近发送列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewFind();
        initListView();
        initToolBarAndMenu();//初始化Toolbar
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

    //集中使用findViewById()
    private void viewFind() {
        navigationMenu = (LinearLayout) findViewById(R.id.aty_home_NaviMenu);
        lstEmptyView = findViewById(R.id.content_home_emptyView);
        lstSmsLog = (ListView) findViewById(R.id.aty_home_content_listView);
    }

    /**
     * 初始化记录列表
     */
    private void initListView() {
        lstSmsLog.setEmptyView(lstEmptyView);
        registerForContextMenu(lstSmsLog);
        adapter = new HomeListAdapter(this, null);
        lstSmsLog.setAdapter(adapter);
        lstSmsLog.setOnItemClickListener(this);
    }

    /**
     * 更新列表
     */
    private void updateListView() {
        groupMessages = SmsService.getGroupMessages();
        adapter.setData(groupMessages);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBarAndMenu() {
        //创建Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_home_toolBar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //带侧滑菜单的布局
        drawerLayout = (DrawerLayout) findViewById(R.id.aty_home_drawerLayout);
        //布局切换按钮
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    /**
     * 点击删除菜单的执行函数
     *
     * @param position 列表中的位置
     */
    private void menu_deleteClick(int position) {
        SmsService.deleteGroup(groupMessages.get(position).getGroupId());
        updateListView();
    }

    /**
     * 点击清除菜单的执行函数
     */
    private void menu_clearClick() {
        SmsService.deleteGroup(-1);
        updateListView();
    }

    /**
     * 底部的悬浮按钮点击事件
     *
     * @param v 被点击的按钮
     */
    public void clk_floatingBar(View v) {
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
        Anim_BetweenActivity.leftOut_rightIn(this);
    }

    /**
     * 点击关于的监听函数
     *
     * @param v 被点击对象
     */
    public void clk_about(View v) {
        startActivity(new Intent(this, AboutActivity.class));
        Anim_BetweenActivity.leftOut_rightIn(this);
    }

    /**
     * 点击定时短信
     *
     * @param v 被点击的按钮
     */
    public void clk_timingSms(View v) {
        startActivity(new Intent(this, TimingSmsActivity.class));
        Anim_BetweenActivity.leftOut_rightIn(this);
    }

    /**
     * 待发短信
     *
     * @param v 被点击的按钮
     */
    public void clk_smsQueen(View v) {
        startActivity(new Intent(this, TimingSmsQueenActivity.class));
        Anim_BetweenActivity.leftOut_rightIn(this);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationMenu)) {
            drawerLayout.closeDrawers();
        } else {
            if (MyHandler.isExit) {
                finish();
            } else {
                MyHandler.isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                myHandler.sendEmptyMessageDelayed(0, 2000);//吐丝两秒有效
            }
        }
    }

    //列表菜单点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(InputActivity.INTENT_EXTRA, groupMessages.get(position));
        startActivity(intent);
        Anim_BetweenActivity.leftOut_rightIn(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = getMenuInflater();
        menu.setHeaderTitle("操作");
        menuInflater.inflate(R.menu.home_context_menu, menu);
    }

    //上下文菜单选择事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        // 获取选中的条目
        final int index = menuInfo.position;
        final int itemId = item.getItemId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("您确定吗？").setIcon(android.R.drawable.stat_sys_warning).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (itemId) {
                    case R.id.home_menu_context_menu_delete://删除记录
                        menu_deleteClick(index);
                        break;
                    case R.id.home_menu_context_menu_clear://清空记录
                        menu_clearClick();
                        break;
                }
            }
        }).setNegativeButton("取消", null).create().show();

        return super.onContextItemSelected(item);
    }
}
