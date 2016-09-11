package com.leedane.cn.financial.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.GalleryBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.activity.CloudActivity;
import com.leedane.cn.financial.activity.HomeActivity;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.activity.OneLevelOperationActivity;
import com.leedane.cn.financial.activity.SettingActivity;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 记账首页的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MainFragment extends FinancialBaseFragment{

    public static final int SYNCHRONIZED_CLOUD_CODE = 25;
    private static final int INIT_DATA_SUCCESS = 19;
    public static final String TAG = "MainFragment";
    private RecyclerView mRecyclerView;
    private FinancialRecyclerViewAdapter mAdapter;
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    private boolean isFirstLoading;

    private LinearLayoutManager mLayoutManager;

    //private TextView mClould; //同步云


    private FinancialList mMonthFinancialList; //本月的数据列表


    /**
     * 头部view
     */
    private View mHeaderView;
    private View mFooterView;

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
        if(!CommonUtil.isEmpty(mFinancialBeans) && mRecyclerView != null /*&& !isLoading*/){
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

        mHeaderView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_header, null);
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);

        mAddFinancial = (TextView)mHeaderView.findViewById(R.id.add_financial);
        mAddFinancial.setOnClickListener(this);

        //mClould = (TextView)mHeader.findViewById(R.id.financial_refresh);
        //mClould.setOnClickListener(this);

        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mAdapter = new FinancialRecyclerViewAdapter(mFinancialBeans);
        mRecyclerView.setAdapter(mAdapter);
        generateData();
        setHeader();
        setFooter();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                Intent it = new Intent(mContext, IncomeOrSpendActivity.class);
                it.putExtra("financialBean", mFinancialBeans.get(position));
                getActivity().startActivityForResult(it, HomeActivity.IS_EDIT_OR_SAVE_FINANCIAL_CODE);
                /*Intent it = new Intent(mContext, IncomeOrSpendActivity.class);
                it.putExtra("financialBean", mFinancialBeans.get(position));
                mContext.startActivity(it);
                Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();*/
            }
        });

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        //refreshHeaderData();
    }

    /**
     * 初始化数据的handler
     */
    Handler firstHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_DATA_SUCCESS:
                    //更新数据
                    mAdapter.addDatas(mFinancialBeans);
                    break;
            }

        }
    };

    /**
     * 刷新headerview的数据
     */
    private void refreshHeaderData(){
        /*if(mHeader == null || mMonthFinancialList == null)
            return;*/

        BigDecimal incomeTotal = FinancialHandler.getTotalData(mMonthFinancialList, IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME);
        BigDecimal spendTotal = FinancialHandler.getTotalData(mMonthFinancialList, IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        //更新
        TextView income = (TextView)mHeaderView.findViewById(R.id.financial_header_income);
        TextView spend = (TextView)mHeaderView.findViewById(R.id.financial_header_spend);
        TextView budget = (TextView)mHeaderView.findViewById(R.id.financial_header_budget);
        income.setText("￥" + incomeTotal.floatValue());
        spend.setText("￥" + spendTotal.floatValue());

        //公式：支出总预算+收入-支出
        BigDecimal surplus = BaseApplication.getTotalBudget().add(incomeTotal).subtract(spendTotal);
        budget.setText( Html.fromHtml(surplus.floatValue() > 0.0f ? "￥" + surplus.floatValue() : "￥" +"<font color='red'>"+String.valueOf(surplus.floatValue())+"</font>"));

        mHeaderView.findViewById(R.id.financial_header_income_linearlayout).setOnClickListener(this);
        mHeaderView.findViewById(R.id.financial_header_spend_linearlayout).setOnClickListener(this);
        mHeaderView.findViewById(R.id.financial_header_budget_linearlayout).setOnClickListener(this);
        mHeaderView.findViewById(R.id.financial_header_edit_one_level).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, OneLevelOperationActivity.class);
                startActivity(it);
            }
        });
        mHeaderView.findViewById(R.id.financial_header_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, SettingActivity.class);
                startActivity(it);
            }
        });
        mHeaderView.findViewById(R.id.financial_header_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, CloudActivity.class);
                startActivityForResult(it, SYNCHRONIZED_CLOUD_CODE);
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

    /**
     * 设置头部view
     */
    private void setHeader() {
        mAdapter.setHeaderView(mHeaderView);
    }

    /**
     * 设置底部view
     */
    private void setFooter() {
        mAdapter.setFooterView(mFooterView);
    }

    /**
     * 获取初始数据
     * @return
     */
    public void generateData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //后台计算记账数据
                FinancialHandler.calculateFinancialData(mContext);
                mFinancialBeans = financialDataBase.query(" where status = "+ ConstantsUtil.STATUS_NORMAL+" order by datetime(addition_time) desc limit 15");
                Message message = new Message();
                message.what = INIT_DATA_SUCCESS;
                //5秒后进行
                firstHandler.sendMessageDelayed(message, 55);

            }
        }).start();

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
                getActivity().startActivityForResult(intent, HomeActivity.IS_EDIT_OR_SAVE_FINANCIAL_CODE);
                break;
            /*case R.id.financial_refresh:
                Intent it = new Intent(mContext, CloudActivity.class);
                startActivityForResult(it, SYNCHRONIZED_CLOUD_CODE);
                break;*/
            case R.id.financial_header_income_linearlayout://本月收入的点击
                ((HomeActivity)getActivity()).tabClick(3);
                break;

            case R.id.financial_header_spend_linearlayout://本月支出的点击
                ((HomeActivity)getActivity()).tabClick(3);
                break;

            case R.id.financial_header_budget_linearlayout://预算结余的点击
                Intent itOneLevel = new Intent(mContext, OneLevelOperationActivity.class);
                startActivity(itOneLevel);
                break;
        }
    }

    @Override
    public void onRefresh() {
        generateData();
    }

    @Override
    public void calculate(FinancialList financialList, int model) {
        super.calculate(financialList, model);
        if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
            mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

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
