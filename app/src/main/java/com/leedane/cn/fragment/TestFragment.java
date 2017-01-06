package com.leedane.cn.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.database.MoodDataBase;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
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
public class TestFragment extends BaseRecyclerViewFragment  implements BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener{

    public static final String TAG = "TestFragment";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private View mRootView;
    private Dialog mDialog;
    private int mPreUid; //当前个人中心用户ID，不一定是系统登录用户ID
    private int clickListItemPosition;//点击ListView的位置

    private List<MoodBean> mMoodBeans = new ArrayList<>();
    private  PersonalMoodListViewAdapter mAdapter;

    //是否是第一次加载
    private boolean isFirstLoading = true;
    public TestFragment(){

    }

    /**
     * 构建Fragment对象
     * @param bundle
     * @return
     */
    public static final TestFragment newInstance(Bundle bundle){
        TestFragment fragment = new TestFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void taskFinished(TaskType type, Object result) {

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            this.mPreUid = bundle.getInt("id");
        }
        if(mContext == null)
            mContext = getActivity();

        ToastUtil.success(mContext, "ID为："+mPreUid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }

    /**
     * 发送第一次刷新的任务
     */
    @Override
    public void sendFirstLoading() {
    }

    /**
     * 发送向上刷新的任务
     */
    @Override
    protected void sendUpLoading(){
    }
    /**
     * 发送向下刷新的任务
     */
    @Override
    protected void sendLowLoading(){
    }

    /**
     * 加载失败后点击加载更多
     * @param view
     */
    @Override
    protected void sendLoadAgain(View view){

    }

    @Override
    public void onDestroy() {
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

    @Override
    public void onItemClick(int position, Object data) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("hasImg", !StringUtil.isNull(mMoodBeans.get(position).getImgs()));
        CommonHandler.startDetailActivity(mContext, "t_mood", mMoodBeans.get(position).getId(), params);
    }

    @Override
    public void onItemLongClick(int position) {

    }
}
