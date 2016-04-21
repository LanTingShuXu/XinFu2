package com.swpu.gantao.xinfu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ev.contactsmultipicker.ContactPickerActivity;
import com.ev.contactsmultipicker.ContactResult;
import com.swpu.gantao.xinfu.adapter.InputLisAdapter;
import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.bean.Linkman;
import com.swpu.gantao.xinfu.bean.SmsContent;
import com.swpu.gantao.xinfu.service.SmsService;
import com.swpu.gantao.xinfu.utils.Anim_BetweenActivity;

import java.util.ArrayList;

/**
 * 编辑短信界面
 */
public class InputActivity extends AppCompatActivity {
    /**
     * intent附加内容的key。
     */
    public static final String INTENT_EXTRA = "extra";
    private final int REQUEST_SELECT_CONTACT = 1;//启动联系人的请求码

    private TextView edt_receiver;//接受者的输入框
    private EditText edt_content;//发送输入框
    private ImageView img_plus;//联系人添加按钮
    private ListView lst_content;//消息列表

    private InputLisAdapter adapter;//消息列表的适配器
    private ArrayList<Linkman> smsQueen = new ArrayList<>();//短信队列
    private ArrayList<SmsContent> smsContents;//聊天历史数据
    private long groupId = -1;//群发组别号（默认为-1，表示数据库中没有此组别。为刚刚创建）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        viewFind();
        initToolBar();
        initList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取来自通讯录的联系人返回uri
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            smsQueen = processContacts((ArrayList<ContactResult>)
                    data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT));
            edt_receiver.setText("");
            for (Linkman linkman : smsQueen) {
                edt_receiver.setText(edt_receiver.getText().toString() + linkman.getName() + ";");
            }
        }
    }

    /**
     * 进行简单的初始化
     */
    private void initList() {
        adapter = new InputLisAdapter(this, smsContents);
        lst_content.setAdapter(adapter);
        registerForContextMenu(lst_content);

        Intent intent = getIntent();
        Object o = intent.getSerializableExtra(INTENT_EXTRA);
        if (o != null) {//如果传递了数据过来，表示不是新建的分组
            GroupMessage groupMessage = (GroupMessage) o;
            fillInput(groupMessage);
            img_plus.setVisibility(View.GONE);
            edt_receiver.setText(groupMessage.getMember());
            smsQueen = groupMessage.getLinkmans();
            groupId = groupMessage.getGroupId();
            updateList();
        }
    }

    /**
     * 填充输入框
     *
     * @param groupMessage
     */
    private void fillInput(GroupMessage groupMessage) {
        //如果有草稿
        if (groupMessage != null && groupMessage.getTime() < 0) {
            String content = groupMessage.getContent();
            content = content.substring("【草稿】".length());
            edt_content.setText(content);
            Selection.setSelection(edt_content.getText(),content.length());
        }
    }

    /**
     * 更新list列表
     */
    private void updateList() {
        smsContents = SmsService.getGroupChatContent(groupId);
        adapter.setData(smsContents);
        adapter.notifyDataSetChanged();
        //如果发送成功后，将添加按钮隐藏
        if (groupId != -1 && img_plus.isShown()) {
            img_plus.setVisibility(View.GONE);
        }
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
     * 初始化Toolbar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_input_toolBar);
        setSupportActionBar(toolbar);
        //必须要设置在后面
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputActivity.this.finish();
                Anim_BetweenActivity.leftIn_rightOut(InputActivity.this);
            }
        });
    }

    /**
     * 集中使用findViewById（）
     */
    private void viewFind() {
        edt_receiver = (TextView) findViewById(R.id.content_input_receiver);
        edt_content = (EditText) findViewById(R.id.content_input_content);
        img_plus = (ImageView) findViewById(R.id.content_input_plus);
        lst_content = (ListView) findViewById(R.id.content_input_list);
    }

    /**
     * 点击删除菜单的执行函数
     *
     * @param position 列表中的位置
     */
    private void menu_deleteClick(int position) {
        SmsService.deleteContent(smsContents.get(position).get_id());
        updateList();
    }

    /**
     * 点击清除菜单的执行函数
     */
    private void menu_clearClick() {
        SmsService.deleteContent(-1);
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
    public void clk_add(View v) {
        selectContact();
    }

    /**
     * 点击发送按钮的响应事件
     *
     * @param v 被点击的按钮对象
     */
    public void clk_send(View v) {
        if (smsQueen.size() == 0) {
            Toast.makeText(this, "您还未选择接收者呢~", Toast.LENGTH_LONG).show();
        }//如果输入内容为空
        else if (TextUtils.isEmpty(edt_content.getText().toString().trim())) {
            Toast.makeText(this, "您还未输入短信内容~", Toast.LENGTH_LONG).show();
        } else {
            groupId = SmsService.sendSms(groupId, smsQueen, edt_content.getText().toString(), false);
            edt_content.setText("");
            updateList();
        }
    }

    @Override
    protected void onPause() {
        //如果有内容没有保存
        if (smsQueen.size() != 0) {
            String content = null;
            if (!TextUtils.isEmpty(edt_content.getText().toString().trim())) {
                content = edt_content.getText().toString();
            }
            SmsService.saveSms(groupId, smsQueen, content);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
        Anim_BetweenActivity.leftIn_rightOut(this);
    }

    //创建上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("操作");
        getMenuInflater().inflate(R.menu.input_context_menu, menu);
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
                    case R.id.input_menu_context_menu_delete://删除记录
                        menu_deleteClick(index);
                        break;
                    case R.id.input_menu_context_menu_clear://清空记录
                        menu_clearClick();
                        break;
                }
            }
        }).setNegativeButton("取消", null).create().show();
        return super.onContextItemSelected(item);
    }
}
