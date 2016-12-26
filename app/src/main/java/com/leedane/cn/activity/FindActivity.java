package com.leedane.cn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leedane.cn.adapter.FindAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MenuBean;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现activity
 * Created by LeeDane on 2016/4/14.
 */
public class FindActivity extends BaseActivity implements FindAdapter.OnRecyclerViewListener{

    private RecyclerView mRecyclerView;

    private FindAdapter mAdapter;

    private List<MenuBean> findBeans = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.find);
        backLayoutVisible();

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        //使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        mRecyclerView.setHasFixedSize(true);

        //如果你需要显示的是横向滚动的列表或者竖直滚动的列表，则使用这个LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FindActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);

        findBeans.add(new MenuBean(R.drawable.menu_circle_of_friends, getStringResource(R.string.circle_of_friend)));
        findBeans.add(new MenuBean(R.drawable.menu_chat, getStringResource(R.string.chat)));
        findBeans.add(new MenuBean(R.drawable.menu_friends, getStringResource(R.string.my_friends)));
        findBeans.add(new MenuBean(R.drawable.menu_search, getStringResource(R.string.search)));
        findBeans.add(new MenuBean(R.drawable.qr_code, getStringResource(R.string.sao_yi_sao)));
        findBeans.add(new MenuBean(R.drawable.no_user, getStringResource(R.string.yao_yi_yao)));
        findBeans.add(new MenuBean(R.drawable.menu_address_list, getStringResource(R.string.address_list)));

        mAdapter = new FindAdapter(findBeans);
        mAdapter.setOnRecyclerViewListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        //ToastUtil.success(FindActivity.this, "点击："+findBeans.get(position).getTitle());
        String title = findBeans.get(position).getTitle();
        if(title.equalsIgnoreCase(getStringResource(R.string.circle_of_friend))){ //朋友圈
            Intent it_circle = new Intent(FindActivity.this, CircleOfFriendActivity.class);
            startActivity(it_circle);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.sao_yi_sao))) {//扫一扫
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.search))) {//搜索
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, SearchActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.my_friends))){//我的朋友
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, FriendActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.address_list))){//通讯录
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, AddressListActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.chat))){//聊天
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, ChatActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.yao_yi_yao))){//摇一摇
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, ShakeActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onItemLongClick(int position) {
        ToastUtil.success(FindActivity.this, "长按："+findBeans.get(position).getTitle());
        return false;
    }
}
