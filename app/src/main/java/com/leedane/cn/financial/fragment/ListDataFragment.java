package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.view.View;

import com.leedane.cn.fragment.BaseFragment;

/**
 * 列表数据的fragment
 * Created by LeeDane on 2016/8/7.
 */
public class ListDataFragment extends BaseFragment {

    public ListDataFragment(){
    }

    public static final ListDataFragment newInstance(Bundle bundle){
        ListDataFragment fragment = new ListDataFragment();
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
