package com.leedane.cn.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.MoodDetailActivity;
import com.leedane.cn.adapter.CommentOrTransmitAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.CommentOrTransmitBean;
import com.leedane.cn.bean.HttpRequestBean;
import com.leedane.cn.bean.HttpResponseCommentOrTransmitBean;
import com.leedane.cn.bean.HttpResponseMoodImagesBean;
import com.leedane.cn.bean.ImageDetailBean;
import com.leedane.cn.bean.MoodImagesBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.helper.PraiseUserHelper;
import com.leedane.cn.task.TaskLoader;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DensityUtil;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.NotificationUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 心情详情的Fragment
 * Created by LeeDane on 2016/5/3.
 */
public class MoodDetailFragment extends BaseFragment implements View.OnLongClickListener{

    public interface OnItemClickListener{
        void onItemClick(int position, CommentOrTransmitBean commentOrTransmitBean, int commentOrTransmit);
        void clearPosition();
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    private OnOperateTypeChangeListener onOperateTypeChangeListener;

    public void setOnOperateTypeChangeListener(OnOperateTypeChangeListener onOperateTypeChangeListener) {
        this.onOperateTypeChangeListener = onOperateTypeChangeListener;
    }

    /**
     * 评论或者转发状态改变的事件的监听
     */
    public interface OnOperateTypeChangeListener{
        /**
         * 成功发表评论或者转发后的监听
         */
        void change(int commentOrTransmit);
    }


    public static final String TAG = "MoodDetailFragment";

    /**
     * 当前心情的ID
     */
    private int mid;

    /**
     * 判断是否有图片
     */
    private boolean hasImg;

    private ListView mListView;


    private View mRootView;

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
    private CircularImageView mIVUserPic;
    private TextView mTVUser;
    private TextView mTVTime;
    private TextView mTVContent;
    private TextView mPraiseUser;
    private TextView mTVComment;
    private TextView mTVTransmit;
    private TextView mTVError;
    private LinearLayout mLLComment;
    private LinearLayout mLLTransmit;
    private TextView mTVPraise;

    private TextView mTVLocation; //显示位置信息
    private LinearLayout mImgContainer; //图像容器
    private Context mContext;

    /**
     * List存放页面上的评论对象列表
     */
    private List<CommentOrTransmitBean> mCommentOrTransmits = new ArrayList<>();
    private CommentOrTransmitAdapter mCommentOrTransmitAdapter;
    private ImageView mCommentLine;
    private ImageView mTransmitLine;
    public MoodDetailFragment(){

    }

    public static final MoodDetailFragment newInstance(Bundle bundle){
        MoodDetailFragment fragment = new MoodDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void afterSuccessAddCommentOrTransmit(int commentOrTransmit) {
        try {
            if(commentOrTransmit == 0){
                int commentNumber = detail.getInt("comment_number");
                detail.put("comment_number", (commentNumber+1));
                mTVComment.setText("评论("+(commentNumber+1)+")");
            }else{
                int transmitNumber = detail.getInt("transmit_number");
                detail.put("transmit_number", (transmitNumber + 1));
                mTVTransmit.setText("转发("+(transmitNumber +1)+")");
            }

            /**
             * 延迟1秒钟后去加载数据
             */
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendFirstLoading();
                }
            }, 1000);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_mood_detail, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.mid = bundle.getInt("tableId", 0);
            this.hasImg = bundle.getBoolean("hasImg", false);
        }
        if(mContext == null)
            mContext = getActivity();

