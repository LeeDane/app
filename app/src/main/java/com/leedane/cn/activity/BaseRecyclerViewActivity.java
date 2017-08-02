package com.leedane.cn.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;

/**
 * RecyclerView做公共的Activity类
 * Created by LeeDane on 2017/8/2.
 */
public abstract class BaseRecyclerViewActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    protected String mPreLoadMethod = "firstloading";//当前的操作方式
    protected boolean isLoading; //标记当前是否在加载数据
    protected int mFirstId;  //页面上第一条数据的ID
    protected int mLastId; //页面上第一条数据的ID

    protected TextView mRecyclerViewFooter;
    protected View mFooterView;
    protected SwipeRefreshLayout mSwipeLayout;

    /**
     * 发送第一次刷新的任务
     */
    protected abstract void sendFirstLoading();
    /**
     * 发送向上刷新的任务
     */
    protected abstract void sendUpLoading();
    /**
     * 发送向下刷新的任务
     */
    protected abstract void sendLowLoading();

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    protected abstract void sendLoadAgain(View view);

    @Override
    public void onRefresh() {
        sendUpLoading();
    }

    protected class RecyclerViewOnScrollListener<T> extends RecyclerView.OnScrollListener{
        RecyclerView recyclerView;
        int lastVisibleItem;
        BaseRecyclerViewAdapter<T> mAdapter;
        boolean isScrooll;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            this.recyclerView = recyclerView;
            isScrooll = true;

        }

        public RecyclerViewOnScrollListener(BaseRecyclerViewAdapter<T> adapter) {
            super();
            mAdapter = adapter;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                Log.i("test", "loading executed");

                if(mSwipeLayout != null){
                    boolean isRefreshing = mSwipeLayout.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                }

                if (!isLoading) {
                    sendLowLoading();
                }
            }
            //滚动停止
            /*if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isScrooll) {
                int childCount = recyclerView.getChildCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == childCount -1) {
                    if(!isLoading){
                        sendLowLoading();
                        isScrooll = false;
                    }
                }
            }*/
        }
    }
}
