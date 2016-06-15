package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 简单的List<String>数据的适配器
 * Created by LeeDane on 2016/1/12.
 */
public class SimpleListAdapter extends BaseAdapter{

    private List<String> mDatas;
    private Context mContext;
    public SimpleListAdapter(Context context, List<String> datas){
        mDatas = datas;
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_listview, null);

            myHolder = new MyHolder();
            myHolder.setTextview((TextView)convertView.findViewById(R.id.simple_listview_item));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        //myHolder.getTextview().setBackgroundColor(mContext.getResources().getColor(R.color.white));
        myHolder.getTextview().setTextColor(mContext.getResources().getColor(R.color.black));
        myHolder.getTextview().setText(StringUtil.changeNotNull(mDatas.get(position)));
        myHolder.getTextview().setTag("leedane");
        return convertView;
    }


    static class MyHolder {
        private TextView textview;

        public TextView getTextview() {
            return textview;
        }

        public void setTextview(TextView textview) {
            this.textview = textview;
        }
    }
}
