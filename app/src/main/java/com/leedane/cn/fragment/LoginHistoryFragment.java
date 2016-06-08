package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.LoginHistotyAdapter;
import com.leedane.cn.bean.HttpResponseLoginHistoryBean;
import com.leedane.cn.bean.LoginHistoryBean;
import com.leedane.cn.handler.LoginHistoryHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 登录历史记录列表的fragment类
 * Created by LeeDane on 2016/5/5.
 */
public class LoginHistoryFragment extends BaseFragment{

    public static final String TAG = "LoginHistoryFragment";
    private Context mContext;
    private ListView mListView;
    private LoginHistotyAdapter mAdapter;
    private List<LoginHistoryBean> mLoginHistoryBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    public LoginHistoryFragment() {
    }

    public static final LoginHistoryFragment newInstance(Bundle bundle){
        LoginHistoryFragment fragment = new LoginHistoryFragment();
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
            if((type == TaskType.LOAD_LOGIN_HISTORY) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_LOGIN_HISTORY){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseLoginHistoryBean httpResponseLoginHistoryBean = BeanConvertUtil.strConvertToLoginHistoryBeans(String.valueOf(result));
                if(httpResponseLoginHistoryBean != null && httpResponseLoginHistoryBean.isSuccess()){
                    List<LoginHistoryBean> loginHistoryBeans = httpResponseLoginHistoryBean.getMessage();
                    if(loginHistoryBeans != null && loginHistoryBeans.size() > 0){
                        //临时list
                        List<LoginHistoryBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mLoginHistoryBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = loginHistoryBeans.size() -1; i>= 0 ; i--){
                                temList.add(loginHistoryBeans.get(i));
                            }
                            temList.addAll(mLoginHistoryBeans);
                        }else{
                            temList.addAll(mLoginHistoryBeans);
                            temList.addAll(loginHistoryBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mLoginHistoryBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new LoginHistotyAdapter(mContext, mLoginHistoryBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mCollectionBeans.size());
                        //ToastUtil.success(mContext, "成功加载" + CollectionBeans.size() + "条数据,总数是：" + mCollectionBeans.size(), Toast.LENGTH_SHORT);
                        int size = mLoginHistoryBeans.size();

                        mFirstId = mLoginHistoryBeans.get(0).getId();
                        mLastId = mLoginHistoryBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mLoginHistoryBeans.clear();
                            mAdapter.refreshData(new ArrayList<LoginHistoryBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mLoginHistoryBeans.clear();
                            mAdapter.refreshData(new ArrayList<LoginHistoryBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext);
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
    @Override
    protected void sendFirstLoading(){

        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_LOGIN_HISTORY);
        LoginHistoryHandler.getLoginHistorysRequest(this, params);
    }
    /**
     * 发送向上刷新的任务
     */
    @Override
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
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_LOGIN_HISTORY);
        LoginHistoryHandler.getLoginHistorysRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
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
        taskCanceled(TaskType.LOAD_LOGIN_HISTORY);
        LoginHistoryHandler.getLoginHistorysRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(mContext, R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getStringResource(mContext, R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){

            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));
            taskCanceled(TaskType.LOAD_LOGIN_HISTORY);
            LoginHistoryHandler.getLoginHistorysRequest(this, params);
        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();
        sendFirstLoading();
        //initFirstData();
        this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
        mAdapter = new LoginHistotyAdapter(mContext, mLoginHistoryBeans);
        mListView.setOnScrollListener(new ListViewOnScrollListener());
        //listview下方的显示
        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(LoginHistoryFragment.this);//添加点击事件
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
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
