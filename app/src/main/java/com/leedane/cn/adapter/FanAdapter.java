package com.leedane.cn.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.FanBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.FanHandler;
import com.leedane.cn.leedaneAPP.R;
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
 * 粉丝列表数据展示的adapter对象
 * Created by LeeDane on 2016/3/11.
 */
public class FanAdapter extends BaseAdapter implements TaskListener{
    private Context mContext;
    private List<FanBean> mFanBeans;

    int userId = 0;
    public FanAdapter(Context context, List<FanBean> fanBeans) {
        this.mContext = context;
        this.mFanBeans = fanBeans;
        userId = BaseApplication.getLoginUserId();
    }
    @Override
    public int getCount() {
        return mFanBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mFanBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final FanBean fanBean = mFanBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fan_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmUserName((TextView) view.findViewById(R.id.fan_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.fan_user_pic));
            viewHolder.setmTime((TextView) view.findViewById(R.id.fan_create_time));
            viewHolder.setmFanShow((TextView)view.findViewById(R.id.fan_add_fan));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmUserName().setText(fanBean.getAccount());
        viewHolder.getmUserName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, fanBean.getToUserId());
            }
        });
        if(StringUtil.isNotNull(fanBean.getUserPicPath())){
            ImageCacheManager.loadImage(fanBean.getUserPicPath(), viewHolder.getmUserPic());
            viewHolder.getmUserPic().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, fanBean.getToUserId());
                }
            });
        }else{
            viewHolder.getmUserPic().setImageResource(R.drawable.no_pic);
        }

        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(fanBean.getCreateTime())));
        if(userId == fanBean.getToUserId()){
            viewHolder.getmFanShow().setVisibility(View.GONE);
        }else{
            viewHolder.getmFanShow().setVisibility(View.VISIBLE);
            if(fanBean.isAttention() && fanBean.isFan()){
                viewHolder.getmFanShow().setText(mContext.getResources().getString(R.string.fan_each_other));
            }else{
                if(fanBean.isAttention() && !fanBean.isFan()){
                    viewHolder.getmFanShow().setText(mContext.getResources().getString(R.string.personal_is_fan));
                }else if(!fanBean.isAttention() && fanBean.isFan()){
                    viewHolder.getmFanShow().setText(mContext.getResources().getString(R.string.personal_add_fan));
                }
            }
            viewHolder.getmFanShow().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关注TA
                    if(!fanBean.isAttention()){
                        FanHandler.addAttention(FanAdapter.this, fanBean.getToUserId());
                        //取消关注
                    }else{
                        FanHandler.cancelAttention(FanAdapter.this, fanBean.getToUserId());
                    }
                }
            });
        }

        return view;
    }

    public void refreshData(List<FanBean> fanBeans){
        this.mFanBeans.clear();
        this.mFanBeans.addAll(fanBeans);
        this.notifyDataSetChanged();
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
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

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

    private class ViewHolder{
        private ImageView mUserPic;
        private TextView mUserName;
        private TextView mTime;
        private TextView mFanShow;

        public ImageView getmUserPic() {
            return mUserPic;
        }

        public void setmUserPic(ImageView mUserPic) {
            this.mUserPic = mUserPic;
        }

        public TextView getmUserName() {
            return mUserName;
        }

        public void setmUserName(TextView mUserName) {
            this.mUserName = mUserName;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmFanShow() {
            return mFanShow;
        }

        public void setmFanShow(TextView mFanShow) {
            this.mFanShow = mFanShow;
        }
    }
}
