package com.leedane.cn.fragment;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.MoodDetailActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.customview.RightImgClickEditText;
import com.leedane.cn.emoji.EmojiBean;
import com.leedane.cn.emoji.EmojiPagerAdapter;
import com.leedane.cn.emoji.EmojiUtil;
import com.leedane.cn.emoji.OnEmojiClickListener;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.helper.SoftKeyboardStateHelper;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 发送工具条
 * Created by LeeDane on 2016/5/3.
 */
public class SendToolbarFragment extends Fragment implements View.OnClickListener,SoftKeyboardStateHelper.SoftKeyboardStateListener
        , TaskListener , RightImgClickEditText.OnEmojiImgClickListener, OnEmojiClickListener {
    public static final String TAG = "SendToolbarFragment";
    private Context mContext;
    private RightImgClickEditText mContentText;
    private ImageView mContentSend;

    private ViewPager mViewPager;

    private LinearLayout mSendBarRootView;
    private View mRootView;
    private SoftKeyboardStateHelper mKeyboardHelper;

    private RightImgClickEditText.OnEmojiImgClickListener onEmojiImgClickListener;

    private int mid;

    private int itemClickPosition;
    private int commentOrTransmit = 0;
    private CommentOrTransmitBean commentOrTransmitBean;

    public SendToolbarFragment(){
    }

    public static final SendToolbarFragment newInstance(Bundle bundle){
        SendToolbarFragment fragment = new SendToolbarFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnAddCommentOrTransmitListener onAddCommentOrTransmitListener;

    public void setOnAddCommentOrTransmitListener(OnAddCommentOrTransmitListener onAddCommentOrTransmitListener) {
        this.onAddCommentOrTransmitListener = onAddCommentOrTransmitListener;
    }

    /**
     * 操作类型改变的时候触发的
     * @param commentOrTransmit
     */
    public void changeOperateType(int commentOrTransmit) {
        this.commentOrTransmit = commentOrTransmit;
    }

    @Override
    public void afterEmojiImgClick(boolean showEmoji) {

        mContentText.setFocusable(!showEmoji);
        mContentText.setFocusableInTouchMode(!showEmoji);
        mContentText.requestFocus();
        if(showEmoji){
            InputMethodManager imm = ( InputMethodManager ) mContentText.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
            if ( imm.isActive( ) ) {
                imm.hideSoftInputFromWindow( mContentText.getApplicationWindowToken( ) , InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }else{
            InputMethodManager imm = ( InputMethodManager ) mContentText.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
            if (!imm.isActive()) {
                imm.showSoftInput(mContentText, InputMethodManager.SHOW_FORCED);
            }
        }
        mKeyboardHelper.setIsSoftKeyboardOpened(!showEmoji);
        if(showEmoji){
            mViewPager.setVisibility(View.VISIBLE);
        }else {
            mViewPager.setVisibility(View.GONE);
        }
        //ToastUtil.success(mContext, "hahahaa" + showEmoji);
    }

    @Override
    public void onDeleteButtonClick(View v) {

    }

    @Override
    public void onEmojiClick(EmojiBean v) {
        //ToastUtil.success(mContext, "啦啦:"+v.getEmojiStr());
        mContentText.setText(StringUtil.changeNotNull(mContentText.getText().toString()) + "[" + v.getEmojiStr() + "]");
        AppUtil.editTextShowImg(mContext, mContentText);
    }

    /**
     * 评论或者转发后的事件的监听
     */
    public interface OnAddCommentOrTransmitListener{
        /**
         * 成功发表评论或者转发后的监听
         */
        void afterSuccessAddCommentOrTransmit(int commentOrTransmit);
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
            this.mid = bundle.getInt("tableId", 0);
        }
        if(mContext == null)
            mContext = getActivity();

        mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow()
                .getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
        this.mSendBarRootView = (LinearLayout)mRootView.findViewById(R.id.send_bar_root);
        this.mContentText = (RightImgClickEditText) mRootView.findViewById(R.id.mood_detail_comment_or_transmit_text);
        this.mContentSend = (ImageView)mRootView.findViewById(R.id.mood_detail_comment_or_transmit_send);
        mContentSend.setOnClickListener(this);
        this.mContentText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //AppUtil.editTextShowImg(mContext, mContentText, R.drawable.emoji_click);
                    goSend();
                }
                return false;
            }
        });
        mContentText.setOnEmojiImgClickListener(SendToolbarFragment.this);
        mViewPager = (ViewPager)mRootView.findViewById(R.id.emoji_viewpager);
        mViewPager.setAdapter(new EmojiPagerAdapter(getFragmentManager(), SendToolbarFragment.this));
    }

    /**
     * 执行发送指令
     */
    private void goSend(){
        String commentOrTransmitText = mContentText.getText().toString();
        if(StringUtil.isNull(commentOrTransmitText) && commentOrTransmit == 0){
            ToastUtil.failure(mContext, "评论内容不能为空", Toast.LENGTH_SHORT);
            return;
        }
        mContentText.setText("");
        HashMap<String, Object> params = new HashMap<>();
        if(commentOrTransmit == 0){
            showLoadingDialog("Comment", "try to commeting...", true);
            params.put("content", commentOrTransmitText);
            params.put("table_name", "t_mood");
            params.put("table_id", mid);
            params.put("level", 1);
            if(itemClickPosition > 0 && commentOrTransmitBean != null)
                params.put("pid", commentOrTransmitBean.getId());
            CommentHandler.sendComment(SendToolbarFragment.this, params);
        }else{
            showLoadingDialog("Transmit", "try to transmiting...", true);
            params.put("content", StringUtil.isNull(commentOrTransmitText)? "转发了这条心情" :commentOrTransmitText);
            params.put("table_name", "t_mood");
            params.put("table_id", mid);
            TransmitHandler.sendTransmit(this, params);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mood_detail_comment_or_transmit_send:
                goSend();
                break;
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
    protected void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(mContext, title, main, true, cancelable);
    }
    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        /*ViewGroup.LayoutParams layoutParams = mSendBarRootView.getLayoutParams();*/
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

    public void onItemClick(int position, CommentOrTransmitBean commentOrTransmitBean) {
        this.itemClickPosition = position;
        this.commentOrTransmitBean = commentOrTransmitBean;
        mContentText.setHint(" @" + commentOrTransmitBean.getAccount());
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
            dismissLoadingDialog();
            return;
        }
        try{
            if(type == TaskType.ADD_COMMENT || type == TaskType.ADD_TRANSMIT){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){

                    //隐藏输入法
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mContentText.getWindowToken(), 0);
                    //注册发表评论或者转发后的监听
                    onAddCommentOrTransmitListener.afterSuccessAddCommentOrTransmit(commentOrTransmit);
                    ToastUtil.success(mContext, "发送成功");
                    //new NotificationUtil(1, mContext).sendTipNotification("信息提示", "您的评论发表成功", "测试", 1, 0);
                    mContentText.setText("");
                } else {
                    ToastUtil.failure(mContext, "发送失败" + ":" + (jsonObject.has("message") ? jsonObject.getString("message") : ""));
                    //new NotificationUtil(1, mContext).sendActionNotification("信息提示", "您的评论发表失败，点击重试", "测试", 1, 0, MoodDetailActivity.class);
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
