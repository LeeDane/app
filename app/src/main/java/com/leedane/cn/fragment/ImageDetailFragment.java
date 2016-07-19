package com.leedane.cn.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.task.NetworkImageLoader;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多图像详情的frament类
 * Created by LeeDane on 2015/11/16.
 */
public class ImageDetailFragment extends Fragment{

    public static final String TAG = "ImageDetailFragment";
    public static final int LOAD_NETWORK_IMG_CODE = 33;
    private Context mContext;
    private int mCurrent;
    private ImageDetailBean mImageDetailBean;

    private NetworkImageLoader mNetworkImageLoader;
    private int acreenWidth;
    private int screenHeight;
    private SubsamplingScaleImageView mScaleImageView; //图像对象

    public ImageDetailFragment(){
        mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        this.acreenWidth = widthAndHeight[0];
        this.screenHeight = widthAndHeight[1]*3/4;
    }

    /*public ImageDetailFragment(int current, Context context, String currentImageUrl, int width, int height){
        this.mNetworkImageLoader = new NetworkImageLoader();
        int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
        if(width > 0){
            this.acreenWidth = width;
        }else{
            this.acreenWidth = widthAndHeight[0];
        }
        if(height >0){
            this.screenHeight = height;
        }else{
            this.screenHeight = widthAndHeight[1]*3/4;
        }
    }*/

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public static final ImageDetailFragment newInstance(Bundle bundle){
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            this.mCurrent = bundle.getInt("current");
            this.mImageDetailBean = (ImageDetailBean)bundle.getSerializable("imageDetailBean");
            this.mNetworkImageLoader = new NetworkImageLoader();
            int[] widthAndHeight = BaseApplication.newInstance().getScreenWidthAndHeight();
            this.acreenWidth = widthAndHeight[0];
            this.screenHeight = widthAndHeight[1]*3/4;
        }

        if(mContext == null)
            mContext = getActivity();

        String currentImageUrl = mImageDetailBean.getPath();
        int width = mImageDetailBean.getWidth() == 0 ||  mImageDetailBean.getWidth() > acreenWidth ? acreenWidth: mImageDetailBean.getWidth();
        int height = mImageDetailBean.getHeight() == 0 ||  mImageDetailBean.getHeight() > screenHeight ? screenHeight: mImageDetailBean.getHeight();
        if(currentImageUrl.startsWith("http://") || currentImageUrl.startsWith("https://")){
           //ImageCacheManager.loadImage(currentImageUrl, (ImageView) getView().findViewById(R.id.image_detail_imageview), width, height);
            mScaleImageView = (SubsamplingScaleImageView)getView().findViewById(R.id.image_detail_imageview);
            ImageCacheManager.loadImage(currentImageUrl, mScaleImageView, width, height);
            mScaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showListItemMenuDialog();
                    return true;
                }
            });

            /*PhotoView photoView = (PhotoView )getView().findViewById(R.id.image_detail_imageview);
            final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

            Picasso.with(mContext)
                    .load(currentImageUrl)
                    .into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attacher.update();
                        }

                        @Override
                        public void onError() {
                        }
                    });*/
        }else{
            Map<String, Object> map = new HashMap<>();

            //请求的文件的名称(这里是图片)
            map.put("filename", currentImageUrl);
            //获取所有的请求基本参数
            map.putAll(BaseApplication.newInstance().getBaseRequestParams());
            String imageUrl = SharedPreferenceUtil.getSettingBean(mContext, ConstantsUtil.STRING_SETTING_BEAN_SERVER).getContent() + "leedane/download/getLocalBase64Image.action";
            String imageTag = "imagedetail" + currentImageUrl;
            //执行网络加载post请求图片
            mNetworkImageLoader.loadNetBitmap(imageTag, imageUrl, new NetworkImageLoader.ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, String tag) {
                    ImageView imageView = (ImageView) getView().findViewById(R.id.image_detail_imageview);
                    if (imageBitmap == null && imageView != null) {
                        imageView.setImageResource(R.drawable.error_cat);
                        return;
                    }

                    if (imageView != null) {
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            }, ConstantsUtil.REQUEST_METHOD_POST, map);
        }
    }

    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    public String getStringResource(int resourseId){
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
    public void onDestroy() {
        super.onDestroy();
    }

    class myThread implements Runnable {
        public void run() {
                Map<String, Object> rs = FileUtil.saveNetWorkLinkToFile(mContext, mImageDetailBean.getPath());
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
}
