package com.leedane.cn.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.MainAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.BlogBean;
import com.leedane.cn.bean.HttpResponseBlogBean;
import com.leedane.cn.database.BlogDataBase;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.BlogHandler;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.DialogHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 博客首页的activity
 * Created by LeeDane on 2018/1/18.
 */
public class BlogActivity extends BaseActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    public static final int[] bgColors = new int[]{R.drawable.tag_textview_bg_blue_account_link, R.drawable.tag_textview_bg_blue_primary_dark, R.drawable.tag_textview_bg_blue_primary_light
            , R.drawable.tag_textview_bg_color_accent, R.drawable.tag_textview_bg_gray, R.drawable.tag_textview_bg_unknow_color_1, R.drawable.tag_textview_bg_red
            , R.drawable.tag_textview_bg_result_view, R.drawable.tag_textview_bg_unknow_color_2, R.drawable.tag_textview_bg_unknow_color_3, R.drawable.tag_textview_bg_unknow_color_4
            , R.drawable.tag_textview_bg_unknow_color_5};

    public static final String TAG = "BlogActivity";

    private BlogDataBase blogDataBase; //数据库

    //展示所有博客信息的ListView对象
    private ListView mlistViewBlogs;

    //自定义的适配器对象
    MainAdapter mAdapter;

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

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedIsLogin();
        setContentView(R.layout.activity_blog);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        //显示整个顶部的导航栏
        backLayoutVisible();
        //以用户名称作为个人中心的标题
        setTitleViewText(getStringResource(R.string.blog));
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    PushClient client = new PushClient();
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);*/

        //初始化控件
        initView();
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
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
        mlistViewBlogs = (ListView)findViewById(R.id.list_blog);
        mlistViewBlogs.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        //listview下方的显示
        viewFooter = LayoutInflater.from(BlogActivity.this).inflate(R.layout.listview_footer_item, null);
        mlistViewBlogs.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(this);//添加点击事件
        mListViewFooter.setText(getStringResource(R.string.loading));


        //加载本地数据库的数据
        blogDataBase = new BlogDataBase(BlogActivity.this);
        mBlogs = blogDataBase.queryBlogLimit25();
        if(mBlogs.size() > 0){
            mFirstId = mBlogs.get(0).getId();
            mLastId = mBlogs.get(mBlogs.size() - 1).getId();
        }else{
            sendFirstLoading();
        }
        mAdapter = new MainAdapter(mBlogs, BlogActivity.this, mlistViewBlogs);
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
                    if(jsonObject != null){
                        //删除
                        if(TaskType.DELETE_BLOG == type){
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, "删除成功");
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
                                ToastUtil.failure(BlogActivity.this, jsonObject);
                            }
                        }
                        //评论
                        if(TaskType.ADD_COMMENT == type){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, "评论成功");
                            }else{
                                ToastUtil.failure(BlogActivity.this, "评论发表失败"+(jsonObject.has("message")? ":" +jsonObject.getString("message"):""));
                            }
                        }
                        //转发
                        if(TaskType.ADD_TRANSMIT == type){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, "转发成功");
                            }else{
                                ToastUtil.failure(BlogActivity.this, "转发失败"+(jsonObject.has("message")? ":"+jsonObject.getString("message"):""));
                            }
                        }

                        //收藏
                        if(TaskType.ADD_COLLECTION == type){
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, "收藏成功");
                            }else{
                                ToastUtil.failure(BlogActivity.this, jsonObject);
                            }
                            //关注
                        }else if(TaskType.ADD_ATTENTION == type){
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, "关注成功");
                            }else{
                                ToastUtil.failure(BlogActivity.this, jsonObject);
                            }
                        }else if(type == TaskType.GET_APP_VERSION){
                            if(jsonObject.optBoolean("isSuccess")){
                                //ToastUtil.success(MainActivity.this, "获取新版本成功" + jsonObject.toString(), Toast.LENGTH_SHORT);
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                DialogHandler.showNewestVersion(BlogActivity.this, jsonArray.getJSONObject(0));
                            }else{
                                ToastUtil.failure(BlogActivity.this, jsonObject, Toast.LENGTH_SHORT);
                            }
                            return;
                        }else if(type == TaskType.ADD_TAG){
                            dismissLoadingDialog();
                            dismissTextDialog();
                            if(jsonObject.optBoolean("isSuccess")){
                                ToastUtil.success(BlogActivity.this, jsonObject);
                            }else{
                                ToastUtil.failure(BlogActivity.this, jsonObject);
                            }
                            return;
                        }
                    }
                }catch (Exception e){
                    ToastUtil.failure(BlogActivity.this, "响应的数据解析异常" + result, Toast.LENGTH_SHORT);
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
                            mAdapter = new MainAdapter(mBlogs, BlogActivity.this, mlistViewBlogs);
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
                            ToastUtil.success(BlogActivity.this, getStringResource(R.string.no_load_more));
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
                        ToastUtil.failure(BlogActivity.this);
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
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
            default:
                ToastUtil.success(BlogActivity.this, "未知点击事件");
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, getStringResource(R.string.personal_look));
        menu.add(0, Menu.FIRST+1, 0, getStringResource(R.string.delete));
        menu.add(0, Menu.FIRST+2, 0, getStringResource(R.string.personal_collection));
        menu.add(0, Menu.FIRST+3, 0, getStringResource(R.string.add_attention));
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
                ToastUtil.success(BlogActivity.this, mClickPosition + "-----" + item.getTitle());
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

        textDialog = new Dialog(BlogActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(BlogActivity.this).inflate(R.layout.show_text_dialog, null);

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
                    ToastUtil.failure(BlogActivity.this, "请输入点什么吧");
                    return;
                }

                if(StringUtil.isNotNull(content) && type == 2 && content.length() > 5){
                    editText.setFocusable(true);
                    ToastUtil.failure(BlogActivity.this, "标签长度不能超过5位");
                    return;
                }
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("table_name", "t_blog");
                params.put("table_id", mBlogs.get(mClickPosition).getId());
                if(type == 0){
                    params.put("content", content);
                    params.put("level", 1);
                    CommentHandler.sendComment(BlogActivity.this,params);
                }else if(type == 1){
                    params.put("content", StringUtil.isNull(content)? "转发了这条心情" :content);
                    TransmitHandler.sendTransmit(BlogActivity.this, params);
                }else if(type == 2){
                    BlogHandler.addTag(BlogActivity.this, mBlogs.get(mClickPosition).getId(), content);
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
        CommonHandler.startDetailActivity(BlogActivity.this, "t_blog", blog_id, params);
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
        mProgressDialog = ProgressDialog.show(BlogActivity.this, "", "Loading. Please wait...", true);
    }

    @Override
    protected void onDestroy() {
        if(blogDataBase != null)
            blogDataBase.destroy();
        super.onDestroy();
    }
}
