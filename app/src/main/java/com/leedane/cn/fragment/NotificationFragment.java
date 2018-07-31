package com.leedane.cn.fragment;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.NotificationHandler;
import com.leedane.cn.handler.NotificationPagingHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 通知消息列表的fragment类
 * Created by LeeDane on 2016/4/27.
 */
public class NotificationFragment extends BaseRecyclerViewLazyFragment implements BaseRecyclerViewAdapter.OnItemLongClickListener, BaseRecyclerViewAdapter.OnItemClickListener{
    public static final String TAG = "NotificationFragment";
    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;
    private List<NotificationBean> mNotifications = new ArrayList<>();
    private View mRootView;
    private NotificationPagingHandler mNotificationPagingHandler;

    private String mType;
    public NotificationFragment(){
    }

    public static final NotificationFragment newInstance(Bundle bundle){
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initPrepare() {
        Bundle bundle = getArguments();
        mType = bundle.getString("type");
        mNotificationPagingHandler = new NotificationPagingHandler(this, mType, MySettingConfigUtil.first_load);
        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mAdapter = new NotificationAdapter(mContext, mNotifications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, 40, Color.parseColor("#F3F3F3")));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

        //listview下方的显示
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.footer_text);
        mRecyclerViewFooter.setOnClickListener(this);//添加点击事件
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRecyclerView.setAdapter(mAdapter);

        //长按事件
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);}

    @Override
    protected void onInvisible() {

    }

    @Override
    protected void initData() {
        sendFirstLoading();
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        mIsLoading = false;
        super.taskFinished(type, result);
        if(result instanceof Error){
            if(type == TaskType.LOAD_NOTIFICATION){
                mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                mRecyclerViewFooter.setOnClickListener(this);
                return;
            }
        }
        try{
            if(type == TaskType.LOAD_NOTIFICATION){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseNotificationBean httpResponseNotificationBean = BeanConvertUtil.strConvertToNotificationBeans(String.valueOf(result));
                if(httpResponseNotificationBean != null && httpResponseNotificationBean.isSuccess()){
                    List<NotificationBean> notificationBeans =  httpResponseNotificationBean.getMessage();
                    if(notificationBeans != null && notificationBeans.size() > 0){
                        //临时list
                        List<NotificationBean> temList = new ArrayList<>();
                        if(mCurrent == 0){ //第一页
                            mNotifications.clear();
                        }
                        Log.i(TAG, "原来的大小：" + mNotifications.size());
                        //将新的数据和以前的数据进行叠加
                        temList.addAll(mNotifications);
                        temList.addAll(notificationBeans);
                        Log.i(TAG, "原来的大小：" + mNotifications.size());

                        if(mAdapter == null) {
                            mAdapter = new NotificationAdapter(mContext, mNotifications);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{
                        if(!mDropDown){ //下拉，说明没有数据，就清空列表
                            mNotifications.clear();
                            mAdapter.addDatas(new ArrayList<NotificationBean>());
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else{
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mDropDown){//非下拉
                        mNotifications.clear();
                        mAdapter.addDatas(new ArrayList<NotificationBean>());
                    }
                    //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                    mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                    mRecyclerViewFooter.setOnClickListener(this);
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
        mCurrent = 0;
        mIsLoading = true;
        mDropDown = false;
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        mNotificationPagingHandler.getNotificationsRequest(mCurrent);
    }

    /**
     * 发送向上刷新的任务
     */
    protected void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        mCurrent = 0;
        mIsLoading = true;
        mDropDown = false;
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        mNotificationPagingHandler.getNotificationsRequest(mCurrent);
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString()) || mIsLoading) {
            return;
        }
        mCurrent ++;
        mIsLoading = true;
        mDropDown = true;
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        mNotificationPagingHandler.getNotificationsRequest(mCurrent);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        mIsLoading = true;
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_NOTIFICATION);
        mNotificationPagingHandler.getNotificationsRequest(mCurrent);
    }

    @Override
    public void onItemLongClick(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.menu_feedback);
        builder.setTitle("提示");
        builder.setMessage("删除该通知记录?");
        builder.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NotificationHandler.delete(NotificationFragment.this, mNotifications.get(position).getId());
                        showLoadingDialog("Delete", "try best to delete...");
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.show();
    }

    @Override
    public void onItemClick(int position, Object data) {
        if(StringUtil.isNotNull(mNotifications.get(position).getTableName()) && mNotifications.get(position).getTableId() > 0)
            CommonHandler.startDetailActivity(mContext, mNotifications.get(position).getTableName(), mNotifications.get(position).getTableId(), null);
    }
}
