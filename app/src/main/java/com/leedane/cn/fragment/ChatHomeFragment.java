package com.leedane.cn.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.ChatAdapter;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.ChatHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天首页列表的Fragment
 * Created by LeeDane on 2016/5/3.
 */
public class ChatHomeFragment extends Fragment implements TaskListener
        , View.OnClickListener, View.OnLongClickListener
        ,SwipeRefreshLayout.OnRefreshListener{

    /**
     * 聊天数据改变的监听器
     */
    public interface OnChatDataChangeListener{
        void change();
    }

    private OnChatDataChangeListener onChatDataChangeListener;

    public void setOnChatDataChangeListener(OnChatDataChangeListener onChatDataChangeListener) {
        this.onChatDataChangeListener = onChatDataChangeListener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position, ChatBean chatBean);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static final String TAG = "ChatHomeFragment";

    private ListView mListView;
    private View mRootView;
    private SwipeRefreshLayout mSwipeLayout;
    private Context mContext;

    private ChatDataBase database;
    /**
     * List存放页面上的评论对象列表
     */
    private List<ChatBean> mChatBeans = new ArrayList<>();
    private ChatAdapter mAdapter;
    public ChatHomeFragment(){
    }

    public static final ChatHomeFragment newInstance(Bundle bundle){
        ChatHomeFragment fragment = new ChatHomeFragment();
        if(bundle != null)
            fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null){

            mRootView = inflater.inflate(R.layout.fragment_chat_home, container,
                    false);
        }
        Log.i(TAG, "onCreateView() ........................");
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();

        database = new ChatDataBase(mContext);

        initView();
    }


    //初次启动或者被销毁、取代之后重新打开都会调用这个方法
    @Override
    public void onStart() {
        super.onStart();
        initData();
        Log.i(TAG, "onstart() ........................");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause() ........................");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView() ........................");
    }

    /**
     * 获取初始化数据
     */
    private void initData(){

        String tempData = SharedPreferenceUtil.getFriends(mContext.getApplicationContext());
        if(StringUtil.isNull(tempData)){
            ToastUtil.success(mContext, "您还没有好友");
            //后台去获取用户的好友信息
            CommonHandler.startUserFreidnsService(mContext.getApplicationContext(), false);
            return;
        }
        try {
            Gson gson = new GsonBuilder().create();
            HttpResponseMyFriendsBean model = gson.fromJson(tempData, HttpResponseMyFriendsBean.class);
            mChatBeans = new ArrayList<>();
            if(model != null && model.getMessage() != null){
                mChatBeans.addAll(database.queryChatHome(mContext, model.getMessage()));
            }
            List<ChatBean> tempChatBeans = new ArrayList<>();
            tempChatBeans.addAll(mChatBeans);
            mAdapter.refreshData(tempChatBeans);
             //mChatBeans.addAll(ChatHandler.getLocalChatBeans(mContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initView(){

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.chat_home_swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mListView = (ListView)mRootView.findViewById(R.id.chat_home_listview);
        mAdapter = new ChatAdapter(mChatBeans, mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mChatBeans.size() > 0)
                    onItemClickListener.onItemClick(position, mChatBeans.get(position));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showMenuDialog(position);
                return true;
            }
        });
    }

    private Dialog mDialog;

    /**
     * 显示弹出自定义菜单view
     * @param index
     */
    public void showMenuDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissMenuDialog();

        mDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(R.string.delete));
        menus.add(getStringResource(R.string.add_top));
        menus.add(getStringResource(R.string.nav_personnal_centre));
        SimpleListAdapter adapter = new SimpleListAdapter(getActivity().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //删除
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.delete))){
                    if(ChatHandler.deleteLocalChat(mContext, mChatBeans.get(index).getToUserId())){
                        mChatBeans.remove(index);
                        mAdapter.notifyDataSetChanged();
                        ToastUtil.success(mContext, "本地聊天记录删除成功");
                        dismissMenuDialog();
                    }else{
                        ToastUtil.success(mContext, "本地聊天记录删除失败");
                    }
                    //个人中心
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.nav_personnal_centre))){
                    CommonHandler.startPersonalActivity(mContext,  mChatBeans.get(index).getToUserId());
                }
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    public String getStringResource(int resourseId){
        if(mContext == null){
            return BaseApplication.newInstance().getResources().getString(resourseId);
        }else{
            return mContext.getResources().getString(resourseId);
        }
    }


    /**
     * 隐藏弹出自定义view
     */
    public void dismissMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
    }

    @Override
    public void taskCanceled(TaskType type) {
    }

    @Override
    public void taskStarted(TaskType type) {
    }

    @Override
    public void onDestroy() {
        if(database != null)
            database.destroy();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
