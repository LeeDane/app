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

import com.leedane.cn.app.R;
import com.leedane.cn.download.DownloadItem;
import com.leedane.cn.download.PortDownload;
import com.leedane.cn.util.RelativeDateFormat;

import java.util.Date;
import java.util.List;

/**
 * 下载列表的适配器
 * Created by LeeDane on 2016/1/24.
 */
public class DownloadAdapter extends BaseAdapter{

    public static final String TAG = "DownloadAdapter";
    private ListView mListView ;

    public List<DownloadItem> mList;  //所有下载列表
    private Context mContext; //上下文对象

    public DownloadAdapter(List<DownloadItem> list, Context context, ListView listView){
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

    public void refreshData(List<DownloadItem> downloadItems){
        this.mList.clear();
        this.mList.addAll(downloadItems);
        this.notifyDataSetChanged();
    }

    private int downloadCount = 0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_download_listview, null);
            myHolder = new MyHolder();
            myHolder.setCreateTime((TextView) convertView.findViewById(R.id.download_create_time));
            myHolder.setDesc((TextView) convertView.findViewById(R.id.download_desc));
            myHolder.setFileName((TextView) convertView.findViewById(R.id.download_filename));
            myHolder.setProgress((ProgressBar) convertView.findViewById(R.id.download_progress));
            myHolder.setPer((TextView) convertView.findViewById(R.id.download_per));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }

        Log.i(TAG, "执行了getView()方法");

        DownloadItem downloadItem = mList.get(position);
        if(downloadItem.getStatus() == 4){
            downloadCount ++;
        }

        Date createTime = downloadItem.getCreateTime();
        if(createTime == null){
            myHolder.getCreateTime().setText("");
        }else{
            myHolder.getCreateTime().setText(RelativeDateFormat.format(createTime));
        }

        myHolder.getFileName().setText(downloadItem.getFileName());

        List<PortDownload> portDownloads = downloadItem.getPortDownloads();
        if(portDownloads.size() >0){
            long size = downloadItem.getSize();
            long allFinishSize = 0;
            for(PortDownload portDownload: portDownloads){
                if(portDownload.isFinish()){
                    allFinishSize += portDownload.getLength();
                }
            }

            int per = (int) (allFinishSize/size * 100);
            myHolder.getProgress().setMax(100);
            myHolder.getProgress().setProgress(per);
            myHolder.getPer().setText(per + "%");
        }

        //异步执行下载任务
        if(downloadCount < 6){
            myHolder.getDesc().setText(mContext.getResources().getString(R.string.download_now));
        }
        else
            myHolder.getDesc().setText(mContext.getResources().getString(R.string.download_stop));
        return convertView;
    }

    private class MyHolder{


        /**
         * 文件名称
         */
        private TextView fileName;

        /**
         * 进度条
         */
        private ProgressBar progress;

        /**
         * 百分比
         */
        private TextView per;

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

        public TextView getPer() {
            return per;
        }

        public void setPer(TextView per) {
            this.per = per;
        }

        public ProgressBar getProgress() {
            return progress;
        }

        public void setProgress(ProgressBar progress) {
            this.progress = progress;
        }
    }
}
