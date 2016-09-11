package com.leedane.cn.financial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.FinancialCloudAdapter;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.JsonUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.util.http.BeanUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与云端同步的activity
 * Created by LeeDane on 2016/8/26.
 */
public class CloudActivity extends BaseActivity{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private FinancialCloudAdapter mAdapter;
    private List<FinancialBean> mFinancialBeans = new ArrayList<>();
    private FinancialDataBase financialDataBase;
    private View mHeaderView;
    private View mFooterView;
    private boolean isEnd = true;//是否单个任务结束
    private boolean isCancel; //是否取消任务
    private int endIndex; //标记同步结束的位置
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.CloudActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_cloud);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(getStringResource(R.string.cloud));
        backLayoutVisible();

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        financialDataBase = new FinancialDataBase(this);

        mHeaderView = LayoutInflater.from(this).inflate(R.layout.activity_cloud_header, null);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.fragment_financial_main_footer, null);

        TextView textView = (TextView)mHeaderView.findViewById(R.id.financial_cloud_header_start);
        textView.setOnClickListener(this);

        this.mRecyclerView = (RecyclerView)findViewById(R.id.financial_cloud);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        mAdapter = new FinancialCloudAdapter(this, mFinancialBeans);

        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setFooterView(mFooterView);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                ToastUtil.success(CloudActivity.this, "click");
            }
        });

        loadInitData();
    }

    private void loadInitData(){
        //获取非同步却状态不是草稿的数据列表
        mFinancialBeans = financialDataBase.query(" where synchronous = "+ ConstantsUtil.STATUS_DISABLE +" and status !="+ ConstantsUtil.STATUS_DRAFT +" order by datetime(addition_time) asc");
        mAdapter.addDatas(mFinancialBeans);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.financial_cloud_header_start:
                if(!isEnd){
                    isCancel = true;
                    ToastUtil.success(this, "还有任务正在同步中，稍等");
                    return;
                }
                isCancel = false;
                endIndex = 0;
                startSynchronous();
        }
    }

    /**
     * 开始数据同步
     */
    private void startSynchronous(){
        if(!isEnd && isCancel)
            return;

        if(endIndex > mFinancialBeans.size() -1){
            ToastUtil.success(this, "没有要同步的任务");
            return;
        }

        if(mFinancialBeans.get(endIndex).isSynchronous()){
            endIndex = endIndex + 1;
            startSynchronous();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> financialBeans = new ArrayList<>();
        BeanUtil.convertBeanToMap(mFinancialBeans.get(endIndex), map);
        financialBeans.add(map);
        isEnd = false;

        mFinancialBeans.get(endIndex).setSynchronousTip("<font color='red'>与云端同步中</font>");
        mAdapter.refresh(mFinancialBeans.get(endIndex), endIndex);

        FinancialHandler.synchronous(this, financialBeans);
        //showLoadingDialog("synchronous", "数据同步中。。。请稍等。");
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        dismissLoadingDialog();
        if(result instanceof Error) {
            ToastUtil.failure(getBaseContext(), ((Error) result).getMessage(), Toast.LENGTH_SHORT);
            isEnd = true;
            try {
                mFinancialBeans.get(endIndex).setSynchronousTip("<font color='red'>超时同步失败</font>");
                mAdapter.refresh(mFinancialBeans.get(endIndex), endIndex);
                endIndex = endIndex + 1;
                Thread.sleep(100);
                startSynchronous();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        try{
            if(type == TaskType.SYNCHRONOUS_FINANCIAL){
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess")){
                    dealResult(jsonObject.getJSONObject("message"));
                }else{
                    ToastUtil.success(this, JsonUtil.getTipMessage(result));
                    mFinancialBeans.get(endIndex).setSynchronousTip("<font color='red'>" + JsonUtil.getTipMessage(result) + "</font>");
                    mAdapter.refresh(mFinancialBeans.get(endIndex), endIndex);
                    financialDataBase.updateSynchronousInfo(mFinancialBeans.get(endIndex).getLocalId(), mFinancialBeans.get(endIndex).getId(), ConstantsUtil.STATUS_NORMAL);
                }

                isEnd = true;
                endIndex = endIndex + 1;
                try {
                    Thread.sleep(100);
                    startSynchronous();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            isEnd = true;
            try {
                mFinancialBeans.get(endIndex).setSynchronousTip("<font color='red'>同步失败</font>");
                mAdapter.refresh(mFinancialBeans.get(endIndex), endIndex);
                endIndex = endIndex + 1;
                Thread.sleep(100);
                startSynchronous();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    /**
     * 同步成功后的处理结果
     * @param message
     */
    private void dealResult(JSONObject message) {
        try {
            if(message.has("inserts")){
                JSONArray list = message.getJSONArray("inserts");
                for(int l = 0; l < list.length(); l++){
                    int localId = list.getJSONObject(l).getInt("localId");
                    int id = list.getJSONObject(l).getInt("id");
                    financialDataBase.updateSynchronousInfo(localId, id, ConstantsUtil.STATUS_NORMAL);
                    int i = 0;
                    for(FinancialBean financialBean: mFinancialBeans){
                                if(financialBean.getLocalId() == localId){
                                    financialBean.setSynchronous(true);
                                    financialBean.setId(id);
                                    financialBean.setSynchronousTip("同步新增成功");
                                    mAdapter.refresh(financialBean, i);
                                    break;
                        }
                        i++;
                    }
                }
            }

            if(message.has("updates")){
                JSONArray list = message.getJSONArray("updates");
                for(int l = 0; l < list.length(); l++){
                    int localId = list.getJSONObject(l).getInt("localId");
                    int id = list.getJSONObject(l).getInt("id");
                    financialDataBase.updateSynchronousInfo(localId, id, ConstantsUtil.STATUS_NORMAL);
                    int i = 0;
                    for(FinancialBean financialBean: mFinancialBeans){
                        if(financialBean.getLocalId() == localId){
                            financialBean.setSynchronous(true);
                            financialBean.setId(id);
                            financialBean.setSynchronousTip("同步修改成功");
                            mAdapter.notifyItemChanged(i+1);
                            break;
                        }
                        i++;
                    }
                }
            }

            if(message.has("deletes")){
                JSONArray list = message.getJSONArray("deletes");
                for(int l = 0; l < list.length(); l++){
                    int localId = list.getJSONObject(l).getInt("localId");
                    int id = list.getJSONObject(l).getInt("id");
                    financialDataBase.delete(localId);
                    int i = 0;
                    for(FinancialBean financialBean: mFinancialBeans){
                        if(financialBean.getLocalId() == localId){
                            financialBean.setSynchronous(true);
                            financialBean.setId(id);
                            financialBean.setSynchronousTip("同步删除成功");
                            mAdapter.notifyItemChanged(i+1);
                            break;
                        }
                        i++;
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        financialDataBase.destroy();
        super.onDestroy();
    }
}
