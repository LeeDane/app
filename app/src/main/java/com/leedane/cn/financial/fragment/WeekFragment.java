package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.fragment.BaseFragment;

/**
 * 记账本周的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class WeekFragment extends BaseFragment {

    public WeekFragment(){
    }

    public static final WeekFragment newInstance(Bundle bundle){
        WeekFragment fragment = new WeekFragment();
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
