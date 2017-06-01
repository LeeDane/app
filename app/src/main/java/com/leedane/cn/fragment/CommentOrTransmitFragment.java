package com.leedane.cn.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.CommentOrTransmitAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.bean.HttpResponseCommentOrTransmitBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.SerializableMap;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 评论列表的fragment类
 * Created by LeeDane on 2015/11/14.
 */
public class CommentOrTransmitFragment extends BaseRecyclerViewFragment implements BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener{

    public static final String TAG = "CommentOrTransmitFragment";
    private boolean isComment;
    private boolean itemSingleClick; //控制每一项是否可以出发单击事件
    private Context mContext;
    private RecyclerView mRecyclerView;
    private CommentOrTransmitAdapter mAdapter;
    private List<CommentOrTransmitBean> mCommentOrTransmitBeans = new ArrayList<>();

    private View mRootView;
    private SerializableMap baseRequestParams = new SerializableMap();

    //是否是第一次加载
    private boolean isFirstLoading = true;
    private boolean isLoginUser;


    public CommentOrTransmitFragment(){
    }
    /**
     * 构建Frament对象(Item默认可以点击)
     * @param index 当前frament是第几个
     * @param context
     * @param params
     * @param isComment
     */
    /*public CommentOrTransmitFragment(int index, Context context, SerializableMap params, boolean isComment){
        this.mContext = context;
        this.baseRequestParams = params;
        this.isComment = isComment;
        this.itemSingleClick = true;
    }*/

    /**
     * 构建Frament对象
     * @param index 当前frament是第几个
     * @param context
     * @param params
     * @param isComment
     * @param itemSingleClick 每一项是否可以出发单击
     */
    /*public CommentOrTransmitFragment(int index, Context context, SerializableMap params, boolean isComment, boolean itemSingleClick){
        this.mContext = context;
        baseRequestParams = params;
        this.isComment = isComment;
        this.itemSingleClick = itemSingleClick;
    }*/

