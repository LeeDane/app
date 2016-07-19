package com.leedane.cn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBgSelectWebBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 聊天背景网络图片的数据展示的adapter对象
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectWebAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChatBgSelectWebBean> mChatBgSelectWebBeans;

    public ChatBgSelectWebAdapter(Context context, List<ChatBgSelectWebBean> chatBgSelectWebBeans) {
        this.mContext = context;
        this.mChatBgSelectWebBeans = chatBgSelectWebBeans;
    }

    @Override
    public int getCount() {
        return mChatBgSelectWebBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatBgSelectWebBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup group) {
        final ChatBgSelectWebBean chatBgSelectWebBean = mChatBgSelectWebBeans.get(position);
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_base_gridview, null);
            viewHolder = new ViewHolder();
            ImageView imageView = (ImageView) view.findViewById(R.id.gridview_img);
            ViewGroup.LayoutParams para = imageView.getLayoutParams();
            para.height = 450;
            para.width = (BaseApplication.newInstance().getScreenWidthAndHeight()[0] -20)/3;//一屏幕显示3行;
            imageView.setLayoutParams(para);
            viewHolder.setmPath(imageView);
            viewHolder.setmIcon((ImageView) view.findViewById(R.id.gridview_icon));
            viewHolder.setmDownload((TextView)view.findViewById(R.id.gridview_download));
            viewHolder.setmShow((TextView)view.findViewById(R.id.gridview_show));
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        if(StringUtil.isNotNull(chatBgSelectWebBean.getPath())){
            ImageCacheManager.loadImage(chatBgSelectWebBean.getPath(), viewHolder.getmPath());
            viewHolder.getmPath().setVisibility(View.VISIBLE);
        }else{
            viewHolder.getmPath().setVisibility(View.GONE);
        }

        viewHolder.getmShow().setText(StringUtil.changeNotNull(chatBgSelectWebBean.getAccount()) +"  "+ RelativeDateFormat.format(DateUtil.stringToDate(chatBgSelectWebBean.getCreateTime())));

        if(chatBgSelectWebBean.getType() == 0){
            viewHolder.getmIcon().setImageResource(R.drawable.free);
        }else{
            viewHolder.getmIcon().setImageResource(R.drawable.charge);
        }

        if(chatBgSelectWebBean.getCreateUserId() == BaseApplication.getLoginUserId()){
            viewHolder.getmDownload().setText("我上传的");
        }else{
            if(chatBgSelectWebBean.isDownload()){
                viewHolder.getmDownload().setText(chatBgSelectWebBean.getAccount() +"上传的,已下载过");
            }else{
                viewHolder.getmDownload().setText(chatBgSelectWebBean.getAccount() +"上传的,还未下载");
            }
        }
        return view;
    }

    public void refreshData(List<ChatBgSelectWebBean> chatBgSelectWebBeans){
        this.mChatBgSelectWebBeans.clear();
        this.mChatBgSelectWebBeans.addAll(chatBgSelectWebBeans);
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        private TextView mShow;
        private TextView mDownload; //是否下载过
        private ImageView mPath;
        private ImageView mIcon; //是否免费

        public ImageView getmPath() {
            return mPath;
        }

        public void setmPath(ImageView mPath) {
            this.mPath = mPath;
        }

        public ImageView getmIcon() {
            return mIcon;
        }

        public void setmIcon(ImageView mIcon) {
            this.mIcon = mIcon;
        }

        public TextView getmShow() {
            return mShow;
        }

        public void setmShow(TextView mShow) {
            this.mShow = mShow;
        }

        public TextView getmDownload() {
            return mDownload;
        }

        public void setmDownload(TextView mDownload) {
            this.mDownload = mDownload;
        }
    }
}
