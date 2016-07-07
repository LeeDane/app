package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.AttentionBean;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 关注列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/6.
 */
public class AttentionAdapter extends BaseAdapter{
    private Context mContext;
    private List<AttentionBean> mAttentionBeans;

    public AttentionAdapter(Context context, List<AttentionBean> attentionBeans) {
        this.mContext = context;
        this.mAttentionBeans = attentionBeans;
    }

    @Override
    public int getCount() {
        return mAttentionBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mAttentionBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        AttentionBean attentionBean = mAttentionBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_attention_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmTime((TextView) view.findViewById(R.id.attention_time));
            /*viewHolder.setmUserName((TextView) view.findViewById(R.id.attention_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.attention_user_pic));*/
            viewHolder.setmSource((TextView)view.findViewById(R.id.attention_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(attentionBean.getCreateTime())));
        /*viewHolder.getmUserName().setText(attentionBean.getAccount());
        if(attentionBean.getUserPicPath() != null)
            ImageCacheManager.loadImage(attentionBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);*/

        if(StringUtil.isNotNull(attentionBean.getSource())){
            viewHolder.getmSource().setText(attentionBean.getSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
            AppUtil.textviewShowImg(mContext, viewHolder.getmSource());
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }
        return view;
    }

    public void refreshData(List<AttentionBean> attentionBeans){
        this.mAttentionBeans.clear();
        this.mAttentionBeans.addAll(attentionBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        /*private ImageView mUserPic;
        private TextView mUserName;*/
        private TextView mTime;
        private TextView mSource;

        /*public ImageView getmUserPic() {
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
        }*/

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }
    }
}
