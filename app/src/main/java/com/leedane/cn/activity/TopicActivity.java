package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.leedane.cn.adapter.PersonalMoodListViewAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseMoodBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.TopicHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 话题的activity
 * Created by LeeDane on 2016/9/11.
 */
public class TopicActivity extends BaseActivity implements BaseRecyclerViewAdapter.OnItemClickListener,
        BaseRecyclerViewAdapter.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener{
    public static final String TAG = "TopicActivity";
    private RecyclerView mRecyclerView;

    private String topic;
    private List<MoodBean> mMoodBeans = new ArrayList<>();
    private PersonalMoodListViewAdapter mAdapter;

    private TextView mRecyclerViewFooter;
    private View mFooterView;

    private SwipeRefreshLayout mSwipeLayout;

    private String mPreLoadMethod = "firstloading";//当前的操作方式

    //当前listview中最旧一篇文章的id
    private int mFirstId;

    //当前listview中最新一篇文章的id
    private int mLastId;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(TopicActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.TopicActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.fragment_recyclerview);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        topic = getIntent().getStringExtra("topic");
        if(StringUtil.isNull(topic)){
            ToastUtil.failure(this, "话题为空");
            finish();
            return;
        }

        //处理一下标题
        String title = topic;
        if(!topic.startsWith("#") && !topic.endsWith("#"))
            title = getStringResource(R.string.topic) +"#" + topic + "#";
            //title = getStringResource(R.string.topic) +"<font color='red'>#" + topic + "#</font>";

        setTitleViewText(title);
        initView();
    }


    private void initView() {
        sendFirstLoading();
        //initFirstData();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mAdapter = new PersonalMoodListViewAdapter(TopicActivity.this, mMoodBeans, null);
        mAdapter.setOnItemClickListener(TopicActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(TopicActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(TopicActivity.this, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());

        //listview下方的显示
        mFooterView = LayoutInflater.from(TopicActivity.this).inflate(R.layout.fragment_financial_main_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView) mFooterView.findViewById(R.id.financial_footer);
        mRecyclerViewFooter.setOnClickListener(TopicActivity.this);//添加点击事件
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(TopicActivity.this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        sendUpLoading();
    }

    protected class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener{
        RecyclerView recyclerView;
        int lastVisibleItem;
        boolean isScrooll;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            this.recyclerView = recyclerView;
            isScrooll = true;

        }

        public RecyclerViewOnScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
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
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_TOPIC && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mRecyclerViewFooter.setText(getStringResource(R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_TOPIC){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseMoodBean httpResponseMoodBean = BeanConvertUtil.strConvertToMoodBeans(String.valueOf(result));
                if(httpResponseMoodBean != null && httpResponseMoodBean.isSuccess()){
                    List<MoodBean> moodBeans = httpResponseMoodBean.getMessage();
                    if(moodBeans != null && moodBeans.size() > 0){
                        //临时list
                        List<MoodBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mMoodBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = moodBeans.size() -1; i>= 0 ; i--){
                                temList.add(moodBeans.get(i));
                            }
                            temList.addAll(mMoodBeans);
                        }else{
                            temList.addAll(mMoodBeans);
                            temList.addAll(moodBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mMoodBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new PersonalMoodListViewAdapter(TopicActivity.this, mMoodBeans, null);
                            mRecyclerView.setAdapter(mAdapter);
                        }

                        mAdapter.addDatas(temList);
                        //Log.i(TAG, "后来的大小：" + mMoodBeans.size());

                        //Toast.makeText(mContext, "成功加载"+ moodBeans.size() + "条数据,总数是："+mMoodBeans.size(), Toast.LENGTH_SHORT).show();
                        int size = mMoodBeans.size();

                        mFirstId = mMoodBeans.get(0).getId();
                        mLastId = mMoodBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRecyclerViewFooter.setText(getStringResource(R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mMoodBeans.clear();
                            mAdapter.addDatas(new ArrayList<MoodBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mRecyclerViewFooter.setText(getStringResource(R.string.no_load_more));
                        }else {
                            ToastUtil.success(this, getStringResource(R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mMoodBeans.clear();
                            mAdapter.addDatas(new ArrayList<MoodBean>());
                        }
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(R.string.click_to_load));
                        mRecyclerViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(this, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }

    /**
     * 将列表移动到最顶部
     */
    public void smoothScrollToTop(){
        if(mMoodBeans != null && mMoodBeans.size() > 0 && mRecyclerView != null /*&& !isLoading*/){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    public void sendFirstLoading() {
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_TOPIC);
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        //HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("topic", topic);
        TopicHandler.paging(this, params);
    }

    /**
     * 显示下拉刷新
     */
    public void showRefresh() {
        mSwipeLayout.setRefreshing(true);
    }
    /**
     * 发送向上刷新的任务
     */
    protected void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        if(mFirstId == 0){
            sendFirstLoading();
            return;
        }
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_TOPIC);
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("topic", topic);
        TopicHandler.paging(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }
        //刷新之前先把以前的任务取消
        taskCanceled(TaskType.LOAD_TOPIC);
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("topic", topic);
        TopicHandler.paging(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        taskCanceled(TaskType.LOAD_TOPIC);
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        params.put("topic", topic);
        TopicHandler.paging(this, params);
    }

    @Override
    public void onItemClick(int position, Object data) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("hasImg", !StringUtil.isNull(mMoodBeans.get(position).getImgs()));
        CommonHandler.startDetailActivity(this, "t_mood", mMoodBeans.get(position).getId(), params);
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
