package com.swpu.gantao.xinfu.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swpu.gantao.xinfu.R;
import com.swpu.gantao.xinfu.bean.GroupMessage;
import com.swpu.gantao.xinfu.bean.Linkman;

import java.util.ArrayList;

/**
 * @author 李长军
 */
public class TimingSmsQueenAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private ArrayList<GroupMessage> groupMessages;//列表的数据

    public TimingSmsQueenAdapter(Context context, ArrayList<GroupMessage> groupMessages) {
        this.context = context;
        this.groupMessages = groupMessages;
    }

    /**
     * 为适配器设置值
     *
     * @param groupMessages 需要显示的数据
     */
    public void setData(ArrayList<GroupMessage> groupMessages) {
        this.groupMessages = groupMessages;
    }

    @Override
    public int getCount() {
        return groupMessages == null ? 0 : groupMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    //显示人数的点击事件
    @Override
    public void onClick(View v) {
        ArrayList<Linkman> linkmans = (ArrayList<Linkman>) v.getTag();
        String[] members = new String[linkmans.size()];
        int i = 0;
        for (Linkman linkman : linkmans) {
            members[i] = linkman.getName() + "(" + linkman.getPhone() + ")";
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("群发人列表").setItems(members, null).create().show();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            v = LayoutInflater.from(context).inflate(R.layout.item_home_list, parent, false);
            viewFind(v, holder);
            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        valueSet(position, holder);
        return v;
    }

    //寻找控件
    private void viewFind(View v, ViewHolder holder) {
        holder.tv_count = (TextView) v.findViewById(R.id.item_home_list_count);
        holder.tv_count.setOnClickListener(this);
        holder.tv_count.setTextColor(Color.BLUE);
        holder.tv_count.setBackgroundResource(R.drawable.saty_timing_sms_queen_count_bg);
        holder.tv_content = (TextView) v.findViewById(R.id.item_home_list_content);
        holder.tv_name = (TextView) v.findViewById(R.id.item_home_list_name);
        holder.tv_time = (TextView) v.findViewById(R.id.item_home_list_time);
    }

    /**
     * 设置值
     *
     * @param position 位置
     * @param holder   holder对象
     */
    private void valueSet(int position, ViewHolder holder) {
        GroupMessage groupMessage = groupMessages.get(position);
        holder.tv_count.setText(groupMessage.getMemberCount() + "");
        holder.tv_count.setTag(groupMessage.getLinkmans());
        holder.tv_content.setText(groupMessage.getContent());
        holder.tv_name.setText(groupMessage.getMember());
        holder.tv_time.setText(groupMessage.getLongFormarttedTime());
    }

    private class ViewHolder {
        //分别表示：名字、人数、内容、时间
        public TextView tv_name, tv_count, tv_content, tv_time;
    }
}
