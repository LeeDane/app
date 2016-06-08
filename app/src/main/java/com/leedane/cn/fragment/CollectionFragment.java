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

import com.leedane.cn.adapter.CollectionAdapter;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.bean.HttpResponseCollectionBean;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 收藏列表的fragment类
 * Created by LeeDane on 2016/4/6.
 */
public class CollectionFragment extends BaseFragment{

    public static final String TAG = "CollectionFragment";
    private Context mContext;
    private ListView mListView;
    private CollectionAdapter mAdapter;
    private List<CollectionBean> mCollectionBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;
    //private HashMap<String, Object> baseRequestParams;
    private int toUserId;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    private boolean isLoginUser;

    public CollectionFragment() {
    }

    public static final CollectionFragment newInstance(Bundle bundle){
        CollectionFragment fragment = new CollectionFragment();
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
            if((type == TaskType.LOAD_COLLECTION) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_COLLECTION){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseCollectionBean httpResponseCollectionBean = BeanConvertUtil.strConvertToCollectionBeans(String.valueOf(result));
                if(httpResponseCollectionBean != null && httpResponseCollectionBean.isSuccess()){
                    List<CollectionBean> CollectionBeans = httpResponseCollectionBean.getMessage();
                    if(CollectionBeans != null && CollectionBeans.size() > 0){
                        //临时list
                        List<CollectionBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mCollectionBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = CollectionBeans.size() -1; i>= 0 ; i--){
                                temList.add(CollectionBeans.get(i));
                            }
                            temList.addAll(mCollectionBeans);
                        }else{
                            temList.addAll(mCollectionBeans);
                            temList.addAll(CollectionBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mCollectionBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new CollectionAdapter(mContext, mCollectionBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mCollectionBeans.size());
                        //ToastUtil.success(mContext, "成功加载" + CollectionBeans.size() + "条数据,总数是：" + mCollectionBeans.size(), Toast.LENGTH_SHORT);
                        int size = mCollectionBeans.size();

                        mFirstId = mCollectionBeans.get(0).getId();
                        mLastId = mCollectionBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCollectionBeans.clear();
                            mAdapter.refreshData(new ArrayList<CollectionBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCollectionBeans.clear();
                            mAdapter.refreshData(new ArrayList<CollectionBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext);
                    }
                }
                return;
            }else if(type == TaskType.DELETE_COLLECTION){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, "删除收藏成功", Toast.LENGTH_SHORT);
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
     * 发送第一次刷新的任务
     */
    @Override
    protected void sendFirstLoading(){

        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_COLLECTION);
        CollectionHandler.getCollectionsRequest(this, params);
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
        taskCanceled(TaskType.LOAD_COLLECTION);
        CollectionHandler.getCollectionsRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
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
        params.put("toUserId", toUserId);
        taskCanceled(TaskType.LOAD_COLLECTION);
        CollectionHandler.getCollectionsRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(mContext, R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getStringResource(mContext, R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){

            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("toUserId", toUserId);
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));
            taskCanceled(TaskType.LOAD_COLLECTION);
            CollectionHandler.getCollectionsRequest(this, params);
        }

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
            this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
            mAdapter = new CollectionAdapter(mContext, mCollectionBeans);
            mListView.setOnScrollListener(new ListViewOnScrollListener());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CommonHandler.startDetailActivity(mContext, mCollectionBeans.get(position).getTableName(), mCollectionBeans.get(position).getTableId(), null);
                }
            });

            if(isLoginUser){
                //长按执行删除的操作
                mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                        builder.setCancelable(true);
                        builder.setIcon(R.drawable.menu_feedback);
                        builder.setTitle("提示");
                        builder.setMessage("删除该收藏记录?");
                        builder.setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        CollectionHandler.deleteCollection(CollectionFragment.this, mCollectionBeans.get(position).getId(), mCollectionBeans.get(position).getCreateUserId());
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
            }

            //listview下方的显示
            viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(CollectionFragment.this);//添加点击事件
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
