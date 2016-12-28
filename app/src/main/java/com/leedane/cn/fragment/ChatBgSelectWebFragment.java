package com.leedane.cn.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.leedane.cn.adapter.ChatBgSelectWebAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.ChatBgSelectWebBean;
import com.leedane.cn.bean.HttpResponseChatBgSelectWebBean;
import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.database.MySettingDataBase;
import com.leedane.cn.handler.ChatBgSelectWebHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 从web服务器获取聊天背景图片的fragment类
 * Created by LeeDane on 2016/6/10.
 */
public class ChatBgSelectWebFragment extends BaseFragment{

    public static final String TAG = "ChatBgSelectWebFragment";
    private Context mContext;
    private GridView mGridView;
    private ChatBgSelectWebAdapter mAdapter;
    private List<ChatBgSelectWebBean> mChatBgSelectWebBeans = new ArrayList<>();

    private SwipeRefreshLayout mSwipeLayout;
    private View mRootView;

    //是否是第一次加载
    private boolean isFirstLoading = true;

    private int type = 2;//聊天背景的类型，0：免费,1:收费, 2:全部

    private ChatBgSelectWebBean selectWebBean; //用户点击下载的bean

    public ChatBgSelectWebFragment(){
    }

    /**
     * 构建Frament对象
     * @param bundle
     * @return
     */
    public static final ChatBgSelectWebFragment newInstance(Bundle bundle){
        ChatBgSelectWebFragment fragment = new ChatBgSelectWebFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_gridview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.LOAD_CHAT_BG_SELECT_WEB  && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                //mListViewFooter.setText(getStringResource(mContext, (R.string.no_load_more)));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.LOAD_CHAT_BG_SELECT_WEB){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseChatBgSelectWebBean httpResponseChatBgSelectWebBean = BeanConvertUtil.strConvertToChatBgSelectWebBeans(String.valueOf(result));
                if(httpResponseChatBgSelectWebBean != null && httpResponseChatBgSelectWebBean.isSuccess()){
                    List<ChatBgSelectWebBean> chatBgSelectWebBeans = httpResponseChatBgSelectWebBean.getMessage();
                    if(chatBgSelectWebBeans != null && chatBgSelectWebBeans.size() > 0){
                        //临时list
                        List<ChatBgSelectWebBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mGridView.removeAllViewsInLayout();
                            mChatBgSelectWebBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = chatBgSelectWebBeans.size() -1; i>= 0 ; i--){
                                temList.add(chatBgSelectWebBeans.get(i));
                            }
                            temList.addAll(mChatBgSelectWebBeans);
                        }else{
                            temList.addAll(mChatBgSelectWebBeans);
                            temList.addAll(chatBgSelectWebBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mCommentOrTransmitBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new ChatBgSelectWebAdapter(mContext, mChatBgSelectWebBeans);
                            mGridView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mCommentOrTransmitBeans.size());

                        //Toast.makeText(mContext, "成功加载"+ commentOrTransmitBeans.size() + "条数据,总数是："+mCommentOrTransmitBeans.size(), Toast.LENGTH_SHORT).show();
                        int size = mChatBgSelectWebBeans.size();

                        mFirstId = mChatBgSelectWebBeans.get(0).getId();
                        mLastId = mChatBgSelectWebBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mGridView.setSelection(0);
                        }
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mChatBgSelectWebBeans.clear();
                            mAdapter.refreshData(new ArrayList<ChatBgSelectWebBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                           // mListView.removeFooterView(viewFooter);
                           // mListView.addFooterView(viewFooter, null, false);
                           // mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            //mCommentOrTransmits = new ArrayList<>();
                            //mListView.removeAllViewsInLayout();
                            mChatBgSelectWebBeans.clear();
                            mAdapter.refreshData(new ArrayList<ChatBgSelectWebBean>());
                            //mListView.addHeaderView(viewHeader);
                        }
                        //mListView.removeFooterView(viewFooter);
                        //mListView.addFooterView(viewFooter, null, false);
                        //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                        //mListViewFooter.setOnClickListener(this);
                    }else{
                        ToastUtil.failure(mContext);
                    }
                }
                return;
            }else if(type == TaskType.VERIFY_CHAT_BG){

                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
                    /*if (selectWebBean.getType() == 1) {

                    }else{
                        downLoadBitMap(selectWebBean.getPath());
                    }*/
                    ToastUtil.success(mContext, jsonObject, Toast.LENGTH_SHORT);
                    downLoadBitMap(selectWebBean.getPath());
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                    dismissLoadingDialog();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 发送第一次刷新的任务
     */
    @Override
    protected void sendFirstLoading(){

        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.first_load);
        params.put("method", mPreLoadMethod);
        params.put("type", type);
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CHAT_BG_SELECT_WEB);
        ChatBgSelectWebHandler.getChatBgSelectWebsRequest(this, params);
    }
    /**
     * 发送向上刷新的任务
     */
    @Override
    protected void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        if(mFirstId == 0){
            sendFirstLoading();
            return;
        }

        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("type", type);
        taskCanceled(TaskType.LOAD_CHAT_BG_SELECT_WEB);
        ChatBgSelectWebHandler.getChatBgSelectWebsRequest(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        //if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString()) || isLoading) {
       //     return;
       // }
        //没有lastID时当作第一次请求加载
        if(mLastId == 0){
            sendFirstLoading();
            return;
        }

        //mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.other_load);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("type", type);
        taskCanceled(TaskType.LOAD_CHAT_BG_SELECT_WEB);
        ChatBgSelectWebHandler.getChatBgSelectWebsRequest(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //只有在加载失败或者点击加载更多的情况下点击才有效
        //if(getStringResource(mContext, R.string.load_more_error).equalsIgnoreCase(mListViewFooter.getText().toString())
         //       || getStringResource(mContext, R.string.load_more).equalsIgnoreCase(mListViewFooter.getText().toString())){

            isLoading = true;
            HashMap<String, Object> params = new HashMap<>();
            params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.first_load: MySettingConfigUtil.other_load);
            params.put("first_id", mFirstId);
            params.put("last_id", mLastId);
            params.put("method", mPreLoadMethod);
            params.put("type", type);
        taskCanceled(TaskType.LOAD_CHAT_BG_SELECT_WEB);
        ChatBgSelectWebHandler.getChatBgSelectWebsRequest(this, params);
       // }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            //baseRequestParams = (SerializableMap) bundle.getSerializable("serializableMap");
        }
        if(mContext == null)
            mContext = getActivity();

        sendFirstLoading();
        //initFirstData();
        this.mGridView = (GridView) mRootView.findViewById(R.id.gridview_items);
        mAdapter = new ChatBgSelectWebAdapter( mContext, mChatBgSelectWebBeans);
        mGridView.setOnScrollListener(new ListViewOnScrollListener());
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                selectWebBean = mChatBgSelectWebBeans.get(position);

                //该聊天资源是自己上传的直接获取
                if(selectWebBean.getCreateUserId() == BaseApplication.getLoginUserId() || selectWebBean.isDownload()){
                    downLoadBitMap(selectWebBean.getPath());
                    return;
                }
                if (selectWebBean.getType() == 1) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("提示");
                    builder.setMessage("该背景是付费背景，下载需要扣除" + selectWebBean.getScore() + "积分,该积分只扣除一次,之后多次下载该背景不再扣除积分?");
                    builder.setPositiveButton("下载",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ChatBgSelectWebHandler.verifyChatBg(ChatBgSelectWebFragment.this, selectWebBean.getId());
                                    //downLoadBitMap(selectWebBean.getPath());
                                }
                            });
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                    builder.show();
                } else {
                    ChatBgSelectWebHandler.verifyChatBg(ChatBgSelectWebFragment.this, selectWebBean.getId());
                    //downLoadBitMap(mChatBgSelectWebBeans.get(position).getPath());
                }

            }
        });

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.gridview_swipe_refresh);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        mGridView.setAdapter(mAdapter);
       // mListView.setOnItemClickListener(this);
    }

    private void downLoadBitMap(String path){
        showLoadingDialog("Loading", "try best to download......");
        Bitmap bitmap = ImageCacheManager.loadImage(path, 500, 500);
        String fileName = StringUtil.getFileName(path);
        if(bitmap != null)
            BitmapUtil.bitmapToLocalPath(bitmap, getChatBgDir(BaseApplication.newInstance()) + File.separator+ fileName);

        MySettingConfigUtil.setCacheChatBgPath(fileName);
        MySettingBean mySettingBean = new MySettingBean();
        mySettingBean.setValue(String.valueOf(fileName));
        mySettingBean.setId(13);
        MySettingDataBase mySettingDataBase = new MySettingDataBase(mContext);
        mySettingDataBase.update(mySettingBean);
        mySettingDataBase.destroy();
        dismissLoadingDialog();
        ToastUtil.success(mContext, "已将该资源设置为聊天背景");
    }

    /**
     * 获取临时文件的文件夹
     * @param context
     * @return
     */
    private File getChatBgDir(Context context){
        File sdDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            sdDir = Environment.getExternalStorageDirectory();
        }
        else{
            sdDir = context.getCacheDir();
        }
        File cacheDir = new File(sdDir, context.getResources().getString(R.string.app_dirsname) + File.separator+ getStringResource(mContext, R.string.chat_bg_filepath));
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
        }
    }
}
