package com.leedane.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leedane.cn.File.FileBean;
import com.leedane.cn.download.DownloadItem;
import com.leedane.cn.download.PortDownload;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * 我的文件列表的适配器
 * Created by LeeDane on 2016/1/24.
 */
public class FileAdapter extends BaseAdapter{

    public static final String TAG = "FileAdapter";
    private ListView mListView ;

    public List<FileBean> mList;  //所有下载列表
    private Context mContext; //上下文对象

    public FileAdapter(List<FileBean> list, Context context, ListView listView){
        super();
        this.mList = list;
        this.mContext = context;
        this.mListView = listView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<FileBean> fileBeans){
        this.mList.clear();
        this.mList.addAll(fileBeans);
        this.notifyDataSetChanged();
    }
    MyHolder myHolder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_listview, null);
            myHolder = new MyHolder();
            myHolder.setCreateTime((TextView) convertView.findViewById(R.id.file_item_createTime));
            myHolder.setDesc((TextView) convertView.findViewById(R.id.file_item_desc));
            myHolder.setFileName((TextView) convertView.findViewById(R.id.file_item_fileName));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }

        Log.i(TAG, "执行了getView()方法");

        FileBean fileBean = mList.get(position);

        myHolder.getDesc().setText("正常");
        String createTime = fileBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getCreateTime().setText("");
        }else{
            myHolder.getCreateTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }
        myHolder.getFileName().setText(fileBean.getFileName());
        return convertView;
    }

    private class MyHolder{


        /**
         * 文件名称
         */
        private TextView fileName;

        /**
         * 状态的描述
         */
        private TextView desc;

        /**
         * 创建时间
         */
        private TextView createTime;


        public TextView getCreateTime() {
            return createTime;
        }

        public void setCreateTime(TextView createTime) {
            this.createTime = createTime;
        }

        public TextView getDesc() {
            return desc;
        }

        public void setDesc(TextView desc) {
            this.desc = desc;
        }

        public TextView getFileName() {
            return fileName;
        }

        public void setFileName(TextView fileName) {
            this.fileName = fileName;
        }

    }
}
