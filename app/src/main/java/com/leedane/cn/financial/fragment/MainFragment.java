package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.activity.IncomeTestActivity;
import com.leedane.cn.financial.adapter.FinancialListAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.HttpResponseFinancialBean;
import com.leedane.cn.fragment.BaseFragment;
import com.leedane.cn.handler.AttentionHandler;
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
 * 记账首页的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";
    private Context mContext;
    private ListView mListView;
    private FinancialListAdapter mAdapter;
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    private boolean isFirstLoading;

    /**
     * 头部view
     */
    private View mHeader;

    public MainFragment(){
    }

    public static MainFragment newInstance(Bundle bundle){
        MainFragment fragment = new MainFragment();
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
            if((type == TaskType.LOAD_ATTENTION) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_ATTENTION){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseFinancialBean httpResponseFinancialBean = BeanConvertUtil.strConvertToFinancialBeanBeans(String.valueOf(result));
                if(httpResponseFinancialBean != null && httpResponseFinancialBean.isSuccess()){
                    List<FinancialBean> financialBeans = httpResponseFinancialBean.getMessage();
                    if(financialBeans != null && financialBeans.size() > 0){
                        //临时list
                        List<FinancialBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mFinancialBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = financialBeans.size() -1; i>= 0 ; i--){
                                temList.add(financialBeans.get(i));
                            }
                            temList.addAll(mFinancialBeans);
                        }else{
                            temList.addAll(mFinancialBeans);
                            temList.addAll(financialBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mFinancialBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new FinancialListAdapter(mContext, mFinancialBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mAttentionBeans.size());
                        //ToastUtil.success(mContext, "成功加载" + attentionBeans.size() + "条数据,总数是：" + mAttentionBeans.size(), Toast.LENGTH_SHORT);
                        int size = mFinancialBeans.size();

                        mFirstId = mFinancialBeans.get(0).getId();
                        mLastId = mFinancialBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFinancialBeans.clear();
                            mAdapter.refreshData(new ArrayList<FinancialBean>());
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
                            mFinancialBeans.clear();
                            mAdapter.refreshData(new ArrayList<FinancialBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }else if(type == TaskType.DELETE_ATTENTION){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, "删除关注成功", Toast.LENGTH_SHORT);
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
        if(mFinancialBeans != null && mFinancialBeans.size() > 0 && mListView != null /*&& !isLoading*/){
            mListView.smoothScrollToPosition(0);
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
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_ATTENTION);
        AttentionHandler.getAttentionsRequest(this, params);
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
        taskCanceled(TaskType.LOAD_ATTENTION);
        AttentionHandler.getAttentionsRequest(this, params);
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
        taskCanceled(TaskType.LOAD_ATTENTION);
        AttentionHandler.getAttentionsRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mListViewFooter.getText().toString())){
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        taskCanceled(TaskType.LOAD_ATTENTION);
        AttentionHandler.getAttentionsRequest(this, params);
    }

    //记一笔
    private TextView mAddFinancial;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();

        mHeader = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_header, null);
        mAddFinancial = (TextView)mHeader.findViewById(R.id.add_financial);
        mAddFinancial.setOnClickListener(MainFragment.this);
        //ToastUtil.success(mContext, "评论");
        //isFirstLoading = false;
        sendFirstLoading();
        //initFirstData();
        this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
        mListView.addHeaderView(mHeader, null, false);
        mAdapter = new FinancialListAdapter(mContext, mFinancialBeans);
        mListView.setOnScrollListener(new ListViewOnScrollListener());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.success(mContext, "item click-->"+position);
            }
        });

        //长按执行删除的操作
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.menu_feedback);
                builder.setTitle("提示");
                builder.setMessage("删除该关注记录?");
                builder.setPositiveButton("删除",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //AttentionHandler.deleteAttention(MainFragment.this, mFinancialBeans.get(position).getId(), mFinancialBeans.get(position).getCreateUserId());
                                showLoadingDialog("Delete", "try best to delete...", true);
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                builder.show();
                return true;
            }
        });
        //listview下方的显示
        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(MainFragment.this);//添加点击事件
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

       mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
            case R.id.add_financial:
                Intent intent = new Intent(mContext, IncomeOrSpendActivity.class);
                startActivity(intent);
            break;
        }
    }
}
