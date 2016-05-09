package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.FriendAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.FriendBean;
import com.leedane.cn.bean.HttpResponseFriendBean;
import com.leedane.cn.handler.FriendHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 朋友相关列表的fragment类
 * Created by LeeDane on 2016/4/13.
 */
public class FriendFragment extends BaseFragment{

    public static final String TAG = "FriendFragment";
    private boolean itemSingleClick; //控制每一项是否可以出发单击事件
    private Context mContext;
    private ListView mListView;
    private FriendAdapter mAdapter;
    private List<FriendBean> mFriendBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    //是否是第一次加载
    private boolean isFirstLoading = true;
    private int type; //0:获取已经跟我成为好友关系的分页列表 1:获取我发送的好友请求列表  2:获取等待我同意的好友关系列表
    private int toUserId;

    public FriendFragment(){
    }

    public static final FriendFragment newInstance(Bundle bundle){
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_listview, container,
                    false);
        setHasOptionsMenu(true);

        sendFirstLoading();
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_FRIENDS_PAGING || type == TaskType.LOAD_REQUEST_PAGING || type == TaskType.LOAD_RESPONSE_PAGING && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getResources().getString(R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_FRIENDS_PAGING || type == TaskType.LOAD_REQUEST_PAGING || type == TaskType.LOAD_RESPONSE_PAGING){
                mSwipeLayout.setRefreshing(false);
                HttpResponseFriendBean responseFriendBean = BeanConvertUtil.strConvertToFriendBeans(String.valueOf(result));
                if(responseFriendBean != null && responseFriendBean.isSuccess()){
                    List<FriendBean> friendBeans =  responseFriendBean.getMessage();
                    if(friendBeans != null && friendBeans.size() > 0){
                        //临时list
                        List<FriendBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mFriendBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = friendBeans.size() -1; i>= 0 ; i--){
                                temList.add(friendBeans.get(i));
                            }
                            temList.addAll(mFriendBeans);
                        }else{
                            temList.addAll(mFriendBeans);
                            temList.addAll(friendBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mFriendBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new FriendAdapter(mContext, mFriendBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);

                        int size = mFriendBeans.size();

                        mFirstId = mFriendBeans.get(0).getId();
                        mLastId = mFriendBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getResources().getString(R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFriendBeans.clear();
                            mAdapter.refreshData(new ArrayList<FriendBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getResources().getString(R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getResources().getString(R.string.no_load_more), Toast.LENGTH_LONG);
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFriendBeans.clear();
                            mAdapter.refreshData(new ArrayList<FriendBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null ,false);
                        mListViewFooter.setText(getResources().getString(R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }
                }
            }else{
                ToastUtil.failure(mContext, "数据加载失败", Toast.LENGTH_SHORT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    protected void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", 10);
        params.put("method", mPreLoadMethod);
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        taskCanceled(TaskType.LOAD_REQUEST_PAGING);
        taskCanceled(TaskType.LOAD_RESPONSE_PAGING);
        if(type == 0) {
            FriendHandler.sendFriendsPaging(FriendFragment.this, params);
        }else if(type == 1) {
            FriendHandler.sendRequestPaging(FriendFragment.this, params);
        }else if(type ==2){
            FriendHandler.sendResponsePaging(FriendFragment.this, params);
        }
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

        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", 5);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        taskCanceled(TaskType.LOAD_REQUEST_PAGING);
        taskCanceled(TaskType.LOAD_RESPONSE_PAGING);
        if(type == 0) {
            FriendHandler.sendFriendsPaging(FriendFragment.this, params);
        }else if(type == 1) {
            FriendHandler.sendRequestPaging(FriendFragment.this, params);
        }else if(type ==2){
            FriendHandler.sendResponsePaging(FriendFragment.this, params);
        }
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getResources().getString(R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString()) || isLoading) {
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
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        taskCanceled(TaskType.LOAD_REQUEST_PAGING);
        taskCanceled(TaskType.LOAD_RESPONSE_PAGING);
        if(type == 0) {
            FriendHandler.sendFriendsPaging(FriendFragment.this, params);
        }else if(type == 1) {
            FriendHandler.sendRequestPaging(FriendFragment.this, params);
        }else if(type ==2){
            FriendHandler.sendResponsePaging(FriendFragment.this, params);
        }
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getResources().getString(R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getResources().getString(R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){
            ToastUtil.success(mContext, "请求重新加载", Toast.LENGTH_SHORT);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? 10 : 5);
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("toUserId", toUserId);
            mListViewFooter.setText(getResources().getString(R.string.loading));
            taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
            taskCanceled(TaskType.LOAD_REQUEST_PAGING);
            taskCanceled(TaskType.LOAD_RESPONSE_PAGING);
            if(type == 0) {
                FriendHandler.sendFriendsPaging(FriendFragment.this, params);
            }else if(type == 1) {
                FriendHandler.sendRequestPaging(FriendFragment.this, params);
            }else if(type ==2){
                FriendHandler.sendResponsePaging(FriendFragment.this, params);
            }
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.itemSingleClick = true;
            this.type = bundle.getInt("type");
            this.toUserId = BaseApplication.getLoginUserId();
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
            mAdapter = new FriendAdapter( mContext, mFriendBeans);
            mListView.setOnScrollListener(new ListViewOnScrollListener());
            if(itemSingleClick){
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ToastUtil.success(mContext, "点击的位置是：" + position + ",内容：" + mFriendBeans.get(position).getId());
                        //CommonHandler.startDetailActivity(mContext,mFriendBeans.get(position).getTableName(), mFriendBeans.get(position).getTableId(), null);
                    }
                });
            }

            //listview下方的显示
            viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(FriendFragment.this);//添加点击事件
            mListViewFooter.setText(getResources().getString(R.string.loading));

            mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);

        }
        mListView.setAdapter(mAdapter);
       // mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
