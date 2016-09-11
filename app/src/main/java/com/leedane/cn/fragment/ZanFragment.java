package com.leedane.cn.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.ZanAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseZanBean;
import com.leedane.cn.bean.ZanBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.PraiseHandler;
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
 * 点赞列表的fragment类
 * Created by LeeDane on 2016/4/5.
 */
public class ZanFragment extends BaseRecyclerViewFragment implements BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener{

    public static final String TAG = "ZanFragment";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ZanAdapter mAdapter;
    private List<ZanBean> mZanBeans = new ArrayList<>();
    private View mRootView;
    private int toUserId;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    private boolean isLoginUser;
    public ZanFragment(){
    }

    public static final ZanFragment newInstance(Bundle bundle){
        ZanFragment fragment = new ZanFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_ZAN) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_ZAN){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseZanBean httpResponseZanBean = BeanConvertUtil.strConvertToZanBeans(String.valueOf(result));
                if(httpResponseZanBean != null && httpResponseZanBean.isSuccess()){
                    List<ZanBean> zanBeans = httpResponseZanBean.getMessage();
                    if(zanBeans != null && zanBeans.size() > 0){
                        //临时list
                        List<ZanBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mZanBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = zanBeans.size() -1; i>= 0 ; i--){
                                temList.add(zanBeans.get(i));
                            }
                            temList.addAll(mZanBeans);
                        }else{
                            temList.addAll(mZanBeans);
                            temList.addAll(zanBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mZanBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new ZanAdapter(mContext, mZanBeans);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        //Log.i(TAG, "后来的大小：" + mZanBeans.size());
                        //ToastUtil.success(mContext, "成功加载" + zanBeans.size() + "条数据,总数是：" + mZanBeans.size(), Toast.LENGTH_SHORT);
                        int size = mZanBeans.size();

                        mFirstId = mZanBeans.get(0).getId();
                        mLastId = mZanBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mZanBeans.clear();
                            mAdapter.addDatas(new ArrayList<ZanBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mZanBeans.clear();
                            mAdapter.addDatas(new ArrayList<ZanBean>());
                        }
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mRecyclerViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }else if(type == TaskType.DELETE_ZAN){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, "删除赞成功", Toast.LENGTH_SHORT);
                    sendFirstLoading();
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将列表移动到最顶部
     */
    public void smoothScrollToTop(){
        if(mZanBeans != null && mZanBeans.size() > 0 && mRecyclerView != null /*&& !isLoading*/){
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * 发送第一次刷新的任务
     */
    @Override
    public void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_ZAN);
        PraiseHandler.getZansRequest(this, params);
    }
    /**
     * 发送向上刷新的任务
     */
    @Override
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
        params.put("toUserId", toUserId);
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.LOAD_ZAN);
        PraiseHandler.getZansRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        taskCanceled(TaskType.LOAD_ZAN);
        PraiseHandler.getZansRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad() : MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        taskCanceled(TaskType.LOAD_ZAN);
        PraiseHandler.getZansRequest(this, params);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.toUserId = bundle.getInt("toUserId");
            this.isLoginUser = bundle.getBoolean("isLoginUser");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            //ToastUtil.success(mContext, "评论");
            //isFirstLoading = false;
            sendFirstLoading();
            //initFirstData();
            this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
            mAdapter = new ZanAdapter(mContext, mZanBeans);
            mAdapter.setOnItemClickListener(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
            mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

            if(isLoginUser){
                //长按执行删除的操作
                mAdapter.setOnItemLongClickListener(this);
            }
            //listview下方的显示
            mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);
            mAdapter.setFooterView(mFooterView);
            mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.financial_footer);
            mRecyclerViewFooter.setOnClickListener(ZanFragment.this);//添加点击事件
            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));

            mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);

        }
        mRecyclerView.setAdapter(mAdapter);
       // mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }

    @Override
    public void onItemClick(int position, Object data) {
        CommonHandler.startDetailActivity(mContext, mZanBeans.get(position).getTableName(), mZanBeans.get(position).getTableId(), null);
    }

    @Override
    public void onItemLongClick(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.menu_feedback);
        builder.setTitle("提示");
        builder.setMessage("删除该点赞记录?");
        builder.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PraiseHandler.deleteZan(ZanFragment.this, mZanBeans.get(position).getId(), mZanBeans.get(position).getCreateUserId());
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
}
