package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.fragment.BaseFragment;

/**
 * 记账昨日的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class YesterDayFragment extends BaseFragment {

    public YesterDayFragment(){
    }

    public static final YesterDayFragment newInstance(Bundle bundle){
        YesterDayFragment fragment = new YesterDayFragment();
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