    /**
     * 构建Frament对象
     * @param bundle
     * @return
     */
    public static final CommentOrTransmitFragment newInstance(Bundle bundle){
        CommentOrTransmitFragment fragment = new CommentOrTransmitFragment();
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
            if((type == TaskType.LOAD_COMMENT || type == TaskType.LOAD_TRANSMIT) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mRecyclerViewFooter.setText(getStringResource(mContext, (R.string.no_load_more)));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_COMMENT || type == TaskType.LOAD_TRANSMIT){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseCommentOrTransmitBean httpResponseCommentOrTransmitBean = BeanConvertUtil.strConvertToCommentOrTransmitBeans(String.valueOf(result));
                if(httpResponseCommentOrTransmitBean != null && httpResponseCommentOrTransmitBean.isSuccess()){
                    List<CommentOrTransmitBean> commentOrTransmitBeans = httpResponseCommentOrTransmitBean.getMessage();
                    if(commentOrTransmitBeans != null && commentOrTransmitBeans.size() > 0){
                        //临时list
                        List<CommentOrTransmitBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmitBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = commentOrTransmitBeans.size() -1; i>= 0 ; i--){
                                temList.add(commentOrTransmitBeans.get(i));
                            }
                            temList.addAll(mCommentOrTransmitBeans);
                        }else{
                            temList.addAll(mCommentOrTransmitBeans);
                            temList.addAll(commentOrTransmitBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mCommentOrTransmitBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new CommentOrTransmitAdapter(mContext, mCommentOrTransmitBeans);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        //Log.i(TAG, "后来的大小：" + mCommentOrTransmitBeans.size());

                        //Toast.makeText(mContext, "成功加载"+ commentOrTransmitBeans.size() + "条数据,总数是："+mCommentOrTransmitBeans.size(), Toast.LENGTH_SHORT).show();
                        int size = mCommentOrTransmitBeans.size();

                        mFirstId = mCommentOrTransmitBeans.get(0).getId();
                        mLastId = mCommentOrTransmitBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmitBeans.clear();
                            mAdapter.addDatas(new ArrayList<CommentOrTransmitBean>());
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
                            mAdapter.addDatas(new ArrayList<CommentOrTransmitBean>());
                        }
                        mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mRecyclerViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
                return;
            }else if(type == TaskType.DELETE_COMMENT || type == TaskType.DELETE_TRANSMIT){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, "删除成功", Toast.LENGTH_SHORT);
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
        if(mCommentOrTransmitBeans != null && mCommentOrTransmitBeans.size() > 0 && mRecyclerView != null /*&& !isLoading*/){
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
        params.put("page_size", MySettingConfigUtil.first_load);
        params.put("method", mPreLoadMethod);
        if(baseRequestParams != null)
            params.putAll(baseRequestParams.getMap());
        if(isComment) {
            //第一次操作取消全部数据
            taskCanceled(TaskType.LOAD_COMMENT);
            CommentHandler.getCommentsRequest(this, params);
        }else{
            //第一次操作取消全部数据
            taskCanceled(TaskType.LOAD_TRANSMIT);
            TransmitHandler.getTransmitsRequest(this, params);
        }

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
        params.put("page_size", MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        if(baseRequestParams != null)
            params.putAll(baseRequestParams.getMap());
        if(isComment){
            //向上刷新也先取消所有的加载操作
            taskCanceled(TaskType.LOAD_COMMENT);
            CommentHandler.getCommentsRequest(this, params);
        }else{
            //向上刷新也先取消所有的加载操作
            taskCanceled(TaskType.LOAD_TRANSMIT);
            TransmitHandler.getTransmitsRequest(this, params);
        }

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

        HashMap<String, Object> params = new HashMap<>();
        params.put("page_size", MySettingConfigUtil.other_load);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        if(baseRequestParams != null)
            params.putAll(baseRequestParams.getMap());
        if(isComment){
            taskCanceled(TaskType.LOAD_COMMENT);
            CommentHandler.getCommentsRequest(this, params);
        }else{
            taskCanceled(TaskType.LOAD_TRANSMIT);
            TransmitHandler.getTransmitsRequest(this, params);
        }
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
        HashMap<String, Object> params = new HashMap<>();
        params.put("page_size", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.first_load: MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        if(baseRequestParams != null)
            params.putAll(baseRequestParams.getMap());
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        if(isComment){
            taskCanceled(TaskType.LOAD_COMMENT);
            CommentHandler.getCommentsRequest(this, params);
        } else{
            taskCanceled(TaskType.LOAD_TRANSMIT);
            TransmitHandler.getTransmitsRequest(this, params);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            baseRequestParams = (SerializableMap) bundle.getSerializable("serializableMap");
            this.isComment = bundle.getBoolean("isComment");
            this.itemSingleClick = bundle.getBoolean("itemSingleClick");
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
            mAdapter = new CommentOrTransmitAdapter( mContext, mCommentOrTransmitBeans);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
            mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

            //只有登录用户才给予删除的权限
            if(isLoginUser){
                //长按执行删除的操作
                mAdapter.setOnItemLongClickListener(this);
            }
            if(itemSingleClick){
                mAdapter.setOnItemClickListener(this);
            }

            //listview下方的显示
            mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);
            mAdapter.setFooterView(mFooterView);
            mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.financial_footer);
            mRecyclerViewFooter.setOnClickListener(CommentOrTransmitFragment.this);//添加点击事件
            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));

            mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);

        }
        mRecyclerView.setAdapter(mAdapter);
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
        CommonHandler.startDetailActivity(mContext,mCommentOrTransmitBeans.get(position).getTableName(), mCommentOrTransmitBeans.get(position).getTableId(), null);
    }

    @Override
    public void onItemLongClick(final int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.menu_feedback);
        builder.setTitle("提示");
        builder.setMessage("删除该记录?");
        builder.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(isComment){
                            CommentHandler.deleteComment(CommentOrTransmitFragment.this, mCommentOrTransmitBeans.get(position).getId(),  mCommentOrTransmitBeans.get(position).getCreateUserId());
                        } else{
                            TransmitHandler.deleteTransmit(CommentOrTransmitFragment.this, mCommentOrTransmitBeans.get(position).getId(),  mCommentOrTransmitBeans.get(position).getCreateUserId());
                        }
                        showLoadingDialog("Delete", "try best to delete...");
                        //ToastUtil.success(mContext, "删除"+position);
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
