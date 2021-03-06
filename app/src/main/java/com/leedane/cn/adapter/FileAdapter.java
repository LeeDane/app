package com.leedane.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.FileBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 我的文件列表的适配器
 * Created by LeeDane on 2016/1/24.
 */
public class FileAdapter extends BaseListAdapter<FileBean>{
    public static final String TAG = "FileAdapter";

    public FileAdapter(Context context, List<FileBean> list){
        super(context, list);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyHolder myHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_file_listview, null);
            myHolder = new MyHolder();
            myHolder.createTime = (TextView) view.findViewById(R.id.file_item_createTime);
            myHolder.desc = (TextView) view.findViewById(R.id.file_item_desc);
            myHolder.fileName = (TextView) view.findViewById(R.id.file_item_fileName);
            view.setTag(myHolder);
        }else{
            myHolder = (MyHolder)view.getTag();
        }

        Log.i(TAG, "执行了getView()方法");

        FileBean fileBean = mDatas.get(position);

        myHolder.desc.setText("正常");
        String createTime = fileBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.createTime.setText("");
        }else{
            myHolder.createTime.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }
        myHolder.fileName.setText(fileBean.getPath());
        return view;
    }

    static class MyHolder{
        /**
         * 文件名称
         */
        TextView fileName;

        /**
         * 状态的描述
         */
        TextView desc;

        /**
         * 创建时间
         */
        TextView createTime;
    }
}
