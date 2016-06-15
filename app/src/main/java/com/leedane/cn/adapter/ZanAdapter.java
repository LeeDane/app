package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.ZanBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;

import java.util.List;

/**
 * 赞列表数据展示的adapter对象
 * Created by LeeDane on 2016/4/5.
 */
public class ZanAdapter extends BaseAdapter{
    private Context mContext;
    private List<ZanBean> mZanBeans;

    public ZanAdapter(Context context, List<ZanBean> zanBeans) {
        this.mContext = context;
        this.mZanBeans = zanBeans;
    }

    @Override
    public int getCount() {
        return mZanBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mZanBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        ZanBean zanBean = mZanBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_zan_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.setmFrom((TextView) view.findViewById(R.id.zan_from));
            viewHolder.setmTime((TextView) view.findViewById(R.id.zan_time));
            /*viewHolder.setmUserName((TextView) view.findViewById(R.id.zan_user_name));
            viewHolder.setmUserPic((ImageView) view.findViewById(R.id.zan_user_pic));*/
            viewHolder.setmContent((TextView)view.findViewById(R.id.zan_content));
            viewHolder.setmSource((TextView)view.findViewById(R.id.zan_source));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.getmFrom().setText(StringUtil.changeNotNull(zanBean.getFroms()));
        viewHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(zanBean.getCreateTime())));
        /*viewHolder.getmUserName().setText(zanBean.getAccount());
        if(zanBean.getUserPicPath() != null)
            ImageCacheManager.loadImage(zanBean.getUserPicPath(), viewHolder.getmUserPic(), 30, 30);*/

        viewHolder.getmContent().setText(StringUtil.changeNotNull(zanBean.getContent()));
        if(StringUtil.isNotNull(zanBean.getSource())){
            viewHolder.getmSource().setText(zanBean.getSource());
            viewHolder.getmSource().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getmSource().setVisibility(View.GONE);
        }
        return view;
    }

    public void refreshData(List<ZanBean> zanBeans){
        this.mZanBeans.clear();
        this.mZanBeans.addAll(zanBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        /*private ImageView mUserPic;
        private TextView mUserName;*/
        private TextView mFrom;
        private TextView mTime;
        private TextView mContent;
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

        public TextView getmFrom() {
            return mFrom;
        }

        public void setmFrom(TextView mFrom) {
            this.mFrom = mFrom;
        }

        public TextView getmTime() {
            return mTime;
        }

        public void setmTime(TextView mTime) {
            this.mTime = mTime;
        }

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmSource() {
            return mSource;
        }

        public void setmSource(TextView mSource) {
            this.mSource = mSource;
        }
    }
}
