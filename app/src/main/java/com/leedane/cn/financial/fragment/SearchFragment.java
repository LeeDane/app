package com.leedane.cn.financial.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.CalculateUtil;
import com.leedane.cn.financial.util.EnumUtil;

/**
 * 记账本月的fragment
 * Created by LeeDane on 2016/7/19.
 */
public class SearchFragment extends Fragment {
    public SearchFragment(){
    }

    public static final SearchFragment newInstance(Bundle bundle){
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


}
