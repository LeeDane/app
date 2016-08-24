package com.leedane.cn.financial.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.activity.OneLevelEditActivity;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账首页的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MainFragment extends FinancialBaseFragment{

    public static final String TAG = "MainFragment";
    private RecyclerView mRecyclerView;
    private FinancialRecyclerViewAdapter mAdapter;
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    private boolean isFirstLoading;

    private LinearLayoutManager mLayoutManager;



    private FinancialList mMonthFinancialList; //本月的数据列表


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
        Log.i(TAG, "onCreateView");
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
        Log.i(TAG, "onActivityCreated");
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();

        //后台计算记账数据
        FinancialHandler.calculateFinancialData(mContext);

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
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mAdapter = new FinancialRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        generateData();
        mAdapter.addDatas(mFinancialBeans);
        setHeader(mRecyclerView);
        mAdapter.setOnItemClickListener(new FinancialRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String data) {
                Intent it = new Intent(mContext, IncomeOrSpendActivity.class);
                it.putExtra("financialBean", mFinancialBeans.get(position));
                mContext.startActivity(it);
                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        refreshHeaderData();
    }

    /**
     * 刷新headerview的数据
     */
    private void refreshHeaderData(){
        /*if(mHeader == null || mMonthFinancialList == null)
            return;*/
        //更新
        TextView income = (TextView)mHeader.findViewById(R.id.financial_header_income);
        TextView spend = (TextView)mHeader.findViewById(R.id.financial_header_spend);
        income.setText(String.valueOf(FinancialHandler.getTotalData(mMonthFinancialList, IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME)));
        spend.setText(String.valueOf(FinancialHandler.getTotalData(mMonthFinancialList, IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND)));
        ImageView editOneLevel = (ImageView)mHeader.findViewById(R.id.financial_header_edit_one_level);
        editOneLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, OneLevelEditActivity.class);
                startActivity(it);
            }
        });
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
    private void generateData(){
        mFinancialBeans = financialDataBase.query(" where status = 1 order by datetime(addition_time) desc");
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
    public void calculate(FinancialList financialList, int model) {
        super.calculate(financialList, model);
        if(model == EnumUtil.FinancialModel.本月.value){
            this.mMonthFinancialList = CalculateUtil.monthList;
            refreshHeaderData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
