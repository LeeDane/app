package com.leedane.cn.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedane.cn.adapter.CircleOfFriendAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.HttpResponseCircleOfFriendBean;
import com.leedane.cn.bean.TimeLineBean;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CircleOfFriendPagingHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.MySettingConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知消息列表的fragment类
 * Created by LeeDane on 2018/3/27.
 */
public class CircleOfFriendFragment extends BaseRecyclerViewLazyFragment implements BaseRecyclerViewAdapter.OnItemClickListener{

    public static final String TAG = "CircleOfFriendFragment";
    private RecyclerView mRecyclerView;
    private CircleOfFriendAdapter mAdapter;
    private List<TimeLineBean> mTimeLineBeans = new ArrayList<>();
    private View mRootView;
    private CircleOfFriendPagingHandler mCircleOfFriendPagingHandler;

    public CircleOfFriendFragment(){
    }

    public static final CircleOfFriendFragment newInstance(Bundle bundle){
        CircleOfFriendFragment fragment = new CircleOfFriendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initPrepare() {
        mCircleOfFriendPagingHandler = new CircleOfFriendPagingHandler(this, MySettingConfigUtil.first_load);
        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mAdapter = new CircleOfFriendAdapter(mContext, mTimeLineBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.HORIZONTAL, 40, Color.parseColor("#F3F3F3")));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

        //listview下方的显示
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.footer_text);
        mRecyclerViewFooter.setOnClickListener(CircleOfFriendFragment.this);//添加点击事件
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mRecyclerView.setAdapter(mAdapter);

        //长按事件
        mAdapter.setOnItemClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);}

    @Override
    protected void onInvisible() {

    }

    @Override
    protected void initData() {
        sendFirstLoading();
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        mIsLoading = false;
        super.taskFinished(type, result);
        if(result instanceof Error){
            if(type == TaskType.LOAD_CIRCLEOFFRIEND){
                mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                mRecyclerViewFooter.setOnClickListener(this);
                return;
            }
        }
        try{
            if(type == TaskType.LOAD_CIRCLEOFFRIEND){
                if(mSwipeLayout !=null && mSwipeLayout.isRefreshing())
                    mSwipeLayout.setRefreshing(false);//下拉刷新组件停止刷新

                HttpResponseCircleOfFriendBean httpResponseCircleOfFriendBean = BeanConvertUtil.strConvertToCircleOfFriendBeans(String.valueOf(result));
                if(httpResponseCircleOfFriendBean != null && httpResponseCircleOfFriendBean.isSuccess()){
                    List<TimeLineBean> timeLineBeans =  httpResponseCircleOfFriendBean.getMessage();
                    if(timeLineBeans != null && timeLineBeans.size() > 0){
                        //临时list
                        List<TimeLineBean> temList = new ArrayList<>();
                        if(mCurrent == 0){ //第一页
                            mTimeLineBeans.clear();
                        }
                        Log.i(TAG, "原来的大小：" + mTimeLineBeans.size());
                        //将新的数据和以前的数据进行叠加
                        temList.addAll(mTimeLineBeans);
                        temList.addAll(timeLineBeans);
                        Log.i(TAG, "原来的大小：" + mTimeLineBeans.size());

                        if(mAdapter == null) {
                            mAdapter = new CircleOfFriendAdapter(mContext, mTimeLineBeans);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        mAdapter.addDatas(temList);
                        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.load_finish));
                    }else{
                        if(!mDropDown){ //下拉，说明没有数据，就清空列表
                            mTimeLineBeans.clear();
                            mAdapter.addDatas(new ArrayList<TimeLineBean>());
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }else{
                            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
                        }
                    }
                }else{
                    if(!mDropDown){//非下拉
                        mTimeLineBeans.clear();
                        mAdapter.addDatas(new ArrayList<TimeLineBean>());
                    }
                    //mListViewFooter.setText(getStringResource(mContext, R.string.load_more_error));
                    mRecyclerViewFooter.setText(JsonUtil.getErrorMessage(result) + "，" + getStringResource(mContext, R.string.click_to_load));
                    mRecyclerViewFooter.setOnClickListener(this);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 发送第一次刷新的任务
     */
    protected void sendFirstLoading(){
        mCurrent = 0;
        mIsLoading = true;
        mDropDown = false;
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        mCircleOfFriendPagingHandler.getCircles(mCurrent);
    }

    /**
     * 发送向上刷新的任务
     */
    protected void sendUpLoading(){
        //没有fistID时当作第一次请求加载
        mCurrent = 0;
        mIsLoading = true;
        mDropDown = false;
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        mCircleOfFriendPagingHandler.getCircles(mCurrent);
    }
    /**
     * 发送向下刷新的任务
     */
    protected void sendLowLoading(){
        //向下刷新时，只有当不是暂无数据的时候才进行下一步的操作
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString()) || mIsLoading) {
            return;
        }
        mCurrent ++;
        mIsLoading = true;
        mDropDown = true;
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        mCircleOfFriendPagingHandler.getCircles(mCurrent);
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    public void sendLoadAgain(View view){
        //加载失败或者点击加载更多的情况下才不能点击
        if(getStringResource(mContext, R.string.no_load_more).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())
                ||  getStringResource(mContext, R.string.load_finish).equalsIgnoreCase(mRecyclerViewFooter.getText().toString())){
            return;
        }
        mIsLoading = true;
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        //第一次操作取消全部数据
        taskCanceled(TaskType.LOAD_CIRCLEOFFRIEND);
        mCircleOfFriendPagingHandler.getCircles(mCurrent);
    }

    @Override
    public void onItemClick(int position, Object data) {
        CommonHandler.startDetailActivity(mContext, mTimeLineBeans.get(position).getTableName(), mTimeLineBeans.get(position).getTableId(), null);
    }
}
