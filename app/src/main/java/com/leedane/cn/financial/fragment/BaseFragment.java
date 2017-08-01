package com.leedane.cn.financial.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;

/**
 * 记账基本的fragment
 * Created by LeeDane on 2016/7/19.
 */
@SuppressLint("ValidFragment")
public abstract class BaseFragment extends FinancialBaseFragment {

    protected View mRootView;
    protected ToggleButton chartOrListButton;
    protected FinancialList financialList = new FinancialList();

    /**
     * 大容器ID
     * @return
     */
    protected abstract int getContainerId();

    /**
     * fragment容器ID
     * @return
     */
    protected abstract int getFragmentContainerId();

    protected abstract String getFinancialListKey();

    /**
     * 列表展示的fragment
     * @param bundle
     * @return
     */
    protected abstract Fragment getListDataFragment(Bundle bundle);

    /**
     * 图标展示的fragment
     * @param bundle
     * @return
     */
    protected abstract Fragment getChartDataFragment(Bundle bundle);

    /**
     * 获取当前的模型类型（如：昨日、本周、本月、本年）
     * @return
     */
    protected abstract int getModel();
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
        if(mContext == null)
            mContext = getActivity();

        //Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        //getActivity().getSupportFragmentManager().beginTransaction().add(getFragmentContainerId(), getListDataFragment(bundle)).commit();

        chartOrListButton = (ToggleButton)mRootView.findViewById(R.id.chart_or_list_toggle_button);
        chartOrListButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chartOrListButton.setSelected(isChecked);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(getFragmentContainerId());
                if(fragment != null){
                    fragmentManager .beginTransaction().remove(fragment).commit();
                }
                Bundle bundle = new Bundle();
                bundle.putInt("model", getModel());
                if(isChecked){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
                }else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getChartDataFragment(bundle)).commit();
                }
            }
        });
        chartOrListButton.setSelected(true);
    }

    @Override
    public void calculate(int model) {
        super.calculate(model);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(getFragmentContainerId());
        if(fragment != null){
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
    }
}
