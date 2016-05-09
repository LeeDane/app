package com.leedane.cn.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.helper.SoftKeyboardStateHelper;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
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
        , TaskListener {
    public static final String TAG = "SendToolbarFragment";
    private Context mContext;
    private EditText mContentText;
    private ImageView mContentSend;
    private LinearLayout mSendBarRootView;
    private View mRootView;
    private SoftKeyboardStateHelper mKeyboardHelper;

    private int mid;

    private int itemClickPosition;
    private int commentOrTransmit = 0;
    private CommentOrTransmitBean commentOrTransmitBean;
    private MoodDetailFragment.OnItemClickListener onItemClickListener;

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
        this.mContentText = (EditText) mRootView.findViewById(R.id.mood_detail_comment_or_transmit_text);
        this.mContentSend = (ImageView)mRootView.findViewById(R.id.mood_detail_comment_or_transmit_send);
        mContentSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mood_detail_comment_or_transmit_send:
                String commentOrTransmitText = mContentText.getText().toString();
                if(StringUtil.isNull(commentOrTransmitText) && commentOrTransmit == 0){
                    ToastUtil.failure(mContext, "评论内容不能为空", Toast.LENGTH_SHORT);
                    return;
                }
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

    public void onItemClick(int position, CommentOrTransmitBean commentOrTransmitBean, int commentOrTransmit) {
        this.itemClickPosition = position;
        this.commentOrTransmit = commentOrTransmit;
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
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){

                    //注册发表评论或者转发后的监听
                    onAddCommentOrTransmitListener.afterSuccessAddCommentOrTransmit(commentOrTransmit);

                    new NotificationUtil(1, mContext).sendTipNotification("信息提示", "您的评论发表成功", "测试", 1, 0);
                    mContentText.setText("");
                } else {
                    new NotificationUtil(1, mContext).sendActionNotification("信息提示", "您的评论发表失败，点击重试", "测试", 1, 0);
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
