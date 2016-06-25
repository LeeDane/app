package com.leedane.cn.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.SearchMoodBean;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 搜索心情列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchMoodAdapter extends BaseAdapter{

    public static final String TAG = "SearchMoodAdapter";

    public List<SearchMoodBean> mSearchMoodBeans;  //所有聊天列表
    private Context mContext; //上下文对象

    public SearchMoodAdapter(Context context, List<SearchMoodBean> searchMoodBeans){
        super();
        this.mSearchMoodBeans = searchMoodBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchMoodBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchMoodBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchMoodBean> searchMoodBeans){
        this.mSearchMoodBeans.clear();
        this.mSearchMoodBeans.addAll(searchMoodBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_mood_listview, null);
            myHolder = new MyHolder();
            myHolder.setmCreateTime((TextView) convertView.findViewById(R.id.search_mood_time));
            myHolder.setmAccount((TextView) convertView.findViewById(R.id.search_mood_user_name));
            myHolder.setmPicPath((ImageView) convertView.findViewById(R.id.search_mood_user_pic));
            myHolder.setmFroms((TextView) convertView.findViewById(R.id.search_mood_froms));
            myHolder.setmContent((TextView) convertView.findViewById(R.id.search_mood_content));
            myHolder.setmMainImg((ImageView) convertView.findViewById(R.id.search_mood_img_main));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        SearchMoodBean searchMoodBean = mSearchMoodBeans.get(position);

        String createTime = searchMoodBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getmCreateTime().setText("");
        }else{
            myHolder.getmCreateTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getmAccount().setText(StringUtil.changeNotNull(searchMoodBean.getAccount()));
        if(StringUtil.isNotNull(searchMoodBean.getUserPicPath()))
            ImageCacheManager.loadImage(searchMoodBean.getUserPicPath(), myHolder.getmPicPath(), 45, 45);
        myHolder.getmFroms().setText("来自："+StringUtil.changeNotNull(searchMoodBean.getFroms()));
        myHolder.getmContent().setText(StringUtil.changeNotNull(searchMoodBean.getContent()));
        if(StringUtil.isNotNull(searchMoodBean.getImgs())){
            myHolder.getmMainImg().setVisibility(View.VISIBLE);
            ImageCacheManager.loadImage(searchMoodBean.getImgs(), myHolder.getmMainImg(), 100, 50);
        }else{
            myHolder.getmMainImg().setVisibility(View.GONE);
        }
        return convertView;
    }

    private class MyHolder{
        /**
         * 搜索心情的用户的账号
         */
        private TextView mAccount;

        /**
         * 搜索心情的用户的头像路径
         */
        private ImageView mPicPath;

        /**
         * 搜索心情的来自
         */
        private TextView mFroms;

        /**
         * 搜索心情的创建时间
         */
        private TextView mCreateTime;

        /**
         * 心情的内容
         */
        private TextView mContent;

        /**
         * 搜索心情的主图
         */
        private ImageView mMainImg;

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

        public TextView getmContent() {
            return mContent;
        }

        public void setmContent(TextView mContent) {
            this.mContent = mContent;
        }

        public TextView getmFroms() {
            return mFroms;
        }

        public void setmFroms(TextView mFroms) {
            this.mFroms = mFroms;
        }

        public ImageView getmMainImg() {
            return mMainImg;
        }

        public void setmMainImg(ImageView mMainImg) {
            this.mMainImg = mMainImg;
        }
    }
}
