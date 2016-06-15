package com.leedane.cn.adapter.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.search.SearchUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.FanHandler;
import com.leedane.cn.handler.FriendHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 搜索用户列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchUserAdapter extends BaseAdapter implements TaskListener{

    public static final String TAG = "SearchUserAdapter";

    public List<SearchUserBean> mSearchUserBeans;  //所有聊天列表
    private Context mContext; //上下文对象
    private int loginUserId;

    public SearchUserAdapter(Context context, List<SearchUserBean> searchUserBeans){
        super();
        this.mSearchUserBeans = searchUserBeans;
        this.mContext = context;
        this.loginUserId = BaseApplication.getLoginUserId();
    }

    @Override
    public int getCount() {
        return mSearchUserBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchUserBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchUserBean> searchUserBeans){
        this.mSearchUserBeans.clear();
        this.mSearchUserBeans.addAll(searchUserBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_user_listview, null);
            myHolder = new MyHolder();
            myHolder.setmUserInfo((LinearLayout)convertView.findViewById(R.id.search_user_info));
            TextView time = (TextView) convertView.findViewById(R.id.search_user_time);
            time.setSelected(true);
            myHolder.setmCreateTime(time);
            myHolder.setmIntroduction((TextView) convertView.findViewById(R.id.search_user_introduction));
            myHolder.setmPicPath((ImageView) convertView.findViewById(R.id.search_user_pic));
            myHolder.setmAccount((TextView) convertView.findViewById(R.id.search_user_name));
            TextView birthDay = (TextView) convertView.findViewById(R.id.search_user_birth_day);
            birthDay.setSelected(true);
            myHolder.setmBirthDay(birthDay);
            TextView email = (TextView) convertView.findViewById(R.id.search_user_email);
            email.setSelected(true);
            myHolder.setmEmail(email);
            TextView phone = (TextView) convertView.findViewById(R.id.search_user_phone);
            phone.setSelected(true);
            myHolder.setmPhone(phone);
            TextView qq = (TextView) convertView.findViewById(R.id.search_user_qq);
            qq.setSelected(true);
            myHolder.setmQq(qq);
            myHolder.setmSex((TextView) convertView.findViewById(R.id.search_user_sex));
            myHolder.setmFan((TextView) convertView.findViewById(R.id.search_user_fan));
            myHolder.setmFriend((TextView) convertView.findViewById(R.id.search_user_friend));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        final SearchUserBean searchUserBean = mSearchUserBeans.get(position);

        if(searchUserBean.getId() == loginUserId){
            myHolder.getmFan().setVisibility(View.GONE);
            myHolder.getmFriend().setVisibility(View.GONE);
        }else{
            myHolder.getmFan().setVisibility(View.VISIBLE);
            myHolder.getmFriend().setVisibility(View.VISIBLE);
            if(searchUserBean.isFan()){//已经是粉丝
                myHolder.getmFan().setText(mContext.getResources().getString(R.string.personal_is_fan));
            }else{//未关注
                myHolder.getmFan().setText(mContext.getResources().getString(R.string.personal_add_fan));
            }
            myHolder.getmFan().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关注TA
                    if (!searchUserBean.isFan()) {
                        FanHandler.addAttention(SearchUserAdapter.this, searchUserBean.getId());
                        //取消关注
                    } else {
                        FanHandler.cancelAttention(SearchUserAdapter.this, searchUserBean.getId());
                    }
                }
            });

            if(searchUserBean.isFriend()){//已经是好友
                myHolder.getmFriend().setText(mContext.getResources().getString(R.string.personal_is_friend));
            }else{//未添加好友
                myHolder.getmFriend().setText(mContext.getResources().getString(R.string.personal_add_friend));
            }
            myHolder.getmFriend().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加他为好友
                    if(!searchUserBean.isFriend()){
                        FriendHandler.addFriend(SearchUserAdapter.this, searchUserBean.getId());
                        //解除好友关系
                    }else{
                        FriendHandler.cancelFriend(SearchUserAdapter.this, searchUserBean.getId());
                    }
                }
            });
        }


        String createTime = searchUserBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getmCreateTime().setText("");
        }else{
            myHolder.getmCreateTime().setText("注册:"+RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getmIntroduction().setText("简介:"+StringUtil.changeNotNull(searchUserBean.getIntroduction()));
        myHolder.getmAccount().setText(StringUtil.changeNotNull(searchUserBean.getAccount()));
        myHolder.getmBirthDay().setText("生日:" +StringUtil.changeNotNull(searchUserBean.getBirthDay()));
        myHolder.getmEmail().setText("邮件:"+StringUtil.changeNotNull(searchUserBean.getEmail()));
        myHolder.getmPhone().setText("手机:"+ StringUtil.changeNotNull(searchUserBean.getPhone()));
        if(StringUtil.isNotNull(searchUserBean.getUserPicPath())){
            ImageCacheManager.loadImage(searchUserBean.getUserPicPath(), myHolder.getmPicPath(), 40, 40);
        }
        myHolder.getmQq().setText("QQ:"+StringUtil.changeNotNull(searchUserBean.getQq()));
        myHolder.getmSex().setText("性别:"+StringUtil.changeNotNull(searchUserBean.getSex()));

        myHolder.getmUserInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, searchUserBean.getId());
            }
        });
        return convertView;
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            dismissLoadingDialog();
            Toast.makeText(mContext, ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            dismissLoadingDialog();
            if(type == TaskType.ADD_FAN){ //添加关注

                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, jsonObject);
                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }else  if(type == TaskType.CANCEL_FAN){//取消关注
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, jsonObject);

                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }else  if(type == TaskType.ADD_FRIEND){//添加好友
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, jsonObject);

                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }else  if(type == TaskType.CANCEL_FRIEND){//解除好友关系
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    ToastUtil.success(mContext, jsonObject);

                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
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
    public void taskCanceled(TaskType type) {

    }

    private class MyHolder{

        private LinearLayout mUserInfo;
        /**
         * 搜索用户的账号
         */
        private TextView mAccount;

        /**
         * 搜索用户的头像路径
         */
        private ImageView mPicPath;

        /**
         * 搜索用户的创建时间
         */
        private TextView mCreateTime;

        private TextView mIntroduction;

        private TextView mBirthDay;

        private TextView mPhone;

        private TextView mSex;

        private TextView mEmail;

        private TextView mQq;

        private TextView mFan;

        private TextView mFriend;

        public TextView getmCreateTime() {
            return mCreateTime;
        }

        public void setmCreateTime(TextView mCreateTime) {
            this.mCreateTime = mCreateTime;
        }

        public ImageView getmPicPath() {
            return mPicPath;
        }

        public void setmPicPath(ImageView mPicPath) {
            this.mPicPath = mPicPath;
        }

        public TextView getmAccount() {
            return mAccount;
        }

        public void setmAccount(TextView mAccount) {
            this.mAccount = mAccount;
        }

        public TextView getmBirthDay() {
            return mBirthDay;
        }

        public void setmBirthDay(TextView mBirthDay) {
            this.mBirthDay = mBirthDay;
        }

        public TextView getmEmail() {
            return mEmail;
        }

        public void setmEmail(TextView mEmail) {
            this.mEmail = mEmail;
        }

        public TextView getmIntroduction() {
            return mIntroduction;
        }

        public void setmIntroduction(TextView mIntroduction) {
            this.mIntroduction = mIntroduction;
        }

        public TextView getmPhone() {
            return mPhone;
        }

        public void setmPhone(TextView mPhone) {
            this.mPhone = mPhone;
        }

        public TextView getmQq() {
            return mQq;
        }

        public void setmQq(TextView mQq) {
            this.mQq = mQq;
        }

        public TextView getmSex() {
            return mSex;
        }

        public void setmSex(TextView mSex) {
            this.mSex = mSex;
        }

        public LinearLayout getmUserInfo() {
            return mUserInfo;
        }

        public void setmUserInfo(LinearLayout mUserInfo) {
            this.mUserInfo = mUserInfo;
        }

        public TextView getmFan() {
            return mFan;
        }

        public void setmFan(TextView mFan) {
            this.mFan = mFan;
        }

        public TextView getmFriend() {
            return mFriend;
        }

        public void setmFriend(TextView mFriend) {
            this.mFriend = mFriend;
        }
    }
}
