package com.leedane.cn.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leedane.cn.app.R;
import com.leedane.cn.util.BitmapUtil;

/**
 * 从本地获取聊天背景图片的fragment类
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectSystemFragment extends Fragment {

    public static final String TAG = "ChatBgSelectSystemFragment";
    private Context mContext;
    private ImageView mImageView;
    private View mRootView;

    private String imagePath;

    public ChatBgSelectSystemFragment(){
    }

    /**
     * 构建Frament对象
     * @param bundle
     * @return
     */
    public static final ChatBgSelectSystemFragment newInstance(Bundle bundle){
        ChatBgSelectSystemFragment fragment = new ChatBgSelectSystemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_chat_bg_system, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            imagePath = bundle.getString("imagePath");
        }
        if(mContext == null)
            mContext = getActivity();

        mImageView = (ImageView)mRootView.findViewById(R.id.chat_bg_system_image);
        Bitmap bitmap = BitmapUtil.getSmallBitmap(mContext, imagePath, 300, 300);
        mImageView.setImageBitmap(bitmap);
    }
}
