package com.leedane.cn.fragment.shake;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 摇一摇心情的fragment类
 * Created by LeeDane on 2016/12/21.
 */
public class ShakeMoodFragment extends Fragment{

    public static final String TAG = "SearchMoodFragment";
    private Context mContext;
    private View mRootView;
    private String data;

    public ShakeMoodFragment(){
    }

    public static final ShakeMoodFragment newInstance(Bundle bundle){
        ShakeMoodFragment fragment = new ShakeMoodFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.one_mood_layout, container,
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
                final MoodBean moodBean = BeanConvertUtil.strMoodBean(data);
                CircularImageView userPic = (CircularImageView)mRootView.findViewById(R.id.one_mood_user_pic);
                if(StringUtil.isNotNull(moodBean.getUserPicPath())){
                    ImageCacheManager.loadImage(moodBean.getUserPicPath(), userPic);
                }
                if(StringUtil.isNotNull(moodBean.getContent())){
                    ((TextView)mRootView.findViewById(R.id.one_mood_content)).setText(moodBean.getContent());
                }

                if(StringUtil.isNotNull(moodBean.getAccount())){
                    ((TextView)mRootView.findViewById(R.id.one_mood_user_name)).setText(AppUtil.textParsing(mContext, moodBean.getAccount()));
                }

                if(moodBean.isHasImg() && StringUtil.isNotNull(moodBean.getImgs())){
                    mRootView.findViewById(R.id.one_mood_img_container).setVisibility(View.VISIBLE);
                    ImageUtil.addImages(mContext, moodBean.getImgs(), ((LinearLayout)mRootView.findViewById(R.id.one_mood_img_container)));
                }else{
                    mRootView.findViewById(R.id.one_mood_img_container).setVisibility(View.GONE);
                }

                if(moodBean.getId() > 0){
                    mRootView.findViewById(R.id.one_mood_root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonHandler.startDetailActivity(mContext, "t_mood", moodBean.getId(), null);
                        }
                    });
                }
                //隐藏没必要的
                mRootView.findViewById(R.id.one_mood_froms).setVisibility(View.GONE);

            }catch (Exception e){
                e.printStackTrace();
                ToastUtil.failure(mContext, "摇到心情数据有误："+e.getMessage());
            }
        }else{
            ToastUtil.failure(mContext, "没有摇到心情");
        }
        //
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
