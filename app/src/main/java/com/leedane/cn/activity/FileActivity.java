package com.leedane.cn.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.FileAdapter;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.FileBean;
import com.leedane.cn.bean.HttpResponseFileBean;
import com.leedane.cn.database.BaseSQLiteDatabase;
import com.leedane.cn.database.FileDataBase;
import com.leedane.cn.handler.FileHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件activity
 * Created by LeeDane on 2016/1/24.
 */
public class FileActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "FileActivity";

    private FileDataBase fileDataBase; //数据库
    /**
     * 是否有上传文件
     */
    public static final int IS_UPLOAD_FILE = 66;

    private ListView mListView;
    private FileAdapter mAdapter;
    private List<FileBean> mFiles;
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
    /**
     * 上传文件的imageview
     */
    private ImageView mRightImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(FileActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.FileActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        setContentView(R.layout.activity_file);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.nav_file);
        backLayoutVisible();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mListView = (ListView)findViewById(R.id.file_listview);

        initData();
        mAdapter = new FileAdapter(FileActivity.this, mFiles);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "触发Listview的长按事件");
                mClickPosition = position;
                //这里返回值必须是false,否则无法触发弹出上下文菜单
                return false;
            }
        });
        mListView.setOnScrollListener(new ListViewOnScrollListener());
        registerForContextMenu(mListView);

        //listview下方的显示
        viewFooter = LayoutInflater.from(FileActivity.this).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)viewFooter.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(this);//添加点击事件
        mListViewFooter.setText(getStringResource(R.string.loading));


        //显示跳转到上传文件页面的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setImageResource(R.mipmap.download);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        //下拉刷新
        mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mySwipeRefreshLayout.setOnRefreshListener(this);
        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

    }

    /**
     * 初始化数据
     */
    private void initData() {

        mFiles = new ArrayList<>();
        //加载本地数据库的数据
        fileDataBase = new FileDataBase(FileActivity.this);
        mFiles = fileDataBase.queryFileLimit50();
        if(mFiles.size() > 0){
            mFirstId = mFiles.get(0).getId();
            mLastId = mFiles.get(mFiles.size() - 1).getId();
        }else{
            sendFirstLoading();
        }
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        if(type == TaskType.LOAD_FILE){
            isLoading = false;
            if(mySwipeRefreshLayout !=null && mySwipeRefreshLayout.isRefreshing())
                mySwipeRefreshLayout.setRefreshing(false);//下拉刷新组件停止刷新
        }
        dismissLoadingDialog();
        try{
            if(type == TaskType.LOAD_FILE){
                if(mySwipeRefreshLayout !=null && mySwipeRefreshLayout.isRefreshing())
                    mySwipeRefreshLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseFileBean httpResponseFileBean = BeanConvertUtil.strConvertToFileBeans(String.valueOf(result));
                if(httpResponseFileBean != null && httpResponseFileBean.isSuccess()){
                    List<FileBean> fileBeans = httpResponseFileBean.getMessage();
                    if(fileBeans != null && fileBeans.size() > 0){
                        //临时list
                        List<FileBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mFiles.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = fileBeans.size() -1; i>= 0 ; i--){
                                temList.add(fileBeans.get(i));
                            }
                            temList.addAll(mFiles);
                        }else{
                            temList.addAll(mFiles);
                            temList.addAll(fileBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mMoodBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new FileAdapter(FileActivity.this, mFiles);
                            mListView.setAdapter(mAdapter);
                        }

                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mMoodBeans.size());

                        //Toast.makeText(mContext, "成功加载"+ moodBeans.size() + "条数据,总数是："+mMoodBeans.size(), Toast.LENGTH_SHORT).show();
                        int size = mFiles.size();

                        mFirstId = mFiles.get(0).getId();
                        mLastId = mFiles.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        if(MySettingConfigUtil.getCacheMood()){
                            for(FileBean fb: fileBeans){
                                fileDataBase.insert(fb);
                            }
                        }
                        mListViewFooter.setText(getStringResource( R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFiles.clear();
                            mAdapter.refreshData(new ArrayList<FileBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(R.string.no_load_more));
                        }else {
                            ToastUtil.success(FileActivity.this, getStringResource(R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mFiles.clear();
                            mAdapter.refreshData(new ArrayList<FileBean>());
                        }
                        mListView.removeFooterView(viewFooter);
                        mListView.addFooterView(viewFooter, null, false);
                        mListViewFooter.setText(getStringResource(R.string.load_more_error));
                        mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(FileActivity.this, "获取文件列表失败啦");
                    }
                }
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if(fileDataBase != null)
            fileDataBase.destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IS_UPLOAD_FILE) {

                ToastUtil.failure(FileActivity.this, "是否有上传了文件?");
            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("操作");
        //添加菜单项
        menu.add(0, Menu.FIRST, 0, "删除");
        menu.add(0, Menu.FIRST + 1, 0, "下载");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * 发送第一次刷新的任务
     */
    private void sendFirstLoading(){
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_FILE);
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        FileHandler.getFilesRequest(this, params);
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
        taskCanceled(TaskType.LOAD_FILE);
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("method",mPreLoadMethod );
        FileHandler.getFilesRequest(this, params);
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
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        FileHandler.getFilesRequest(this, params);
    }

    /**
     *
     * ListView向下滚动事件的监听
     */
    class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            //滚动停止
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                //当倒数第三个数据出现的时候就开始加载
                if (view.getLastVisiblePosition() == view.getCount() -1) {
                    if(!isLoading){
                        sendLowLoading();
                    }
                }
            }
        }

        /**
         * 获取字符串资源
         * @param context
         * @param resourseId
         * @return
         */
        public String getStringResource1(Context context, int resourseId){
            if(context == null){
                return BaseApplication.newInstance().getResources().getString(resourseId);
            }else{
                return context.getResources().getString(resourseId);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
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
            case R.id.view_right_img:
                //Toast.makeText(FileActivity.this, "上传文件", Toast.LENGTH_LONG).show();
                Intent it_upload_file = new Intent();
                it_upload_file.setClass(FileActivity.this, UploadFileActivity.class);
                startActivityForResult(it_upload_file, IS_UPLOAD_FILE);
                break;
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
            default:
                ToastUtil.success(FileActivity.this, "未知点击事件");
                break;
        }
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        if(getStringResource(R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
                || getStringResource(R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){
            taskCanceled(TaskType.LOAD_FILE);
            isLoading = true;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            mListViewFooter.setText(getStringResource(R.string.loading));
            FileHandler.getFilesRequest(this, params);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                ToastUtil.success(FileActivity.this, mClickPosition + "-----" + item.getTitle());
                break;
            case 2:
                ToastUtil.success(FileActivity.this, mClickPosition + "-----" + item.getTitle());
                break;
        }
        return super.onContextItemSelected(item);
    }
}
