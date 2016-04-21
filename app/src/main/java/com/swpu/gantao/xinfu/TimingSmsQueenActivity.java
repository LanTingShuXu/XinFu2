package com.swpu.gantao.xinfu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.swpu.gantao.xinfu.adapter.TimingSmsQueenAdapter;
import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.service.SmsService;
import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

import java.util.ArrayList;

/**
 * 短信队列界面
 */
public class TimingSmsQueenActivity extends AppCompatActivity {

    private View emptyView;
    private ListView lst_content;//列表
    private TimingSmsQueenAdapter adapter;//适配器

    private ArrayList<GroupMessage> groupMessages = null;//定时短信数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_sms_queen);
        initToolBar();
        viewFind();
        initListView();
    }

    private void viewFind() {
        emptyView = findViewById(R.id.aty_timing_sms_queen_emptyView);
        lst_content = (ListView) findViewById(R.id.aty_timing_sms_queen_list);
    }

    //初始化ListView
    private void initListView() {
        lst_content.setEmptyView(emptyView);
        registerForContextMenu(lst_content);
        adapter = new TimingSmsQueenAdapter(this, groupMessages);
        lst_content.setAdapter(adapter);
        updateListView();
    }

    public void updateListView() {
        groupMessages = SmsService.getTimingSmsQueen();
        adapter.setData(groupMessages);
        adapter.notifyDataSetChanged();
    }

    /**
     * 点击删除菜单的执行函数
     *
     * @param position 列表中的位置
     */
    private void menu_deleteClick(int position) {
        SmsService.deleteTimingSms(groupMessages.get(position).getGroupId());
        updateListView();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimingSmsQueenActivity.this.finish();
                Anim_BetweenActivity.leftIn_rightOut(TimingSmsQueenActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        TimingSmsQueenActivity.this.finish();
        Anim_BetweenActivity.leftIn_rightOut(TimingSmsQueenActivity.this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作");
        getMenuInflater().inflate(R.menu.timing_queen_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

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
                    case R.id.timing_sms_queen_menu_delete://删除记录
                        menu_deleteClick(index);
                        break;
                }
            }
        }).setNegativeButton("取消", null).create().show();

        return super.onContextItemSelected(item);
    }
}
