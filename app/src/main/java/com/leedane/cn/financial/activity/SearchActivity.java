package com.leedane.cn.financial.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.fragment.SearchFragment;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.fragment.search.SearchBlogFragment;
import com.leedane.cn.fragment.search.SearchHistoryFragment;
import com.leedane.cn.fragment.search.SearchMoodFragment;
import com.leedane.cn.fragment.search.SearchUserFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 搜索ctivity
 * Created by LeeDane on 2016/12/5.
 */
public class SearchActivity extends BaseActivity implements SearchHistoryFragment.SearchHistorItemClickListener{
    private static final String TAG = "SearchActivity";
    private EditText mSearchKey;
    private Button mSearchGo;
    private String selectType = EnumUtil.SearchType.记账.name();
    private FinancialDataBase financialDataBase;

    /**
     * 搜索handler
     */
    private Handler searchAction = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.FINANCIAL_SEARCH_ACTION:
                    Bundle bundle = msg.getData();
                    SearchFragment searchFragment = SearchFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchFragment).commit();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_search);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.search);
        backLayoutVisible();
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = new Bundle();
        bundle.putString("searchWhereSql", " where search_type = '" + selectType + "' order by datetime(create_time) desc");
        String[] searchTypes = new String[]{selectType};
        bundle.putStringArray("searchTypes", searchTypes);
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance(bundle);
        searchHistoryFragment.setSearchHistorItemClickListener(this);//注册监听器
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchHistoryFragment).commit();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        financialDataBase = new FinancialDataBase(SearchActivity.this);
        mSearchKey = (EditText)findViewById(R.id.search_key);
        mSearchGo = (Button)findViewById(R.id.do_search);
        mSearchGo.setOnClickListener(this);

        mSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    goSearch();
                }
                return false;
            }
        });
    }

    /**
     * 执行搜索操作
     */
    public void goSearch(){
        String key = mSearchKey.getText().toString();
        if(StringUtil.isNull(key)){
            ToastUtil.failure(SearchActivity.this, "请先输入搜索内容");
            mSearchKey.setSelected(true);
            return;
        }
        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
        SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
        searchHistoryBean.setCreateTime(DateUtil.DateToString(new Date()));
        searchHistoryBean.setSearchType(selectType);
        searchHistoryBean.setSearchKey(key);
        searchHistoryDataBase.insert(searchHistoryBean);
        searchHistoryDataBase.destroy();
        Bundle bundle = new Bundle();
        bundle.putSerializable("financialSearchList", getFinancialList(key));
        Message message = new Message();
        message.setData(bundle);
        message.what = FlagUtil.FINANCIAL_SEARCH_ACTION;
        //55毫秒秒后进行
        searchAction.sendMessageDelayed(message, 55);

    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        if(searchAction != null)
            searchAction.removeCallbacksAndMessages(null);
        if(financialDataBase != null)
            financialDataBase.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.do_search:
                goSearch();
                break;
        }
    }

    @Override
    public void itemClick(SearchHistoryBean searchHistoryBean) {
        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
        searchHistoryDataBase.insert(searchHistoryBean);
        searchHistoryDataBase.destroy();
        Bundle bundle = new Bundle();
        bundle.putSerializable("financialSearchList", getFinancialList(searchHistoryBean.getSearchKey()));
        Message message = new Message();
        message.setData(bundle);
        message.what = FlagUtil.FINANCIAL_SEARCH_ACTION;
        //55毫秒秒后进行
        searchAction.sendMessageDelayed(message, 55);
    }

    private FinancialList getFinancialList(String searchKey){
        FinancialList financialList = new FinancialList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where status=");
        buffer.append(ConstantsUtil.STATUS_NORMAL);
        buffer.append(" and (one_level like'%");
        buffer.append(searchKey);
        buffer.append("%' or  two_level like'%");
        buffer.append(searchKey);
        buffer.append("%' or location like'%");
        buffer.append(searchKey);
        buffer.append("%' or financial_desc like'%");
        buffer.append(searchKey);
        buffer.append("%') order by datetime(addition_time) desc");

        financialList.setFinancialBeans(financialDataBase.query(buffer.toString()));
        return financialList;
    }
}
