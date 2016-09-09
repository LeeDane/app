package com.leedane.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
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
public class DownloadAdapter extends BaseListAdapter<DownloadItem> {

    public static final String TAG = "DownloadAdapter";
    public DownloadAdapter(List<DownloadItem> list, Context context){
        super(context, list);
    }

    private int downloadCount = 0;
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyHolder myHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_download_listview, null);
            myHolder = new MyHolder();
            myHolder.createTime = (TextView) view.findViewById(R.id.download_create_time);
            myHolder.desc = (TextView) view.findViewById(R.id.download_desc);
            myHolder.fileName = (TextView) view.findViewById(R.id.download_filename);
            myHolder.progress = (ProgressBar) view.findViewById(R.id.download_progress);
            myHolder.per = (TextView) view.findViewById(R.id.download_per);
            view.setTag(myHolder);
        }else{
            myHolder = (MyHolder)view.getTag();
        }

        Log.i(TAG, "执行了getView()方法");

        DownloadItem downloadItem = mDatas.get(position);
        if(downloadItem.getStatus() == 4){
            downloadCount ++;
        }

        Date createTime = downloadItem.getCreateTime();
        if(createTime == null){
            myHolder.createTime.setText("");
        }else{
            myHolder.createTime.setText(RelativeDateFormat.format(createTime));
        }

        myHolder.fileName.setText(downloadItem.getFileName());

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
            myHolder.progress.setMax(100);
            myHolder.progress.setProgress(per);
            myHolder.per.setText(per + "%");
        }

        //异步执行下载任务
        if(downloadCount < 6){
            myHolder.desc.setText(mContext.getResources().getString(R.string.download_now));
        }
        else
            myHolder.desc.setText(mContext.getResources().getString(R.string.download_stop));
        return view;
    }

    static class MyHolder{


        /**
         * 文件名称
         */
        TextView fileName;

        /**
         * 进度条
         */
        ProgressBar progress;

        /**
         * 百分比
         */
        TextView per;

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