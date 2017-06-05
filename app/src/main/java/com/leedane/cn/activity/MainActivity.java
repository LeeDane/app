package com.leedane.cn.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.HomeAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.HttpResponseBlogBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.database.BlogDataBase;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.BlogHandler;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.DialogHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.helper.DoubleClickExitHelper;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ImageUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.SharedPreferenceUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends NavigationActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    public static final int[] bgColors = new int[]{R.drawable.tag_textview_bg_blue_account_link, R.drawable.tag_textview_bg_blue_primary_dark, R.drawable.tag_textview_bg_blue_primary_light
            , R.drawable.tag_textview_bg_color_accent, R.drawable.tag_textview_bg_gray, R.drawable.tag_textview_bg_unknow_color_1, R.drawable.tag_textview_bg_red
            , R.drawable.tag_textview_bg_result_view, R.drawable.tag_textview_bg_unknow_color_2, R.drawable.tag_textview_bg_unknow_color_3, R.drawable.tag_textview_bg_unknow_color_4
            , R.drawable.tag_textview_bg_unknow_color_5};

    public static final String TAG = "MainActivity";

    private BlogDataBase blogDataBase; //数据库

    //跳转到登录页面的请求码
    public static final int LOGIN_REQUEST_CODE = 100;

    /**
     * 头像的imageview
     */
    private CircularImageView mHeadPortraitImageView;

    /**
     * 用户名的文本
     */
    private TextView mTextViewUsername;

    /**
     * 用户邮箱的文本
     */
    private TextView mTextViewEmail;

    /**
     * 登录按钮
     */
    private Button mButtonLogin;

    /**
     * 欢迎登录的提示文字
     */
    private TextView mTextviewWelcomeLogin;

    //展示所有博客信息的ListView对象
    private ListView mlistViewBlogs;

    //自定义的适配器对象
    HomeAdapter mAdapter;

    //所有的博客对象
    private List<BlogBean> mBlogs = new ArrayList<BlogBean>();

    //当前listview中最旧一篇文章的id
    private int mFirstId;

    //当前listview中最新一篇文章的id
    private int mLastId;

    //当前的加载方式
    private String mPreLoadMethod;

    //下拉刷新的对象
    private SwipeRefreshLayout mySwipeRefreshLayout;

    //长按的项的索引(位置)
    private int mClickPosition;

    /**
     * ListView底部控制
     */
    private TextView mListViewFooter;
    private View viewFooter;

    private boolean isLoading;

    //加载DiaLog
    private ProgressDialog mProgressDialog;

    private JSONObject mUserInfo;
    private int mLoginAccountId;

    public static boolean isForeground = false;

    private DoubleClickExitHelper mDoubleClickExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedIsLogin();
        //初始化控件
        initView();
    }

    /**
     * 检查是否登录
     */
    private void checkedIsLogin() {
        //判断是否有缓存用户信息
        if(BaseApplication.getLoginUserId() > 0 ){
            try {
                mLoginAccountId = BaseApplication.getLoginUserId();
            }catch (Exception e){
                Log.i(TAG, "获取缓存的用户名称为空");
            }
        }
        initJPush();
    }

    /**
     * 初始化极光推送
     */
    private void initJPush(){
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        /*BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(MainActivity.this);
        builder.
        JPushInterface.setDefaultPushNotificationBuilder();*/
        JPushInterface.setAlias(getApplicationContext(), "leedane_user_" + mLoginAccountId, new TagAliasCallback() {
            @Override
            public void gotResult(int responseCode, String s, Set<String> set) {
                ToastUtil.success(getApplicationContext(), mLoginAccountId + ",responseCode=" + responseCode + ",s=" + s + ",set=" + set);
            }
        });
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(getApplicationContext());
        isForeground = true;
        super.onResume();
        updateShowUserinfo();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(getApplicationContext());
        isForeground = false;
        super.onPause();
    }
    /**
     * 检查是否加载远程服务器上的数据
     */
    private boolean checkedIsLoadServerBlog() {
        return true;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mDoubleClickExit = new DoubleClickExitHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动用户中心
                CommonHandler.startUserInfoActivity(MainActivity.this);
            }
        });*/

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setBackgroundResource(R.drawable.head);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(500, 500);
        itemIcon.setLayoutParams(layoutParams);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .attachTo(fab)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mButtonLogin = (Button)findViewById(R.id.button_login);
        mHeadPortraitImageView = (CircularImageView)findViewById(R.id.imageview_head_portrait);
        mTextViewUsername = (TextView)findViewById(R.id.textview_username);
        mTextViewEmail = (TextView)findViewById(R.id.textview_email);
        mTextviewWelcomeLogin = (TextView)findViewById(R.id.textview_welcomelogin);

        updateShowUserinfo();

        mlistViewBlogs = (ListView)findViewById(R.id.list_blog);
        mlistViewBlogs.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        //listview下方的显示
        viewFooter = LayoutInflater.from(MainActivity.this).inflate(R.layout.listview_footer_item, null);
        mlistViewBlogs.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(this);//添加点击事件
        mListViewFooter.setText(getStringResource(R.string.loading));


        //加载本地数据库的数据
        blogDataBase = new BlogDataBase(MainActivity.this);
        mBlogs = blogDataBase.queryBlogLimit25();
        if(mBlogs.size() > 0){
            mFirstId = mBlogs.get(0).getId();
            mLastId = mBlogs.get(mBlogs.size() - 1).getId();
        }else{
            sendFirstLoading();
        }
        mAdapter = new HomeAdapter(mBlogs, MainActivity.this, mlistViewBlogs);
        mlistViewBlogs.setAdapter(mAdapter);
        mlistViewBlogs.setOnScrollListener(new ListViewOnScrollListener());
        mlistViewBlogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mBlogs != null && mBlogs.size() > 1 && position != mBlogs.size() -1) {
                    mClickPosition = position;
                    //查看全文详情
                    startLookDetailActivity();
                }
            }
        });

        mlistViewBlogs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "触发Listview的长按事件");
                mClickPosition = position;
                //这里返回值必须是false,否则无法触发弹出上下文菜单
                return false;
            }
        });
        registerForContextMenu(mlistViewBlogs);

        //下拉刷新
        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mySwipeRefreshLayout.setOnRefreshListener(this);
        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        //检查是否加载远程服务器上的数据
        if(checkedIsLoadServerBlog()){

        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //mAdapter.notifyDataSetChanged();
    }

    /**
     * 更新展示用户的信息
     */
    private void updateShowUserinfo() {

        mUserInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());

        //判断是否有缓存用户信息
        if(mUserInfo != null && mUserInfo.has("account")){
            mButtonLogin.setVisibility(View.GONE);
            mTextviewWelcomeLogin.setVisibility(View.GONE);

            mHeadPortraitImageView.setVisibility(View.VISIBLE);
            mTextViewUsername.setVisibility(View.VISIBLE);
            mTextViewEmail.setVisibility(View.VISIBLE);

            try {
                if(mUserInfo.has("pic_base64") && !StringUtil.isNull(mUserInfo.getString("pic_base64"))){
                   Bitmap bitmap = ImageUtil.getInstance().getBitmapByBase64(mUserInfo.getString("pic_base64"));
                    if(bitmap != null){
                        mHeadPortraitImageView.setImageBitmap(bitmap);
                    }else{
                        mHeadPortraitImageView.setImageResource(R.mipmap.head);
                    }
                }

                if(mUserInfo.has("user_pic_path") && !StringUtil.isNull(mUserInfo.getString("user_pic_path"))){
                    ImageCacheManager.loadImage(mUserInfo.getString("user_pic_path"), mHeadPortraitImageView );
                }else{
                    mHeadPortraitImageView.setImageResource(R.drawable.no_pic);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHeadPortraitImageView.setOnClickListener(this);
            try {
                mLoginAccountId = mUserInfo.getInt("id");
                mTextViewUsername.setText(StringUtil.changeNotNull(mUserInfo.getString("account")));
                Object obj = mUserInfo.getString("email");
                String str = StringUtil.changeNotNull(obj);
                mTextViewEmail.setText(StringUtil.changeNotNull(mUserInfo.getString("email")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //启动消息推送服务
           /* ServiceManager serviceManager = new ServiceManager(this);
            serviceManager.setNotificationIcon(R.drawable.notification);
            serviceManager.startService();*/

            if(pMenu != null){
                for(int i = 0; i < pMenu.size(); i++){
                    if(pMenu.getItem(i).getItemId() == R.id.nav_loginout){
                        pMenu.getItem(i).setVisible(true);
                        break;
                    }
                }
            }
        }else{
            mHeadPortraitImageView.setVisibility(View.GONE);
            mTextViewUsername.setVisibility(View.GONE);
            mTextViewEmail.setVisibility(View.GONE);

            mButtonLogin.setVisibility(View.VISIBLE);
            mTextviewWelcomeLogin.setVisibility(View.VISIBLE);
            mButtonLogin.setOnClickListener(this);
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(mySwipeRefreshLayout.isRefreshing()){
            mySwipeRefreshLayout.setRefreshing(false);
        }
        if(type == TaskType.HOME_LOADBLOGS){
            isLoading = false;
            if(mySwipeRefreshLayout !=null && mySwipeRefreshLayout.isRefreshing());
        }
        dismissLoadingDialog();
        if(result == null || result instanceof Error){
            if(!"uploading".equalsIgnoreCase(mPreLoadMethod)){
                //mListViewFooter.setText(getStringResource(R.string.load_more_error));
                mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(R.string.click_to_load));
                mListViewFooter.setOnClickListener(this);
            }
            return;
        }
        try{
            if(result instanceof String){
                try{
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    if(jsonObject.has("isSuccess")){
                        //删除
                        if(TaskType.DELETE_BLOG == type){
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, "删除成功");
                                List<BlogBean> temList = new ArrayList<>();
                                for(BlogBean blogBean: mBlogs){
                                    temList.add(blogBean);
                                }
                                //把本地的博客数据也删除掉
                                blogDataBase.delete(temList.get(mClickPosition).getId());

                                temList.remove(mClickPosition);
                                mAdapter.refreshData(temList);
                                Log.i(TAG, "删除后文章的数量：" + mBlogs.size());
                            }else {
                                ToastUtil.failure(MainActivity.this, jsonObject);
                            }
                        }
                        //评论
                        if(TaskType.ADD_COMMENT == type){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, "评论成功");
                            }else{
                                ToastUtil.failure(MainActivity.this, "评论发表失败"+(jsonObject.has("message")? ":" +jsonObject.getString("message"):""));
                            }
                        }
                        //转发
                        if(TaskType.ADD_TRANSMIT == type){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, "转发成功");
                            }else{
                                ToastUtil.failure(MainActivity.this, "转发失败"+(jsonObject.has("message")? ":"+jsonObject.getString("message"):""));
                            }
                        }

                        //收藏
                        if(TaskType.ADD_COLLECTION == type){
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, "收藏成功");
                            }else{
                                ToastUtil.failure(MainActivity.this, jsonObject);
                            }
                            //关注
                        }else if(TaskType.ADD_ATTENTION == type){
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, "关注成功");
                            }else{
                                ToastUtil.failure(MainActivity.this, jsonObject);
                            }
                        }else if(type == TaskType.GET_APP_VERSION){
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                //ToastUtil.success(MainActivity.this, "获取新版本成功" + jsonObject.toString(), Toast.LENGTH_SHORT);
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                DialogHandler.showNewestVersion(MainActivity.this, jsonArray.getJSONObject(0));
                            }else{
                                ToastUtil.failure(MainActivity.this, "获取新版本失败", Toast.LENGTH_SHORT);
                            }
                            return;
                        }else if(type == TaskType.ADD_TAG){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                                ToastUtil.success(MainActivity.this, jsonObject);
                            }else{
                                ToastUtil.failure(MainActivity.this, jsonObject);
                            }
                            return;
                        }
                    }
                }catch (Exception e){
                    ToastUtil.failure(MainActivity.this, "响应的数据解析异常" + result, Toast.LENGTH_SHORT);
                    if("lowloading".equalsIgnoreCase(mPreLoadMethod)){
                        mlistViewBlogs.removeFooterView(viewFooter);
                        mlistViewBlogs.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(R.string.click_to_load));
                        mListViewFooter.setOnClickListener(this);
                    }
                    e.printStackTrace();
                }
            }
            if(result instanceof HttpResponseBlogBean){
                HttpResponseBlogBean httpResponseBlogBean = (HttpResponseBlogBean)result;
                if(httpResponseBlogBean != null && httpResponseBlogBean.isSuccess()){
                    //清空本地的博客数据
                    //blogDataBase.deleteAll();

                    List<BlogBean> blogBeans = httpResponseBlogBean.getMessage();
                    if(blogBeans != null && blogBeans.size() > 0){
                        //临时list
                        List<BlogBean> temList = new ArrayList<>();
                        if("firstloading".equalsIgnoreCase(mPreLoadMethod)){
                            mlistViewBlogs.removeAllViewsInLayout();
                            mBlogs.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if("uploading".equalsIgnoreCase(mPreLoadMethod)){
                            for(int i = blogBeans.size() -1; i>= 0 ; i--){
                                temList.add(blogBeans.get(i));
                            }
                            temList.addAll(mBlogs);
                        }else{
                            temList.addAll(mBlogs);
                            temList.addAll(blogBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mBlogs.size());
                        if(mAdapter == null) {
                            mAdapter = new HomeAdapter(mBlogs, MainActivity.this, mlistViewBlogs);
                            mlistViewBlogs.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mBlogs.size());

                        int size = mBlogs.size();
                        if(size > 0){
                            mFirstId = mBlogs.get(0).getId();
                            mLastId = mBlogs.get(size - 1).getId();
                        }
                        //将ListView的位置设置为0
                        if("firstloading".equalsIgnoreCase(mPreLoadMethod)){
                            //mlistViewBlogs.smoothScrollToPosition(0);
                            //mlistViewBlogs.setSelection(0);
                        }
                        if(MySettingConfigUtil.cache_blog) {
                            //把获取到的数据全部加载到博客数据库中
                            for (BlogBean bb : mBlogs) {
                                blogDataBase.insert(bb);
                            }
                        }

                    }else{
                        if("firstloading".equalsIgnoreCase(mPreLoadMethod)){
                            mBlogs.clear();
                            mAdapter.refreshData(new ArrayList<BlogBean>());
                        }
                        if(!"uploading".equalsIgnoreCase(mPreLoadMethod)){
                            mlistViewBlogs.removeFooterView(viewFooter);
                            mlistViewBlogs.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(R.string.no_load_more));
                        }else {
                            ToastUtil.success(MainActivity.this, getStringResource(R.string.no_load_more));
                        }
                    }
                }else{
                    if(!"uploading".equalsIgnoreCase(mPreLoadMethod)){
                        if("firstloading".equalsIgnoreCase(mPreLoadMethod)){
                            mBlogs.clear();
                            mAdapter.refreshData(new ArrayList<BlogBean>());
                        }
                        mlistViewBlogs.removeFooterView(viewFooter);
                        mlistViewBlogs.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(R.string.click_to_load));
                    }else{
                        ToastUtil.failure(MainActivity.this);
                    }
                }
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取字符串资源
     * @param resourseId
     * @return
     */
    protected String getStringResource(int resourseId){
        return getResources().getString(resourseId);
    }

    @Override
    public void taskStarted(TaskType type) {

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
                        taskCanceled(TaskType.HOME_LOADBLOGS);
                        sendLowLoading();
                    }
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // return true;//返回真表示返回键被屏蔽掉
            if(MySettingConfigUtil.double_click_out){
                return mDoubleClickExit.onKeyDown(keyCode, event);
            }else{
                createLeaveAlertDialog();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 发送第一次刷新的任务
     */
    private void sendFirstLoading(){
        //第一次操作取消全部数据
        taskCanceled(TaskType.HOME_LOADBLOGS);
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.first_load);
        params.put("method", mPreLoadMethod);
        BlogHandler.getBlogsRequest(this, params);
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
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.HOME_LOADBLOGS);
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("method",mPreLoadMethod );
        BlogHandler.getBlogsRequest(this, params);
    }

    /**
     * 发送向下刷新的任务
     */
    private void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString()) || isLoading) {
            return;
        }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }
        mListViewFooter.setText(getStringResource(R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        BlogHandler.getBlogsRequest(this, params);
    }

    //下拉刷新监听需要实现的方法
    @Override
    public void onRefresh() {
        sendUpLoading();
    }


    @Override
    public void onClick(View v) {
        int clickViewId = v.getId();
        switch (clickViewId){
            case R.id.imageview_head_portrait:
                /*ToastUtil.success(MainActivity.this, "点击头像");*/
                CommonHandler.startUpdateHeaderActivity(MainActivity.this);
                break;
            case R.id.button_login:
                ToastUtil.success(MainActivity.this, "将跳转到登录页面");
                Intent it = new Intent();
                it.setClass(MainActivity.this, LoginActivity.class);
                startActivityForResult(it, MainActivity.LOGIN_REQUEST_CODE);
                break;
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
            default:
                ToastUtil.success(MainActivity.this, "未知点击事件");
                break;
        }
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())
                ||  getStringResource(R.string.load_finish).equalsIgnoreCase(mListViewFooter.getText().toString())){
            return;
        }
        taskCanceled(TaskType.HOME_LOADBLOGS);
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", "firstloading".equalsIgnoreCase(mPreLoadMethod) ? MySettingConfigUtil.first_load: MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mListViewFooter.setText(getStringResource(R.string.loading));
        BlogHandler.getBlogsRequest(this, params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MainActivity.LOGIN_REQUEST_CODE:
                updateShowUserinfo();
                initJPush();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, getStringResource(R.string.personal_look));
        menu.add(0, Menu.FIRST+1, 0, getStringResource(R.string.delete));
        menu.add(0, Menu.FIRST+2, 0, getStringResource(R.string.personal_collection));
        menu.add(0, Menu.FIRST+3, 0, getStringResource(R.string.attention));
        menu.add(0, Menu.FIRST+4, 0, getStringResource(R.string.comment));
        menu.add(0, Menu.FIRST+5, 0, getStringResource(R.string.transmit));
        menu.add(0, Menu.FIRST+6, 0, getStringResource(R.string.add_tag));
        menu.add(0, Menu.FIRST+7, 0, getStringResource(R.string.unlike));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        StringBuffer notice = new StringBuffer();
        switch (item.getItemId()){
            case 1://查看详情
                startLookDetailActivity();
                break;
            case 2://删除
                HashMap<String, Object> params = new HashMap<>();
                params.put("b_id", mBlogs.get(mClickPosition).getId());
                BlogHandler.deleteBlog(this, params);
                showLoadingDialog();
                break;
            case 3://收藏
                executeCollection();
                break;
            case 4://关注
                executeAttention();
                break;
            case 5: //评论
                notice.append("注意事项：\r\n");
                notice.append("1、输入的内容不能为空值，长度最好不要超过255个字符。\r\n");
                notice.append("2、请不要输入跟该博客无关的内容。\r\n");
                notice.append("3、不能包含违法中华人民共和国相关法律法规的信息(内容)。\r\n");
                notice.append("4、建议不要输入一些特殊字符/字符串/emoji表情(系统会自动过滤掉)。\r\n");
                showTextDialog(0, notice.toString());
                break;
            case 6://转发
                notice.append("注意事项：\r\n");
                notice.append("1、输入的内容不能为空值，长度最好不要超过255个字符。\r\n");
                notice.append("2、请不要输入跟该博客无关的内容。\r\n");
                notice.append("3、不能包含违法中华人民共和国相关法律法规的信息(内容)。\r\n");
                notice.append("4、建议不要输入一些特殊字符/字符串/emoji表情(系统会自动过滤掉)。\r\n");
                showTextDialog(1, notice.toString());
                break;
            case 7: //添加标签
                notice.append("注意事项：\r\n");
                notice.append("1、输入的内容不能为空值，长度不要超过5个字符。\r\n");
                notice.append("2、标签内容不能出现\",\"或者\"，\"分号\r\n");
                notice.append("3、不能包含违法中华人民共和国相关法律法规的信息(内容)。\r\n");
                notice.append("4、建议不要输入一些特殊字符/字符串/emoji表情(系统会自动过滤掉)。\r\n");
                showTextDialog(2, notice.toString());
                break;
            case 8: //不喜欢
                ToastUtil.success(MainActivity.this, mClickPosition + "-----" + item.getTitle());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private Dialog textDialog;
    /**
     * 显示弹出自定义文本view
     * @param type 0: 评论， 1：转发， 2：添加标签
     */
    public void showTextDialog(final int type, String notice){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissTextDialog();

        textDialog = new Dialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.show_text_dialog, null);

        final EditText editText = (EditText)view.findViewById(R.id.show_text_dialog_text);
        TextView submitBtn = (TextView)view.findViewById(R.id.show_text_dialog_submit);
        TextView noticeTV = (TextView)view.findViewById(R.id.show_text_dialog_notice);
        noticeTV.setText(StringUtil.changeNotNull(notice));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if(StringUtil.isNull(content) && (type == 0 || type == 2)){
                    editText.setFocusable(true);
                    ToastUtil.failure(MainActivity.this, "请输入点什么吧");
                    return;
                }

                if(StringUtil.isNotNull(content) && type == 2 && content.length() > 5){
                    editText.setFocusable(true);
                    ToastUtil.failure(MainActivity.this, "标签长度不能超过5位");
                    return;
                }
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("table_name", "t_blog");
                params.put("table_id", mBlogs.get(mClickPosition).getId());
                if(type == 0){
                    params.put("content", content);
                    params.put("level", 1);
                    CommentHandler.sendComment(MainActivity.this,params);
                }else if(type == 1){
                    params.put("content", StringUtil.isNull(content)? "转发了这条心情" :content);
                    TransmitHandler.sendTransmit(MainActivity.this, params);
                }else if(type == 2){
                    BlogHandler.addTag(MainActivity.this, mBlogs.get(mClickPosition).getId(), content);
                }

                showLoadingDialog();
            }
        });

        textDialog.setTitle("操作");
        textDialog.setCancelable(true);
        textDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissTextDialog();
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        textDialog.setContentView(view, layoutParams);
        textDialog.show();
    }

    private void dismissTextDialog(){
        if(textDialog != null && textDialog.isShowing()){
            textDialog.dismiss();
        }
    }

    /**
     * 启动查看全文详情的activity
     */
    private void startLookDetailActivity(){
        //传递获取博客id
        int blog_id = mBlogs.get(mClickPosition).getId();
        //传递获取标题
        String title = mBlogs.get(mClickPosition).getTitle();

        if (StringUtil.isNull(title) || blog_id < 1) {
            ToastUtil.failure(getApplicationContext(), "不能点击，请核实博客id是否大于0并且标题不能为空", Toast.LENGTH_SHORT);
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("title", title);
        CommonHandler.startDetailActivity(MainActivity.this, "t_blog", blog_id, params);
    }

    /**
     * 执行收藏
     */
    public void executeCollection(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_id", mBlogs.get(mClickPosition).getId());
        params.put("table_name", "t_blog");
        CollectionHandler.sendCollection(this, params);
        showLoadingDialog();
    }

    /**
     * 执行关注
     */
    public void executeAttention(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("table_id", mBlogs.get(mClickPosition).getId());
        params.put("table_name", "t_blog");
        AttentionHandler.sendAttention(this, params);
        showLoadingDialog();
    }

    /**
     * 显示加载Dialog
     */
    private void showLoadingDialog(){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(MainActivity.this, "", "Loading. Please wait...", true);
    }

    /**
     * 隐藏加载的Dialog
     */
    private void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if(blogDataBase != null)
            blogDataBase.destroy();
        super.onDestroy();
    }
}
