package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.leedane.cn.adapter.DownloadAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.download.DownloadItem;
import com.leedane.cn.download.PortDownload;
import com.leedane.cn.task.TaskType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 下载activity
 * Created by LeeDane on 2015/10/17.
 */
public class DownloadActivity extends BaseActivity {
    private static final String TAG = "DownloadActivity";

    private ListView mListView;
    private DownloadAdapter mAdapter;
    private List<DownloadItem> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(DownloadActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.DownloadActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_download);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.system_download);
        backLayoutVisible();

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mListView = (ListView)findViewById(R.id.download_listview);
        initData();
        mAdapter = new DownloadAdapter(items, DownloadActivity.this);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        items = new ArrayList<>();
        DownloadItem item1 = new DownloadItem();
        item1.setCreateTime(new Date());
        item1.setFileName("1_82378545124366_20160119184016_myJava.pdf");
        PortDownload portDownload1 = new PortDownload();
        portDownload1.setIsFinish(false);
        portDownload1.setFrom(0);
        portDownload1.setLength(1024 * 1024);
        PortDownload portDownload2 = new PortDownload();
        portDownload2.setIsFinish(false);
        portDownload2.setFrom(1024 * 1024);
        portDownload2.setLength(1024 * 1024);
        PortDownload portDownload3 = new PortDownload();
        portDownload3.setIsFinish(false);
        portDownload3.setFrom(1024 * 1024 * 2);
        portDownload3.setLength(1024 * 1024);
        PortDownload portDownload4 = new PortDownload();
        portDownload4.setIsFinish(false);
        portDownload4.setFrom(1024 * 1024 * 3);
        portDownload4.setLength(1024 * 1024);
        List<PortDownload> portDownloads = new ArrayList<>();
        portDownloads.add(portDownload1);
        portDownloads.add(portDownload2);
        portDownloads.add(portDownload3);
        portDownloads.add(portDownload4);
        item1.setPortDownloads(portDownloads);
        item1.setSize(1024 * 1024 * 4);
        item1.setStatus(1);
        item1.setUuid(UUID.randomUUID().toString());
        items.add(item1);
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
