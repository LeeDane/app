package com.leedane.cn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.CircleOfFriendAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpResponseCircleOfFriendBean;
import com.leedane.cn.bean.TimeLineBean;
import com.leedane.cn.handler.CircleOfFriendHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 朋友圈activity
 * Created by LeeDane on 2016/4/15.
 */
public class CircleOfFriendActivity  extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "CircleOfFriendActivity";
    private ListView mListView;
    private CircleOfFriendAdapter mAdapter;
    protected TextView mListViewFooter;
    protected View viewFooter;
    protected String mPreLoadMethod = "firstloading";//当前的操作方式
    protected boolean isLoading; //标记当前是否在加载数据
    protected int mFirstId;  //页面上第一条数据的ID
    protected int mLastId; //页面上第一条数据的ID
    private List<TimeLineBean> mTimeLineBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkedIsLogin();
        setContentView(R.layout.fragment_listview);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.circle_of_friend);
        backLayoutVisible();

        mListView = (ListView)findViewById(R.id.listview_items);
        mAdapter = new CircleOfFriendAdapter(CircleOfFriendActivity.this, mTimeLineBeans);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new ListViewOnScrollListener());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonHandler.startDetailActivity(CircleOfFriendActivity.this, mTimeLineBeans.get(position).getTableName(), mTimeLineBeans.get(position).getTableId(), null);
            }
        });

        //listview下方的显示
        viewFooter = LayoutInflater.from(CircleOfFriendActivity.this).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(CircleOfFriendActivity.this);//添加点击事件
        mListViewFooter.setText(getResources().getString(R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        sendFirstLoading();
    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() < 1){
            Intent it = new Intent(CircleOfFriendActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.CircleOfFriendActivity");
            startActivity(it);
            finish();
            return;
        }
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_CIRCLEOFFRIEND) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getResources().getString(R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_CIRCLEOFFRIEND){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseCircleOfFriendBean httpResponseCircleOfFriendBean = BeanConvertUtil.strConvertToCircleOfFriendBeans(String.valueOf(result));
                if(httpResponseCircleOfFriendBean != null && httpResponseCircleOfFriendBean.isSuccess()){
                    List<TimeLineBean> timeLineBeans = httpResponseCircleOfFriendBean.getMessage();
                    if(timeLineBeans != null && timeLineBeans.size() > 0){
                        //临时list
                        List<TimeLineBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mTimeLineBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = timeLineBeans.size() -1; i>= 0 ; i--){
                                temList.add(timeLineBeans.get(i));
                            }
                            temList.addAll(mTimeLineBeans);
                        }else{
                            temList.addAll(mTimeLineBeans);
                            temList.addAll(timeLineBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mTimeLineBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new CircleOfFriendAdapter(CircleOfFriendActivity.this, mTimeLineBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        int size = mTimeLineBeans.size();

                        mFirstId = 0;
                        mLastId = size;

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getResources().getString(R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mTimeLineBeans.clear();
                            mAdapter.refreshData(new ArrayList<TimeLineBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getResources().getString(R.string.no_load_more));
                        }else {
                            ToastUtil.success(CircleOfFriendActivity.this, getResources().getString(R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mTimeLineBeans.clear();
                            mAdapter.refreshData(new ArrayList<TimeLineBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(getResources().getString(R.string.load_more_error));
                    }else{
                        ToastUtil.failure(CircleOfFriendActivity.this);
                    }
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    private void sendFirstLoading(){

        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", 10);
        params.put("method", mPreLoadMethod);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        CircleOfFriendHandler.getCircleOfFriendsRequest(this, params);
    }
    /**
     * 发送向上刷新的任务
     */
    private void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        if(mFirstId == 0){
            sendFirstLoading();
            return;
        }
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", 5);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        CircleOfFriendHandler.getCircleOfFriendsRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    private void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getResources().getString(R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mListViewFooter.setText(getResources().getString(R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", 5);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        CircleOfFriendHandler.getCircleOfFriendsRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    private void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getResources().getString(R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getResources().getString(R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){

            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? 10: 5);
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            mListViewFooter.setText(getResources().getString(R.string.loading));
            taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
            CircleOfFriendHandler.getCircleOfFriendsRequest(this, params);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onRefresh() {
        sendUpLoading();
    }

    /**
     *
     * ListView向下滚动事件的监听
     */
    class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            //滚动停止
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                //当倒数第三个数据出现的时候就开始加载
                if (view.getLastVisiblePosition() == view.getCount() -1) {
                    if(!isLoading){
                        sendLowLoading();
                    }
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }
}
