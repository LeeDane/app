package com.leedane.cn.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.ChatDetailAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.bean.HttpResponseChatDetailBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.ChatDetailHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天详情列表的Fragment
 * Created by LeeDane on 2016/5/5.
 */
public class ChatDetailListFragment extends Fragment implements TaskListener, View.OnClickListener, View.OnLongClickListener{

    public interface OnItemClickListener{
        void onItemClick(int position, ChatDetailBean chatDetailBean);
        void clearPosition();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static final String TAG = "ChatDetailListFragment";
    private ListView mListView;
    private View mRootView;

    private String mPreLoadMethod = "firstloading";//当前的操作方式
    private boolean isLoading; //标记当前是否在加载数据
    private int mFirstId;  //页面上第一条数据的ID
    private int mLastId; //页面上第一条数据的ID
    private String userPicPath;
    private String toUserPicPath;

   // private SwipeRefreshLayout mSwipeLayout;
    private View viewHeader;
    private TextView mListViewHeader;

    private Context mContext;

    /**
     * List存放页面上的评论对象列表
     */
    private List<ChatDetailBean> mChatDetailBeans = new ArrayList<>();
    private ChatDetailAdapter mAdapter;
    private ChatDataBase dataBase;

    private int toUserId;

    public ChatDetailListFragment(){

    }

    public static final ChatDetailListFragment newInstance(Bundle bundle){
        ChatDetailListFragment fragment = new ChatDetailListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 发送消息成功后的回调
     * @param chatDetailBean
     */
    public void afterSuccessSendMessage(ChatDetailBean chatDetailBean) {
        chatDetailBean.setRead(true);
        dataBase.insert(chatDetailBean);
        List<ChatDetailBean> tempList = new ArrayList<>();
        tempList.addAll(mChatDetailBeans);
        tempList.add(chatDetailBean);
        mAdapter.refreshData(tempList);
        mListView.smoothScrollToPosition(mChatDetailBeans.size() - 1);
        //mListView.setSelection(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_chat_detail_list, container,
                    false);
        }
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            toUserId = bundle.getInt("toUserId");
        }
        if(mContext == null)
            mContext = getActivity();

