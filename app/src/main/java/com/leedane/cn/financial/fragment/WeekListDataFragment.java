package com.leedane.cn.financial.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.util.ToastUtil;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public class WeekListDataFragment extends FinancialBaseFragment {

    private View mRootView;
    private FinancialList mFinancialList;
    private RecyclerView mRecyclerView;
    private FinancialRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public WeekListDataFragment(){
    }

    public static final WeekListDataFragment newInstance(Bundle bundle){
        WeekListDataFragment fragment = new WeekListDataFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_financial_week_list, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            mFinancialList = (FinancialList) bundle.getSerializable("financialWeekList");
        }
        if(mContext == null)
            mContext = getActivity();
        Log.i("weekListData", "展示本周的列表");

        if(mFinancialList == null){
            ToastUtil.failure(mContext, "暂无数据");
            return;
        }
        this.mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.financial_week_list);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mAdapter = new FinancialRecyclerViewAdapter(mContext, mFinancialList.getFinancialBeans());
        mRecyclerView.setAdapter(mAdapter);

        //mAdapter.addDatas();
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                if (mFinancialList != null && mFinancialList.getFinancialBeans() != null && mFinancialList.getFinancialBeans().size() > 0) {
                    Intent it = new Intent(mContext, IncomeOrSpendActivity.class);
                    it.putExtra("financialBean", mFinancialList.getFinancialBeans().get(position));
                    mContext.startActivity(it);
                }
            }
        });
    }
}
