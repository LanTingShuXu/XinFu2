package com.swpu.gantao.xinfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swpu.gantao.xinfu.R;
import com.swpu.gantao.xinfu.bean.SmsContent;

import java.util.ArrayList;

/**
 * 群组消息界面适配器
 *
 * @author 李长军
 */
public class InputLisAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SmsContent> smsContents;

    public InputLisAdapter(Context context, ArrayList<SmsContent> smsContents) {
        this.context = context;
        this.smsContents = smsContents;
    }

    /**
     * 设置适配的数据
     *
     * @param smsContents 数据
     */
    public void setData(ArrayList<SmsContent> smsContents) {
        this.smsContents = smsContents;
    }

    @Override
    public int getCount() {
        return smsContents == null ? 0 : smsContents.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            v = LayoutInflater.from(context).inflate(R.layout.item_input_list, parent, false);
            viewFind(v, holder);
            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        valueSet(position, holder);
        return v;
    }

    /**
     * 为控件设置值
     *
     * @param position 列表的position
     * @param holder   holder对象
     */
    public void valueSet(int position, ViewHolder holder) {
        SmsContent smsContent = smsContents.get(position);
        if (smsContent.getTime() > 0) {
            holder.tv_time.setText(smsContent.getFormarttedTime());
            holder.tv_content.setText(smsContent.getContent());
        }
    }

    /**
     * 集中使用findViewById()
     *
     * @param v      父view
     * @param holder holder对象
     */
    private void viewFind(View v, ViewHolder holder) {
        holder.tv_time = (TextView) v.findViewById(R.id.item_input_list_time);
        holder.tv_content = (TextView) v.findViewById(R.id.item_input_list_content);
    }

    private class ViewHolder {
        public TextView tv_time, tv_content;
    }
}
