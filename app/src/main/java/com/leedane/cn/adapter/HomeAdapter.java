package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.activity.MainActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页显示文章列表的适配器类
 * Created by LeeDane on 2015/10/6.
 */
public class HomeAdapter extends BaseAdapter{

    public static final String TAG = "HomeAdapter";
    private NetworkImageLoader mNetworkImageLoader = null;
    private ListView mListView ;

    public List<BlogBean> mList;  //所获取的博客的结果集列表
    private Context mContext; //上下文对象
    private SpannableString mSpanTitle;

    public  HomeAdapter(){
        super();
    }

    public  HomeAdapter(List<BlogBean> list, Context context, ListView listView){
        super();
        this.mList = list;
        this.mContext = context;
        this.mListView = listView;
        mNetworkImageLoader = new NetworkImageLoader();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void refreshData(List<BlogBean> blogs){
        this.mList.clear();
        this.mList.addAll(blogs);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_listview, null);
            myHolder = new MyHolder();
            myHolder.setmTitle((TextView) convertView.findViewById(R.id.home_item_title));
            myHolder.setmImg((ImageView) convertView.findViewById(R.id.home_item_img));
            myHolder.setmAccount((TextView) convertView.findViewById(R.id.home_item_account));
            myHolder.setmTime((TextView) convertView.findViewById(R.id.home_item_time));
            myHolder.setmFrom((TextView) convertView.findViewById(R.id.home_item_from));
            myHolder.setmDigest((TextView) convertView.findViewById(R.id.home_item_digest));
            myHolder.setmTags((LinearLayout)convertView.findViewById(R.id.home_item_tags));
            convertView.setTag(myHolder);
        }else{
            myHolder = (MyHolder)convertView.getTag();
        }
        final BlogBean blogBean = mList.get(position);
        Log.i(TAG, "执行了getView()方法");
        String title = blogBean.getTitle();
       //mSpanTitle = new SpannableString(title);

        //设置字体大小（绝对值,单位：像素）
        //mSpanTitle.setSpan(new AbsoluteSizeSpan(70), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体
        //mSpanTitle.setSpan(new TypefaceSpan("monospace"), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        myHolder.getmTitle().setText(title);

        int bid = blogBean.getId();
        final String imgUrl = blogBean.getImgUrl();
        //final String imgUrl = ConstantsUtil.QINIU_CLOUD_SERVER + "head.jpg";
        //final String imgUrl = ConstantsUtil.DEFAULT_SERVER_URL + "leedane/download_executeDown.action";

        if(imgUrl.startsWith("http://") || imgUrl.startsWith("https://")){
            ImageCacheManager.loadImage(imgUrl, myHolder.getmImg(), BaseApplication.getDefaultImage(), BaseApplication.getErrorImage());
        }else{
            //从base64格式的字符串中截取
            String imageTag = imgUrl.substring(10,30) + blogBean.getId();
            mNetworkImageLoader.loadBase64Bitmap(imageTag, imgUrl, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String imageTag) {
                    ImageView imageView = (ImageView) mListView.findViewWithTag(imageTag);
                    if (imageBitmap == null && imageView != null) {
                        imageView.setImageResource(R.drawable.no_pic);
                        return;
                    }
                    if (imageView != null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });
        }
        myHolder.getmImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String picPath = blogBean.getImgUrl();
                if(StringUtil.isNotNull(picPath)){
                    List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();
                    ImageDetailBean imageDetailBean = new ImageDetailBean();
                    imageDetailBean.setPath(picPath);
                    list.add(imageDetailBean);
                    CommonHandler.startImageDetailActivity(mContext, list, 0);
                }else{
                    ToastUtil.success(mContext, "暂无图片");
                }
            }
        });

        final int userId = blogBean.getCreateUserId();
        myHolder.getmAccount().setText(Html.fromHtml("<font color=\"blue\">" + StringUtil.changeNotNull(blogBean.getAccount()) + "</font>"));
        myHolder.getmAccount().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, PersonalActivity.class);
                it.putExtra("userId", userId);
                mContext.startActivity(it);
            }
        });

        myHolder.getmFrom().setText("来自：" + StringUtil.changeNotNull(blogBean.getFroms()));

        String createTime = blogBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getmTime().setText("");
        }else{
            myHolder.getmTime().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getmDigest().setText(StringUtil.changeNotNull(blogBean.getDigest()) +"...");
        myHolder.getmTags().removeAllViewsInLayout();
        if(StringUtil.isNotNull(blogBean.getTag())){
            String[] tagArray = blogBean.getTag().split(",");
            for(int i = 0; i< tagArray.length; i++){
                myHolder.getmTags().addView(buildTagTextView(tagArray[i]), i);
            }
        }
        return convertView;
    }

    private TextView buildTagTextView(String tag){
        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);
        //这里的Textview的父layout是ListView，所以要用ListView.LayoutParams
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 10;
        layoutParams.rightMargin = 45;
        textView.setLayoutParams(layoutParams);
        textView.setPadding(20, 10, 20, 10);
        textView.setText(tag);
        int rn = (int) (Math.random() * MainActivity.bgColors.length);
        textView.setTextColor(mContext.getResources().getColor(R.color.white));
        textView.setBackgroundResource(MainActivity.bgColors[rn]);
        textView.setTextSize(14);

        return textView;
    }

    private class MyHolder{

        private TextView mTitle;
        private ImageView mImg;
        private TextView mAccount;
        private TextView mFrom;
        private TextView mTime;
        private TextView mDigest;
        private LinearLayout mTags;

        public TextView getmTitle() {
            return mTitle;
        }

        public void setmTitle(TextView mTitle) {
            this.mTitle = mTitle;
        }

        public ImageView getmImg() {
            return mImg;
        }

        public void setmImg(ImageView mImg) {
            this.mImg = mImg;
        }

        public TextView getmFrom() {
            return mFrom;
        }

        public void setmFrom(TextView mFrom) {
            this.mFrom = mFrom;
        }

        public TextView getmDigest() {
            return mDigest;
        }

        public void setmDigest(TextView mDigest) {
            this.mDigest = mDigest;
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

        public LinearLayout getmTags() {
            return mTags;
        }

        public void setmTags(LinearLayout mTags) {
            this.mTags = mTags;
        }
    }
}
