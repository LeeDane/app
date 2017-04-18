package com.leedane.cn.customview;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.leedane.cn.activity.GalleryActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.GalleryBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.HttpResponseGalleryBean;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.leedane.cn.volley.ImageCacheUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图库滚动的scrollViiew
 * Created by Leedane on 2016/1/14.
 */
public class GalleryScrollView_old extends ScrollView implements View.OnTouchListener, TaskListener{

    public static final String TAG = "GalleryScrollView";

    /**
     * 每一列的宽度
     */
    private int columnWidth;

    /**
     * 第一列的高度
     */
    private int firstColumnHeight;

    /**
     * 第二列的高度
     */
    private int secondColumnHeight;

    /**
     * 第三列的高度
     */
    private int thirdColumnHeight;

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOne;

    /**
     * 第一列的部署
     */
    private LinearLayout firstColumn;

    /**
     * 第二列的部署
     */
    private LinearLayout secondColumn;

    /**
     * 第三列的部署
     */
    private LinearLayout thirdColumn;


    /**
     * 记录所有正在下载或者等待下载的任务
     */
    //private static Set<LoadImageTask> taskCollection;

    /**
     * 当前scrollView的直接子布局
     */
    private static View scrollLayout;

    /**
     * ScrollView布局的高度
     */
    private static int scrollViewHeight;

    /**
     * 记录垂直方向的滚动距离
     */
    private static int lastScrollY = -1;

    /**
     * 标记是否执行获取列表的操作
     */
    private boolean isLoading;

    /**
     * 记录所有界面上的图片，用于可以随时控制对图片的释放
     */
    private List<ImageViewInfo> imageViewList = new ArrayList<>();

    private ImageLoader.ImageCache mImageCache;

    /**
     * 所有的图库对象列表集合
     */
    private List<GalleryBean> mBeans;
    /**
     * 第一张图片的id
     */
    private int first_id;

    /**
     * 最后一张图片的id
     */
    private int last_id;

    /**
     * 当前的操作方式
     */
    private String mPreMethod;

    private GalleryActivity galleryActivity;

    private ProgressDialog mProgressDialog;

    public GalleryScrollView_old(Context context, AttributeSet attrs) {
        super(context, attrs);
        //taskCollection = new HashSet<>();
        mImageCache = new ImageCacheUtil();
        mBeans = new ArrayList<>();
        setOnTouchListener(this);
    }

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            GalleryScrollView_old myScrollView = (GalleryScrollView_old)msg.obj;
            int scrollY = myScrollView.getScrollY();

