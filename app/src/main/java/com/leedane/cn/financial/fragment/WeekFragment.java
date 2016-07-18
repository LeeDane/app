package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.EnumUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.Date;

/**
 * 记账本周的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class WeekFragment extends FinancialBaseFragment {

    private View mRootView;

    private Button mBtnList;
    private Button mBtnChart;
    private FinancialList financialList = new FinancialList();
    public WeekFragment(){
    }

    public static final WeekFragment newInstance(Bundle bundle){
        WeekFragment fragment = new WeekFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_financial_week_list_or_chart, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();
        Log.i("WeekFragment", "展示本周的");

        //generateData();
        ListDataFragment listDataFragment = ListDataFragment.newInstance(new Bundle());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.week_container, listDataFragment).commit();

        mBtnList = (Button)mRootView.findViewById(R.id.btn_list);
        mBtnChart = (Button)mRootView.findViewById(R.id.btn_chart);
        mBtnList.setOnClickListener(WeekFragment.this);
        mBtnChart.setOnClickListener(WeekFragment.this);
    }

    /**
     * 获取初始数据
     * @return
     */
    private void generateData(){
        String startTime = DateUtil.DateToString(DateUtil.getThisWeekStart());
        String endTime = DateUtil.DateToString(new Date());
        financialList.setFinancialBeans(financialDataBase.query(" where datetime(addition_time) between datetime('"+startTime+"') and datetime('"+ endTime+"')"));
        financialList.setModel(EnumUtil.FinancialModel.本周.value);
       /* if(financialList.getFinancialBeans() != null && financialList.getFinancialBeans().size() > 0)
            ToastUtil.success(mContext, "本周获取到的数据是" + financialList.getFinancialBeans().size() + "条,startTime="+startTime+",endTime="+endTime);
        else
            ToastUtil.success(mContext, "本周获取不到数据");*/
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_list: //展示列表
                Bundle bundle = new Bundle();
                bundle.putSerializable("financialWeekList", financialList);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.week_container, ListDataFragment.newInstance(bundle)).commit();
                break;
            case R.id.btn_chart: //展示图表
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("financialWeekList", financialList);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.week_container, WeekChartDataFragment.newInstance(bundle1)).commit();
                break;
        }
    }

    @Override
    public void calculate(FinancialList financialList, int model) {
        super.calculate(financialList, model);
        if(model == EnumUtil.FinancialModel.本周.value){
            this.financialList = financialList;
            ToastUtil.success(mContext, "本周数量：" + financialList.getFinancialBeans().size());
        }
    }
}
