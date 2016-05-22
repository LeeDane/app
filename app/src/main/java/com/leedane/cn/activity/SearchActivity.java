package com.leedane.cn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.fragment.search.SearchHistoryFragment;
import com.leedane.cn.fragment.search.SearchBlogFragment;
import com.leedane.cn.fragment.search.SearchMoodFragment;
import com.leedane.cn.fragment.search.SearchUserFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.Date;

/**
 * 搜索ctivity
 * Created by LeeDane on 2016/5/22.
 */
public class SearchActivity extends BaseActivity implements SearchHistoryFragment.SearchHistorItemClickListener{
    private static final String TAG = "SearchActivity";


    private Spinner mSearchType;
    private EditText mSearchKey;
    private Button mSearchGo;
    private String[] types;
    private String selectType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.search);
        backLayoutVisible();

        if(BaseApplication.getLoginUserId() < 1){
            types = new String[]{"博客"};
        }else{
            types = new String[]{"用户名", "博客", "心情"};
        }
        selectType = types[0];
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance(new Bundle());
        searchHistoryFragment.setSearchHistorItemClickListener(this);//注册监听器
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchHistoryFragment).commit();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mSearchType = (Spinner)findViewById(R.id.search_type);
        mSearchKey = (EditText)findViewById(R.id.search_key);
        mSearchGo = (Button)findViewById(R.id.do_search);
        mSearchGo.setOnClickListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        mSearchType.setAdapter(arrayAdapter);
        mSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectType = types[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectType = types[0];
            }
        });
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.do_search:

                String key = mSearchKey.getText().toString();
                if(StringUtil.isNull(key)){
                    ToastUtil.failure(SearchActivity.this, "请先输入搜索内容");
                    mSearchKey.setSelected(true);
                    return;
                }
                ToastUtil.failure(SearchActivity.this, "类型："+selectType);
                SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
                SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
                searchHistoryBean.setCreateTime(DateUtil.DateToString(new Date()));
                searchHistoryBean.setSearchType(selectType);
                searchHistoryBean.setSearchKey(key);
                searchHistoryDataBase.insert(searchHistoryBean);
                searchHistoryDataBase.destroy();
                Bundle bundle = new Bundle();
                bundle.putString("searchKey", key);
                if(selectType.equalsIgnoreCase(types[0])){//搜索用户
                    SearchUserFragment searchUserFragment = SearchUserFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchUserFragment).commit();
                }else if(selectType.equalsIgnoreCase(types[1])){//搜索博客
                    SearchBlogFragment searchBlogFragment = SearchBlogFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchBlogFragment).commit();
                }else if(selectType.equalsIgnoreCase(types[2])){//搜索心情
                    SearchMoodFragment searchMoodFragment = SearchMoodFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchMoodFragment).commit();
                }
                break;
        }
    }

    @Override
    public void itemClick(SearchHistoryBean searchHistoryBean) {

        if(BaseApplication.getLoginUserId() < 1 && (searchHistoryBean.getSearchType().equalsIgnoreCase("用户") || searchHistoryBean.getSearchType().equalsIgnoreCase("心情"))){
            ToastUtil.failure(SearchActivity.this, "请先登录才能搜索该类型");
            return;
        }

        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
        searchHistoryDataBase.insert(searchHistoryBean);
        searchHistoryDataBase.destroy();
        mSearchKey.setText(searchHistoryBean.getSearchKey());
        mSearchType.setPrompt(searchHistoryBean.getSearchType());
        Bundle bundle = new Bundle();
        bundle.putString("searchKey", searchHistoryBean.getSearchKey());
        if(searchHistoryBean.getSearchType().equalsIgnoreCase(types[0])){//搜索用户
            SearchUserFragment searchUserFragment = SearchUserFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchUserFragment).commit();
        }else if(searchHistoryBean.getSearchType().equalsIgnoreCase(types[1])){//搜索博客
            SearchBlogFragment searchBlogFragment = SearchBlogFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchBlogFragment).commit();
        }else if(searchHistoryBean.getSearchType().equalsIgnoreCase(types[2])){//搜索心情
            SearchMoodFragment searchMoodFragment = SearchMoodFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchMoodFragment).commit();
        }
    }
}
