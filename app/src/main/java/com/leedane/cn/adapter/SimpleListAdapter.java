package com.leedane.cn.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 简单的List<String>数据的适配器
 * Created by LeeDane on 2016/1/12.
 */
public class SimpleListAdapter extends BaseListAdapter<String>{
    public SimpleListAdapter(Context context, List<String> datas){
        super(context, datas);
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
            myHolder.tv = (TextView)convertView.findViewById(R.id.simple_listview_item);
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        //myHolder.getTextview().setBackgroundColor(mContext.getResources().getColor(R.color.white));
        myHolder.tv.setTextColor(mContext.getResources().getColor(R.color.black));
        myHolder.tv.setText(Html.fromHtml(StringUtil.changeNotNull(mDatas.get(position))));
        myHolder.tv.setTag("leedane");
        return convertView;
    }

    static class MyHolder {
        TextView tv;
    }
}
