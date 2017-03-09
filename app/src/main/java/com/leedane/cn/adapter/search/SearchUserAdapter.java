package com.leedane.cn.adapter.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.one_user_layout, null);
            myHolder = new MyHolder();
            TextView time = (TextView) convertView.findViewById(R.id.one_user_time);
            time.setSelected(true);
            myHolder.createTime = time;
            myHolder.introduction = (TextView) convertView.findViewById(R.id.one_user_introduction);
            myHolder.picPath = (ImageView) convertView.findViewById(R.id.one_user_pic);
            myHolder.account = (TextView) convertView.findViewById(R.id.one_user_name);
            TextView birthDay = (TextView) convertView.findViewById(R.id.one_user_birth_day);
            birthDay.setSelected(true);
            myHolder.birthDay = birthDay;
            TextView email = (TextView) convertView.findViewById(R.id.one_user_email);
            email.setSelected(true);
            myHolder.email = email;
            TextView phone = (TextView) convertView.findViewById(R.id.one_user_phone);
            phone.setSelected(true);
            myHolder.phone = phone;
            TextView qq = (TextView) convertView.findViewById(R.id.one_user_qq);
            qq.setSelected(true);
            myHolder.qq = qq;
            myHolder.sex = (TextView) convertView.findViewById(R.id.one_user_sex);
            myHolder.fan = (TextView) convertView.findViewById(R.id.one_user_fan);
            myHolder.friend = (TextView) convertView.findViewById(R.id.one_user_friend);
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        final SearchUserBean searchUserBean = mSearchUserBeans.get(position);

        if(searchUserBean.getId() == loginUserId){
            myHolder.fan.setVisibility(View.GONE);
            myHolder.friend.setVisibility(View.GONE);
        }else{
            myHolder.fan.setVisibility(View.VISIBLE);
            myHolder.friend.setVisibility(View.VISIBLE);
            if(searchUserBean.isFan()){//已经是粉丝
                myHolder.fan.setText(mContext.getResources().getString(R.string.personal_is_fan));
            }else{//未关注
                myHolder.fan.setText(mContext.getResources().getString(R.string.personal_add_fan));
            }
            myHolder.fan.setOnClickListener(new View.OnClickListener() {
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
                myHolder.friend.setText(mContext.getResources().getString(R.string.personal_is_friend));
            }else{//未添加好友
                myHolder.friend.setText(mContext.getResources().getString(R.string.personal_add_friend));
            }
            myHolder.friend.setOnClickListener(new View.OnClickListener() {
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
            myHolder.createTime.setText("");
        }else{
            myHolder.createTime.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.introduction.setText("简介:"+StringUtil.changeNotNull(searchUserBean.getPersonalIntroduction()));
        myHolder.account.setText(StringUtil.changeNotNull(searchUserBean.getAccount()));
        myHolder.birthDay.setText("生日:" +StringUtil.changeNotNull(searchUserBean.getBirthDay()));
        myHolder.email.setText("邮件:"+StringUtil.changeNotNull(searchUserBean.getEmail()));
        myHolder.phone.setText("手机:"+ StringUtil.changeNotNull(searchUserBean.getMobilePhone()));
        if(StringUtil.isNotNull(searchUserBean.getUserPicPath())){
            ImageCacheManager.loadImage(searchUserBean.getUserPicPath(), myHolder.picPath, 45, 45);
        }
        myHolder.qq.setText("QQ:"+StringUtil.changeNotNull(searchUserBean.getQq()));
        myHolder.sex.setText("性别:"+StringUtil.changeNotNull(searchUserBean.getSex()));

        myHolder.picPath.setOnClickListener(new View.OnClickListener() {
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
        /**
         * 搜索用户的账号
         */
        private TextView account;

        /**
         * 搜索用户的头像路径
         */
        private ImageView picPath;

        /**
         * 搜索用户的创建时间
         */
        private TextView createTime;

        private TextView introduction;

        private TextView birthDay;

        private TextView phone;

        private TextView sex;

        private TextView email;

        private TextView qq;

        private TextView fan;

        private TextView friend;
    }
}
