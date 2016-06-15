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

import com.leedane.cn.adapter.FanAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.FanBean;
import com.leedane.cn.bean.HttpResponseFanBean;
import com.leedane.cn.handler.FanHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 粉丝列表的fragment类
 * Created by LeeDane on 2016/4/13.
 */
public class FanFragment extends BaseFragment{

    public static final String TAG = "FanFragment";
    private boolean itemSingleClick; //控制每一项是否可以出发单击事件
    private Context mContext;
    private ListView mListView;
    private FanAdapter mAdapter;
    private List<FanBean> mFanBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    //是否是第一次加载
    private boolean isFirstLoading = true;
    private int fanOrAttention;
    private boolean isLoginUser;
    private int toUserId;

    public FanFragment(){
    }

    public static final FanFragment newInstance(Bundle bundle){
        FanFragment fragment = new FanFragment();
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
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_MY_FAN && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_MY_FAN || type == TaskType.LOAD_MY_ATTENTION){
                mSwipeLayout.setRefreshing(false);
                HttpResponseFanBean responseFanBean = BeanConvertUtil.strConvertToFanBeans(String.valueOf(result));
                if(responseFanBean != null && responseFanBean.isSuccess()){
                    List<FanBean> fanBeans =  responseFanBean.getMessage();
                    if(fanBeans != null && fanBeans.size() > 0){
                        //临时list
                        List<FanBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mFanBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = fanBeans.size() -1; i>= 0 ; i--){
                                temList.add(fanBeans.get(i));
                            }
                            temList.addAll(mFanBeans);
                        }else{
                            temList.addAll(mFanBeans);
                            temList.addAll(fanBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mFanBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new FanAdapter(mContext, mFanBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);

                        int size = mFanBeans.size();

                        mFirstId = mFanBeans.get(0).getId();
                        mLastId = mFanBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFanBeans.clear();
                            mAdapter.refreshData(new ArrayList<FanBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more), Toast.LENGTH_LONG);
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFanBeans.clear();
                            mAdapter.refreshData(new ArrayList<FanBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null ,false);
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
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
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        taskCanceled(TaskType.LOAD_MY_ATTENTION);
        taskCanceled(TaskType.LOAD_MY_FAN);
        if(fanOrAttention == 0) {
            if(isLoginUser){
                FanHandler.getMyFansRequest(FanFragment.this, params);
            }else{
                FanHandler.getToFansRequest(FanFragment.this, params);
            }
        }else {
            if(isLoginUser){
                FanHandler.getMyAttentionsRequest(FanFragment.this, params);
            }else{
                FanHandler.getToAttentionsRequest(FanFragment.this, params);
            }
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
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        //获取当前是评论还是转发
        taskCanceled(TaskType.LOAD_MY_ATTENTION);
        taskCanceled(TaskType.LOAD_MY_FAN);
        if(fanOrAttention == 0) {
            if(isLoginUser){
                FanHandler.getMyFansRequest(FanFragment.this, params);
            }else{
                FanHandler.getToFansRequest(FanFragment.this, params);
            }
        }else {
            if(isLoginUser){
                FanHandler.getMyAttentionsRequest(FanFragment.this, params);
            }else{
                FanHandler.getToAttentionsRequest(FanFragment.this, params);
            }
        }
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        taskCanceled(TaskType.LOAD_MY_ATTENTION);
        taskCanceled(TaskType.LOAD_MY_FAN);
        if(fanOrAttention == 0) {
            if(isLoginUser){
                FanHandler.getMyFansRequest(FanFragment.this, params);
            }else{
                FanHandler.getToFansRequest(FanFragment.this, params);
            }
        }else {
            if(isLoginUser){
                FanHandler.getMyAttentionsRequest(FanFragment.this, params);
            }else{
                FanHandler.getToAttentionsRequest(FanFragment.this, params);
            }
        }
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(mContext, R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getStringResource(mContext, R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){
            ToastUtil.success(mContext, "请求重新加载", Toast.LENGTH_SHORT);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad() : MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("toUserId", toUserId);
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));
            taskCanceled(TaskType.LOAD_MY_ATTENTION);
            taskCanceled(TaskType.LOAD_MY_FAN);
            if(fanOrAttention == 0) {
                if(isLoginUser){
                    FanHandler.getMyFansRequest(FanFragment.this, params);
                }else{
                    FanHandler.getToFansRequest(FanFragment.this, params);
                }
            }else {
                if(isLoginUser){
                    FanHandler.getMyAttentionsRequest(FanFragment.this, params);
                }else{
                    FanHandler.getToAttentionsRequest(FanFragment.this, params);
                }
            }

        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            this.toUserId = bundle.getInt("toUserId");
            this.itemSingleClick = true;
            this.fanOrAttention = bundle.getInt("fanOrAttention");
            this.isLoginUser = bundle.getBoolean("isLoginUser");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            sendFirstLoading();
            //ToastUtil.success(mContext, "评论");
            //isFirstLoading = false;
            //initFirstData();
            this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
            mAdapter = new FanAdapter( getContext(), mFanBeans);
            mListView.setOnScrollListener(new ListViewOnScrollListener());
            if(itemSingleClick){
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ToastUtil.success(getContext(), "点击的位置是：" + position + ",内容：" + mFanBeans.get(position).getAccount());
                        //CommonHandler.startDetailActivity(mContext,mFanBeans.get(position).getTableName(), mFanBeans.get(position).getTableId(), null);
                    }
                });
            }

            //listview下方的显示
            viewFooter = LayoutInflater.from(getContext()).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(FanFragment.this);//添加点击事件
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));

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
        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
        }
    }
}
