package com.leedane.cn.financial.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.BaseRecyclerViewActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialLocationAdapter;
import com.leedane.cn.financial.bean.FinancialLocationBean;
import com.leedane.cn.financial.bean.HttpResponseLocationBean;
import com.leedane.cn.financial.database.FinancialLocationDataBase;
import com.leedane.cn.financial.handler.FinancialLocationHandler;
import com.leedane.cn.handler.BlogHandler;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 记账位置activity
 */
public class LocationActivity extends BaseRecyclerViewActivity implements SwipeRefreshLayout.OnRefreshListener,
        BaseRecyclerViewAdapter.OnItemClickListener , BaseRecyclerViewAdapter.OnItemLongClickListener{
    public static final String TAG = "LocationActivity";

    private RecyclerView mRecyclerView;
    private FinancialLocationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<FinancialLocationBean> mDatas = new ArrayList<>();;
    private FinancialLocationDataBase financialLocationDataBase;

    /**
     * 添加位置信息imageview
     */
    private ImageView mRightImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(LocationActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.LocationActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_location);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.financial));
        backLayoutVisible();
        initView();
    }

    /**
     * 初始化试图控件
     */
    private void initView() {
        financialLocationDataBase = new FinancialLocationDataBase(LocationActivity.this);
        //显示标题栏的添加位置的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setImageResource(R.mipmap.add);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        mAdapter = new FinancialLocationAdapter(this, mDatas);

        mRecyclerView = (RecyclerView)findViewById(R.id.financial_location_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

        mFooterView = LayoutInflater.from(LocationActivity.this).inflate(R.layout.fragment_financial_main_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.financial_footer);
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        mRecyclerViewFooter.setOnClickListener(this);//添加点击事件

        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        sendFirstLoading();
        /*if(mDatas.size() > 0){
            mFirstId = mDatas.get(0).getId();
            mLastId = mDatas.get(mDatas.size() - 1).getId();
            mAdapter.addDatas(mDatas);
        }else{
            sendFirstLoading();
        }*/
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_FINANCIAL_LOCATION) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mRecyclerViewFooter.setText(getStringResource(R.string.no_load_more));
            }
            if(type == TaskType.LOAD_FINANCIAL_LOCATION && mPreLoadMethod.equalsIgnoreCase("firstloading")){
                mDatas = financialLocationDataBase.query(" order by id desc ");
                if(mDatas != null && mDatas.size() > 0 ){
                    mFirstId = mDatas.get(0).getId();
                    mLastId = mDatas.get(mDatas.size() - 1).getId();
                    mAdapter.addDatas(mDatas);
                }
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_FINANCIAL_LOCATION){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseLocationBean httpResponseLocationBean = BeanConvertUtil.strConvertToLocationBeans(String.valueOf(result));
                if(httpResponseLocationBean != null && httpResponseLocationBean.isSuccess()){
                    List<FinancialLocationBean> locationBeans = httpResponseLocationBean.getMessage();
                    if(locationBeans != null && locationBeans.size() > 0){
                        //临时list
                        List<FinancialLocationBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            //mRecyclerView.removeAllViewsInLayout();
                            mDatas.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = locationBeans.size() -1; i>= 0 ; i--){
                                temList.add(locationBeans.get(i));
                            }
                            temList.addAll(mDatas);
                        }else{
                            temList.addAll(mDatas);
                            temList.addAll(locationBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mDatas.size());
                        if(mAdapter == null) {
                            mAdapter = new FinancialLocationAdapter(LocationActivity.this, mDatas);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        //mDatas = mAdapter.getmDatas();
                        int size = mDatas.size();

                        mFirstId = mDatas.get(0).getId();
                        mLastId = mDatas.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(FinancialLocationBean fl: mDatas){
                                    financialLocationDataBase.insert(fl);
                                }
                            }
                        }).start();

                        mRecyclerViewFooter.setText(getStringResource(R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mDatas.clear();
                            mAdapter.addDatas(new ArrayList<FinancialLocationBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            //mAdapter.setFooterView(mFooterView);
                            mRecyclerViewFooter.setText(getStringResource(R.string.no_load_more));
                        }else {
                            ToastUtil.success(LocationActivity.this, getStringResource(R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mDatas.clear();
                            mAdapter.addDatas(new ArrayList<FinancialLocationBean>());
                        }
                        //mAdapter.setFooterView(mFooterView);
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(R.string.click_to_load));
                        mRecyclerViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(LocationActivity.this, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }else if(type == TaskType.DELETE_FINANCIAL_LOCATION || type == TaskType.ADD_FINANCIAL_LOCATION || type == TaskType.UPDATE_FINANCIAL_LOCATION){ //删除位置信息
                dismissLoadingDialog();
                dismissOperateLocationDialog();
                dismissListItemMenuDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(LocationActivity.this, jsonObject.getString("message"));
                    sendFirstLoading();
                }else{
                    ToastUtil.failure(LocationActivity.this, JsonUtil.getErrorMessage(result));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    public void sendFirstLoading(){
        //清空所有的数据
        mDatas.clear();
        mAdapter.notifyDataSetChanged();
        //另外启动一个线程去删除
        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求将清空所有的缓存数据
                financialLocationDataBase.deleteAll();
            }
        }).start();

        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.first_load);
        params.put("method", mPreLoadMethod);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_FINANCIAL_LOCATION);
        isLoading = true;
        FinancialLocationHandler.paging(this, params);
    }
    /**
     * 发送向上刷新的任务
     */
    protected void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        if(mFirstId == 0){
            sendFirstLoading();
            return;
        }
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_FINANCIAL_LOCATION);
        FinancialLocationHandler.paging(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //ToastUtil.success(getBaseContext(), "mLastId"+mLastId +", ");
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        taskCanceled(TaskType.LOAD_FINANCIAL_LOCATION);
        FinancialLocationHandler.paging(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.first_load: MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mRecyclerViewFooter.setText(getStringResource(R.string.loading));
        taskCanceled(TaskType.LOAD_FINANCIAL_LOCATION);
        FinancialLocationHandler.paging(this, params);
    }

    @Override
    public void onRefresh() {
        sendUpLoading();
    }

    private Dialog textDialog;
    /**
     * 显示弹出自定义文本view
     */
    public void showOperateLocationDialog(final boolean edit, final FinancialLocationBean bean){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissOperateLocationDialog();

        textDialog = new Dialog(LocationActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(LocationActivity.this).inflate(R.layout.add_financial_location_dialog, null);

        final EditText location = (EditText)view.findViewById(R.id.financial_location_dialog_location);
        TextView submitBtn = (TextView)view.findViewById(R.id.financial_location_dialog_submit);
        final TextView desc = (TextView)view.findViewById(R.id.financial_location_dialog_desc);
        ImageView addPosition = (ImageView)view.findViewById(R.id.financial_location_dialog_position);
        final RadioButton normalButton = (RadioButton)view.findViewById(R.id.financial_location_dialog_normal);
        if(edit && bean != null){
            location.setText(bean.getLocation());
            desc.setText(StringUtil.changeNotNull(bean.getLocationDesc()));
            RadioButton disableButton = (RadioButton)view.findViewById(R.id.financial_location_dialog_disable);
            if(bean.getStatus() == ConstantsUtil.STATUS_NORMAL){
                normalButton.setChecked(true);
                disableButton.setChecked(false);
            }else{
                normalButton.setChecked(false);
                disableButton.setChecked(true);
            }
        }
        //获取定位列表
        addPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = location.getText().toString();
                if (StringUtil.isNull(content)) {
                    location.setFocusable(true);
                    ToastUtil.failure(LocationActivity.this, "请输入位置信息吧");
                    return;
                }
                HashMap<String, Object> params = new HashMap<>();
                params.put("location", content);
                params.put("locationDesc", desc.getText().toString());
                params.put("status", normalButton.isChecked() ? ConstantsUtil.STATUS_NORMAL : ConstantsUtil.STATUS_DISABLE);
                if (edit) {
                    params.put("flid", bean.getId());
                    FinancialLocationHandler.update(LocationActivity.this, params);
                    showLoadingDialog("Edit", "try best to loading...");
                } else {
                    FinancialLocationHandler.add(LocationActivity.this, params);
                    showLoadingDialog("Add", "try best to loading...");
                }
            }
        });

        textDialog.setTitle("操作");
        textDialog.setCancelable(true);
        textDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissOperateLocationDialog();
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        textDialog.setContentView(view, layoutParams);
        textDialog.show();
    }

    private void dismissOperateLocationDialog(){
        if(textDialog != null && textDialog.isShowing()){
            textDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_img:
                showOperateLocationDialog(false, null);
                break;
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }

    @Override
    public void onItemClick(int position, Object data) {
        showOperateLocationDialog(true, (FinancialLocationBean)data);
    }

    @Override
    public void onItemLongClick(int position) {
        showListItemMenuDialog(position);
    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     * @param index
     */
    public void showListItemMenuDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();

        mDialog = new Dialog(LocationActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(LocationActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();
        menus.add(getStringResource(R.string.delete));//删除
        SimpleListAdapter adapter = new SimpleListAdapter(LocationActivity.this, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //删除
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.delete))){
                    final int flid = mDatas.get(index).getId();
                    AppUtil.vibrate(LocationActivity.this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(LocationActivity.this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除位置：" + mDatas.get(index).getLocation() + "吗？这是不可逆行为，成功删除掉将不能恢复！");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        FinancialLocationHandler.delete(LocationActivity.this, flid);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(LocationActivity.this, "删除位置：" + mDatas.get(index).getLocation() +  "失败。");
                                    }

                                }
                            });
                    builder.setNegativeButton("放弃",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                    builder.show();
                }

                dismissListItemMenuDialog();
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissListItemMenuDialog();
            }
        });
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

    @Override
    protected void onDestroy() {
        financialLocationDataBase.destroy();
        super.onDestroy();
    }
}
