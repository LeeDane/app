package com.leedane.cn.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.leedane.cn.activity.ImageDetailActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.GalleryHandler;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多图像详情的frament类
 * Created by LeeDane on 2015/11/16.
 */
public abstract class ImageBaseFragment extends Fragment implements TaskListener {

    public static final String TAG = "ImageBaseFragment";
    public static final int LOAD_NETWORK_IMG_CODE = 33;
    protected Context mContext;
    protected int mCurrent;
    protected ImageDetailBean mImageDetailBean;

    protected NetworkImageLoader mNetworkImageLoader;
    protected int acreenWidth;
    protected int screenHeight;

    public ImageBaseFragment(){
        mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1]*3/4;
    }

    /**
     * 获取fragmentId
     * @return
     */
    protected abstract int fragmentId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(fragmentId(), container,
                false);
    }
    /**
     * 获取当前图片的链接
     * @return
     */
    protected String getImageUrl(){
        return mImageDetailBean.getPath();
    }

    /**
     * 获取当前图片的宽度
     * @return
     */
    protected int getImageWidth(){
        return mImageDetailBean.getWidth() == 0 ||  mImageDetailBean.getWidth() > acreenWidth ? acreenWidth: mImageDetailBean.getWidth();
    }

    /**
     * 获取当前图片的高度
     * @return
     */
    protected int getImageHeight(){
        return mImageDetailBean.getHeight() == 0 ||  mImageDetailBean.getHeight() > screenHeight ? screenHeight: mImageDetailBean.getHeight();
    }
    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    protected String getStringResource(int resourseId){
        if(mContext == null){
            return BaseApplication.newInstance().getResources().getString(resourseId);
        }else{
            return mContext.getResources().getString(resourseId);
        }
    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showListItemMenuDialog() {

        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();

        mDialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView) view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();
        menus.add(getResources().getString(R.string.copyLink));
        menus.add(getResources().getString(R.string.setWallpaper));
        menus.add(getResources().getString(R.string.save_imgage));
        menus.add(getResources().getString(R.string.gallery_add));
        menus.add(getResources().getString(R.string.browser_imgage));

        SimpleListAdapter adapter = new SimpleListAdapter(getContext().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //复制链接
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.copyLink))){
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, mImageDetailBean.getPath()));
                        ToastUtil.success(mContext, "复制成功", Toast.LENGTH_SHORT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //设置壁纸
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.setWallpaper))){
                    /*try {
                        showLoadingDialog("Wallpaper", "set wallpaper...");
                        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                        wallpaperManager.setBitmap(bitmap);
                        ToastUtil.success(getContext(), "设为壁纸成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.failure(getContext(), "设为壁纸失败");
                    } finally {
                        dismissLoadingDialog();
                    }*/

                    //保存图片
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.save_imgage))){
                    new Thread(new myThread()).start();

                    //浏览器查看图片
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.browser_imgage))){
                    CommonHandler.openLink(mContext, mImageDetailBean.getPath());
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.gallery_add))){
                    if(/*StringUtil.isLink(getImageUrl()) &&*/ getImageUrl().indexOf(ConstantsUtil.QINIU_CLOUD_SERVER) >= 0){
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("添加图库")
                                .setMessage("把该图片加入我的图库？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        HashMap<String, Object> params = new HashMap<>();
                                        params.put("path", getImageUrl());
                                        params.put("width", mImageDetailBean.getWidth());
                                        params.put("height", mImageDetailBean.getHeight());
                                        params.put("lenght", mImageDetailBean.getLenght());
                                        params.put("desc", "androidApp图库查看器上加入");
                                        GalleryHandler.add(ImageBaseFragment.this, params);
                                    }
                                })
                                .setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                }).create(); // 创建对话框
                        alertDialog.show();
                    }else{
                        ToastUtil.failure(mContext, "抱歉，当前的图片链接不是图片链接或者不是七牛服务器上的图片，添加失败");
                    }

                }
                dismissListItemMenuDialog();
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissListItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800, (menus.size() + 1) * 90 + 20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_NETWORK_IMG_CODE:
                    ToastUtil.success(mContext, StringUtil.changeNotNull(msg.getData().getString("message")));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            ToastUtil.failure(mContext, ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            dismissLoadingDialog();
            return;
        }
        try{
            dismissLoadingDialog();
            if(type == TaskType.ADD_GALLERY){
                JSONObject jsonObject = new JSONObject(StringUtil.changeNotNull(result));
                if(jsonObject != null && jsonObject.has("isSuccess")){
                    if(jsonObject.optBoolean("isSuccess"))
                        //隐藏掉添加的弹出框
                        ToastUtil.success(mContext, "添加入图库成功");
                    else
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                }else{
                    ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    class myThread implements Runnable {
        public void run() {
            Map<String, Object> rs = FileUtil.saveNetWorkLinkToFile(mImageDetailBean.getPath());
            Message message = new Message();
            message.what = LOAD_NETWORK_IMG_CODE;
            Bundle bundle = new Bundle();
            bundle.putBoolean("isSuccess", StringUtil.changeObjectToBoolean(rs.get("isSuccess")));
            bundle.putString("message", StringUtil.changeNotNull(rs.get("message")));
            message.setData(bundle);
            myHandler.sendMessage(message);
        }
    }
    /**
     * 隐藏弹出自定义view
     */
    public void dismissListItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getActivity(), title, main, true, cancelable);
    }
    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
