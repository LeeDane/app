package com.leedane.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.bean.SettingBean;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置ListView选项的adapter
 * Created by Leedane on 2015/11/6.
 */
public class SettingAdapter extends BaseAdapter{

    public static final String TAG = "SettingAdapter";
    private List<SettingBean> mListData = new ArrayList<>();
    private Context mContext;
    private ListView mListView;

    public SettingAdapter(List<SettingBean> listData, ListView listview, Context context){
        this.mListData = listData;
        this.mContext = context;
        this.mListView = listview;
    }
    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyHodler myHodler;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_setting_listview, null);
            myHodler = new MyHodler();
            myHodler.setSetting_item_edittext((EditText)convertView.findViewById(R.id.setting_item_edittext));
            myHodler.setSetting_item_textview((TextView) convertView.findViewById(R.id.setting_item_btn));
            convertView.setTag(myHodler);
        }else{
            myHodler = (MyHodler)convertView.getTag();
        }

        String text = mListData.get(position).getContent();

        if(StringUtil.isNull(text)){
            myHodler.getSetting_item_edittext().setHint(mListData.get(position).getHint());
        }else{
            myHodler.getSetting_item_edittext().setText(text);
        }

        final String uuid = mListData.get(position).getUuid();
        myHodler.getSetting_item_edittext().setTag(uuid);
        myHodler.getSetting_item_textview().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Toast.makeText(mContext, "当前位置"+position, Toast.LENGTH_SHORT).show();
                    EditText edt = (EditText)mListView.findViewWithTag(uuid);
                    String textvalue = edt.getText().toString();
                    Toast.makeText(mContext, "点击的选项的EditText文本是："+ textvalue, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "当前位置"+position+"点击的选项的EditText文本是："+textvalue);
                    if(StringUtil.isNull(textvalue)){
                        //ToastUtil.success(mContext, "文本不能为空", Toast.LENGTH_SHORT);
                        edt.setFocusable(true);
                        return;
                    }
                    mListData.get(position).setContent(textvalue);
                    switch (position){
                        case 0:
                            SharedPreferenceUtil.saveSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER, mListData.get(position));
                            break;
                    }

                    //ToastUtil.success(mContext, "保存成功", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    private class MyHodler{
        private EditText setting_item_edittext;
        private TextView setting_item_textview;

        public EditText getSetting_item_edittext() {
            return setting_item_edittext;
        }

        public void setSetting_item_edittext(EditText setting_item_edittext) {
            this.setting_item_edittext = setting_item_edittext;
        }

        public TextView getSetting_item_textview() {
            return setting_item_textview;
        }

        public void setSetting_item_textview(TextView setting_item_textview) {
            this.setting_item_textview = setting_item_textview;
        }
    }
}
