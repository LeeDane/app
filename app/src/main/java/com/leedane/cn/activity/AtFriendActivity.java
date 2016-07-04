package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.adapter.MyFriendsAdapter;
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
import com.leedane.cn.widget.DividerDecoration;
import com.leedane.cn.widget.MyFriendsTouchableRecyclerView;
import com.leedane.cn.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 展示@friend列表
 * Created by LeeDane on 2016/4/30.
 */
public class AtFriendActivity extends BaseActivity implements MyFriendsAdapter.OnCheckedChangedListener{
    public static final String TAG = "AtFriendActivity";

    /**
     * 选择好友的key
     */
    public static final int SELECT_AT_FRIENDS_CODE = 1123;
    /**
     * 选择完毕
     */
    private Button mComplate;

    private SideBar mSideBar;
    private TextView mUserDialog;
    private MyFriendsTouchableRecyclerView mRecyclerView;

    HttpResponseMyFriendsBean mModel;
    private List<MyFriendsBean> mMembers = new ArrayList<>();
    private CharacterParser characterParser;
    private MyFriendsPinyinComparator pinyinComparator;
    private MyFriendsAdapter mAdapter;
    private List<MyFriendsBean> mAllLists = new ArrayList<>();

    private LinearLayout mLinearLayoutSelectFriends;

    /**
     * 保存选择的集合，key为索引位置
     */
    private Set<Integer> selectSet = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfriends_list);
        initView();

    }
    private void initView() {
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.my_friends));
        //显示整个顶部的导航栏
        backLayoutVisible();

        //选择完毕
        mComplate = (Button)findViewById(R.id.view_right_button);
        mComplate.setVisibility(View.VISIBLE);
        mComplate.setOnClickListener(this);
        mComplate.setText("选中("+selectSet.size()+")位");

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new MyFriendsPinyinComparator();
        mLinearLayoutSelectFriends = (LinearLayout)findViewById(R.id.select_friend);
        mSideBar = (SideBar) findViewById(R.id.myfriends_sidebar);
        mUserDialog = (TextView) findViewById(R.id.myfriends_dialog);
        mRecyclerView = (MyFriendsTouchableRecyclerView) findViewById(R.id.myfriends_member);
        mSideBar.setTextView(mUserDialog);
        getNetData();

    }

    public void getNetData() {
        String tempData = SharedPreferenceUtil.getFriends(getApplicationContext());
        if(StringUtil.isNotNull(tempData)){
            Gson gson = new GsonBuilder().create();
            mModel = gson.fromJson(tempData, HttpResponseMyFriendsBean.class);
            if(mModel != null && mModel.getMessage().size() > 0){
                setUI();
            }else{
                loadMyFriends();
            }
        }else{
            loadMyFriends();
        }
    }

    private void loadMyFriends(){
        ToastUtil.success(AtFriendActivity.this, "您还没有好友");
        //后台去获取用户的好友信息
        CommonHandler.startUserFreidnsService(getApplicationContext(), false);
        finish();
        return;
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
            mAdapter = new MyFriendsAdapter(this, mAllLists/*, mPermission, mModel.getCreater().getId()*/);
            int orientation = LinearLayoutManager.VERTICAL;
            final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.setAdapter(mAdapter);
            final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
            mRecyclerView.addItemDecoration(headersDecor);
            mRecyclerView.addItemDecoration(new DividerDecoration(this));
            mAdapter.setOnCheckedBoxListener(this);
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
        ToastUtil.success(AtFriendActivity.this, "解除好友关系功能暂时还没有实现");
        //mAdapter.remove(mAdapter.getItem(position));
    }
    @Override
    public void select(int position, boolean isChecked) {
        //选中
        if(isChecked){
            selectSet.add(position);
        }else{
            selectSet.remove(position);
        }
        mLinearLayoutSelectFriends.removeAllViewsInLayout();
        for(Integer p: selectSet){
            Button button = new Button(AtFriendActivity.this);
            button.setText(mAllLists.get(p).getAccount());
            mLinearLayoutSelectFriends.addView(button);
        }
        mComplate.setText("选中("+selectSet.size()+")位");
        //ToastUtil.success(AtFriendActivity.this, "选择：" + position+",isChecked:"+isChecked);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button: //完成
                StringBuffer buffer = new StringBuffer();
                if(selectSet.size() > 0){
                    for (Integer select: selectSet){
                        buffer.append("@"+mAllLists.get(select).getAccount()+ " ");
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("select", buffer.toString());
                setResult(SELECT_AT_FRIENDS_CODE, intent);
                finish();
                break;
        }
    }
}
