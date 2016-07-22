package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.fragment.BaseFragment;

/**
 * 记账本月的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class MonthFragment extends BaseFragment {

    public MonthFragment(){
    }

    public static final MonthFragment newInstance(Bundle bundle){
        MonthFragment fragment = new MonthFragment();
        fragment.setArguments(bundle);
        return fragment;
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
}
