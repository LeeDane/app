package com.leedane.cn.fragment.shake;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.search.SearchBlogAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.search.HttpResponseSearchBlogBean;
import com.leedane.cn.bean.search.SearchBlogBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.SearchHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 摇一摇博客的fragment类
 * Created by LeeDane on 2016/12/21.
 */
public class ShakeBlogFragment extends Fragment{

    public static final String TAG = "ShakeBlogFragment";
    private Context mContext;
    private View mRootView;

    private String data;
    public ShakeBlogFragment(){
    }

    public static final ShakeBlogFragment newInstance(Bundle bundle){
        ShakeBlogFragment fragment = new ShakeBlogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.one_blog_layout, container,
                    false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();

        Bundle bundle = getArguments();
        if(bundle != null){
            this.data = bundle.getString("data");
        }

        if(StringUtil.isNotNull(data)){
            try {
                final BlogBean blogBean = BeanConvertUtil.strBlogBean(data);
                CircularImageView userPic = (CircularImageView)mRootView.findViewById(R.id.one_blog_user_pic);
                if(StringUtil.isNotNull(blogBean.getUserPicPath())){
                    ImageCacheManager.loadImage(blogBean.getUserPicPath(), userPic);
                }
                if(StringUtil.isNotNull(blogBean.getTitle())){
                    ((TextView)mRootView.findViewById(R.id.one_blog_title)).setText(blogBean.getTitle());
                }else{
                    mRootView.findViewById(R.id.one_blog_title).setVisibility(View.GONE);
                }

                if(StringUtil.isNotNull(blogBean.getDigest())){
                    ((TextView)mRootView.findViewById(R.id.one_blog_digest)).setText(blogBean.getDigest());
                }else{
                    mRootView.findViewById(R.id.one_blog_digest).setVisibility(View.GONE);
                }

                if(StringUtil.isNotNull(blogBean.getAccount())){
                    ((TextView)mRootView.findViewById(R.id.one_blog_user_name)).setText(AppUtil.textParsing(mContext, blogBean.getAccount()));
                }

                if(blogBean.isHasImg() && StringUtil.isNotNull(blogBean.getImgUrl())){
                    mRootView.findViewById(R.id.one_blog_img_container).setVisibility(View.VISIBLE);
                    ImageUtil.addImages(mContext, blogBean.getImgUrl(), ((LinearLayout) mRootView.findViewById(R.id.one_blog_img_container)));
                }else{
                    mRootView.findViewById(R.id.one_blog_img_container).setVisibility(View.GONE);
                }

                if(blogBean.getId() > 0){
                    mRootView.findViewById(R.id.one_blog_root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("title", blogBean.getTitle());
                            CommonHandler.startDetailActivity(mContext, "t_blog", blogBean.getId(), params);
                        }
                    });
                }

                //隐藏没有必要的
                mRootView.findViewById(R.id.one_blog_source).setVisibility(View.GONE);
            }catch (Exception e){
                e.printStackTrace();
                ToastUtil.failure(mContext, "摇到博客数据有误："+e.getMessage());
            }
        }else{
            ToastUtil.failure(mContext, "没有摇到博客");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 统一获取string资源的值
     * @param resourceId
     * @return
     */
    public String getStringResource(int resourceId){
        if(mContext == null){
            return BaseApplication.newInstance().getResources().getString(resourceId);
        }
        return mContext.getResources().getString(resourceId);
    }
}
