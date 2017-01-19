package com.leedane.cn.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.SearchBlogBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.List;

/**
 * 搜索博客列表的适配器
 * Created by LeeDane on 2016/5/22.
 */
public class SearchBlogAdapter extends BaseAdapter{

    public static final String TAG = "SearchBlogAdapter";

    public List<SearchBlogBean> mSearchBlogBeans;  //所有聊天列表
    private Context mContext; //上下文对象

    public SearchBlogAdapter(Context context, List<SearchBlogBean> searchBlogBeans){
        super();
        this.mSearchBlogBeans = searchBlogBeans;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mSearchBlogBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchBlogBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<SearchBlogBean> searchBlogBeans){
        this.mSearchBlogBeans.clear();
        this.mSearchBlogBeans.addAll(searchBlogBeans);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.one_blog_layout, null);
            myHolder = new MyHolder();
            myHolder.createTime = (TextView) convertView.findViewById(R.id.one_blog_time);
            myHolder.account = (TextView) convertView.findViewById(R.id.one_blog_user_name);
            myHolder.picPath = (ImageView) convertView.findViewById(R.id.one_blog_user_pic);
            myHolder.source = (TextView) convertView.findViewById(R.id.one_blog_source);
            myHolder.title = (TextView) convertView.findViewById(R.id.one_blog_title);
            myHolder.digest = (TextView) convertView.findViewById(R.id.one_blog_digest);
            myHolder.imgContainer = (LinearLayout) convertView.findViewById(R.id.one_blog_img_container);
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        final SearchBlogBean searchBlogBean = mSearchBlogBeans.get(position);

        String createTime = searchBlogBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.createTime.setText("");
        }else{
            myHolder.createTime.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.account.setText(StringUtil.changeNotNull(searchBlogBean.getAccount()));
        if(StringUtil.isNotNull(searchBlogBean.getUserPicPath()))
            ImageCacheManager.loadImage(searchBlogBean.getUserPicPath(), myHolder.picPath, 45, 45);

        myHolder.picPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHandler.startPersonalActivity(mContext, searchBlogBean.getCreateUserId());
            }
        });
        myHolder.source.setText(StringUtil.changeNotNull(searchBlogBean.getSource()));
        myHolder.title.setText(StringUtil.changeNotNull(searchBlogBean.getTitle()));
        myHolder.digest.setText(StringUtil.changeNotNull(searchBlogBean.getDigest()) + "...");
        if(StringUtil.isNotNull(searchBlogBean.getImgUrl())){
            myHolder.imgContainer.setVisibility(View.VISIBLE);
            ImageUtil.addImages(mContext, searchBlogBean.getImgUrl(), myHolder.imgContainer);
        }else{
            myHolder.imgContainer.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class MyHolder{
        /**
         * 搜索博客的用户的账号
         */
        private TextView account;

        /**
         * 搜索心情的用户的头像路径
         */
        private ImageView picPath;

        /**
         * 搜索博客的来自
         */
        private TextView source;

        /**
         * 搜索博客的创建时间
         */
        private TextView createTime;

        /**
         * 博客的标题
         */
        private TextView title;

        /**
         * 博客的内容
         */
        private TextView digest;

        /**
         * 搜索博客的主图
         */
        private LinearLayout imgContainer;
    }
}
