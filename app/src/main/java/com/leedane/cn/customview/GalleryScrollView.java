package com.leedane.cn.customview;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leedane.cn.activity.GalleryActivity;
import com.leedane.cn.activity.ImageDetailActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.GalleryBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.HttpResponseGalleryBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.database.GalleryDataBase;
import com.leedane.cn.handler.ChatBgSelectWebHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.leedane.cn.volley.ImageCacheUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图库滚动的scrollViiew
 * Created by Leedane on 2016/1/14.
 */
public class GalleryScrollView extends ScrollView implements View.OnTouchListener, TaskListener{

    public static final String TAG = "GalleryScrollView";

    private GalleryDataBase galleryDataBase;

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

    public GalleryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //taskCollection = new HashSet<>();
        mImageCache = new ImageCacheUtil();
        mBeans = new ArrayList<>();
        setOnTouchListener(this);
    }

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            GalleryScrollView myScrollView = (GalleryScrollView)msg.obj;
            int scrollY = myScrollView.getScrollY();

            //如果当前的滚动位置和上次的相同，表示已经停止滚动
            if(scrollY == lastScrollY){
                Log.i(TAG, "scrollY + scrollViewHeight"+(scrollY + scrollViewHeight));
                Log.i(TAG, "scrollLayout.getHeight():"+scrollLayout.getHeight());

                //当滚动到最底部，并且当前没有正在下载的任务，开始加载下一页的数据
                if((scrollY + scrollViewHeight) >= scrollLayout.getHeight() /*&& taskCollection.isEmpty()*/){
                    myScrollView.loadMoreImage("lowLoading", null);
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
                    //volleyImageTask(imageUrl, imageView, imageViewList.get(i).getWidth(), imageViewList.get(i).getHeight());
                    int[] widthAndHeight = getShowWidthAndHeight(imageViewList.get(i));
                    ImageCacheManager.loadImage(imageUrl, imageView, widthAndHeight[0], widthAndHeight[1]);
                }

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(getContext(), "点击："+v.getTag(R.string.image_url), Toast.LENGTH_LONG).show();
                        //Toast.makeText(getContext(),"点击："+galleryBean.getPath(),Toast.LENGTH_LONG).show();
                        //StringBuffer imageBuffer = new StringBuffer();
                        int current = 0;
                        String path = null;
                        List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();
                        ImageDetailBean imageDetailBean;
                        for (int i = 0; i < mBeans.size(); i++) {
                            path = mBeans.get(i).getPath();
                            if (path.equalsIgnoreCase(v.getTag(R.string.image_url).toString())) {
                                current = i;
                            }
                            imageDetailBean = new ImageDetailBean();
                            imageDetailBean.setPath(path);
                            imageDetailBean.setWidth(mBeans.get(i).getWidth());
                            imageDetailBean.setHeight(mBeans.get(i).getHeight());
                            imageDetailBean.setLenght(mBeans.get(i).getLength());
                            list.add(imageDetailBean);
                            //imageBuffer.append(path);
                        }
                        Toast.makeText(getContext(), "current：" + current, Toast.LENGTH_LONG).show();
                        //String imageUrls = imageBuffer.toString().substring(0, imageBuffer.length() -1);
                        Intent itImageDetail = new Intent();
                        itImageDetail.setClass(getContext(), ImageDetailActivity.class);
                        Type type = new TypeToken<ArrayList<ImageDetailBean>>() {
                        }.getType();
                        String json = new Gson().toJson(list, type);
                        itImageDetail.putExtra("ImageDetailBeans", json);
                        //itImageDetail.putExtra("imageUrls", imageUrls);
                        itImageDetail.putExtra("current", current);
                        getContext().startActivity(itImageDetail);
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
            //加载本地数据库的数据
            galleryDataBase = new GalleryDataBase(getContext());
            mBeans = galleryDataBase.queryGalleryLimit50(BaseApplication.getLoginUserId());
            if(mBeans.size() > 0){
                first_id = mBeans.get(0).getId();
                last_id = mBeans.get(mBeans.size() - 1).getId();
                mPreMethod = "lowloading";
                for(GalleryBean galleryBean: mBeans)
                    showImageView(galleryBean);
                checkVisibility();
            }else{
                loadMoreImage("firstLoading", null);
                loadOne = true;
            }
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
     * @param galleryActivity 不为空，需要隐藏下拉条
     */
    public void loadMoreImage(String method, GalleryActivity galleryActivity) {
        if(method.equalsIgnoreCase("lowloading") && isLoading)
            return;

        //正在加载中直接返回
        if(method.equalsIgnoreCase("firstloading") && isLoading){
            if(galleryActivity != null){
                galleryActivity.mySwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        if(method.equalsIgnoreCase("firstloading")){
            firstColumnHeight = 0;
            secondColumnHeight = 0;
            thirdColumnHeight = 0;
            lastScrollY = -1;
            imageViewList.clear();
            mBeans.clear();
            this.galleryActivity = galleryActivity;
            if(galleryActivity == null){
                showLoadingDialog("Gallery", "try to loading, please wait...");
            }
            first_id = 0;
            last_id = 0;
            firstColumn.removeAllViews();
            secondColumn.removeAllViews();
            thirdColumn.removeAllViews();

            //第一次加载清空所有的数据
            galleryDataBase.deleteAll();
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
            params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        }else{
            params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        }
        params.put("last_id", last_id);
        params.put("first_id", first_id);
        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        requestBean.setParams(params);
        requestBean.setServerMethod("leedane/gallery_getGalleryPaging.action");
        //showLoadingDialog("Gallery", "Loading, please wait。。。");
        TaskLoader.getInstance().startTaskForResult(TaskType.DO_GALLERY, this, requestBean);
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        dismissLoadingDialog();
        dismissDeleteAlertDialog();
        if(result instanceof Error){
            Toast.makeText(getContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(galleryActivity != null){
            galleryActivity.mySwipeRefreshLayout.setRefreshing(false);
            galleryActivity = null;
        }
        try {
            if(result != null && !StringUtil.isNull(StringUtil.changeNotNull(result))){
                if(type == TaskType.DO_GALLERY){
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
                                //volleyImageTask(galleryBeans.get(i).getPath(),galleryBeans.get(i).getWidth(), galleryBeans.get(i).getHeight() );
                                showImageView(galleryBeans.get(i));
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
                            //ToastUtil.success(getContext(), "此时图库开始ID：" + first_id + ",结束ID:" + last_id, Toast.LENGTH_LONG);
                            if(mPreMethod.equalsIgnoreCase("firstloading")){
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(MySettingConfigUtil.getCacheGallery()){
                                for(GalleryBean gb: mBeans){
                                    galleryDataBase.insert(gb);
                                }
                            }
                            checkVisibility();
                        }
                        //checkVisibility();
                    }else{

                        if(mPreMethod.equalsIgnoreCase("firstloading")){
                            ToastUtil.success(getContext(), "您的图库还没有数据");
                        }else{
                            ToastUtil.success(getContext(), "没有更多数据");
                        }
                    }
                }else if(type == TaskType.DELETE_GALLERY){
                    JSONObject jsonObject = new JSONObject(StringUtil.changeNotNull(result));
                    if(jsonObject != null && jsonObject.has("isSuccess")){
                        ToastUtil.success(getContext(), "移出图库成功!");
                        loadMoreImage("firstloading", null);
                    }else{
                        ToastUtil.success(getContext(), "移出图库失败");
                    }
                }else if(type == TaskType.ADD_REPORT){
                    JSONObject jsonObject = new JSONObject(StringUtil.changeNotNull(result));
                    if(jsonObject != null && jsonObject.has("isSuccess")){
                        ToastUtil.success(getContext(), "已成功举报，我们会尽快处理");
                        //Toast.makeText(getContext(), "举报失败，请稍后重试"+(jsonObject.has("message") ? ",原因是：" + jsonObject.getString("message"):""), Toast.LENGTH_LONG).show();
                    }else{
                        ToastUtil.success(getContext(), "举报失败，请稍后重试" + (jsonObject.has("message") ? ",原因是：" + jsonObject.getString("message") : ""));
                        //Toast.makeText(getContext(), "举报失败，请稍后重试", Toast.LENGTH_LONG).show();
                    }
                }else{
                    JSONObject jsonObject = new JSONObject(StringUtil.changeNotNull(result));
                    if(jsonObject != null && jsonObject.has("isSuccess")){
                        ToastUtil.success(getContext(), jsonObject);
                        if(jsonObject.getBoolean("isSuccess")){
                            dismissPublishChatBgDialog();
                        }
                    }
                }
            }else{
                ToastUtil.success(getContext(), "服务器返回信息为空");
            }
        }catch (JSONException e){
            e.printStackTrace();
            ToastUtil.success(getContext(), "服务器返回信息解析失败");
        }

    }

    /**
     * 展示图片
     * @param galleryBean
     */
    private void showImageView(final GalleryBean galleryBean) {
        ImageView imageView = new ImageView(getContext());
        int[] widthAndHeight = getShowWidthAndHeight(galleryBean);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthAndHeight[0], widthAndHeight[1]);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(5, 5, 5, 5);
        imageView.setTag(R.string.image_url, galleryBean.getPath());
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int current = 0;
                String path = null;
                for (int i = 0; i < mBeans.size(); i++) {
                    path = mBeans.get(i).getPath();
                    if (path.equalsIgnoreCase(v.getTag(R.string.image_url).toString())) {
                        current = i;
                        break;
                    }
                }
                showListItemMenuDialog(current);
                return true;
            }
        });
        ImageViewInfo imageViewInfo = new ImageViewInfo();
        imageViewInfo.setWidth(widthAndHeight[0]);
        imageViewInfo.setHeight(widthAndHeight[1]);
        imageViewInfo.setImageView(imageView);
        imageViewList.add(imageViewInfo);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"点击："+galleryBean.getPath(),Toast.LENGTH_LONG).show();
                //StringBuffer imageBuffer = new StringBuffer();
                int current = 0;
                String path = null;
                List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();
                ImageDetailBean imageDetailBean;
                for (int i = 0; i < mBeans.size(); i++) {
                    path = mBeans.get(i).getPath();
                    if (path.equalsIgnoreCase(v.getTag(R.string.image_url).toString())) {
                        current = i;
                    }
                    imageDetailBean = new ImageDetailBean();
                    imageDetailBean.setPath(path);
                    imageDetailBean.setWidth(mBeans.get(i).getWidth());
                    imageDetailBean.setHeight(mBeans.get(i).getHeight());
                    imageDetailBean.setLenght(mBeans.get(i).getLength());
                    list.add(imageDetailBean);
                    //imageBuffer.append(path);
                }
                Toast.makeText(getContext(), "current：" + current, Toast.LENGTH_LONG).show();
                //String imageUrls = imageBuffer.toString().substring(0, imageBuffer.length() -1);
                Intent itImageDetail = new Intent();
                itImageDetail.setClass(getContext(), ImageDetailActivity.class);
                Type type = new TypeToken<ArrayList<ImageDetailBean>>() {
                }.getType();
                String json = new Gson().toJson(list, type);
                itImageDetail.putExtra("ImageDetailBeans", json);
                //itImageDetail.putExtra("imageUrls", imageUrls);
                itImageDetail.putExtra("current", current);
                getContext().startActivity(itImageDetail);
            }
        });
        findColumnToAdd(imageView, widthAndHeight[1]).addView(imageView);

        ImageCacheManager.loadImage(galleryBean.getPath(), imageView, columnWidth, widthAndHeight[1]);
    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showListItemMenuDialog(final int index) {

        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();

        mDialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView) view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();
        menus.add(getResources().getString(R.string.gallery_menu_delete));
        menus.add(getResources().getString(R.string.gallery_menu_report));
        menus.add(getResources().getString(R.string.copyLink));
        menus.add(getResources().getString(R.string.setWallpaper));
        menus.add(getResources().getString(R.string.publish_chat_bg));
        menus.add(getResources().getString(R.string.as_chat_bg));

        SimpleListAdapter adapter = new SimpleListAdapter(getContext().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        deleteAlertDialog(index);
                        //Toast.makeText(getContext(), index+ "删除："+mBeans.get(index).getPath(), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        reportAlertDialog(index);
                        //Toast.makeText(getContext(), index + "举报：" + mBeans.get(index).getPath(), Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //Toast.makeText(getContext(), index + "复制：" + mBeans.get(index).getPath(), Toast.LENGTH_SHORT).show();
                        try {
                            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, mBeans.get(index).getPath()));
                            Toast.makeText(getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3://设为壁纸
                        try {
                            showLoadingDialog("Wallpaper", "set wallpaper...");
                            ImageView imageView = imageViewList.get(index).getImageView();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                            wallpaperManager.setBitmap(bitmap);
                            ToastUtil.success(getContext(), "设为壁纸成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.failure(getContext(), "设为壁纸失败");
                        } finally {
                            dismissLoadingDialog();
                        }

                        break;
                    case 4: //发布聊天背景
                        showPublishChatBgDialog(index);
                        break;
                    case 5://设置为聊天背景
                        break;

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

    private Dialog publishChatBgDialog;
    private boolean isFree;
    /**
     * 显示弹出自定义发布聊天背景view
     */
    public void showPublishChatBgDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissPublishChatBgDialog();
        isFree = true;
        publishChatBgDialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.publish_chat_bg_dialog, null);

        final EditText editText = (EditText)view.findViewById(R.id.chat_bg_charge_score);
        final EditText editTextDesc = (EditText)view.findViewById(R.id.chat_bg_charge_desc);
        final LinearLayout chagerLayout = (LinearLayout)view.findViewById(R.id.chat_bg_charge_layout);
        TextView submitBtn = (TextView)view.findViewById(R.id.chat_bg_dialog_submit);
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.chat_bg_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.chat_bg_charge:
                        chagerLayout.setVisibility(View.VISIBLE);
                        isFree = false;
                        break;
                    case R.id.chat_bg_free:
                        chagerLayout.setVisibility(View.GONE);
                        isFree = true;
                        break;
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String score = editText.getText().toString();
                if (!isFree && StringUtil.isNull(score)) {
                    editText.setFocusable(true);
                    ToastUtil.failure(getContext(), "请先输入用户每次下载的积分");
                    return;
                }
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("path", mBeans.get(index).getPath());
                params.put("type", isFree ? 0: 1);
                params.put("score", StringUtil.changeObjectToInt(score));
                params.put("desc", editTextDesc.getText().toString());
                ChatBgSelectWebHandler.publishChatBg(GalleryScrollView.this, params);
                showLoadingDialog("ChatBg", "try best to publish......");
            }
        });

        publishChatBgDialog.setTitle("操作");
        publishChatBgDialog.setCancelable(true);
        publishChatBgDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissPublishChatBgDialog();
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(800,500);
        publishChatBgDialog.setContentView(view, layoutParams);
        publishChatBgDialog.show();
    }

    private void dismissPublishChatBgDialog(){
        if(publishChatBgDialog != null && publishChatBgDialog.isShowing()){
            publishChatBgDialog.dismiss();
        }
    }

    private AlertDialog deleteAlertDialog;
    private Dialog reportAlertDialog;
    private int menuType;
    private EditText qingsuMore;
    /**
     * 举报前弹出的警告框
     */
    public void reportAlertDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissReportDialog();

        reportAlertDialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.report_dialog, null);
        RadioGroup reportGroup = (RadioGroup)view.findViewById(R.id.report_group);
        qingsuMore = (EditText)view.findViewById(R.id.report_qingsu_more);
        TextView reportSubmit = (TextView)view.findViewById(R.id.report_submit);
        menuType = 0;
        reportGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                qingsuMore.setVisibility(View.GONE);
                switch (checkedId){
                    case R.id.report_seqing:
                        menuType = 1;
                        //Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.report_guanggao:
                        menuType = 2;
                        //Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.report_zhengzhi:
                        menuType = 3;
                        //Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.report_weifa:
                        menuType = 4;
                        //Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.report_qingsu: //倾诉
                        menuType = 5;
                        qingsuMore.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        reportSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuType == 0){
                    Toast.makeText(getContext(), "请先选择举报的类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                HttpRequestBean requestBean = new HttpRequestBean();
                HashMap<String, Object> params = new HashMap<>();
                params.put("table_name", "t_gallery");
                params.put("table_id", mBeans.get(index).getId());
                params.put("type", menuType);
                if(menuType == 5 ){
                    if(qingsuMore == null || StringUtil.isNull(qingsuMore.getText().toString())){
                        Toast.makeText(getContext(), "请先输入举报信息", Toast.LENGTH_SHORT).show();
                        if(qingsuMore != null)
                            qingsuMore.setFocusable(true);
                        return;
                    }
                    params.put("reason", qingsuMore.getText().toString());
                }


                params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                requestBean.setParams(params);
                requestBean.setServerMethod("leedane/report_add.action");
                TaskLoader.getInstance().startTaskForResult(TaskType.ADD_REPORT, GalleryScrollView.this, requestBean);
                showLoadingDialog("Report", "Loading, add report...", true);
                dismissReportDialog();
            }
        });
        reportAlertDialog.setTitle("操作");
        reportAlertDialog.setCancelable(true);
        reportAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissReportDialog();
            }
        });
        reportAlertDialog.setContentView(view);
        reportAlertDialog.show();
    }
    /**
     * 隐藏举报前弹出的警告框
     */
    public void dismissReportDialog(){
        if(reportAlertDialog != null && reportAlertDialog.isShowing()){
            reportAlertDialog.dismiss();
        }
    }

    /**
     * 移出图库前弹出的警告框
     */
    public void deleteAlertDialog(final int index){
        deleteAlertDialog = new AlertDialog.Builder(getContext()).setTitle("提示")
                .setMessage("确定把该图片:"+mBeans.get(index).getPath()+" 移出我的图库?")
                .setPositiveButton("移出", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        int gid = mBeans.get(index).getId();
                        if(gid == 0){
                            Toast.makeText(getContext(), "图库所在的ID不存在",Toast.LENGTH_LONG).show();
                            return;
                        }
                        HttpRequestBean requestBean = new HttpRequestBean();
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("gid", gid);
                        params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                        requestBean.setParams(params);
                        requestBean.setServerMethod("leedane/gallery_delete.action");
                        TaskLoader.getInstance().startTaskForResult(TaskType.DELETE_GALLERY, GalleryScrollView.this, requestBean);
                        showLoadingDialog("Gallery", "Loading, delete gallery...");
                    }
                })
                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create(); // 创建对话框
        deleteAlertDialog.show(); // 显示对话框
    }
    /**
     * 移出图库前弹出的警告框的移出
     */
    public void dismissDeleteAlertDialog(){
        if(deleteAlertDialog != null && deleteAlertDialog.isShowing()){
            deleteAlertDialog.dismiss();
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
     * 获取展示的图片的宽和高
     * @param width
     * @param height
     * @return
     */
    public int[] getShowWidthAndHeight(int width, int height){
        int[] widthAndHeight = new int[2];
        double ratio = width / (columnWidth * 1.0);
        int scaleHeight = (int) (height / ratio);
        if(scaleHeight > columnWidth*1.5){//限制高度最大为2.5倍宽度
            scaleHeight = (int) (columnWidth * 1.5);
        }
        widthAndHeight[0] = columnWidth;
        widthAndHeight[1] = scaleHeight;
        return widthAndHeight;
    }
    public int[] getShowWidthAndHeight(GalleryBean galleryBean){
        return getShowWidthAndHeight(galleryBean.getWidth(), galleryBean.getHeight());
    }

    public int[] getShowWidthAndHeight(ImageViewInfo info){
        return getShowWidthAndHeight(info.getWidth(), info.getHeight());
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    /*private void volleyImageTask(String imageUrl, int width, int height){
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
    }*/

    /**
     * 向ImageView中添加一张图片
     * @param bitmap
     * @param imageWidth
     * @param imageHeight
     */
    /*private void addImage(String mImageUrl, ImageView mImageView, Bitmap bitmap, int imageWidth, int imageHeight) {
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
    }*/

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
    /*private Bitmap loadImage(String mImageUrl) {
        //Log.i("GalleryScrollView", "img_url:"+mImageUrl);
        //控制获取图片的最大宽度为屏幕宽度的1/3，高度为屏幕宽度的2/3
        Bitmap b = ImageCacheManager.loadImage(mImageUrl, columnWidth, 2*columnWidth);
        //Bitmap b = ImageCacheManager.loadImage(mImageUrl, 0, 0);
        *//*if(b != null)
            Toast.makeText(getContext(), "bitmap不为空", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "bitmap为空", Toast.LENGTH_LONG).show();*//*
        return b;
    }*/

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
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    private void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getContext(), title, main, true, cancelable);
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

    /**
     * 销毁数据库资源
     */
    public void destroy() {
        if(galleryDataBase != null)
            galleryDataBase.destroy();
    }
}
