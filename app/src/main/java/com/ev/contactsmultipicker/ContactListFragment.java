/**
 * Copyright 2013 Ernestas Vaiciukevicius (ernestas.vaiciukevicius@gmail.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. *
 */
package com.ev.contactsmultipicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.ev.contactsmultipicker.ContactResult.ResultItem;
import com.swpu.gantao.xinfu.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * 显示列表的Fragment
 *
 * @author 李长军
 */
public class ContactListFragment extends Fragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private final static String SAVE_STATE_KEY = "mcListFrag";

    //默认获取通讯录中的联系人姓名、id、头像
    private final String[] projection = new String[]{Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.PHOTO_THUMBNAIL_URI};
    //筛选有手机号码的联系人
    private final String selection = Contacts.HAS_PHONE_NUMBER + " = 1";
    //联系人列表
    private ListView mContactListView;
    //列表适配器
    private ContactsCursorAdapter mCursorAdapter;
    //存放勾选记录的哈希表
    private Hashtable<String, ContactResult> results = new Hashtable<String, ContactResult>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCursorAdapter = new ContactsCursorAdapter(getActivity(), R.layout.contact_list_item, null,
                new String[]{Contacts.DISPLAY_NAME,
                        Contacts.PHOTO_THUMBNAIL_URI},
                new int[]{R.id.contactLabel, R.id.contactImage}, 0);

        getLoaderManager().initLoader(0, null, this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //如果意外杀掉了此界面。我们应该恢复数据
            results = (Hashtable<String, ContactResult>) savedInstanceState.getSerializable(SAVE_STATE_KEY);
        }

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container);

        mContactListView = (ListView) rootView.findViewById(R.id.contactListView);

        mContactListView.setAdapter(mCursorAdapter);
        mContactListView.setOnItemClickListener(this);

        return rootView;
    }

    /**
     * 列表适配器
     */
    private class ContactsCursorAdapter extends SimpleCursorAdapter {
        public ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ret = super.getView(position, convertView, parent);

            CheckBox checkbox = (CheckBox) ret.findViewById(R.id.contactCheck);

            getCursor().moveToPosition(position);
            String id = getCursor().getString(0);//拿到联系人的id
            checkbox.setChecked(results.containsKey(id));//判断勾选列表中是否存在此数据。
            ImageView imageView = (ImageView) ret.findViewById(R.id.contactImage);
            String imgUri = getCursor().getString(2);//拿到联系人的头像地址
            if (TextUtils.isEmpty(imgUri)) {//如果头像地址为空，使用默认头像
                imageView.setImageResource(R.drawable.ic_contact_picture);
            }
            return ret;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_STATE_KEY, results);
    }

    /**
     * 获取存放记录的哈希表
     *
     * @return 记录表
     */
    public Hashtable<String, ContactResult> getResults() {
        return results;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        //开始查询
        return new CursorLoader(getActivity(), Contacts.CONTENT_URI,
                projection, selection, null, Contacts.DISPLAY_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);//cursor加载完毕后，置换适配器的cursor
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);//重置cursor
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.contactCheck);

        Cursor cursor = mCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        String id = cursor.getString(0);//获取通讯录联系人的id

        if (checkbox.isChecked()) {//如果之前是勾选，现在应该反勾选
            checkbox.setChecked(false);
            results.remove(id);
        } else {
            checkbox.setChecked(true);
            String name = cursor.getString(1);//获取到名字
            //获取指定id的手机号码等详细信息。（在cursor中）
            Cursor itemCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            List<ResultItem> resultItems = new LinkedList<ResultItem>();

            //遍历cursor，获取指定id的手机号码。（如果有多个手机号码，应该给用户选择）
            itemCursorLoop:
            while (itemCursor.moveToNext()) {
                String contactNumber = itemCursor.getString(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int contactKind = itemCursor.getInt(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                for (ResultItem previousItem : resultItems) {
                    //如果包含了同样的手机号码，直接跳过
                    if (contactNumber.equals(previousItem.getResult())) {
                        continue itemCursorLoop;
                    }
                }
                //将联系方式方到集合中
                resultItems.add(new ResultItem(contactNumber, contactKind));
            }
            itemCursor.close();//遍历完毕后，关闭游标

            if (resultItems.size() > 1) {
                // 如果手机号码有多个。应该弹出对话框。让用户选择
                chooseFromMultipleItems(resultItems, checkbox, id, name);
            } else {
                // 如果只有一个手机号码
                results.put(id, new ContactResult(id, name, resultItems));
            }
        }
    }

    /**
     * 联系人有多个手机号码的时候，弹出对话框。选择具体的手机号码
     *
     * @param items    联系人对应的手机号码结果
     * @param checkbox 复选框
     * @param id       联系人id
     */
    protected void chooseFromMultipleItems(List<ResultItem> items, CheckBox checkbox, String id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayList<String> itemLabels = new ArrayList<>(items.size());

        for (ResultItem resultItem : items) {
            itemLabels.add(resultItem.getResult());
        }

        /**
         * 复选框的监听器
         */
        class ClickListener implements OnCancelListener, OnClickListener, OnMultiChoiceClickListener {
            private List<ResultItem> items;
            private CheckBox checkbox;
            private String id;
            private String name;
            private boolean[] checked;

            public ClickListener(List<ResultItem> items, CheckBox checkbox, String id, String name) {
                this.items = items;
                this.checkbox = checkbox;
                this.id = id;
                this.name = name;
                checked = new boolean[items.size()];
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

            @Override
            public void onClick(DialogInterface arg0, int which, boolean isChecked) {
                checked[which] = isChecked;
            }

            /**
             * 对话框消失的执行函数
             */
            private void finish() {
                ArrayList<ResultItem> result = new ArrayList<>(items.size());
                for (int i = 0; i < items.size(); ++i) {
                    if (checked[i]) {
                        //将勾选了的号码添加到集合
                        result.add(items.get(i));
                    }
                }
                if (result.size() == 0) {
                    checkbox.setChecked(false);
                } else {
                    results.put(id, new ContactResult(id, name, result));
                }
            }

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }

        }

        ClickListener clickListener = new ClickListener(items, checkbox, id, name);

        builder
                .setMultiChoiceItems(itemLabels.toArray(new String[0]), null, clickListener)
                .setOnCancelListener(clickListener)
                .setPositiveButton(android.R.string.ok, clickListener)
                .show();
    }

}
