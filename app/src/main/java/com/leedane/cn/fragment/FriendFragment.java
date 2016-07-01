package com.leedane.cn.fragment;

import android.app.Dialog;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.FriendAdapter;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.FriendBean;
import com.leedane.cn.bean.HttpResponseFriendBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.FriendHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 朋友相关列表的fragment类
 * Created by LeeDane on 2016/4/13.
 */
public class FriendFragment extends BaseFragment{

    public static final String TAG = "FriendFragment";
    private boolean itemSingleClick; //控制每一项是否可以出发单击事件
    private Context mContext;
    private ListView mListView;
    private FriendAdapter mAdapter;
    private List<FriendBean> mFriendBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    //是否是第一次加载
    private boolean isFirstLoading = true;
    private int toUserId;

    private int clickListItemPosition;

    public FriendFragment(){
    }

    public static final FriendFragment newInstance(Bundle bundle){
        FriendFragment fragment = new FriendFragment();
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

        sendFirstLoading();
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_FRIENDS_PAGING && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_FRIENDS_PAGING){
                mSwipeLayout.setRefreshing(false);
                HttpResponseFriendBean responseFriendBean = BeanConvertUtil.strConvertToFriendBeans(String.valueOf(result));
                if(responseFriendBean != null && responseFriendBean.isSuccess()){
                    List<FriendBean> friendBeans =  responseFriendBean.getMessage();
                    if(friendBeans != null && friendBeans.size() > 0){
                        //临时list
                        List<FriendBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mFriendBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = friendBeans.size() -1; i>= 0 ; i--){
                                temList.add(friendBeans.get(i));
                            }
                            temList.addAll(mFriendBeans);
                        }else{
                            temList.addAll(mFriendBeans);
                            temList.addAll(friendBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mFriendBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new FriendAdapter(mContext, mFriendBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);

                        int size = mFriendBeans.size();

                        mFirstId = mFriendBeans.get(0).getId();
                        mLastId = mFriendBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFriendBeans.clear();
                            mAdapter.refreshData(new ArrayList<FriendBean>());
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
                            mFriendBeans.clear();
                            mAdapter.refreshData(new ArrayList<FriendBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null ,false);
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }
                }
            }else if(type == TaskType.CANCEL_FRIEND){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    //异步去加载用户的好友数据
                    String friendsStr = SharedPreferenceUtil.getFriends(mContext);
                    int fid = mFriendBeans.get(clickListItemPosition).getFid();
                    if(StringUtil.isNotNull(friendsStr)){
                        Gson gson = new GsonBuilder().create();
                        HttpResponseMyFriendsBean myFriendsBean = gson.fromJson(friendsStr, HttpResponseMyFriendsBean.class);
                        List<MyFriendsBean> myFriendsBeans = myFriendsBean.getMessage();
                        if(myFriendsBeans != null && myFriendsBeans.size() > 0){
                            for(MyFriendsBean myFriendsBean1: myFriendsBeans){
                                if(myFriendsBean1.getId() == fid){
                                    myFriendsBeans.remove(myFriendsBean1);
                                    break;
                                }
                            }
                        }
                        SharedPreferenceUtil.saveFriends(mContext, gson.toJson(myFriendsBean).toString());
                        mFriendBeans.remove(clickListItemPosition);
                        mAdapter.notifyDataSetChanged();
                        dismissItemMenuDialog();
                        ToastUtil.success(mContext, jsonObject);
                    }else{
                        ToastUtil.failure(mContext, "没有好友");
                    }

                    //删除本地的聊天记录
                    ChatDataBase chatDataBase = new ChatDataBase(mContext);
                    chatDataBase.deleteByUser(fid);
                    chatDataBase.destroy();
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
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        FriendHandler.sendFriendsPaging(FriendFragment.this, params);
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
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        FriendHandler.sendFriendsPaging(FriendFragment.this, params);
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
        taskCanceled(TaskType.LOAD_FRIENDS_PAGING);
        FriendHandler.sendFriendsPaging(FriendFragment.this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(mContext, R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getStringResource(mContext, R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){
            ToastUtil.success(mContext, "请求重新加载", Toast.LENGTH_SHORT);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad() : MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("toUserId", toUserId);
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));
            FriendHandler.sendFriendsPaging(FriendFragment.this, params);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.itemSingleClick = true;
            this.toUserId = BaseApplication.getLoginUserId();
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            this.mListView = (ListView) mRootView.findViewById(R.id.listview_items);
            mAdapter = new FriendAdapter( mContext, mFriendBeans);
            mListView.setOnScrollListener(new ListViewOnScrollListener());
            if(itemSingleClick){
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showItemMenuDialog(position);
                    }
                });
            }

            //listview下方的显示
            viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(FriendFragment.this);//添加点击事件
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

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     * @param index
     */
    public void showItemMenuDialog(int index){
        clickListItemPosition = index;
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissItemMenuDialog();

        mDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(mContext, R.string.nav_personnal_centre));
        //解除好友关系
        menus.add(getStringResource(mContext, R.string.personal_no_friend));

        SimpleListAdapter adapter = new SimpleListAdapter(getActivity().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //解除好友关系
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_no_friend))){
                    FriendHandler.cancelFriend(FriendFragment.this, mFriendBeans.get(clickListItemPosition).getId());
                    showLoadingDialog("DELETE", "try best to loading...");
                    //个人中心
                } else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.nav_personnal_centre))){
                    CommonHandler.startPersonalActivity(mContext, mFriendBeans.get(clickListItemPosition).getFid());
                    dismissItemMenuDialog();
                }
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissItemMenuDialog();
            }
        });
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
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
