package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.ScoreAdapter;
import com.leedane.cn.adapter.ZanAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseScoreBean;
import com.leedane.cn.bean.ScoreBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.handler.ScoreHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 积分历史记录列表的fragment类
 * Created by LeeDane on 2016/5/5.
 */
public class ScoreFragment extends BaseRecyclerViewFragment{

    public static final String TAG = "ScoreFragment";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ScoreAdapter mAdapter;
    private List<ScoreBean> mScoreBeans = new ArrayList<>();
    private View mRootView;

    public ScoreFragment() {
    }

    public static final ScoreFragment newInstance(Bundle bundle){
        ScoreFragment fragment = new ScoreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_SCORE) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_SCORE){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseScoreBean httpResponseScoreBean = BeanConvertUtil.strConvertToScoreBeans(String.valueOf(result));
                if(httpResponseScoreBean != null && httpResponseScoreBean.isSuccess()){
                    List<ScoreBean> scoreBeans = httpResponseScoreBean.getMessage();
                    if(scoreBeans != null && scoreBeans.size() > 0){
                        //临时list
                        List<ScoreBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mScoreBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = scoreBeans.size() -1; i>= 0 ; i--){
                                temList.add(scoreBeans.get(i));
                            }
                            temList.addAll(mScoreBeans);
                        }else{
                            temList.addAll(mScoreBeans);
                            temList.addAll(scoreBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mScoreBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new ScoreAdapter(mContext, mScoreBeans);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        //Log.i(TAG, "后来的大小：" + mCollectionBeans.size());
                        //ToastUtil.success(mContext, "成功加载" + CollectionBeans.size() + "条数据,总数是：" + mCollectionBeans.size(), Toast.LENGTH_SHORT);
                        int size = mScoreBeans.size();

                        mFirstId = mScoreBeans.get(0).getId();
                        mLastId = mScoreBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mScoreBeans.clear();
                            mAdapter.addDatas(new ArrayList<ScoreBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mScoreBeans.clear();
                            mAdapter.addDatas(new ArrayList<ScoreBean>());
                        }
                        mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mRecyclerViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }else if(type == TaskType.DELETE_COLLECTION){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(mContext, "删除收藏成功", Toast.LENGTH_SHORT);
                    sendFirstLoading();
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将列表移动到最顶部
     */
    public void smoothScrollToTop(){
        if(mScoreBeans != null && mScoreBeans.size() > 0 && mRecyclerView != null /*&& !isLoading*/){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * 发送第一次刷新的任务
     */
    @Override
    public void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.first_load);
        params.put("method", mPreLoadMethod);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_SCORE);
        ScoreHandler.getScoresRequest(this, params);
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
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_SCORE);
        ScoreHandler.getScoresRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        taskCanceled(TaskType.LOAD_SCORE);
        ScoreHandler.getScoresRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.first_load: MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        taskCanceled(TaskType.LOAD_SCORE);
        ScoreHandler.getScoresRequest(this, params);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();
        sendFirstLoading();

        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mAdapter = new ScoreAdapter(mContext, mScoreBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

        //listview下方的显示
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.financial_footer);
        mRecyclerViewFooter.setOnClickListener(ScoreFragment.this);//添加点击事件
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRecyclerView.setAdapter(mAdapter);
       // mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }
}
