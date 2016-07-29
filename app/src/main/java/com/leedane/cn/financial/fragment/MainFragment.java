package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.fragment.BaseFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账首页的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MainFragment extends BaseFragment {

    public static final String TAG = "MainFragment";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private FinancialRecyclerViewAdapter mAdapter;
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    private boolean isFirstLoading;

    private LinearLayoutManager mLayoutManager;

    private FinancialDataBase financialDataBase;

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
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_ATTENTION) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.DELETE_ATTENTION){
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
        if(mFinancialBeans != null && mFinancialBeans.size() > 0 && mRecyclerView != null /*&& !isLoading*/){
            mRecyclerView.smoothScrollToPosition(0);
        }
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

        financialDataBase = new FinancialDataBase(mContext);

        mHeader = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_header, null);

        //必须，在把headview添加到recycleview中时告诉recycleview期望的布局方式，
        // 也就是将一个认可的layoutParams传递进去，不然显示的不是全部宽度
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(layoutParams);

        mAddFinancial = (TextView)mHeader.findViewById(R.id.add_financial);
        mAddFinancial.setOnClickListener(MainFragment.this);


        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FinancialRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addDatas(generateData());
        setHeader(mRecyclerView);
        mAdapter.setOnItemClickListener(new FinancialRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String data) {
                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

    }

    @Override
    protected void sendFirstLoading() {

    }

    @Override
    protected void sendUpLoading() {

    }

    @Override
    protected void sendLowLoading() {

    }

    @Override
    protected void sendLoadAgain(View view) {

    }

    private void setHeader(RecyclerView view) {
        //View header = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_header, view, false);
        mAdapter.setHeaderView(mHeader);
    }

    /**
     * 获取初始数据
     * @return
     */
    private List<FinancialBean> generateData(){
        List<FinancialBean> financialBeans = new ArrayList<>();
        financialBeans = financialDataBase.query();
        return financialBeans;
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

    @Override
    public void onDestroy() {
        financialDataBase.destroy();
        super.onDestroy();
    }
}
