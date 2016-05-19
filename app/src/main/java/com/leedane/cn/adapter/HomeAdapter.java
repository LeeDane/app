package com.leedane.cn.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leedane.cn.activity.ImageDetailActivity;
import com.leedane.cn.activity.PersonalActivity;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;

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
            myHolder.setHome_item_title((TextView)convertView.findViewById(R.id.home_item_title));
            myHolder.setHome_item_img((ImageView) convertView.findViewById(R.id.home_item_img));
            myHolder.setHome_item_account((TextView) convertView.findViewById(R.id.home_item_account));
            myHolder.setHome_item_time((TextView)convertView.findViewById(R.id.home_item_time));
            myHolder.setHome_item_from((TextView) convertView.findViewById(R.id.home_item_from));
            myHolder.setHome_item_digest((TextView)convertView.findViewById(R.id.home_item_digest));
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

        myHolder.getHome_item_title().setText(title);

        int bid = blogBean.getId();
        final String imgUrl = blogBean.getImgUrl();
        //final String imgUrl = ConstantsUtil.QINIU_CLOUD_SERVER + "head.jpg";
        //final String imgUrl = ConstantsUtil.DEFAULT_SERVER_URL + "leedane/download_executeDown.action";

        if(imgUrl.startsWith("http://") || imgUrl.startsWith("https://")){
            ImageCacheManager.loadImage(imgUrl, myHolder.getHome_item_img(), BaseApplication.getDefaultImage(), BaseApplication.getErrorImage());
        }else{
            //从base64格式的字符串中截取
            String imageTag = imgUrl.substring(10,30) + blogBean.getId();
            mNetworkImageLoader.loadBase64Bitmap(imageTag, imgUrl, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String imageTag) {
                    ImageView imageView = (ImageView) mListView.findViewWithTag(imageTag);
                    if (imageBitmap == null && imageView != null) {
                        imageView.setImageResource(R.drawable.error_cat);
                        return;
                    }
                    if (imageView != null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });
        }
        myHolder.getHome_item_img().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String picPath = blogBean.getImgUrl();
                if(StringUtil.isNotNull(picPath)){
                    List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();

                    ImageDetailBean imageDetailBean = new ImageDetailBean();
                    imageDetailBean.setPath(picPath);
                    list.add(imageDetailBean);

                    Intent itImageDetail = new Intent();
                    itImageDetail.setClass(mContext, ImageDetailActivity.class);
                    Type type = new TypeToken<ArrayList<ImageDetailBean>>() {
                    }.getType();
                    String json = new Gson().toJson(list, type);
                    itImageDetail.putExtra("ImageDetailBeans", json);
                    itImageDetail.putExtra("current", 0);
                    mContext.startActivity(itImageDetail);
                }
            }
        });

        final int userId = blogBean.getCreateUserId();
        myHolder.getHome_item_account().setText(Html.fromHtml("<font color=\"blue\">" + StringUtil.changeNotNull(blogBean.getAccount()) + "</font>"));
        myHolder.getHome_item_account().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击作者的ID" + userId, Toast.LENGTH_LONG).show();
                Intent it = new Intent(mContext, PersonalActivity.class);
                it.putExtra("userId", userId);
                mContext.startActivity(it);
            }
        });

        myHolder.getHome_item_from().setText("来自：" + StringUtil.changeNotNull(blogBean.getFroms()));

        String createTime = blogBean.getCreateTime();
        if(StringUtil.isNull(createTime)){
            myHolder.getHome_item_time().setText("");
        }else{
            myHolder.getHome_item_time().setText(RelativeDateFormat.format(DateUtil.stringToDate(createTime)));
        }

        myHolder.getHome_item_digest().setText(blogBean.getDigest());
        return convertView;
    }

    private class MyHolder{

        private TextView home_item_title;
        private ImageView home_item_img;
        private TextView home_item_account;
        private TextView home_item_from;
        private TextView home_item_time;
        private TextView home_item_digest;

        public TextView getHome_item_title() {
            return home_item_title;
        }

        public void setHome_item_title(TextView home_item_title) {
            this.home_item_title = home_item_title;
        }
        public ImageView getHome_item_img() {
            return home_item_img;
        }

        public void setHome_item_img(ImageView home_item_img) {
            this.home_item_img = home_item_img;
        }

        public TextView getHome_item_from() {
            return home_item_from;
        }

        public void setHome_item_from(TextView home_item_from) {
            this.home_item_from = home_item_from;
        }

        public TextView getHome_item_digest() {
            return home_item_digest;
        }

        public void setHome_item_digest(TextView home_item_digest) {
            this.home_item_digest = home_item_digest;
        }

        public TextView getHome_item_account() {
            return home_item_account;
        }

        public void setHome_item_account(TextView home_item_account) {
            this.home_item_account = home_item_account;
        }

        public TextView getHome_item_time() {
            return home_item_time;
        }

        public void setHome_item_time(TextView home_item_time) {
            this.home_item_time = home_item_time;
        }
    }
}
