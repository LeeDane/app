package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.fragment.search.SearchBlogFragment;
import com.leedane.cn.fragment.search.SearchHistoryFragment;
import com.leedane.cn.fragment.search.SearchMoodFragment;
import com.leedane.cn.fragment.search.SearchUserFragment;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.MoodHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 搜索ctivity
 * Created by LeeDane on 2016/5/22.
 */
public class SearchActivity extends BaseActivity implements SearchHistoryFragment.SearchHistorItemClickListener{
    private static final String TAG = "SearchActivity";

    private TextView mSearchType;
    private EditText mSearchKey;
    private Button mSearchGo;
    private String[] searchTypes = new String[]{EnumUtil.SearchType.用户名.name(), EnumUtil.SearchType.博客.name(), EnumUtil.SearchType.心情.name()};
    private String selectType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.search);
        backLayoutVisible();

        if(BaseApplication.getLoginUserId() < 1){
            searchTypes = new String[]{EnumUtil.SearchType.博客.name()};
        }else{
            searchTypes = new String[]{EnumUtil.SearchType.用户名.name(), EnumUtil.SearchType.博客.name(), EnumUtil.SearchType.心情.name()};
        }
        selectType = searchTypes[0];
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = new Bundle();
        bundle.putString("searchWhereSql", " where search_type in (" + getSqlTypeSql() + ") order by datetime(create_time) desc");
        bundle.putStringArray("searchTypes", searchTypes);
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance(bundle);
        searchHistoryFragment.setSearchHistorItemClickListener(this);//注册监听器
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchHistoryFragment).commit();
    }

    private String getSqlTypeSql(){
        StringBuffer buffer = new StringBuffer();
        if(searchTypes.length > 0){
            for(int i = 0; i < searchTypes.length; i++){
                if(i == searchTypes.length -1)
                    buffer.append("'"+ searchTypes[i] +"'");
                else
                    buffer.append("'"+ searchTypes[i] +"',");
            }
        }
        return buffer.toString();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mSearchType = (TextView)findViewById(R.id.search_type);
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
        mSearchType.setText(selectType);
        mSearchType.setOnClickListener(this);

        String key = getIntent().getStringExtra("key");
        if(StringUtil.isNotNull(key)){
            mSearchKey.setText(key);
            SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
            SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
            searchHistoryBean.setCreateTime(DateUtil.DateToString(new Date()));
            searchHistoryBean.setSearchType(selectType);
            searchHistoryBean.setSearchKey(key);
            searchHistoryDataBase.insert(searchHistoryBean);
            searchHistoryDataBase.destroy();
            Bundle bundle = new Bundle();
            bundle.putString("searchKey", key);
            if(selectType.equalsIgnoreCase(EnumUtil.SearchType.用户名.name())){//搜索用户
                SearchUserFragment searchUserFragment = SearchUserFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchUserFragment).commit();
            }else if(selectType.equalsIgnoreCase(EnumUtil.SearchType.博客.name())){//搜索博客
                SearchBlogFragment searchBlogFragment = SearchBlogFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchBlogFragment).commit();
            }else if(selectType.equalsIgnoreCase(EnumUtil.SearchType.心情.name())){//搜索心情
                SearchMoodFragment searchMoodFragment = SearchMoodFragment.newInstance(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchMoodFragment).commit();
            }
        }
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
        bundle.putString("searchKey", key);
        if(selectType.equalsIgnoreCase(EnumUtil.SearchType.用户名.name())){//搜索用户
            SearchUserFragment searchUserFragment = SearchUserFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchUserFragment).commit();
        }else if(selectType.equalsIgnoreCase(EnumUtil.SearchType.博客.name())){//搜索博客
            SearchBlogFragment searchBlogFragment = SearchBlogFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchBlogFragment).commit();
        }else if(selectType.equalsIgnoreCase(EnumUtil.SearchType.心情.name())){//搜索心情
            SearchMoodFragment searchMoodFragment = SearchMoodFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchMoodFragment).commit();
        }
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        if(mDialog != null)
            mDialog = null;
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.do_search:
                goSearch();
                break;
            case R.id.search_type:
                showListItemMenuDialog();
                break;
        }
    }

    @Override
    public void itemClick(SearchHistoryBean searchHistoryBean) {

        if(BaseApplication.getLoginUserId() < 1 && (EnumUtil.SearchType.用户名.name().equalsIgnoreCase(searchHistoryBean.getSearchType()) || EnumUtil.SearchType.心情.name().equalsIgnoreCase(searchHistoryBean.getSearchType()))){
            ToastUtil.failure(SearchActivity.this, "请先登录才能搜索该类型");
            return;
        }

        selectType = searchHistoryBean.getSearchType();
        if(BaseApplication.getLoginUserId() > 0){
            mSearchType.setText(selectType);
        }
        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
        searchHistoryDataBase.insert(searchHistoryBean);
        searchHistoryDataBase.destroy();
        mSearchKey.setText(searchHistoryBean.getSearchKey());
        Bundle bundle = new Bundle();
        bundle.putString("searchKey", searchHistoryBean.getSearchKey());
        if(EnumUtil.SearchType.用户名.name().equals(selectType)){//搜索用户
            SearchUserFragment searchUserFragment = SearchUserFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchUserFragment).commit();
        }else if(EnumUtil.SearchType.博客.name().equals(selectType)){//搜索博客
            SearchBlogFragment searchBlogFragment = SearchBlogFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchBlogFragment).commit();
        }else if(EnumUtil.SearchType.心情.name().equals(selectType)){//搜索心情
            SearchMoodFragment searchMoodFragment = SearchMoodFragment.newInstance(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchMoodFragment).commit();
        }
    }

    private Dialog mDialog;

    /**
     * 显示弹出自定义菜单view
     */
    public void showListItemMenuDialog(){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();
        mDialog = new Dialog(SearchActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        SimpleListAdapter adapter = new SimpleListAdapter(SearchActivity.this, Arrays.asList(searchTypes));
        listView.setAdapter(adapter);

        //, EnumUtil.SearchType.博客.name(), EnumUtil.SearchType.心情.name()
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                mSearchType.setText(textView.getText().toString());
                selectType = textView.getText().toString();
                dismissListItemMenuDialog();
            }
        });
        mDialog.setTitle("请选择");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissListItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissListItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
}
