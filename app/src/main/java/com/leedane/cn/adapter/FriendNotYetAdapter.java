package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.FriendBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 暂时还未是好友列表数据展示的adapter对象
 * Created by LeeDane on 2016/6/23.
 */
public class FriendNotYetAdapter extends BaseAdapter{
    private Context mContext;
    private List<FriendBean> mFriendBeans;
    public FriendNotYetAdapter(Context context, List<FriendBean> friendBeans) {
        this.mContext = context;
        this.mFriendBeans = friendBeans;
    }

    @Override
    public int getCount() {
        return mFriendBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mFriendBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final FriendBean friendBean = mFriendBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_friend_no_yet_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.friend_no_yet_user_pic));
            viewHolder.setmAccount((TextView) view.findViewById(R.id.friend_no_yet_user_name));
            viewHolder.setmTime((TextView) view.findViewById(R.id.friend_no_yet_time));
            TextView introduce = (TextView)view.findViewById(R.id.friend_no_yet_introduce);
            introduce.setSelected(true);
            viewHolder.setmIntroduce(introduce);
            viewHolder.setmOperate((TextView) view.findViewById(R.id.friend_no_yet_operate));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(friendBean.getUserPicPath()))
            ImageCacheManager.loadImage(friendBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);
        if(StringUtil.isNotNull(friendBean.getAccount()) && !friendBean.getAccount().equals(friendBean.getRemark())){
            viewHolder.getmAccount().setText(friendBean.getAccount() + (StringUtil.isNotNull(friendBean.getRemark())? "(" + friendBean.getRemark() + ")" : ""));
        }else{
            viewHolder.getmAccount().setText(StringUtil.changeNotNull(friendBean.getRemark()));
        }
        if(StringUtil.isNotNull(friendBean.getCreateTime()))
            viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(friendBean.getCreateTime())));
        else
            viewHolder.getmTime().setText("");
        viewHolder.getmIntroduce().setText(StringUtil.changeNotNull(friendBean.getIntroduce()));

        if(friendBean.getStatus() == 0){
            viewHolder.getmOperate().setText("等待确认");
        }else{
            viewHolder.getmOperate().setText("同意添加");
        }
        viewHolder.getmOperate().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.success(mContext, "点击啦:"+friendBean.getStatus());
            }
        });
        return view;
    }

    public void refreshData(List<FriendBean> friendBeans){
        this.mFriendBeans.clear();
        this.mFriendBeans.addAll(friendBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private ImageView mUserPic;
        private TextView mAccount;
        private TextView mTime;
        private TextView mIntroduce;
        private TextView mOperate;

        public ImageView getmUserPic() {
            return mUserPic;
        }

        public void setmUserPic(ImageView mUserPic) {
            this.mUserPic = mUserPic;
        }

        public TextView getmAccount() {
            return mAccount;
        }

        public void setmAccount(TextView mAccount) {
            this.mAccount = mAccount;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmIntroduce() {
            return mIntroduce;
        }

        public void setmIntroduce(TextView mIntroduce) {
            this.mIntroduce = mIntroduce;
        }

        public TextView getmOperate() {
            return mOperate;
        }

        public void setmOperate(TextView mOperate) {
            this.mOperate = mOperate;
        }
    }
}
