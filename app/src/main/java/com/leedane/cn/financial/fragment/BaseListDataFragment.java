package com.leedane.cn.financial.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/22.
 */
public abstract class BaseListDataFragment extends FinancialBaseFragment {

    private View mRootView;
    private FinancialList mFinancialList;
    private RecyclerView mRecyclerView;
    private FinancialRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View mFooterView;
    /**
     * 所在容器的ID
     * @return
     */
    protected abstract int getContainerId();


    /**
     * 获取bunde中的对象
     * @return
     */
    protected abstract String getFinancialListKey();

    /**
     * 获取RecyclerView的实体ID
     * @return
     */
    protected abstract int getRecyclerViewId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(getContainerId(), container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            int model = bundle.getInt("model");
            switch (model) {
                case 1://今日
                    mFinancialList = CalculateUtil.toDayList;
                case 2://昨日
                    mFinancialList = CalculateUtil.yesterDayList;
                    break;
                case 3://本周
                    mFinancialList = CalculateUtil.weekList;
                    break;
                case 4://本月
                    mFinancialList = CalculateUtil.monthList;
                    break;
                case 5://本年
                    mFinancialList = CalculateUtil.yearList;
                    break;
            }
            //mFinancialList = (FinancialList) bundle.getSerializable(getFinancialListKey());
        }
        if(mContext == null)
            mContext = getActivity();

        if(mFinancialList == null){
            ToastUtil.failure(mContext, "暂无数据");
            return;
        }

        this.mRecyclerView = (RecyclerView)mRootView.findViewById(getRecyclerViewId());
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mAdapter = new FinancialRecyclerViewAdapter(mContext, mFinancialList.getFinancialBeans());
        mRecyclerView.setAdapter(mAdapter);

        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);
        View v = mFooterView.findViewById(R.id.financial_footer);
        if(v != null && v instanceof TextView){
            TextView tv = (TextView)v;
            tv.setText(getStringResource(mContext, R.string.footer)+ "(一共"+ mFinancialList.getFinancialBeans().size()+"条记录)");
        }
        mAdapter.setFooterView(mFooterView);

        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                if(mFinancialList != null && mFinancialList.getFinancialBeans() != null && mFinancialList.getFinancialBeans().size() > 0){
                    Intent it = new Intent(mContext, IncomeOrSpendActivity.class);
                    it.putExtra("local_id", mFinancialList.getFinancialBeans().get(position).getLocalId());
                    mContext.startActivity(it);
                }
            }
        });
    }
}
