package com.leedane.cn.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.ChatContactAdapter;
import com.leedane.cn.adapter.expandRecyclerviewadapter.StickyRecyclerHeadersDecoration;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.bean.MyFriendsBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.pinyin.CharacterParser;
import com.leedane.cn.pinyin.MyFriendsPinyinComparator;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.widget.ChatContactTouchableRecyclerView;
import com.leedane.cn.widget.DividerDecoration;
import com.leedane.cn.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聊天联系人列表的Fragment
 * Created by LeeDane on 2016/5/12.
 */
public class ChatContactFragment extends Fragment implements View.OnClickListener, ChatContactAdapter.OnRecyclerViewListener{

    public static final String TAG = "ChatContactFragment";

    /**
     * 选择好友的key
     */
    public static final int SELECT_AT_FRIENDS_CODE = 1123;

    private SideBar mSideBar;
    private TextView mUserDialog;
    private ChatContactTouchableRecyclerView mRecyclerView;

    HttpResponseMyFriendsBean mModel;
    private List<MyFriendsBean> mMembers = new ArrayList<>();
    private CharacterParser characterParser;
    private MyFriendsPinyinComparator pinyinComparator;
    private ChatContactAdapter mAdapter;
    private List<MyFriendsBean> mAllLists = new ArrayList<>();

    private View mRootView;
    private Context mContext;
    private Activity mActivity;

    public ChatContactFragment(){

    }

    public static final ChatContactFragment newInstance(Bundle bundle){
        ChatContactFragment fragment = new ChatContactFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_chat_contact_list, container,
                    false);
        }
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        /*if(bundle != null){
            toUserId = bundle.getInt("toUserId");
        }*/
        if(mContext == null)
            mContext = getActivity();

        if(mActivity == null){
            mActivity = getActivity();
        }

        initView();
    }

    private void initView(){

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new MyFriendsPinyinComparator();
        mSideBar = (SideBar) mRootView.findViewById(R.id.myfriends_sidebar);
        mUserDialog = (TextView) mRootView.findViewById(R.id.myfriends_dialog);
        mRecyclerView = (ChatContactTouchableRecyclerView) mRootView.findViewById(R.id.myfriends_member);
        mSideBar.setTextView(mUserDialog);

        getNetData();
    }

        @Override
        public void onDestroy() {
        super.onDestroy();
    }

    public void getNetData() {
        String tempData = SharedPreferenceUtil.getFriends(mContext.getApplicationContext());
        if(StringUtil.isNull(tempData)){
            ToastUtil.success(mContext, "您还没有好友");
            //后台去获取用户的好友信息
            CommonHandler.startUserFreidnsService(mContext.getApplicationContext(), false);
            mActivity.finish();
            return;
        }
        try {
            Gson gson = new GsonBuilder().create();
            mModel = gson.fromJson(tempData, HttpResponseMyFriendsBean.class);
            setUI();
        } catch (Exception e) {

        }
    }

    private void setUI() {

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (mAdapter != null) {
                    mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }
            }
        });
        seperateLists(mModel);

        if (mAdapter == null) {
            mAdapter = new ChatContactAdapter(mContext, mAllLists/*, mPermission, mModel.getCreater().getId()*/);
            int orientation = LinearLayoutManager.VERTICAL;
            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, orientation, false);
            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.setAdapter(mAdapter);
            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            mRecyclerView.addItemDecoration(headersDecor);
            mRecyclerView.addItemDecoration(new DividerDecoration(mContext));
            mAdapter.setOnRecyclerViewListener(ChatContactFragment.this);
            //   setTouchHelper();
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    headersDecor.invalidateHeaders();
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void seperateLists(HttpResponseMyFriendsBean mModel) {
        if (mModel.getMessage() != null && mModel.getMessage().size() > 0) {
            for (int i = 0; i < mModel.getMessage().size(); i++) {
                MyFriendsBean entity = new MyFriendsBean();
                entity.setId(mModel.getMessage().get(i).getId());
                entity.setAccount(mModel.getMessage().get(i).getAccount());
                String pinyin = characterParser.getSelling(mModel.getMessage().get(i).getAccount());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }
                mMembers.add(entity);
            }
            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);
        }
    }

    public void deleteUser(final int position) {
        ToastUtil.success(mContext, "解除好友关系功能暂时还没有实现");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position) {
        CommonHandler.startChatDetailActivity(mContext, mAllLists.get(position).getId(), mAllLists.get(position).getAccount());
    }
}
