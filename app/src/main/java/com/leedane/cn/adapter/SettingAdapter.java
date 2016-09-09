package com.leedane.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.SettingBean;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 设置ListView选项的adapter
 * Created by Leedane on 2015/11/6.
 */
public class SettingAdapter extends BaseListAdapter<SettingBean>{

    public static final String TAG = "SettingAdapter";
    private ListView mListView;

    public SettingAdapter(List<SettingBean> listData, ListView listview, Context context){
        super(context, listData);
        this.mListView = listview;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHodler myHodler;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_setting_listview, null);
            myHodler = new MyHodler();
            myHodler.itemEdittext = (EditText)convertView.findViewById(R.id.setting_item_edittext);
            myHodler.itemTextview = (TextView) convertView.findViewById(R.id.setting_item_btn);
            convertView.setTag(myHodler);
        }else{
            myHodler = (MyHodler)convertView.getTag();
        }

        String text = mDatas.get(position).getContent();

        if(StringUtil.isNull(text)){
            myHodler.itemEdittext.setHint(mDatas.get(position).getHint());
        } else{
            myHodler.itemEdittext.setText(text);
        }

        final String uuid = mDatas.get(position).getUuid();
        myHodler.itemEdittext.setTag(uuid);
        myHodler.itemTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "当前位置" + position, Toast.LENGTH_SHORT).show();
                EditText edt = (EditText) mListView.findViewWithTag(uuid);
                String textvalue = edt.getText().toString();
                Toast.makeText(mContext, "点击的选项的EditText文本是：" + textvalue, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "当前位置" + position + "点击的选项的EditText文本是：" + textvalue);
                if (StringUtil.isNull(textvalue)) {
                    //ToastUtil.success(mContext, "文本不能为空", Toast.LENGTH_SHORT);
                    edt.setFocusable(true);
                    return;
                }
                mDatas.get(position).setContent(textvalue);
                switch (position) {
                    case 0:
                        SharedPreferenceUtil.saveSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER, mDatas.get(position));
                        break;
                }

                //ToastUtil.success(mContext, "保存成功", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    static class MyHodler{
        EditText itemEdittext;
        TextView itemTextview;
    }
}
