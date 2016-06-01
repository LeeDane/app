package com.leedane.cn.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.bean.HttpResponseMyFriendsBean;
import com.leedane.cn.fragment.ChatDetailListFragment;
import com.leedane.cn.fragment.SendChatToolbarFragment;
import com.leedane.cn.fragment.SendToolbarFragment;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

/**
 * 聊天首页activity
 * Created by LeeDane on 2016/5/4.
 */
public class ChatDetailActivity extends BaseActivity implements ChatDetailListFragment.OnItemClickListener
        , SendChatToolbarFragment.OnSendChatListener{

    public static final String TAG = "ChatDetailActivity";

    public static int toUserId;
    private String toUserAccount;
    public static boolean isForeground = false;

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.leedane.cn.activity.ChatDetailActivity.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(ChatDetailActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.ChatDetailActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_chat_detail);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        Intent it = getIntent();
        toUserAccount = it.getStringExtra("toUserAccount");
        toUserId = it.getIntExtra("toUserId", 0);

        if(toUserId < 1){
            ToastUtil.failure(ChatDetailActivity.this, "该用户不存在");
            finish();
        }
        setTitleViewText("与" + toUserAccount + "聊天中");
        backLayoutVisible();

        Bundle bundle = new Bundle();
        bundle.putInt("toUserId", toUserId);
        ChatDetailListFragment chatDetailListFragment = ChatDetailListFragment.newInstance(bundle);
        chatDetailListFragment.setOnItemClickListener(this);
        SendChatToolbarFragment sendChatToolbarFragment = SendChatToolbarFragment.newInstance(bundle);
        sendChatToolbarFragment.setOnSendChatListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_detail_container, chatDetailListFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.chat_detail_toolbar, sendChatToolbarFragment).commit();

        registerMessageReceiver();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, ChatDetailBean chatDetailBean) {
        ToastUtil.success(ChatDetailActivity.this, String.valueOf(chatDetailBean.getCreateUserId()));
    }

    @Override
    public void clearPosition() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //找到SendToolBarFragment
        SendToolbarFragment sendToolbarFragment = (SendToolbarFragment) fragmentManager.findFragmentById(R.id.chat_detail_toolbar);
        sendToolbarFragment.clearPosition();
    }

    @Override
    public void afterSuccessSendChat(ChatDetailBean chatDetailBean) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //找到ChatDetailListFragment
        ChatDetailListFragment chatDetailListFragment = (ChatDetailListFragment) fragmentManager.findFragmentById(R.id.chat_detail_container);
        chatDetailListFragment.afterSuccessSendMessage(chatDetailBean);
    }

    /**
     * 注册接受者
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                Gson gson = new GsonBuilder().create();
                ChatDetailBean chatDetailBean = gson.fromJson(messge, ChatDetailBean.class);
                FragmentManager fragmentManager = getSupportFragmentManager();
                //找到ChatDetailListFragment
                ChatDetailListFragment chatDetailListFragment = (ChatDetailListFragment) fragmentManager.findFragmentById(R.id.chat_detail_container);
                chatDetailListFragment.afterSuccessSendMessage(chatDetailBean);
               /* String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (StringUtil.isNotNull(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());*/
            }
        }
    }

    private void setCostomMsg(String msg){
        ToastUtil.success(ChatDetailActivity.this, msg);
    }
}