            //如果当前的滚动位置和上次的相同，表示已经停止滚动
            if(scrollY == lastScrollY){
                Log.i(TAG, "scrollY + scrollViewHeight"+(scrollY + scrollViewHeight));
                Log.i(TAG, "scrollLayout.getHeight():"+scrollLayout.getHeight());

                //当滚动到最底部，并且当前没有正在下载的任务，开始加载下一页的数据
                if((scrollY + scrollViewHeight) >= scrollLayout.getHeight() /*&& taskCollection.isEmpty()*/){
                    myScrollView.loadMoreImage("lowLoading");
                }
                myScrollView.checkVisibility();
            }else{
                lastScrollY = scrollY;
                Message message = new Message();
                message.obj = myScrollView;

                //5秒后再次对滚动位置进行判断
                handler.sendMessageDelayed(message, 55);
            }
        }
    };

    /**
     * 判断ImageViewList中的每张图片，对图片的可见性进行检查，如果图片已经离开屏幕可见范围，则将图片替换成一张空图片
     */
    private void checkVisibility() {

        for(int i =0; i< imageViewList.size(); i++){
            ImageView imageView = imageViewList.get(i).getImageView();
            imageView.setImageResource(R.drawable.no_pic);
        }
        //Toast.makeText(getContext(), "此时List的大小是："+imageViewList.size(), Toast.LENGTH_LONG).show();
        ImageView imageView = null;
        int borderTop,borderBottom;
        String imageUrl;
        Bitmap bitmap;
        boolean isAdd = false;
        for(int i =0; i< imageViewList.size(); i++){
            imageView = imageViewList.get(i).getImageView();
            borderTop = (Integer) imageView.getTag(R.string.border_top);
            borderBottom = (Integer) imageView.getTag(R.string.border_bottom);
            if(borderBottom > getScrollY() && borderTop < getScrollY() +scrollViewHeight){
                isAdd = true;
                imageUrl = (String)imageView.getTag(R.string.image_url);
                bitmap = mImageCache.getBitmap(imageUrl);
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }else{
                    volleyImageTask(imageUrl, imageView, imageViewList.get(i).getWidth(), imageViewList.get(i).getHeight());
                }

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "点击："+v.getTag(R.string.image_url), Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                imageView.setImageResource(R.drawable.no_pic);
                Log.i(TAG, "回收的图片的位置："+i);
            }
        }

        //第一次刷新
        if(mPreMethod.equalsIgnoreCase("firstloading") && !isAdd && imageViewList.size() > 0){
            checkVisibility();
        }else{
            dismissLoadingDialog();
        }
    }

    /**
     * 进行一些关键性的初始化操作，获取scrollView的高度，以及得到第一列的宽度值，并在这里开始加载第一列的数据
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed && !loadOne){
            scrollViewHeight = getHeight();
            scrollLayout = getChildAt(0);
            firstColumn = (LinearLayout)findViewById(R.id.first_column);
            secondColumn = (LinearLayout)findViewById(R.id.second_column);
            thirdColumn = (LinearLayout)findViewById(R.id.third_column);
            columnWidth = firstColumn.getWidth();
            loadMoreImage("firstLoading");
            loadOne = true;
        }
    }

    /**
     * 监听用户的触屏事件，如果用户手指离开屏幕则开始进行滚动检测
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            Message message = new Message();
            message.obj = this;
            handler.sendMessageDelayed(message, 55);
        }
        return false;
    }

    /**
     * 开始加载下一页的图片，每张图片都会开启一个异步线程去下载
     * @param method
     */
    public void loadMoreImage(String method/*, GalleryActivity galleryActivity*/) {
        if(method.equalsIgnoreCase("lowloading") && isLoading)
            return;

        if(!loadOne){
            showLoadingDialog("Gallery", "try to loading, please wait...");
        }

        isLoading = true;
        /*this.galleryActivity = galleryActivity;*/
        mPreMethod = method;
        taskCanceled(TaskType.DO_GALLERY);
        //请求拿到的照片列表
        HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("method", method);
        if(method.equalsIgnoreCase("firstloading")){
            params.put("pageSize", 18);
        }else{
            params.put("pageSize", 8);
        }
        params.put("last_id", last_id);
        params.put("first_id", first_id);
        params.putAll(getBaseRequestParams());
        requestBean.setParams(params);
        //requestBean.setServerMethod("leedane/gallery_getGalleryPaging");
        //showLoadingDialog("Gallery", "Loading, please wait。。。");
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_GALLERY, this, requestBean);
    }

    /**
     * 构建基本的请求参数
     * @return
     */
    protected HashMap<String, Object> getBaseRequestParams(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("login_mothod", "android");
        try{
            JSONObject userInfo = SharedPreferenceUtil.getUserInfo(getContext());
            if(userInfo != null){
                if(userInfo.has("no_login_code"))
                    params.put("no_login_code", userInfo.getString("no_login_code"));
                if(userInfo.has("account"))
                    params.put("account", userInfo.getString("account"));
            }
            return params;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        /*if(galleryActivity != null){
            galleryActivity.dismissLoadingDialog();
            galleryActivity.mySwipeRefreshLayout.setRefreshing(false);//下拉刷新组件停止刷新
        }*/

        if(result instanceof Error){
            Toast.makeText(getContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(result != null && !StringUtil.isNull(StringUtil.changeNotNull(result))){
            HttpResponseGalleryBean responseGalleryBean = BeanConvertUtil.strConvertToGalleryBeans(StringUtil.changeNotNull(result));
            if(responseGalleryBean != null && responseGalleryBean.getMessage().size() > 0){
                List<GalleryBean> galleryBeans = responseGalleryBean.getMessage();
                int size = galleryBeans.size();
                if(size > 0){
                    for(int i = 0; i < size ; i++){
                        Log.i(TAG, "Gallery获取到的图片地址是："+ galleryBeans.get(i).getPath());
                       /* LoadImageTask task = new LoadImageTask();
                        taskCollection.add(task);
                        task.execute(beans.get(i).getPath());*/
                        volleyImageTask(galleryBeans.get(i).getPath(),galleryBeans.get(i).getWidth(), galleryBeans.get(i).getHeight() );

                    }

                    //临时list
                    List<GalleryBean> temList = new ArrayList<>();
                    //将新的数据和以前的数据进行叠加
                    if(mPreMethod.equalsIgnoreCase("uploading")){
                        for(int i = galleryBeans.size() -1; i>= 0 ; i--){
                            temList.add(galleryBeans.get(i));
                        }
                        temList.addAll(mBeans);
                    }else{
                        temList.addAll(mBeans);
                        temList.addAll(galleryBeans);
                    }
                    mBeans.clear();
                    mBeans.addAll(temList);
                    first_id = mBeans.get(0).getId();
                    last_id = mBeans.get(mBeans.size() -1).getId();
                    Toast.makeText(getContext(), "此时图库开始ID："+first_id+",结束ID:"+last_id, Toast.LENGTH_LONG).show();

                    if(mPreMethod.equalsIgnoreCase("firstloading")){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    checkVisibility();
                }

                //checkVisibility();
            }else{

                if(mPreMethod.equalsIgnoreCase("firstloading")){
                    Toast.makeText(getContext(), "您的图库还没有数据", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_LONG).show();
                }
            }

        }else{
            Toast.makeText(getContext(), "服务器返回信息为空", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    private void volleyImageTask(String imageUrl, int width, int height){
        Bitmap imageBitmap =  mImageCache.getBitmap(imageUrl);
        if(imageBitmap == null){
            imageBitmap = loadImage(imageUrl);
            if(imageBitmap != null){
                double ratio = width / (columnWidth * 1.0);
                int scaleHeight = (int) (height / ratio);
                addImage(imageUrl, null, imageBitmap, columnWidth, scaleHeight);
            }
        }else{
            //if(!loadOne){
                double ratio = width / (columnWidth * 1.0);
                int scaleHeight = (int) (height / ratio);
                addImage(imageUrl, null, imageBitmap, columnWidth, scaleHeight);
            //}
        }
    }

    private void volleyImageTask(String imageUrl, ImageView imageView, int width, int height){
        Bitmap imageBitmap =  mImageCache.getBitmap(imageUrl);
        if(imageBitmap == null){
            imageBitmap = loadImage(imageUrl);
            if(imageBitmap != null){
                double ratio = width / (columnWidth * 1.0);
                int scaleHeight = (int) (height / ratio);
                addImage(imageUrl, imageView, imageBitmap, columnWidth, scaleHeight);
            }
        }else{
            double ratio = width / (columnWidth * 1.0);
            int scaleHeight = (int) (height / ratio);
            addImage(imageUrl, imageView, imageBitmap, columnWidth, scaleHeight);
        }
    }

    /**
     * 向ImageView中添加一张图片
     * @param bitmap
     * @param imageWidth
     * @param imageHeight
     */
    private void addImage(String mImageUrl, ImageView mImageView, Bitmap bitmap, int imageWidth, int imageHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);

        if(mImageView != null){
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(View.VISIBLE);
        }else{
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setTag(R.string.image_url, mImageUrl);
            imageView.setVisibility(View.VISIBLE);
            findColumnToAdd(imageView, imageHeight).addView(imageView);
            ImageViewInfo info = new ImageViewInfo();
            info.setImageView(imageView);
            info.setWidth(imageWidth);
            info.setHeight(imageHeight);
            imageViewList.add(info);
        }
    }

    /**
     * 找到此时应该添加图片的一列，原则就是对三列的高度进行判断，当前高度最小的一列就是应该添加的一列
     * @param imageView
     * @param imageHeight
     * @return
     */
    private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
        if(firstColumnHeight <= secondColumnHeight){
            if(firstColumnHeight <= thirdColumnHeight){
                imageView.setTag(R.string.border_top, firstColumnHeight);
                firstColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, firstColumnHeight);
                return firstColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        }else{
            if(secondColumnHeight <= thirdColumnHeight){
                imageView.setTag(R.string.border_top, secondColumnHeight);
                secondColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, secondColumnHeight);
                return secondColumn;
            }
            imageView.setTag(R.string.border_top, thirdColumnHeight);
            thirdColumnHeight += imageHeight;
            imageView.setTag(R.string.border_bottom, thirdColumnHeight);
            return thirdColumn;
        }
    }

    /**
     * 根据传入的URL,对图片进行加载，如果这张图片已经存在SD卡，则直接从SD卡里面取，否则从网络上下载
     * @param mImageUrl
     * @return
     */
    private Bitmap loadImage(String mImageUrl) {
        //Log.i("GalleryScrollView", "img_url:"+mImageUrl);
        //控制获取图片的最大宽度为屏幕宽度的1/3，高度为屏幕宽度的2/3
        Bitmap b = ImageCacheManager.loadImage(mImageUrl, columnWidth, 2*columnWidth);
        //Bitmap b = ImageCacheManager.loadImage(mImageUrl, 0, 0);
        /*if(b != null)
            Toast.makeText(getContext(), "bitmap不为空", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "bitmap为空", Toast.LENGTH_LONG).show();*/
        return b;
    }

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    private void showLoadingDialog(String title, String main){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getContext(), title, main, true);
    }
    /**
     * 隐藏加载Dialog
     */
    private void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    class ImageViewInfo{
        private ImageView imageView;
        private int width;
        private int height;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
