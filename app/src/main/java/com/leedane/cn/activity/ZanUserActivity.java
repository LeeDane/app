package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.ZanUserAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseZanUserBean;
import com.leedane.cn.bean.ZanUserBean;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赞用户列表activity
 * Created by LeeDane on 2016/5/21.
 */
public class ZanUserActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "ZanUserActivity";
    private int tableId;
    private String tableName;

    private List<ZanUserBean> mZanUsers = new ArrayList<>();
    private ZanUserAdapter mAdapter;

    private ListView mListView;

    private SwipeRefreshLayout mSwipeLayout;

    //当前listview中最旧一篇文章的id
    private int mFirstId;

    //当前listview中最新一篇文章的id
    private int mLastId;

    //当前的加载方式
    private String mPreLoadMethod;

    /**
     * ListView底部控制
     */
    private TextView mListViewFooter;
    private View viewFooter;

    private boolean isLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(ZanUserActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.ZanUserActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_zan_user);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.zan_user);
        backLayoutVisible();
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Intent it = getIntent();
        tableId = it.getIntExtra("table_id", 0);
        tableName = it.getStringExtra("table_name");
        if(tableId < 1 || StringUtil.isNull(tableName)){
            ToastUtil.failure(ZanUserActivity.this, "点赞用户参数不正确");
            finish();
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(type == TaskType.LOAD_ZAN_USER){
            isLoading = false;
            if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
        }
        if(result instanceof Error){
            Toast.makeText(ZanUserActivity.this, ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            if(type == TaskType.LOAD_ZAN_USER){
                HttpResponseZanUserBean httpResponseZanUserBean = BeanConvertUtil.strConvertToZanUserBeans(String.valueOf(result));
                if(httpResponseZanUserBean != null && httpResponseZanUserBean.isSuccess()){
                    List<ZanUserBean> zanUserBeans = httpResponseZanUserBean.getMessage();
                    if(zanUserBeans != null && zanUserBeans.size() > 0){
                        //临时list
                        List<ZanUserBean> temList = new ArrayList<>();

                        temList.addAll(zanUserBeans);
                        if(mAdapter == null) {
                            mAdapter = new ZanUserAdapter(ZanUserActivity.this, zanUserBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        mListViewFooter.setText(getResources().getString(R.string.load_finish));
                    }else {
                        mListViewFooter.setText(getResources().getString(R.string.no_load_more));
                    }
                }else{
                    mListViewFooter.setText(getResources().getString(R.string.load_more_error));
                }
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initView(){
        mListView = (ListView)findViewById(R.id.listview_items);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mAdapter = new ZanUserAdapter(ZanUserActivity.this, mZanUsers);
        mListView.setAdapter(mAdapter);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        sendFirstLoading();
    }

    /**
     * 进来第一次发送请求
     */
    private void sendFirstLoading() {
        Map<String, Object> params = new HashMap<>();
        params.put("table_id", tableId);
        params.put("table_name", tableName);
        isLoading = true;
        PraiseHandler.getZanUsersRequest(ZanUserActivity.this, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        if(isLoading){
            return;
        }
        sendFirstLoading();
    }
}