        initView();
        synchronizedData();
    }

    private void synchronizedData(){

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
    private void initView(){
        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.mood_detail_swipe_listview);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mListView = (ListView)mRootView.findViewById(R.id.mood_detail_listview);
        mCommentOrTransmitAdapter = new CommentOrTransmitAdapter(mContext, mCommentOrTransmits, true);
        mListView.setAdapter(mCommentOrTransmitAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCommentOrTransmits.size() > 0) {
                    ToastUtil.success(mContext, "type:" + getOperateType());
                    onItemClickListener.onItemClick(position, mCommentOrTransmits.get(position - 1), getOperateType());
                }
            }
        });



        viewHeader = LayoutInflater.from(mContext).inflate(R.layout.mood_detail_header, null);
        mDetailInfoShowLinearLayout = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_info_show);
        mTVUser = (TextView)viewHeader.findViewById(R.id.mood_detail_user);
        mIVUserPic = (CircularImageView)viewHeader.findViewById(R.id.mood_detail_user_pic);
        mTVTime = (TextView)viewHeader.findViewById(R.id.mood_detail_time);
        mTVContent = (TextView)viewHeader.findViewById(R.id.mood_detail_content);
        mTVComment = (TextView)viewHeader.findViewById(R.id.mood_detail_comment_show);
        mTVTransmit = (TextView)viewHeader.findViewById(R.id.mood_detail_transmit_show);
        mTVError = (TextView)viewHeader.findViewById(R.id.mood_detail_error);
        mLLComment = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_comment);
        mLLTransmit = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_transmit);
        mLLComment.setOnClickListener(this);
        mLLTransmit.setOnClickListener(this);

        mTVLocation = (TextView)viewHeader.findViewById(R.id.mood_detail_location);
        mImgContainer = (LinearLayout)viewHeader.findViewById(R.id.mood_detail_img_container);
        mPraiseUser = (TextView)viewHeader.findViewById(R.id.mood_detail_praise);
        mTVPraise = (TextView)viewHeader.findViewById(R.id.mood_detail_praise_show);
        mTVPraise.setOnClickListener(this);
        mCommentLine = (ImageView)viewHeader.findViewById(R.id.mood_detail_comment_show_line);
        mTransmitLine = (ImageView)viewHeader.findViewById(R.id.mood_detail_transmit_show_line);
        mListView.addHeaderView(viewHeader, null, false);
        mListView.setOnScrollListener(new ListViewOnScrollListener());

        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mListViewFooter.setOnClickListener(this);//添加点击事件
    }

    private int getOperateType(){
        return commentOrTransmit;
    }
    /**
     * 初始化参数
     */
    private void init() {
        try{
            //mIVUserPic
            if(detail.has("user_pic_path") && StringUtil.isNotNull(detail.getString("user_pic_path"))){
                ImageCacheManager.loadImage(detail.getString("user_pic_path"), mIVUserPic);
                mIVUserPic.setOnClickListener(MoodDetailFragment.this);
            }
            mTVUser.setText(detail.getString("account"));
            if(detail.has("create_user_id") && detail.getInt("create_user_id") > 0)
                mTVUser.setOnClickListener(MoodDetailFragment.this);
            mTVTime.setText(detail.getString("create_time"));
            mTVContent.setText(detail.getString("content"));
            AppUtil.textviewShowImg(mContext, mTVContent);
            mTVPraise.setText(getStringResource(mContext, R.string.personal_praise) +"("+ detail.getInt("zan_number")+")");
            mTVComment.setText("评论("+detail.getInt("comment_number")+")");
            mTVTransmit.setText("转发(" + detail.getInt("transmit_number") + ")");

            if(detail.has("location") && StringUtil.isNotNull(detail.getString("location"))){
                mTVLocation.setVisibility(View.VISIBLE);
                mTVLocation.setText("我的位置:"+detail.getString("location"));
            }
            showPraiseUser();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 存放的是点赞的用户对象，key为用户名称，value为用户的ID
     */
    private Map<String, Integer> zanUserMap = new HashMap<>();
    /**
     * 展示赞的用户列表
     * @throws JSONException
     */
    private void showPraiseUser() throws JSONException{
        String praiseList = null;
        if(detail.has("zan_users"))
            praiseList = detail.getString("zan_users");

        PraiseUserHelper helper = new PraiseUserHelper("t_mood", mid);
        helper.setLikeUsers(mContext, mPraiseUser, praiseList, detail.getInt("zan_number"));

        /*if(StringUtil.isNotNull(praiseList) && detail.getInt("zan_number") > 0){
            mPraiseUser.setVisibility(View.VISIBLE);

            String[] users = praiseList.split(";");
            String[] u;
            StringBuffer showPraise = new StringBuffer();
            for(String user: users){
                if(StringUtil.isNotNull(user)){

                    u = user.split(",");
                    showPraise.append(u[1]);
                    showPraise.append("、");
                    zanUserMap.put(u[1], StringUtil.stringToInt(u[0]));
                }
            }
            String show = showPraise.toString();
            show = show.substring(0, show.length()-1) + "等"+detail.getInt("zan_number")+"人觉得很赞";
            mPraiseUser.setText(show);

            Pattern numberPattern = Pattern.compile("等\\d+人");
            SpannableString ss = new SpannableString(show);
            setZanUserNumberClickable(mPraiseUser, ss, numberPattern, new ZanUserNumberClickableSpan(detail.getInt("zan_number"), "t_mood", mid, new OnZanUserNumberClickListener() {
                @Override
                public void clickTextView(int number, String tableName, int tableId) {
                    Intent intent = new Intent(mContext, ZanUserActivity.class);
                    intent.putExtra("table_id", tableId);
                    intent.putExtra("table_name", tableName);
                    startActivity(intent);
                }

                @Override
                public void setStyle(TextPaint ds) {
                    ds.setColor(mContext.getResources().getColor(R.color.blueAccountLink));
                    ds.setUnderlineText(false);
                }
            }));


            String userSource = showPraise.toString().substring(0, showPraise.toString().length() -1);
            String[] arr = userSource.split("、");
            int start = 0;
            int end = 0;
            int toUserId = 0;
            for(int i = 0; i < arr.length; i++){
                if(i > 0 ){
                    start = end + 1;
                }else{
                    start = end;
                }
                end = start + arr[i].length();
                toUserId = StringUtil.changeObjectToInt(zanUserMap.get(arr[i]));
                setUserNameClickable(mPraiseUser, ss, start, end, new UserNameClickableSpan(toUserId, arr[i], new OnUserNameClickListener() {
                    @Override
                    public void clickTextView(int toUserId, String username) {
                        CommonHandler.startPersonalActivity(mContext,toUserId);
                    }

                    @Override
                    public void setStyle(TextPaint ds) {
                        ds.setColor(mContext.getResources().getColor(R.color.blueAccountLink));
                        ds.setUnderlineText(false);
                    }
                }));
            }
        }else{
            mPraiseUser.setVisibility(View.GONE);
        }*/
    }

    private void setZanUserNumberClickable(TextView textView, SpannableString ss, Pattern pattern, ClickableSpan cs){
        Matcher matcher = pattern.matcher(ss.toString());
        while (matcher.find()){
            String key = matcher.group();
            if(StringUtil.isNotNull(key) && key.startsWith("等")){
                int start = ss.toString().indexOf(key) +1; //把等去掉，不高亮显示
                int end = start + (key.length() -1);
                setClickTextView(textView, ss, start, end, cs);
            }
        }
    }
    private void setUserNameClickable(TextView textView, SpannableString ss, int start, int end,ClickableSpan cs){
        setClickTextView(textView, ss, start, end, cs);
    }

    private void setClickTextView(TextView textView, SpannableString ss, int start, int end, ClickableSpan cs) {
        ss.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if((type == TaskType.LOAD_COMMENT || type == TaskType.LOAD_TRANSMIT) && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{

            if(type == TaskType.DETAIL_MOOD){
                if(result == null){
                    ToastUtil.failure(mContext, "服务器返回异常");
                    return;
                }
                JSONObject resultObject = new JSONObject(String.valueOf(result));
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    detail = resultObject.getJSONArray("message").getJSONObject(0);
                    mDetailInfoShowLinearLayout.setVisibility(View.VISIBLE);
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
                    mImgContainer.setVisibility(View.VISIBLE);
                    StringBuffer buffer = new StringBuffer();
                    for(MoodImagesBean imagesBean: moodImagesBeans){
                        if((imagesBean.getSize().equalsIgnoreCase("120x120") || imagesBean.getSize().equalsIgnoreCase("source"))){
                            //ImageCacheManager.loadImage(imagesBean.getPath(), mIVImg, 120, 120);
                            buffer.append(imagesBean.getPath() +";");
                        }else{
                            continue;
                        }
                    }

                    String imgs = buffer.toString();
                    if(imgs.endsWith(";")){
                        imgs = imgs.substring(0, imgs.length() -1);
                    }
                    addImages(mContext, imgs, mImgContainer);
                   //mIVImg.setOnClickListener(this);
                    //mIVImg.setOnLongClickListener(this);

                }
            }else if(type == TaskType.ADD_GALLERY){
                dismissLoadingDialog();
                JSONObject resultObject = new JSONObject(String.valueOf(result));
                if(resultObject.has("isSuccess") && resultObject.getBoolean("isSuccess")){
                    Toast.makeText(mContext, "该图片已成功加入我的图库" +resultObject, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "加入我的图库失败"+(resultObject.has("message") ? ",原因是：" + resultObject.getString("message"):""), Toast.LENGTH_LONG).show();
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
                            mCommentOrTransmitAdapter = new CommentOrTransmitAdapter(mContext, mCommentOrTransmits, true);
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
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    } else {

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmits.clear();
                            mCommentOrTransmitAdapter.refreshData(new ArrayList<CommentOrTransmitBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            Toast.makeText(mContext, getStringResource(mContext, R.string.no_load_more), Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mCommentOrTransmits.clear();
                            mCommentOrTransmitAdapter.refreshData(new ArrayList<CommentOrTransmitBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext, JsonUtil.getErrorMessage(result));
                    }
                }
            }else if(type == TaskType.ADD_COMMENT || type == TaskType.ADD_TRANSMIT){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    new NotificationUtil(1, mContext).sendTipNotification("信息提示", "您的"+(type == TaskType.ADD_COMMENT? "评论": "转发")+"发表成功", "测试", 1, 0);
                    if(type == TaskType.ADD_COMMENT){
                        int commentNumber = detail.getInt("comment_number");
                        detail.put("comment_number", (commentNumber+1));
                        mTVComment.setText("评论("+(commentNumber+1)+")");
                    }else{
                        int transmitNumber = detail.getInt("transmit_number");
                        detail.put("transmit_number", (transmitNumber + 1));
                        mTVTransmit.setText("转发("+(transmitNumber +1)+")");
                    }
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
                    new NotificationUtil(1, mContext).sendActionNotification("信息提示", (type == TaskType.ADD_COMMENT? "评论": "转发") + "发表失败"+(jsonObject.has("message")? ":" +jsonObject.getString("message"): ""), "测试", 1, 0, MoodDetailActivity.class);
                }
            } else if (type == TaskType.ADD_ZAN) {
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true) {
                    Toast.makeText(mContext, "点赞成功", Toast.LENGTH_SHORT).show();
                    int zanNumber = detail.getInt("zan_number");
                    detail.put("zan_number", (zanNumber + 1));
                    mTVPraise.setText(getStringResource(mContext, R.string.personal_praise) + "(" + detail.getInt("zan_number") + ")");

                    //拼接赞用户列表数据
                    String oldZanUsers = "";
                    if(detail.has("zan_users")){
                        oldZanUsers = detail.getString("zan_users");
                    }
                    String myZanUser = null;
                    JSONObject userInfo = SharedPreferenceUtil.getUserInfo(mContext.getApplicationContext());
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
                    Toast.makeText(mContext, "点赞失败", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 将图像添加到Linnear中
     * @param context
     * @param imgs
     * @param linearLayout
     */
    public static void addImages(final Context context, String imgs, LinearLayout linearLayout){
        //异步去获取该心情的图像路径列表
        if(!StringUtil.isNull(imgs)) {
            final String[] showImages = imgs.split(";");
            if(showImages.length > 0){
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout.removeAllViewsInLayout();
                ImageView imageView = null;
                final List<ImageDetailBean> list = new ArrayList<>();
                ImageDetailBean imageDetailBean = null;
                for(int i = 0; i< showImages.length; i++){
                    imageDetailBean = new ImageDetailBean();
                    imageDetailBean.setPath(showImages[i]);
                    list.add(imageDetailBean);
                }
                for(int i = 0; i< showImages.length; i++){
                    final int index = i;
                    final String path = showImages[index];
                    if(StringUtil.isNotNull(showImages[i])){
                        imageView = new ImageView(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 80) , DensityUtil.dip2px(context, 100));
                        params.rightMargin = 16;
                        params.topMargin = 3;
                        imageView.setLayoutParams(params);
                        imageView.setMaxWidth(100);
                        imageView.setMaxHeight(120);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        ImageCacheManager.loadImage(path, imageView, 80, 100);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonHandler.startImageDetailActivity(context, list, index);
                            }
                        });
                        imageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                //创建离开的警告提示框
                                /*AlertDialog alertDialog = new AlertDialog.Builder(MoodDetailFragment.newInstance(null)).setTitle("添加图库")
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
                                                TaskLoader.getInstance().startTaskForResult(TaskType.ADD_GALLERY, MoodDetailFragment.this, requestBean);
                                                showLoadingDialog("Gallery","Loading, try my best to adding gallery...");
                                            }
                                        })
                                        .setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                return;
                                            }
                                        }).create(); // 创建对话框
                                alertDialog.show(); // 显示对话框*/
                                return true;
                            }
                        });
                        linearLayout.addView(imageView);
                    }
                }
                return;
            }
        }
        linearLayout.setVisibility(View.GONE);
    }

   /* private void showAddGallery(){

    }*/

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
    protected void sendFirstLoading(){
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        //HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
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
            CommentHandler.getCommentsRequest(MoodDetailFragment.this, params);

        }else {
            taskCanceled(TaskType.LOAD_TRANSMIT);
            mTransmitLine.setVisibility(View.VISIBLE);
            mCommentLine.setVisibility(View.GONE);
            //mCommentOrTransmits.clear();
            //mCommentOrTransmitAdapter = null;
            // mTransmitAdapter = new TransmitAdapter(MoodDetailActivity.this, mCommentOrTransmits);
            TransmitHandler.getTransmitsRequest(MoodDetailFragment.this, params);
        }

    }

    /**
     * 发送向上刷新的任务
     */
    protected void sendUpLoading(){
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
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        //获取当前是评论还是转发
        if(commentOrTransmit == 0){
            CommentHandler.getCommentsRequest(MoodDetailFragment.this, params);
        }else{
            TransmitHandler.getTransmitsRequest(MoodDetailFragment.this, params);
        }
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        if(commentOrTransmit == 0){
            CommentHandler.getCommentsRequest(MoodDetailFragment.this, params);
        }else{
            TransmitHandler.getTransmitsRequest(MoodDetailFragment.this, params);
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
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mListViewFooter.getText().toString())){
            return;
        }
        taskCanceled(TaskType.LOAD_COMMENT);
        taskCanceled(TaskType.LOAD_TRANSMIT);
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("table_name", "t_mood");
        params.put("table_id", mid);
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("showUserInfo", true);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        if(commentOrTransmit == 0){
            CommentHandler.getCommentsRequest(MoodDetailFragment.this, params);
        }else{
            TransmitHandler.getTransmitsRequest(MoodDetailFragment.this, params);
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        HashMap<String, Object> params = new HashMap<>();
        switch (v.getId()){
            /*case R.id.mood_detail_img:
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
                CommonHandler.startImageDetailActivity(mContext, list, 0);
                break;*/
            case R.id.mood_detail_comment:
                //Toast.makeText(MoodDetailActivity.this, "评论", Toast.LENGTH_SHORT).show();
                commentOrTransmit = 0;
                onItemClickListener.clearPosition();
                onOperateTypeChangeListener.change(commentOrTransmit);
                sendFirstLoading();
                break;
            case R.id.mood_detail_transmit:
                commentOrTransmit = 1;
                onItemClickListener.clearPosition();
                onOperateTypeChangeListener.change(commentOrTransmit);
                sendFirstLoading();
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
            case R.id.mood_detail_user:
                try {
                    CommonHandler.startPersonalActivity(mContext, detail.getInt("create_user_id"));
                }catch (JSONException e){
                    Log.i(TAG, "获取从json中获取创建人失败："+e.getMessage().toString());
                }
                break;
            case R.id.mood_detail_user_pic:
                try {
                    CommonHandler.startPersonalActivity(mContext, detail.getInt("create_user_id"));
                }catch (JSONException e){
                    Log.i(TAG, "获取从json中获取创建人失败："+e.getMessage().toString());
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            /*case R.id.mood_detail_img:
                //创建离开的警告提示框
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("添加图库")
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
                                TaskLoader.getInstance().startTaskForResult(TaskType.ADD_GALLERY, MoodDetailFragment.this, requestBean);
                                showLoadingDialog("Gallery","Loading, try my best to adding gallery...");
                            }
                        })
                        .setNegativeButton("放弃",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create(); // 创建对话框
                alertDialog.show(); // 显示对话框
                break;*/
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
