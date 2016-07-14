package com.leedane.cn.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.NotificationAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseNotificationBean;
import com.leedane.cn.bean.NotificationBean;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.NotificationHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 通知消息列表的fragment类
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationFragment extends BaseFragment{

    public static final String TAG = "NotificationFragment";
    private Context mContext;
    private ListView mListView;
    private NotificationAdapter mAdapter;
    private List<NotificationBean> mNotificationBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    /**
     * 通知消息的类型
     */
    private String mType;

    public NotificationFragment(){
    }

    public static final NotificationFragment newInstance(Bundle bundle){
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_listview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_NOTIFICATION && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_NOTIFICATION){
                mSwipeLayout.setRefreshing(false);
                HttpResponseNotificationBean responseNotificationBean = BeanConvertUtil.strConvertToNotificationBeans(String.valueOf(result));
                if(responseNotificationBean != null && responseNotificationBean.isSuccess()){
                    List<NotificationBean> NotificationBeans =  responseNotificationBean.getMessage();
                    if(NotificationBeans != null && NotificationBeans.size() > 0){
                        //临时list
                        List<NotificationBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mNotificationBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = NotificationBeans.size() -1; i>= 0 ; i--){
                                temList.add(NotificationBeans.get(i));
                            }
                            temList.addAll(mNotificationBeans);
                        }else{
                            temList.addAll(mNotificationBeans);
                            temList.addAll(NotificationBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mNotificationBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new NotificationAdapter(mContext, mNotificationBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);

                        int size = mNotificationBeans.size();

                        mFirstId = mNotificationBeans.get(0).getId();
                        mLastId = mNotificationBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mNotificationBeans.clear();
                            mAdapter.refreshData(new ArrayList<NotificationBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more), Toast.LENGTH_LONG);
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mNotificationBeans.clear();
                            mAdapter.refreshData(new ArrayList<NotificationBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
            }else if(type == TaskType.DELETE_NOTIFICATION){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, jsonObject);
                    sendFirstLoading();
                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    protected void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("type", mType);
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        NotificationHandler.getNotificationsRequest(NotificationFragment.this, params);
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
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("type", mType);
        //获取当前是评论还是转发
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        NotificationHandler.getNotificationsRequest(NotificationFragment.this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("type", mType);
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        NotificationHandler.getNotificationsRequest(NotificationFragment.this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mListViewFooter.getText().toString())){
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad() : MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("type", mType);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        NotificationHandler.getNotificationsRequest(NotificationFragment.this, params);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            mType = bundle.getString("type");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            sendFirstLoading();
            this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
            mAdapter = new NotificationAdapter(mContext, mNotificationBeans);
            mListView.setOnScrollListener(new ListViewOnScrollListener());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //ToastUtil.success(mContext, "点击的位置是：" + position + ",内容：" + mNotificationBeans.get(position).getToUserAccount());
                    CommonHandler.startDetailActivity(mContext, mNotificationBeans.get(position).getTableName(), mNotificationBeans.get(position).getTableId(), null);
                }
            });

            //长按执行删除的操作
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("提示");
                    builder.setMessage("删除该通知记录?");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    NotificationHandler.delete(NotificationFragment.this, mNotificationBeans.get(position).getId());
                                    showLoadingDialog("Delete", "try best to delete...");
                                }
                            });
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                    builder.show();
                    return true;
                }
            });

            //listview下方的显示
            viewFooter = LayoutInflater.from(getContext()).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(NotificationFragment.this);//添加点击事件
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));

            mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);

        }
        mListView.setAdapter(mAdapter);
       // mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
        }
    }
}
