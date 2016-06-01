package com.leedane.cn.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;
import com.leedane.cn.adapter.CommentOrTransmitAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.HttpResponseCommentOrTransmitBean;
import com.leedane.cn.bean.HttpResponseMoodImagesBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.bean.MoodImagesBean;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.EncodingHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.NotificationUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 查看心情详情activity
 * Created by LeeDane on 2016/3/1.
 */
public class MoodDetailOldActivity extends BaseActivity implements View.OnLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MoodDetailActivity";

    /**
     * 当前心情的ID
     */
    private int mid;

    /**
     * 判断是否有图片
     */
    private boolean hasImg;

    private ListView mListView;

    /**
     * 发送心情的imageview
     */
    private ImageView mRightImg;

    private int commentOrTransmit = 0;//控制展示的是评论或者是转发(0：表示评论，1：表示转发)
    private String mPreLoadMethod = "firstloading";//当前的操作方式
    private boolean isLoading; //标记当前是否在加载数据
    private int mFirstId;  //页面上第一条数据的ID
    private int mLastId; //页面上第一条数据的ID
    private TextView mListViewFooter;

    private SwipeRefreshLayout mSwipeLayout;
    private View viewHeader;
    private View viewFooter;
    private JSONObject detail = new JSONObject();
    private HttpResponseMoodImagesBean mMoodImagesBean;
    private LinearLayout mDetailInfoShowLinearLayout;
    private TextView mTVUser;
    private TextView mTVTime;
    private TextView mTVContent;
    private TextView mPraiseZanUser;
    private TextView mTVComment;
    private TextView mTVTransmit;
    private TextView mTVError;
    private LinearLayout mLLComment;
    private LinearLayout mLLTransmit;
    private TextView mTVPraise;
    private EditText mETCommentOrTransmit;
    private ImageView mIVCommentOrTransmit;

    private ImageView mIVImg;
    private int itemClickPosition = 0;

    /**
     * List存放页面上的评论对象列表
     */
    private List<CommentOrTransmitBean> mCommentOrTransmits = new ArrayList<>();
    private CommentOrTransmitAdapter mCommentOrTransmitAdapter;
    private ImageView mCommentLine;
    private ImageView mTransmitLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(MoodDetailOldActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.MoodDetailOldActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_mood_detail);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.mood_detail);
        backLayoutVisible();
        initView();
        initData();
        synchronizedData();//同步数据
    }

    /**
     * 初始化数据
     */
    private void initData(){
        Intent it = getIntent();
        mid = it.getIntExtra("tableId", 0);
        hasImg = it.getBooleanExtra("hasImg", false);
        if(mid < 1){
            this.finish();
            Toast.makeText(MoodDetailOldActivity.this, "心情ID不存在", Toast.LENGTH_LONG).show();
        }
    }

    public void synchronizedData(){

        //获取心情详情基本信息
        HttpRequestBean requestBean = new HttpRequestBean();
        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
        requestBean.setServerMethod("leedane/mood_detail.action");
        HashMap<String, Object> params = new HashMap<>();
        params.put("mid", mid);
        requestBean.setParams(params);
        TaskLoader.getInstance().startTaskForResult(TaskType.DETAIL_MOOD, this, requestBean);
        sendFirstLoading();
    }

    public void initView(){

        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.mood_detail_swipe_listview);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mETCommentOrTransmit = (EditText)findViewById(R.id.mood_detail_comment_or_transmit_text);
        mIVCommentOrTransmit = (ImageView)findViewById(R.id.mood_detail_comment_or_transmit_send);
        mIVCommentOrTransmit.setOnClickListener(this);

        mListView = (ListView)findViewById(R.id.mood_detail_listview);
        mCommentOrTransmitAdapter = new CommentOrTransmitAdapter(MoodDetailOldActivity.this, mCommentOrTransmits, true);
        mListView.setAdapter(mCommentOrTransmitAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (commentOrTransmit == 0) {
                    itemClickPosition = position - 1;
                    mETCommentOrTransmit.setHint(" @" + mCommentOrTransmits.get(itemClickPosition).getAccount());
                }
            }
        });

        viewHeader = LayoutInflater.from(MoodDetailOldActivity.this).inflate(R.layout.mood_detail_header, null);
        mDetailInfoShowLinearLayout = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_info_show);
        mTVUser = (TextView)viewHeader.findViewById(R.id.mood_detail_user);
        mTVTime = (TextView)viewHeader.findViewById(R.id.mood_detail_time);
        mTVContent = (TextView)viewHeader.findViewById(R.id.mood_detail_content);
        mTVComment = (TextView)viewHeader.findViewById(R.id.mood_detail_comment_show);
        mTVTransmit = (TextView)viewHeader.findViewById(R.id.mood_detail_transmit_show);
        mTVError = (TextView)viewHeader.findViewById(R.id.mood_detail_error);
        mLLComment = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_comment);
        mLLTransmit = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_transmit);
        mLLComment.setOnClickListener(this);
        mLLTransmit.setOnClickListener(this);

        mIVImg = (ImageView)viewHeader.findViewById(R.id.mood_detail_img);
        mPraiseZanUser = (TextView)viewHeader.findViewById(R.id.mood_detail_praise);
        mTVPraise = (TextView)viewHeader.findViewById(R.id.mood_detail_praise_show);
        mTVPraise.setOnClickListener(this);
        mCommentLine = (ImageView)viewHeader.findViewById(R.id.mood_detail_comment_show_line);
        mTransmitLine = (ImageView)viewHeader.findViewById(R.id.mood_detail_transmit_show_line);
        mListView.addHeaderView(viewHeader, null, false);
        mListView.setOnScrollListener(new ListViewOnScrollListener());

        viewFooter = LayoutInflater.from(MoodDetailOldActivity.this).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setText(getResources().getString(R.string.loading));
        mListViewFooter.setOnClickListener(this);//添加点击事件
        //mListView.addFooterView(mListViewFooter, null, true);
        //mListViewFooter.setVisibility(View.GONE);
    }


    /**
     * 初始化参数
     */
    private void init() {
        try{
            mTVUser.setText(detail.getString("account"));
            mTVTime.setText(detail.getString("create_time"));
            mTVContent.setText(detail.getString("content"));
            mTVPraise.setText(getResources().getString(R.string.personal_praise) +"("+ detail.getInt("zan_number")+")");
            mTVComment.setText("评论("+detail.getInt("comment_number")+")");
            mTVTransmit.setText("转发("+detail.getInt("transmit_number")+")");

            showPraiseUser();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 展示赞的用户列表
     * @throws JSONException
     */
    private void showPraiseUser() throws JSONException{
        String praiseList = null;
        if(detail.has("zan_users"))
            praiseList = detail.getString("zan_users");

        if(StringUtil.isNotNull(praiseList) && detail.getInt("zan_number") > 0){
            mPraiseZanUser.setVisibility(View.VISIBLE);
           // mPraiseZanUser.removeAllViewsInLayout();
            String[] users = praiseList.split(";");
            TextView textView;
            StringBuffer showPraiseHtml;
            int i = 0;
            for(String user: users){
                if(StringUtil.isNotNull(user)){
                    textView = new TextView(MoodDetailOldActivity.this);
                    showPraiseHtml = new StringBuffer();
                    showPraiseHtml.append("<html><body>");

                    final String[] u = user.split(",");
                    showPraiseHtml.append("<font color=\"#8181F7\">");
                    showPraiseHtml.append(u[1]);
                    showPraiseHtml.append("</font>");
                    if(i != users.length-1)
                        showPraiseHtml.append("、");
                    showPraiseHtml.append("</body></html>");
                    textView.setText(Html.fromHtml(showPraiseHtml.toString()));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonHandler.startPersonalActivity(MoodDetailOldActivity.this, Integer.parseInt(u[0]));
                        }
                    });
                   // mPraiseZanUser.addView(textView);
                }
                i++;
            }
            TextView endTextView = new TextView(MoodDetailOldActivity.this);
            //showPraiseHtml.append("<font color=\"#00bbaa\">颜色2</font></body></html>");
            endTextView.setText(Html.fromHtml("等" + detail.getInt("zan_number") + "位用户觉得很赞"));
            //mPraiseZanUser.addView(endTextView);

        }else{
            mPraiseZanUser.setVisibility(View.GONE);
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_COMMENT || type == TaskType.LOAD_TRANSMIT) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getResources().getString(R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{

            if(type == TaskType.DETAIL_MOOD){
                JSONObject resultObject = new JSONObject(String.valueOf(result));
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    //Toast.makeText(MoodDetailActivity.this, "成功" +resultObject, Toast.LENGTH_SHORT).show();
                    detail = resultObject.getJSONArray("message").getJSONObject(0);
                    mDetailInfoShowLinearLayout.setVisibility(View.VISIBLE);
                    //显示标题栏的生成二维码的图片按钮
                    mRightImg = (ImageView)findViewById(R.id.view_right_img);
                    mRightImg.setVisibility(View.VISIBLE);
                    mRightImg.setImageResource(R.drawable.qr_code);
                    mRightImg.setOnClickListener(this);

                    init();
                    //if(hasImg){//获取图片的信息
                        HttpRequestBean requestBean = new HttpRequestBean();
                        requestBean.setRequestMethod(ConstantsUtil.REQUEST_METHOD_POST);
                        requestBean.setServerMethod("leedane/mood_detailImgs.action");
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("mid", mid);
                        params.put("table_name", "t_mood");
                        params.put("table_uuid", detail.getString("uuid"));
                        requestBean.setParams(params);
                        TaskLoader.getInstance().startTaskForResult(TaskType.DETAIL_MOOD_IMAGE, this, requestBean);
                    //}
                }else{
                    //Toast.makeText(MoodDetailActivity.this, "失败"+resultObject, Toast.LENGTH_LONG).show();
                    mTVError.setVisibility(View.VISIBLE);
                    mTVError.setText("心情详情获取失败"+(resultObject.has("message")? ",原因是："+resultObject.getString("message"):""));
                }
            }else if(type == TaskType.DETAIL_MOOD_IMAGE){
                mMoodImagesBean = BeanConvertUtil.strConvertToMoodImagesBeans(String.valueOf(result));
                List<MoodImagesBean> moodImagesBeans = mMoodImagesBean.getMessage();
                if(mMoodImagesBean.isSuccess() && moodImagesBeans.size() > 0){
                    //Toast.makeText(MoodDetailActivity.this, "获取图片成功", Toast.LENGTH_SHORT).show();
                    mIVImg.setVisibility(View.VISIBLE);
                    for(MoodImagesBean imagesBean: moodImagesBeans){
                        if((imagesBean.getSize().equalsIgnoreCase("120x120") || imagesBean.getSize().equalsIgnoreCase("source")) && imagesBean.getOrder() == 0){
                            ImageCacheManager.loadImage(imagesBean.getPath(), mIVImg, 120, 120);
                            break;
                        }else{
                            continue;
                        }
                    }
                    mIVImg.setOnClickListener(this);
                    mIVImg.setOnLongClickListener(this);

                }/*else{
                    Toast.makeText(MoodDetailActivity.this, "获取图片失败", Toast.LENGTH_LONG).show();
                }*/
            }else if(type == TaskType.ADD_GALLERY){
                dismissLoadingDialog();
                JSONObject resultObject = new JSONObject(String.valueOf(result));
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    Toast.makeText(MoodDetailOldActivity.this, "该图片已成功加入我的图库" +resultObject, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MoodDetailOldActivity.this, "加入我的图库失败"+(resultObject.has("message") ? ",原因是：" + resultObject.getString("message"):""), Toast.LENGTH_LONG).show();
                }
            }else if(type == TaskType.LOAD_COMMENT || type == TaskType.LOAD_TRANSMIT){
                mSwipeLayout.setRefreshing(false);
                HttpResponseCommentOrTransmitBean responseCommentBean = BeanConvertUtil.strConvertToCommentOrTransmitBeans(String.valueOf(result));
                if(responseCommentBean != null && responseCommentBean.isSuccess()){
                    List<CommentOrTransmitBean> commentOrTransmitBeans =  responseCommentBean.getMessage();
                    if(commentOrTransmitBeans != null && commentOrTransmitBeans.size() > 0){
                        //临时list
                        List<CommentOrTransmitBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mCommentOrTransmits.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = commentOrTransmitBeans.size() -1; i>= 0 ; i--){
                                temList.add(commentOrTransmitBeans.get(i));
                            }
                            temList.addAll(mCommentOrTransmits);
                        }else{
                            temList.addAll(mCommentOrTransmits);
                            temList.addAll(commentOrTransmitBeans);
                        }
                        Log.i(TAG, "原来的大小：" + mCommentOrTransmits.size());
                        if(mCommentOrTransmitAdapter == null) {
                            mCommentOrTransmitAdapter = new CommentOrTransmitAdapter(MoodDetailOldActivity.this, mCommentOrTransmits, true);
                            mListView.setAdapter(mCommentOrTransmitAdapter);
                        }
                        mCommentOrTransmitAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mCommentOrTransmits.size());
                        //Toast.makeText(MoodDetailActivity.this, "成功加载"+ commentOrTransmitBeans.size() + "条数据,总数是："+mCommentOrTransmits.size(), Toast.LENGTH_SHORT).show();
                        int size = mCommentOrTransmits.size();

                        mFirstId = mCommentOrTransmits.get(0).getId();
                        mLastId = mCommentOrTransmits.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        mListViewFooter.setText(getResources().getString(R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmits.clear();
                            mCommentOrTransmitAdapter.refreshData(new ArrayList<CommentOrTransmitBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getResources().getString(R.string.no_load_more));
                        }else {
                            Toast.makeText(MoodDetailOldActivity.this, getResources().getString(R.string.no_load_more), Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmits.clear();
                            mCommentOrTransmitAdapter.refreshData(new ArrayList<CommentOrTransmitBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null ,false);
                        mListViewFooter.setText(getResources().getString(R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }
                }
            }else if(type == TaskType.ADD_COMMENT || type == TaskType.ADD_TRANSMIT){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    new NotificationUtil(1, MoodDetailOldActivity.this).sendTipNotification("信息提示", "您的评论发表成功", "测试", 1, 0);
                    if(type == TaskType.ADD_COMMENT){
                        int commentNumber = detail.getInt("comment_number");
                        detail.put("comment_number", (commentNumber+1));
                        mTVComment.setText("评论("+(commentNumber+1)+")");
                    }else{
                        int transmitNumber = detail.getInt("transmit_number");
                        detail.put("transmit_number", (transmitNumber + 1));
                        mTVTransmit.setText("转发("+(transmitNumber +1)+")");
                    }
                    mETCommentOrTransmit.setText("");
                    /**
                     * 延迟1秒钟后去加载数据
                     */
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendFirstLoading();
                        }
                    }, 1000);

                } else {
                    new NotificationUtil(1, MoodDetailOldActivity.this).sendActionNotification("信息提示", "您的评论发表失败，点击重试", "测试", 1, 0, MoodDetailOldActivity.class);
                }
            } else if (type == TaskType.ADD_ZAN) {
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true) {
                    Toast.makeText(MoodDetailOldActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                    int zanNumber = detail.getInt("zan_number");
                    detail.put("zan_number", (zanNumber + 1));
                    mTVPraise.setText(getResources().getString(R.string.personal_praise) + "(" + detail.getInt("zan_number") + ")");

                    //拼接赞用户列表数据
                    String oldZanUsers = "";
                    if(detail.has("zan_users")){
                        oldZanUsers = detail.getString("zan_users");
                    }
                    String myZanUser = null;
                    JSONObject userInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
                    //判断是否有缓存用户信息
                    if(userInfo != null && userInfo.has("account") ){
                        if(StringUtil.isNull(oldZanUsers)){
                            myZanUser = userInfo.getInt("id")+"," +userInfo.getString("account");
                        }else{
                            myZanUser = userInfo.getInt("id")+"," +userInfo.getString("account") +";" +oldZanUsers;
                        }
                        detail.put("zan_users", myZanUser);
                        showPraiseUser();
                    }

                }else{
                    Toast.makeText(MoodDetailOldActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //滚动停止
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                //当倒数第三个数据出现的时候就开始加载
                if (view.getLastVisiblePosition() == view.getCount() -1) {
                    if(!isLoading){
                        //刷新之前先把以前的任务取消
                        taskCanceled(TaskType.LOAD_COMMENT);
                        taskCanceled(TaskType.LOAD_TRANSMIT);
                        Log.i(TAG, "正在准备加载。。。。。。。。。。。");
                        sendLowLoading();
                    }
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /**
     * 发送第一次刷新的任务
     */
    private void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        //HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", 10);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        //params.putAll(BaseApplication.newInstance().getBaseRequestParams());
        //requestBean.setParams(params);
        //mListView.removeAllViewsInLayout();
       // mCommentOrTransmits.clear();
        if(commentOrTransmit == 0) {
            taskCanceled(TaskType.LOAD_TRANSMIT);
            mCommentLine.setVisibility(View.VISIBLE);
            mTransmitLine.setVisibility(View.GONE);

            //mTransmitAdapter = null;
            //mCommentOrTransmitAdapter = new CommentOrTransmitAdapter(MoodDetailActivity.this, mCommentOrTransmits, true);
            CommentHandler.getCommentsRequest(MoodDetailOldActivity.this, params);

        }else {
            taskCanceled(TaskType.LOAD_TRANSMIT);
            mTransmitLine.setVisibility(View.VISIBLE);
            mCommentLine.setVisibility(View.GONE);
            //mCommentOrTransmits.clear();
            //mCommentOrTransmitAdapter = null;
           // mTransmitAdapter = new TransmitAdapter(MoodDetailActivity.this, mCommentOrTransmits);
            TransmitHandler.getTransmitsRequest(MoodDetailOldActivity.this, params);
        }

    }

    /**
     * 发送向上刷新的任务
     */
    private void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        if(mFirstId == 0){
            sendFirstLoading();
            return;
        }

        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", 5);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        //获取当前是评论还是转发
        if(commentOrTransmit == 0){
            CommentHandler.getCommentsRequest(MoodDetailOldActivity.this, params);
        }else{
            TransmitHandler.getTransmitsRequest(MoodDetailOldActivity.this, params);
        }
    }
    /**
     * 发送向下刷新的任务
     */
    private void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getResources().getString(R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mListViewFooter.setText(getResources().getString(R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", 5);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        if(commentOrTransmit == 0){
            CommentHandler.getCommentsRequest(MoodDetailOldActivity.this, params);
        }else{
            TransmitHandler.getTransmitsRequest(MoodDetailOldActivity.this, params);
        }
    }

    @Override
    public void taskCanceled(TaskType type) {
        super.taskCanceled(type);
    }

    @Override
    public void taskStarted(TaskType type) {
        super.taskStarted(type);
    }

    @Override
    protected void onDestroy() {
        recycleQrCodeBitmap();
        super.onDestroy();
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getResources().getString(R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getResources().getString(R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){
            Toast.makeText(MoodDetailOldActivity.this, "请求重新加载", Toast.LENGTH_SHORT).show();
            taskCanceled(TaskType.LOAD_COMMENT);
            taskCanceled(TaskType.LOAD_TRANSMIT);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("table_name", "t_mood");
            params.put("table_id", mid);
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? 10: 5);
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("showUserInfo", true);
            mListViewFooter.setText(getResources().getString(R.string.loading));
            if(commentOrTransmit == 0){
                CommentHandler.getCommentsRequest(MoodDetailOldActivity.this, params);
            }else{
                TransmitHandler.getTransmitsRequest(MoodDetailOldActivity.this, params);
            }
        }

    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        HashMap<String, Object> params = new HashMap<>();
        switch (v.getId()){
            case R.id.mood_detail_img:
                Intent it_detail = new Intent(MoodDetailOldActivity.this, ImageDetailActivity.class);
                List<ImageDetailBean> list = new ArrayList<ImageDetailBean>();
                ImageDetailBean imageDetailBean;
                for(MoodImagesBean bean: mMoodImagesBean.getMessage()){
                    if((bean.getSize().equalsIgnoreCase("120x120") || bean.getSize().equalsIgnoreCase("source"))&& bean.getOrder() == 0){
                        imageDetailBean = new ImageDetailBean();
                        imageDetailBean.setPath(bean.getPath());
                        imageDetailBean.setWidth(bean.getWidth());
                        imageDetailBean.setHeight(bean.getHeight());
                        imageDetailBean.setLenght(bean.getLenght());
                        list.add(imageDetailBean);
                        break;
                    }else{
                        continue;
                    }
                }

                Type type = new TypeToken<ArrayList<ImageDetailBean>>(){}.getType();
                String json = new Gson().toJson(list,type);
                it_detail.putExtra("ImageDetailBeans", json);
                //it_detail.putExtra("imageUrls", imageUrls);
                startActivity(it_detail);
                break;
            case R.id.mood_detail_comment:
                //Toast.makeText(MoodDetailActivity.this, "评论", Toast.LENGTH_SHORT).show();
                commentOrTransmit = 0;
                itemClickPosition = 0;
                mETCommentOrTransmit.setText("");
                mETCommentOrTransmit.setHint(getStringResource(R.string.comment_hint));
                sendFirstLoading();
                break;
            case R.id.mood_detail_transmit:
                commentOrTransmit = 1;
                itemClickPosition = 0;
                mETCommentOrTransmit.setText("");
                mETCommentOrTransmit.setHint(getStringResource(R.string.comment_hint));
                sendFirstLoading();
                break;
            case R.id.mood_detail_comment_or_transmit_send:
                String commentOrTransmitText = mETCommentOrTransmit.getText().toString();
                if(StringUtil.isNull(commentOrTransmitText) && commentOrTransmit == 0){
                    Toast.makeText(MoodDetailOldActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                    mETCommentOrTransmit.setFocusable(true);
                    return;
                }
                if(commentOrTransmit == 0){
                    showLoadingDialog("Comment", "try to commeting...", true);
                    params.put("content", commentOrTransmitText);
                    params.put("table_name", "t_mood");
                    params.put("table_id", mid);
                    params.put("level", 1);
                    if(mCommentOrTransmits.size()> 0 && itemClickPosition > 0)
                        params.put("pid", mCommentOrTransmits.get(itemClickPosition).getId());
                    CommentHandler.sendComment(this, params);
                }else{
                    showLoadingDialog("Transmit", "try to transmiting...", true);
                    params.put("content", StringUtil.isNull(commentOrTransmitText)? "转发了这条心情" :commentOrTransmitText);
                    params.put("table_name", "t_mood");
                    params.put("table_id", mid);
                    TransmitHandler.sendTransmit(this, params);
                }

                break;
            case R.id.mood_detail_praise_show:
                params = new HashMap<>();
                params.put("table_name", "t_mood");
                params.put("table_id", mid);
                params.put("content", "赞了这条心情");
                PraiseHandler.sendZan(this, params);
                break;
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
            case  R.id.view_right_img://生成二维码
                showQrCodeDialog();
                break;
           /* case R.id.detail_comment_btn:
                break;
            case R.id.detail_transmit_btn:
                break;*/
        }
    }

    private Bitmap qrCodeBitmap;
    private Dialog mQrCodeDialog;
    /**
     * 显示弹出二维码的Dialog
     */
    public void showQrCodeDialog(){
        dismissQrCodeDialog();
        recycleQrCodeBitmap();
        mQrCodeDialog = new Dialog(MoodDetailOldActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        ImageView imageView = new ImageView(MoodDetailOldActivity.this);
        String contentString = null;
        try{
            String str = "{'tableName':'t_mood','tableId':"+mid+"}";
            contentString = new JSONObject(str).toString();
        }catch (JSONException e){
            e.printStackTrace();
        }

        if (StringUtil.isNotNull(contentString)) {
            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
            contentString =  CommonHandler.encodeQrCodeStr(contentString);
            ToastUtil.failure(MoodDetailOldActivity.this, "二维码创建成功，长按保存到本地", Toast.LENGTH_SHORT);
            try {
                qrCodeBitmap = EncodingHandler.createQRCode(contentString, 720);
                imageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String fPath = getQrCodeDir() + File.separator + System.currentTimeMillis() +".jpg";
                File f = new File(fPath);
                if(f.exists()){
                    f.delete();
                }
                if(BitmapUtil.bitmapToLocalPath(qrCodeBitmap, fPath)){
                    ToastUtil.failure(MoodDetailOldActivity.this, "二维码图片保存成功，路径是："+fPath);
                    dismissQrCodeDialog();
                }else
                    ToastUtil.failure(MoodDetailOldActivity.this, "二维码图片保存失败");
                return true;
            }
        });
        mQrCodeDialog.setTitle("我的二维码");
        mQrCodeDialog.setCancelable(true);
        mQrCodeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissQrCodeDialog();
            }
        });
        mQrCodeDialog.setContentView(imageView);
        mQrCodeDialog.show();
    }

    /**
     * 获取存放本地二维码的文件夹
     * @return
     */
    private File getQrCodeDir(){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = getCacheDir();
        }
        File cacheDir = new File(sdDir, getResources().getString(R.string.app_dirsname) + File.separator+ getResources().getString(R.string.qr_code_filepath));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    /**
     * 回收二维码生成的bitmap
     */
    private void recycleQrCodeBitmap(){
        if(qrCodeBitmap != null && !qrCodeBitmap.isRecycled()){
            qrCodeBitmap.recycle();
            System.gc();
        }
    }

    /**
     * 隐藏二维码
     */
    public void dismissQrCodeDialog(){
        if(mQrCodeDialog != null && mQrCodeDialog.isShowing())
            mQrCodeDialog.dismiss();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.mood_detail_img:
                 //创建离开的警告提示框
                AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("添加图库")
                        .setMessage("把该图片加入我的图库？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                HttpRequestBean requestBean = new HttpRequestBean();
                                HashMap<String, Object> params = new HashMap<>();

                                for (MoodImagesBean moodImagesBean : mMoodImagesBean.getMessage()) {
                                    if ((moodImagesBean.getSize().equalsIgnoreCase("120x120") || moodImagesBean.getSize().equalsIgnoreCase("source")) && moodImagesBean.getOrder() == 0) {
                                        params.put("path", moodImagesBean.getPath());
                                        params.put("desc", "app心情详情图片加入图库");
                                        params.put("width", moodImagesBean.getWidth());
                                        params.put("height", moodImagesBean.getHeight());
                                        break;
                                    } else {
                                        continue;
                                    }
                                }
                                params.putAll(BaseApplication.newInstance().getBaseRequestParams());
                                requestBean.setParams(params);
                                requestBean.setServerMethod("leedane/gallery_addLink.action");
                                //showLoadingDialog("Gallery", "Loading, please wait。。。");ss
                                TaskLoader.getInstance().startTaskForResult(TaskType.ADD_GALLERY, MoodDetailOldActivity.this, requestBean);
                                showLoadingDialog("Gallery","Loading, try my best to adding gallery...");
                            }
                        })
                        .setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create(); // 创建对话框
                alertDialog.show(); // 显示对话框
                break;
        }
        return true;
    }

    @Override
    public void onRefresh() {
        if(isLoading){
            taskCanceled(TaskType.LOAD_COMMENT);
            taskCanceled(TaskType.LOAD_TRANSMIT);
        }
        sendUpLoading();
    }
}
