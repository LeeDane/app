package com.leedane.cn.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.PersonalMoodListViewAdapter;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.HttpResponseMoodBean;
import com.leedane.cn.bean.MoodBean;
import com.leedane.cn.database.MoodDataBase;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CollectionHandler;
import com.leedane.cn.handler.CommentHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.MoodHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.handler.TransmitHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 个人中心显示心情列表的frament类
 * Created by LeeDane on 2015/12/7.
 */
public class PersonalMoodFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    public static final String TAG = "PersonalMoodFragment";

    private MoodDataBase moodDataBase;
    private Context mContext;
    private ListView mListView;
    //private TextView mTextViewLoading;
    private SwipeRefreshLayout mSwipeLayout;

    private View mRootView;
    private Dialog mDialog;
    private int mPreUid; //当前个人中心用户ID，不一定是系统登录用户ID
    private int clickListItemPosition;//点击ListView的位置

    private List<MoodBean> mMoodBeans = new ArrayList<>();
    private  PersonalMoodListViewAdapter mAdapter;

    //是否是第一次加载
    private boolean isFirstLoading = true;
    public PersonalMoodFragment(){

    }
    /**
     * 构建Fragment对象
     * @param bundle
     * @return
     */
    public static final PersonalMoodFragment newInstance(Bundle bundle){
        PersonalMoodFragment fragment = new PersonalMoodFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_personal_list, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        isLoading = false;
        if(result instanceof Error){
            if(type == TaskType.PERSONAL_LOADMOODS && !mPreLoadMethod.equalsIgnoreCase("uploading")){
                mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
            }
        }
        super.taskFinished(type, result);
        try{
            if(type == TaskType.PERSONAL_LOADMOODS){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新
                if(isFirstLoading) {
                    isFirstLoading = false;
                }
                HttpResponseMoodBean httpResponseMoodBean = BeanConvertUtil.strConvertToMoodBeans(String.valueOf(result));
                if(httpResponseMoodBean != null && httpResponseMoodBean.isSuccess()){
                    List<MoodBean> moodBeans = httpResponseMoodBean.getMessage();
                    if(moodBeans != null && moodBeans.size() > 0){
                        //临时list
                        List<MoodBean> temList = new ArrayList<>();
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.removeAllViewsInLayout();
                            mMoodBeans.clear();
                        }
                        //将新的数据和以前的数据进行叠加
                        if(mPreLoadMethod.equalsIgnoreCase("uploading")){
                            for(int i = moodBeans.size() -1; i>= 0 ; i--){
                                temList.add(moodBeans.get(i));
                            }
                            temList.addAll(mMoodBeans);
                        }else{
                            temList.addAll(mMoodBeans);
                            temList.addAll(moodBeans);
                        }
                        //Log.i(TAG, "原来的大小：" + mMoodBeans.size());
                        if(mAdapter == null) {
                            mAdapter = new PersonalMoodListViewAdapter(mContext, mMoodBeans, this);
                            mListView.setAdapter(mAdapter);
                        }

                        mAdapter.refreshData(temList);
                        //Log.i(TAG, "后来的大小：" + mMoodBeans.size());

                        //Toast.makeText(mContext, "成功加载"+ moodBeans.size() + "条数据,总数是："+mMoodBeans.size(), Toast.LENGTH_SHORT).show();
                        int size = mMoodBeans.size();

                        mFirstId = mMoodBeans.get(0).getId();
                        mLastId = mMoodBeans.get(size - 1).getId();

                        //将ListView的位置设置为0
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mListView.setSelection(0);
                        }
                        if(MySettingConfigUtil.getCacheMood()){
                            for(MoodBean mb: moodBeans){
                                moodDataBase.insert(mb);
                            }
                        }
                        mListViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{

                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mMoodBeans.clear();
                            mAdapter.refreshData(new ArrayList<MoodBean>());
                        }
                        if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                            mListView.removeFooterView(viewFooter);
                            mListView.addFooterView(viewFooter, null, false);
                            mListViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else {
                            ToastUtil.success(mContext, getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mPreLoadMethod.equalsIgnoreCase("uploading")){
                        if(mPreLoadMethod.equalsIgnoreCase("firstloading")){
                            mMoodBeans.clear();
                            mAdapter.refreshData(new ArrayList<MoodBean>());
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
                return;
            }else  if(type == TaskType.ADD_ZAN) {
                dismissMoodListItemMenuDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(mContext, "点赞成功", Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
            }else  if(type == TaskType.ADD_ATTENTION) {//添加关注
                dismissMoodListItemMenuDialog();
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(mContext, "关注成功", Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
            }else  if(type == TaskType.ADD_COLLECTION) {//添加收藏
                dismissMoodListItemMenuDialog();
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(mContext, "添加收藏成功", Toast.LENGTH_SHORT);
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
            }else  if(type == TaskType.DELETE_MOOD) {
                dismissMoodListItemMenuDialog();

                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(mContext, "删除心情成功", Toast.LENGTH_SHORT);
                    List<MoodBean> tempList = new ArrayList<>();
                    for(int i = 0;i < mMoodBeans.size(); i++){
                        if(clickListItemPosition != i)
                            tempList.add(mMoodBeans.get(i));
                        else
                            //把本地的心情数据也删除掉
                            moodDataBase.delete(mMoodBeans.get(clickListItemPosition).getCreateUserId(), mMoodBeans.get(clickListItemPosition).getId());
                    }
                    //mMoodBeans.remove(clickListItemPosition);
                    mAdapter.refreshData(tempList);
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
                dismissLoadingDialog();
            }else  if(type == TaskType.FANYI) { //翻译
                dismissMoodListItemMenuDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    showFanyiDialog(jsonObject.getString("message"));
                }else{
                    ToastUtil.failure(mContext, jsonObject, Toast.LENGTH_SHORT);
                }
                dismissLoadingDialog();
            }else if(type == TaskType.UPDATE_COMMENT_STATUS){//更新评论状态
                dismissMoodListItemMenuDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.success(mContext, "更新评论状态成功");
                    boolean canComment = mMoodBeans.get(clickListItemPosition).isCanComment();
                    mMoodBeans.get(clickListItemPosition).setCanComment(!canComment);
                    mAdapter.notifyDataSetChanged();
                }else{
                    ToastUtil.failure(mContext, "更新评论状态失败:" + jsonObject);
                }
                dismissLoadingDialog();
            }else if(type == TaskType.UPDATE_TRANSMIT_STATUS){//更新转发状态
                dismissMoodListItemMenuDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if (jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")) {
                    ToastUtil.failure(mContext, "更新转发状态成功");
                    boolean canTransmit = mMoodBeans.get(clickListItemPosition).isCanTransmit();
                    mMoodBeans.get(clickListItemPosition).setCanTransmit(!canTransmit);
                    mAdapter.notifyDataSetChanged();
                }else{
                    ToastUtil.failure(mContext, "更新转发状态失败:" + jsonObject);
                }
                dismissLoadingDialog();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 展示翻译结果的信息
     * @param message
     */
    private void showFanyiDialog(final String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.menu_feedback);
        builder.setTitle("翻译结果");
        builder.setMessage(message);
        builder.setPositiveButton("复制",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(getContext().CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, message));
                            ToastUtil.success(getContext(), "复制成功", Toast.LENGTH_SHORT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        builder.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.mPreUid = bundle.getInt("toUserId");
        }
        if(mContext == null)
            mContext = getActivity();

        if(isFirstLoading){
            //ToastUtil.success(getContext(), "第一次加载");
            //加载本地数据库的数据
            moodDataBase = new MoodDataBase(mContext);
            mMoodBeans = moodDataBase.queryMoodLimit25(mPreUid);
            if(mMoodBeans.size() > 0){
                mFirstId = mMoodBeans.get(0).getId();
                mLastId = mMoodBeans.get(mMoodBeans.size() - 1).getId();
            }else{
                sendFirstLoading();
            }
            //initFirstData();
            mListView = (ListView)mRootView.findViewById(R.id.personal_fragment_listview);
            mAdapter = new PersonalMoodListViewAdapter(mContext, mMoodBeans, this);
            mListView.setOnScrollListener(new ListViewOnScrollListener());

            //listview下方的显示
            viewFooter = LayoutInflater.from(getContext()).inflate(R.layout.listview_footer_item, null);
            mListView.addFooterView(viewFooter, null, false);
            mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
            mListViewFooter.setOnClickListener(PersonalMoodFragment.this);//添加点击事件
            mListViewFooter.setText(getStringResource(mContext, R.string.loading));
            mSwipeLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.id_swipe_listview);
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
        }

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        Log.i(TAG, "哈哈onActivityCreated............");

        //setListViewHeightBasedOnChildren();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("hasImg", !StringUtil.isNull(mMoodBeans.get(position).getImgs()));
        CommonHandler.startDetailActivity(mContext, "t_mood", mMoodBeans.get(position).getId(), params);
    }

    /**
     * 显示弹出自定义view
     * @param index
     * @param mb 操作对象
     */
    public void showMoodListItemMenuDialog(int index, final MoodBean mb){
        clickListItemPosition = index;
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissMoodListItemMenuDialog();

        mDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(mContext, R.string.personal_look));
        menus.add(getStringResource(mContext, R.string.personal_praise));
        menus.add(getStringResource(mContext, R.string.fanyi));
        menus.add(getStringResource(mContext, R.string.copyText));
        if(mb.isHasImg()){
            menus.add(getStringResource(mContext, R.string.copyLink));
        }
        //登录的用户：查看，点赞，复制文字，复制图像链接，删除，是否可以评论，是否可以转发
        if(BaseApplication.getLoginUserId() == mb.getCreateUserId()){
            menus.add(getStringResource(mContext, R.string.delete));
            if(mb.isCanComment()){
                menus.add(getStringResource(mContext, R.string.personal_no_comment));
            }else{
                menus.add(getStringResource(mContext, R.string.personal_can_comment));
            }
            if(mb.isCanTransmit()){
                menus.add(getStringResource(mContext, R.string.personal_no_transmit));
            }else{
                menus.add(getStringResource(mContext, R.string.personal_can_transmit));
            }
            //非登录用户：查看, 点赞，复制文字，复制图像链接，屏蔽，举报
        }else{
            menus.add(getStringResource(mContext, R.string.personal_attention));
            menus.add(getStringResource(mContext, R.string.personal_collection));
            menus.add(getStringResource(mContext, R.string.personal_report));
        }

        SimpleListAdapter adapter = new SimpleListAdapter(getActivity().getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //查看
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_look))){
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("hasImg", !StringUtil.isNull(mMoodBeans.get(clickListItemPosition).getImgs()));
                    CommonHandler.startDetailActivity(mContext, "t_mood", mMoodBeans.get(clickListItemPosition).getId(), params);
                    dismissMoodListItemMenuDialog();
                    //点赞
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_praise))){
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("table_name", "t_mood");
                    params.put("table_id", mMoodBeans.get(clickListItemPosition).getId());
                    params.put("content", "赞了这条心情");
                    PraiseHandler.sendZan(PersonalMoodFragment.this, params);
                    //复制文本
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.copyText))){
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager)mContext.getSystemService(getContext().CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, mMoodBeans.get(clickListItemPosition).getContent()));
                        ToastUtil.success(getContext(), "复制成功", Toast.LENGTH_SHORT);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dismissMoodListItemMenuDialog();
                    //复制图片链接
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.copyLink))){
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager)mContext.getSystemService(getContext().CLIPBOARD_SERVICE);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, mMoodBeans.get(clickListItemPosition).getImgs()));
                        ToastUtil.success(getContext(), "复制成功", Toast.LENGTH_SHORT);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    dismissMoodListItemMenuDialog();
                    //删除
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.delete))){
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("mid", mMoodBeans.get(clickListItemPosition).getId());
                    MoodHandler.delete(PersonalMoodFragment.this, params);
                    showLoadingDialog("Delete", "is deleting......", true);
                    //举报
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_report))){

                    //关注
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_attention))){//关注
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("table_name", "t_mood");
                    params.put("table_id", mMoodBeans.get(clickListItemPosition).getId());
                    AttentionHandler.sendAttention(PersonalMoodFragment.this, params);
                    showLoadingDialog("Attention", "add attentioning......", true);

                    //收藏
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_collection))){//收藏
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("table_name", "t_mood");
                    params.put("table_id", mMoodBeans.get(clickListItemPosition).getId());
                    CollectionHandler.sendCollection(PersonalMoodFragment.this, params);
                    showLoadingDialog("Collection", "add collectioning......", true);

                    //翻译
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.fanyi))){
                    CommonHandler.getFanYiRequest(PersonalMoodFragment.this, mMoodBeans.get(clickListItemPosition).getContent());
                    showLoadingDialog("Fanyi", "try best to fanyi......", true);
                    //设置为不可评论
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_no_comment))){
                    CommentHandler.updateCommentStatus(PersonalMoodFragment.this, "t_mood", mb.getId(), !mb.isCanComment());
                    showLoadingDialog("Loading", "try best to update comment status......", true);
                    //设置为可以评论
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_can_comment))){
                    CommentHandler.updateCommentStatus(PersonalMoodFragment.this, "t_mood", mb.getId(), !mb.isCanComment());
                    showLoadingDialog("Loading", "try best to update comment status......", true);
                    //设置为不可转发
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_no_transmit))){
                    TransmitHandler.updateTransmitStatus(PersonalMoodFragment.this, "t_mood", mb.getId(), !mb.isCanTransmit());
                    showLoadingDialog("Loading", "try best to update transmit status......", true);
                    //设置为可以转发
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(mContext, R.string.personal_can_transmit))){
                    TransmitHandler.updateTransmitStatus(PersonalMoodFragment.this, "t_mood", mb.getId(), !mb.isCanTransmit());
                    showLoadingDialog("Loading", "try best to update transmit status......", true);
                }
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissMoodListItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissMoodListItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                sendLoadAgain(v);
                break;
        }
    }

    /**
     * 将列表移动到最顶部
     */
    public void smoothScrollToTop(){
        if(mMoodBeans != null && mMoodBeans.size() > 0 && mListView != null /*&& !isLoading*/){
            mListView.smoothScrollToPosition(0);
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    @Override
    public void sendFirstLoading() {
        //第一次操作取消全部数据
        taskCanceled(TaskType.PERSONAL_LOADMOODS);
        mPreLoadMethod = "firstloading";
        mFirstId = 0;
        mLastId = 0;
        //HttpRequestBean requestBean = new HttpRequestBean();
        HashMap<String, Object> params = new HashMap<>();
        params.put("toUserId", mPreUid);
        params.put("pageSize", MySettingConfigUtil.getFirstLoad());
        params.put("method", mPreLoadMethod);
        MoodHandler.sendMood(this, params);
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
        //向上刷新也先取消所有的加载操作
        taskCanceled(TaskType.PERSONAL_LOADMOODS);
        mPreLoadMethod = "uploading";
        isLoading = true;
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("toUserId", mPreUid);
        params.put("method", mPreLoadMethod);
        MoodHandler.sendMood(this, params);
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
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
        //刷新之前先把以前的任务取消
        taskCanceled(TaskType.PERSONAL_LOADMOODS);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        mPreLoadMethod = "lowloading";
        isLoading = true;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageSize", MySettingConfigUtil.getOtherLoad());
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        params.put("toUserId", mPreUid);
        MoodHandler.sendMood(this, params);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mListViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mListViewFooter.getText().toString())){
            return;
        }
        taskCanceled(TaskType.PERSONAL_LOADMOODS);
        isLoading = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("toUserId", mPreUid);
        params.put("pageSize", mPreLoadMethod.equalsIgnoreCase("firstloading") ? MySettingConfigUtil.getFirstLoad(): MySettingConfigUtil.getOtherLoad());
        params.put("first_id", mFirstId);
        params.put("last_id", mLastId);
        params.put("method", mPreLoadMethod);
        mListViewFooter.setText(getStringResource(mContext, R.string.loading));
        MoodHandler.sendMood(this, params);
    }

    @Override
    public void onDestroy() {
        if(moodDataBase != null)
            moodDataBase.destroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context == null){
            this.mContext = this.getContext();
        }
    }
}
