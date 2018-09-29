package com.leedane.cn.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.FanBean;
import com.leedane.cn.handler.FanHandler;
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
public class FanAdapter extends BaseListAdapter<FanBean> implements TaskListener{
    int userId = 0;
    public FanAdapter(Context context, List<FanBean> fanBeans) {
        super(context, fanBeans);
        userId = BaseApplication.getLoginUserId();
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final FanBean fanBean = mDatas.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fan_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) view.findViewById(R.id.fan_user_name);
            viewHolder.userPic = (ImageView) view.findViewById(R.id.fan_user_pic);
            viewHolder.time = (TextView) view.findViewById(R.id.fan_create_time);
            viewHolder.fanShow = (TextView)view.findViewById(R.id.fan_add_fan);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.userName.setText(fanBean.getAccount());
        /*viewHolder.getmUserName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, fanBean.getToUserId());
            }
        });*/
        if(StringUtil.isNotNull(fanBean.getUserPicPath())){
            ImageCacheManager.loadImage(fanBean.getUserPicPath(), viewHolder.userPic);
            /*viewHolder.getmUserPic().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(mContext, fanBean.getToUserId());
                }
            });*/
        }else{
            viewHolder.userPic.setImageResource(R.drawable.no_pic);
        }

        viewHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(fanBean.getCreateTime())));
        if(userId == fanBean.getToUserId()){
            viewHolder.fanShow.setVisibility(View.GONE);
        }else{
            viewHolder.fanShow.setVisibility(View.VISIBLE);
            if(fanBean.isAttention() && fanBean.isFan()){
                viewHolder.fanShow.setText(mContext.getResources().getString(R.string.fan_each_other));
            }else{
                if(fanBean.isAttention() && !fanBean.isFan()){
                    viewHolder.fanShow.setText(mContext.getResources().getString(R.string.personal_is_fan));
                }else if(!fanBean.isAttention() && fanBean.isFan()){
                    viewHolder.fanShow.setText(mContext.getResources().getString(R.string.personal_add_fan));
                }
            }
            viewHolder.fanShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关注TA
                    if (!fanBean.isAttention()) {
                        FanHandler.addAttention(FanAdapter.this, fanBean.getToUserId());
                        //取消关注
                    } else {
                        FanHandler.cancelAttention(FanAdapter.this, fanBean.getToUserId());
                    }
                }
            });
        }
        //设置动画效果
        setAnimation(view, position);
        return view;
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

                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(mContext, jsonObject);
                }else{
                    ToastUtil.failure(mContext, jsonObject);
                }
            }else  if(type == TaskType.CANCEL_FAN){//取消关注
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
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

    static class ViewHolder{
        ImageView userPic;
        TextView userName;
        TextView time;
        TextView fanShow;
    }
}