        dataBase = new ChatDataBase(mContext);
        initUserPicPath();
        initView();
        loadLocalData();
    }

    /**
     * 初始化用户头像的路径
     */
    private void initUserPicPath() {
        userPicPath = BaseApplication.getLoginUserPicPath();
        String friends = SharedPreferenceUtil.getFriends(mContext.getApplicationContext());
        if(StringUtil.isNotNull(friends)){
            Gson gson = new GsonBuilder().create();
            HttpResponseMyFriendsBean mModel = gson.fromJson(friends, HttpResponseMyFriendsBean.class);
            if(mModel != null && mModel.getMessage().size() > 0){
                for(MyFriendsBean friendsBean: mModel.getMessage()){
                    if(friendsBean.getId() == toUserId){
                        toUserPicPath = friendsBean.getUserPicPath();
                        break;
                    }
                }
            }
        }

    }

    /**
     * 加载本地数据
     */
    private void loadLocalData(){
        sendFirstLoading();
    }

    private void initView(){

        mListView = (ListView)mRootView.findViewById(R.id.chat_detail_listview);
        mAdapter = new ChatDetailAdapter(mContext, mChatDetailBeans, userPicPath, toUserPicPath);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mChatDetailBeans.size() > 0)
                    onItemClickListener.onItemClick(position, mChatDetailBeans.get(position -1));
            }
        });
        
        mListView.setOnScrollListener(new ListViewOnScrollListener());
        viewHeader = LayoutInflater.from(mContext).inflate(R.layout.listview_header_item, null);
        mListView.addHeaderView(viewHeader, null, false);
        mListViewHeader = (TextView)viewHeader.findViewById(R.id.listview_header_reLoad);
        mListViewHeader.setText(getStringResource(R.string.loading));
        mListViewHeader.setOnClickListener(this);//添加点击事件
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_CHAT ){
                mListViewHeader.setText(getStringResource(R.string.load_more_error));
            }
        }
        try{
            if(type == TaskType.LOAD_CHAT){
                HttpResponseChatDetailBean httpResponseChatDetailBean = BeanConvertUtil.strConvertToChatDetailBeans(String.valueOf(result));
                if(httpResponseChatDetailBean != null && httpResponseChatDetailBean.isSuccess()){
                    List<ChatDetailBean> chatDetailBeans =  httpResponseChatDetailBean.getMessage();
                    if(chatDetailBeans != null && chatDetailBeans.size() > 0){
                        //临时list
                        List<ChatDetailBean> temList = new ArrayList<>();

                        //倒序获取数据
                        for(int i = chatDetailBeans.size() -1; i>= 0 ; i--){
                            temList.add(chatDetailBeans.get(i));
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mChatDetailBeans.clear();
                        }else{
                            temList.addAll(mChatDetailBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mChatDetailBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new ChatDetailAdapter(mContext, mChatDetailBeans, userPicPath, toUserPicPath);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        int size = mChatDetailBeans.size();

                        mFirstId = mChatDetailBeans.get(0).getId();
                        mLastId = mChatDetailBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(mChatDetailBeans.size() -1);
                        }
                        mListViewHeader.setText(getStringResource(R.string.load_finish));

                        //将记录保存在本地数据库
                        for(ChatDetailBean chatDetailBean: chatDetailBeans){
                            chatDetailBean.setRead(true);
                            dataBase.insert(chatDetailBean);
                        }

                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mChatDetailBeans.clear();
                            mAdapter.refreshData(new ArrayList<ChatDetailBean>());
                        }
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeHeaderView(viewHeader);
                            mListView.addHeaderView(viewHeader, null, false);
                        }
                        mListViewHeader.setText(getStringResource(R.string.no_load_more));
                    }
                }else{
                    if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                        mChatDetailBeans.clear();
                        mAdapter.refreshData(new ArrayList<ChatDetailBean>());
                    }
                    mListView.removeHeaderView(viewHeader);
                    mListView.addHeaderView(viewHeader, null, false);
                    mListViewHeader.setText(getStringResource(R.string.load_more_error));
                    mListViewHeader.setOnClickListener(this);
                }
            }else if(type == TaskType.ADD_CHAT){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, "聊天信息发送成功");
                    /**
                     * 延迟1秒钟后去加载数据
                     */
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendFirstLoading();
                        }
                    }, 1000);

                } else {
                    ToastUtil.failure(mContext, "聊天信息发送失败");
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //滚动停止
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                //当倒数第三个数据出现的时候就开始加载
                if (view.getFirstVisiblePosition() < 3) {
                    if(!isLoading){
                        //刷新之前先把以前的任务取消
                        taskCanceled(TaskType.LOAD_CHAT);
                        Log.i(TAG, "正在准备加载。。。。。。。。。。。");
                        sendUpLoading();
                    }
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    private void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    private void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getActivity(), title, main, true, cancelable);
    }
    /**
     * 隐藏加载Dialog
     */
    private void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    private void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        params.put("toUserId", toUserId);
        taskCanceled(TaskType.LOAD_CHAT);
        ChatDetailHandler.getChatDetailsRequest(ChatDetailListFragment.this, params);
    }

    /**
     * 发送向上刷新的任务
     */
    private void sendUpLoading(){

        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mListViewHeader.getText().toString()) || isLoading) {
            return;
        }

        mListViewHeader.setText(getStringResource(R.string.loading));

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
        taskCanceled(TaskType.LOAD_CHAT);
        ChatDetailHandler.getChatDetailsRequest(ChatDetailListFragment.this, params);
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void onDestroy() {
        /*if(userPicBitMap != null && !userPicBitMap.isRecycled()){
            userPicBitMap.recycle();
            System.gc();
            userPicBitMap = null;
        }

        if(toUserPicBitMap != null && !toUserPicBitMap.isRecycled()){
            toUserPicBitMap.recycle();
            System.gc();
            toUserPicBitMap = null;
        }*/
        dataBase.destroy();
        taskCanceled(TaskType.LOAD_CHAT);
        super.onDestroy();
    }

    /**
     * 统一获取string资源的值
     * @param resourceId
     * @return
     */
    public String getStringResource(int resourceId){
        if(mContext == null){
            return BaseApplication.newInstance().getResources().getString(resourceId);
        }
        return mContext.getResources().getString(resourceId);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(R.string.load_more_error).equalsIgnoreCase(mListViewHeader.getText().toString())
                || getStringResource(R.string.load_more).equalsIgnoreCase(mListViewHeader.getText().toString())){
            //Toast.makeText(mContext, "请求重新加载", Toast.LENGTH_SHORT).show();
            taskCanceled(TaskType.LOAD_CHAT);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad() : MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("toUserId", toUserId);
            mListViewHeader.setText(getStringResource(R.string.loading));
            ChatDetailHandler.getChatDetailsRequest(ChatDetailListFragment.this, params);
        }

    }
    @Override
    public void onClick(View v) {
        HashMap<String, Object> params = new HashMap<>();
        switch (v.getId()){
            case R.id.listview_header_reLoad:
                sendLoadAgain(v);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){

        }
        return true;
    }
}
