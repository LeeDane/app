package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.fragment.BaseFragment;

/**
 * 记账本年的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class YearFragment extends BaseFragment {

    public YearFragment(){
    }

    public static final YearFragment newInstance(Bundle bundle){
        YearFragment fragment = new YearFragment();
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
