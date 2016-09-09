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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.activity.MainActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.adapter.BaseAdapter.BaseListAdapter;
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
public class HomeAdapter extends BaseListAdapter<BlogBean>{

    public static final String TAG = "HomeAdapter";
    private NetworkImageLoader mNetworkImageLoader = null;
    private ListView mListView ;

    private SpannableString mSpanTitle;
    private int lastPosition = -1;


    public  HomeAdapter(List<BlogBean> list, Context context, ListView listView){
        super(context, list);
        this.mListView = listView;
        mNetworkImageLoader = new NetworkImageLoader();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyHolder myHolder;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_home_listview, null);
            myHolder = new MyHolder();
            myHolder.title = (TextView) view.findViewById(R.id.home_item_title);
            myHolder.img = (ImageView) view.findViewById(R.id.home_item_img);
            myHolder.account = (TextView) view.findViewById(R.id.home_item_account);
            myHolder.time = (TextView) view.findViewById(R.id.home_item_time);
            myHolder.from = (TextView) view.findViewById(R.id.home_item_from);
            myHolder.digest = (TextView) view.findViewById(R.id.home_item_digest);
            myHolder.tags = (LinearLayout)view.findViewById(R.id.home_item_tags);
            view.setTag(myHolder);
        }else{
            myHolder = (MyHolder)view.getTag();
        }

        //setAnimation(convertView, position);
        final BlogBean blogBean = mDatas.get(position);
        Log.i(TAG, "执行了getView()方法");
        String title = blogBean.getTitle();
       //mSpanTitle = new SpannableString(title);

        //设置字体大小（绝对值,单位：像素）
        //mSpanTitle.setSpan(new AbsoluteSizeSpan(70), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体
        //mSpanTitle.setSpan(new TypefaceSpan("monospace"), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        myHolder.title.setText(title);

        int bid = blogBean.getId();
        final String imgUrl = blogBean.getImgUrl();
        //final String imgUrl = ConstantsUtil.QINIU_CLOUD_SERVER + "head.jpg";
        //final String imgUrl = ConstantsUtil.DEFAULT_SERVER_URL + "leedane/download/executeDown.action";

        if(StringUtil.isNotNull(imgUrl)){
            //myHolder.getmImg().setVisibility(View.VISIBLE);
            if(imgUrl.startsWith("http://") || imgUrl.startsWith("https://")){
                ImageCacheManager.loadImage(imgUrl, myHolder.img, BaseApplication.getDefaultImage(), BaseApplication.getErrorImage());
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
            myHolder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String picPath = blogBean.getImgUrl();
                    if (StringUtil.isNotNull(picPath)) {
                        List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();
                        ImageDetailBean imageDetailBean = new ImageDetailBean();
                        imageDetailBean.setPath(picPath);
                        list.add(imageDetailBean);
                        CommonHandler.startImageDetailActivity(mContext, list, 0);
                    } else {
                        ToastUtil.success(mContext, "暂无图片");
                    }
                }
            });
        }else{
            myHolder.img.setClickable(false);
            //myHolder.getmImg().setVisibility(View.GONE);
            myHolder.img.setImageBitmap(BaseApplication.getNotPicImage());
        }


        final int userId = blogBean.getCreateUserId();
        myHolder.account.setText(Html.fromHtml("<font color=\"blue\">" + StringUtil.changeNotNull(blogBean.getAccount()) + "</font>"));
        myHolder.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, PersonalActivity.class);
                it.putExtra("userId", userId);
                mContext.startActivity(it);
            }
        });

        myHolder.from.setText("来自：" + StringUtil.changeNotNull(blogBean.getFroms()));

        String createTime = blogBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.time.setText("");
        }else{
            myHolder.time.setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.digest.setText(StringUtil.changeNotNull(blogBean.getDigest()) + "...");
        myHolder.tags.removeAllViewsInLayout();
        if(StringUtil.isNotNull(blogBean.getTag())){
            String[] tagArray = blogBean.getTag().split(",");
            for(int i = 0; i< tagArray.length; i++){
                myHolder.tags.addView(buildTagTextView(tagArray[i]), i);
            }
        }
        return view;
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

    static class MyHolder{
        TextView title;
        ImageView img;
        TextView account;
        TextView from;
        TextView time;
        TextView digest;
        LinearLayout tags;
    }
}
