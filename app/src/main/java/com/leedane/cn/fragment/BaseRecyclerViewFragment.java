package com.leedane.cn.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;

/**
 * RecyclerView做fragment公共的fragment类
 * Created by LeeDane on 2016/9/11.
 */
public abstract class BaseRecyclerViewFragment extends Fragment implements TaskListener, View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;

    protected String mPreLoadMethod = "firstloading";//当前的操作方式
    protected boolean isLoading; //标记当前是否在加载数据
    protected int mFirstId;  //页面上第一条数据的ID
    protected int mLastId; //页面上第一条数据的ID

    protected TextView mRecyclerViewFooter;
    protected View mFooterView;

    protected SwipeRefreshLayout mSwipeLayout;

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            Toast.makeText(BaseApplication.newInstance(), ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            dismissLoadingDialog();
            return;
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    protected void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getActivity(), title, main, true, cancelable);
    }
    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    /**
     * 获取字符串资源
     * @param mContext
     * @param resourseId
     * @return
     */
    public String getStringResource(Context mContext, int resourseId){
        if(mContext == null){
            return BaseApplication.newInstance().getResources().getString(resourseId);
        }else{
            return mContext.getResources().getString(resourseId);
        }
    }

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
    public void onClick(View v) {

    }

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
