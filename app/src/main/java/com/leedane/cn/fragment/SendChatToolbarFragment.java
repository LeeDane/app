package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leedane.cn.activity.ChatDetailActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.ChatDetailBean;
import com.leedane.cn.handler.ChatDetailHandler;
import com.leedane.cn.helper.SoftKeyboardStateHelper;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 发送工具条
 * Created by LeeDane on 2016/5/3.
 */
public class SendChatToolbarFragment extends Fragment implements View.OnClickListener,SoftKeyboardStateHelper.SoftKeyboardStateListener
        , TaskListener {
    public static final String TAG = "SendChatToolbarFragment";
    private Context mContext;
    private EditText mContentText;
    private ImageView mContentSend;
    private LinearLayout mSendBarRootView;
    private View mRootView;
    private SoftKeyboardStateHelper mKeyboardHelper;
    private int itemClickPosition;
    private ChatDetailBean chatDetailBean;

    private int toUserId;
    public SendChatToolbarFragment(){
    }

    public static final SendChatToolbarFragment newInstance(Bundle bundle){
        SendChatToolbarFragment fragment = new SendChatToolbarFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnSendChatListener onSendChatListener;

    public void setOnSendChatListener(OnSendChatListener onSendChatListener) {
        this.onSendChatListener = onSendChatListener;
    }

    /**
     * 发送聊天信息成功后的事件的监听
     */
    public interface OnSendChatListener{
        /**
         * 成功发送聊天信息后的监听
         */
        void afterSuccessSendChat(ChatDetailBean chatDetailBean);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_send_toor_bar, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.toUserId = bundle.getInt("toUserId", 0);
        }
        if(mContext == null)
            mContext = getActivity();

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        this.mSendBarRootView = (LinearLayout)mRootView.findViewById(R.id.send_bar_root);
        this.mContentText = (EditText) mRootView.findViewById(R.id.mood_detail_comment_or_transmit_text);
        this.mContentSend = (ImageView)mRootView.findViewById(R.id.mood_detail_comment_or_transmit_send);
        mContentSend.setOnClickListener(this);
        this.mContentText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    goSend();
                }
                return false;
            }
        });
    }

    /**
     * 执行发送指令
     */
    private void goSend(){
        String contentText = mContentText.getText().toString();
        if(StringUtil.isNull(contentText)){
            ToastUtil.failure(mContext, "聊天内容不能为空", Toast.LENGTH_SHORT);
            return;
        }
        mContentText.setText("");
        HashMap<String, Object> params = new HashMap<>();
        params.put("toUserId", toUserId);
        params.put("content", contentText);
        params.put("type", 0);
        ChatDetailHandler.sendChatDetail(SendChatToolbarFragment.this, params);
        ((ChatDetailActivity)getActivity()).showLoadingDialog("Chat", "try best to loading...");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mood_detail_comment_or_transmit_send:
                goSend();
                break;
        }
    }

    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = keyboardHeightInPx;
        mSendBarRootView.setLayoutParams(layoutParams);
    }

    @Override
    public void onSoftKeyboardClosed() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 0;
        mSendBarRootView.setLayoutParams(layoutParams);
    }

    public void onItemClick(int position, ChatDetailBean chatDetailBean) {
        this.itemClickPosition = position;
        this.chatDetailBean = chatDetailBean;
        //mContentText.setHint(" @" + chatDetailBean.getAccount());
    }

    public void clearPosition() {
        itemClickPosition = -1;
        mContentText.setText("");
        mContentText.setHint("说点什么吧");
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            ToastUtil.failure(mContext, ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            return;
        }
        try{
            if(type == TaskType.ADD_CHAT){
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    try{
                        Gson gson = new GsonBuilder()
                                .enableComplexMapKeySerialization()
                                .create();
                        ChatDetailBean detailBean = gson.fromJson(jsonObject.getString("message"), ChatDetailBean.class);

                        //隐藏输入法
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mContentText.getWindowToken(), 0);

                        //注册发表评论或者转发后的监听
                        onSendChatListener.afterSuccessSendChat(detailBean);


                    }catch (Exception e){
                        ToastUtil.failure(mContext, "发送不成功，原始是服务器返回的不是json对象");
                        e.printStackTrace();
                    }

                    ToastUtil.success(mContext, "聊天信息发送成功");
                    mContentText.setText("");
                } else {
                    ToastUtil.failure(mContext, "信息发送失败"+(jsonObject.has("message")? ":"+jsonObject.getString("message"):""));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
