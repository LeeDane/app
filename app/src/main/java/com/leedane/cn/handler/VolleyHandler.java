package com.leedane.cn.handler;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * volley的处理类
 */
public class VolleyHandler {

    public static VolleyHandler mVolleyHandler;

    public static RequestQueue mRequestQueue;

    private VolleyHandler(){}

    private Context mContext;



    public synchronized VolleyHandler getInstance(){
        if(mVolleyHandler == null){
            mVolleyHandler = new VolleyHandler();
        }
        return mVolleyHandler;
    }

    public void init(Context context){
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    public void sendPost(){

    }
}