package com.leedane.cn.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.ChatAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBean;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.database.ChatDataBase;
import com.leedane.cn.handler.ChatHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.leedaneAPP.R;
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
        initData();
        initView();
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
            if(model != null && model.getMessage() != null){
                mChatBeans.addAll(database.queryChatHome(mContext, model.getMessage()));
            }
             mChatBeans.addAll(ChatHandler.getLocalChatBeans(mContext));
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
