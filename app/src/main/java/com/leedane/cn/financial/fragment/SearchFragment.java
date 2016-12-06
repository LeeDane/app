package com.leedane.cn.financial.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.search.SearchUserAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.HttpResponseSearchUserBean;
import com.leedane.cn.bean.search.SearchUserBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.activity.IncomeOrSpendActivity;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialLocationAdapter;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.SearchHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索列表的fragment类
 * Created by LeeDane on 2016/12/4.
 */
public class SearchFragment extends BaseListDataFragment{

    public static final String TAG = "SearchFragment";
    public SearchFragment(){
    }

    public static final SearchFragment newInstance(Bundle bundle){
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getContainerId() {
        return R.layout.fragment_recyclerview;
    }

    @Override
    protected String getFinancialListKey() {
        return "financialSearchList";
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.id_recyclerview;
    }
}
