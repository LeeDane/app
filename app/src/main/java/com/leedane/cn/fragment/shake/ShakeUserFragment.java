package com.leedane.cn.fragment.shake;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.search.SearchUserAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.UserBean;
import com.leedane.cn.bean.search.HttpResponseSearchUserBean;
import com.leedane.cn.bean.search.SearchUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.SearchHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 摇一摇用户的fragment类
 * Created by LeeDane on 2016/12/21.
 */
public class ShakeUserFragment extends Fragment{

    public static final String TAG = "ShakeUserFragment";
    private Context mContext;
    private View mRootView;
    private String data;

    public ShakeUserFragment(){
    }

    public static final ShakeUserFragment newInstance(Bundle bundle){
        ShakeUserFragment fragment = new ShakeUserFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.one_user_layout, container,
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
                final UserBean userBean = BeanConvertUtil.strUserBean(data);
                ImageView userPic = (ImageView)mRootView.findViewById(R.id.one_user_pic);
                if(StringUtil.isNotNull(userBean.getUserPicPath())){
                    ImageCacheManager.loadImage(userBean.getUserPicPath(), userPic);
                }
                if(StringUtil.isNotNull(userBean.getPersonalIntroduction())){
                    ((TextView)mRootView.findViewById(R.id.one_user_introduction)).setText(userBean.getPersonalIntroduction());
                }

                if(StringUtil.isNotNull(userBean.getAccount())){
                    ((TextView)mRootView.findViewById(R.id.one_user_name)).setText(userBean.getAccount());
                }

                if(StringUtil.isNotNull(userBean.getBirthDay())){
                    ((TextView)mRootView.findViewById(R.id.one_user_birth_day)).setText(userBean.getBirthDay());
                }

                if(StringUtil.isNotNull(userBean.getMobilePhone())){
                    ((TextView)mRootView.findViewById(R.id.one_user_phone)).setText(userBean.getMobilePhone());
                }

                if(StringUtil.isNotNull(userBean.getSex())){
                    ((TextView)mRootView.findViewById(R.id.one_user_sex)).setText(userBean.getSex());
                }

                if(StringUtil.isNotNull(userBean.getEmail())){
                    ((TextView)mRootView.findViewById(R.id.one_user_email)).setText(userBean.getEmail());
                }

                if(StringUtil.isNotNull(userBean.getQq())){
                    ((TextView)mRootView.findViewById(R.id.one_user_qq)).setText(userBean.getQq());
                }

                if(userBean.getId() > 0){
                    mRootView.findViewById(R.id.one_user_root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonHandler.startPersonalActivity(mContext, userBean.getId());
                        }
                    });
                }

                //隐藏没有必要的
                mRootView.findViewById(R.id.one_user_friend).setVisibility(View.GONE);
                mRootView.findViewById(R.id.one_user_fan).setVisibility(View.GONE);

            }catch (Exception e){
                e.printStackTrace();
                ToastUtil.failure(mContext, "摇到用户数据有误："+e.getMessage());
            }
        }else{
            ToastUtil.failure(mContext, "没有摇到用户");
        }
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
