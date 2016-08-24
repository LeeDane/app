package com.leedane.cn.financial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.Helper.SimpleItemTouchHelperCallback;
import com.leedane.cn.financial.adapter.FinancialRecyclerViewAdapter;
import com.leedane.cn.financial.adapter.OneLevelEditAdapter;
import com.leedane.cn.financial.bean.OneLevelGategory;
import com.leedane.cn.financial.bean.OneLevelGategoryEdit;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 一级分类编辑activity
 * Created by LeeDane on 2016/8/23.
 */
public class OneLevelEditActivity extends BaseActivity implements OnStartDragListener {

    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private RecyclerView mRecyclerView;
    private OneLevelEditAdapter mAdapter;
    private List<OneLevelGategoryEdit> mOneLevelGategoryEdits = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private Button mRight;
    private boolean edit;
    private SimpleItemTouchHelperCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(OneLevelEditActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.OneLevelEditActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_one_level_edit);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        setTitleViewText(getStringResource(R.string.one_level_edit));
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(OneLevelEditActivity.this);
        initData();
        initView();
    }

    private void initData(){
        List<OneLevelGategory> oneLevelGategories = oneLevelCategoryDataBase.query();
        if(!CommonUtil.isEmpty(oneLevelGategories)){
            OneLevelGategoryEdit oneLevelGategoryEdit = null;
            for(OneLevelGategory oneLevelGategory: oneLevelGategories){
                oneLevelGategoryEdit = new OneLevelGategoryEdit();
                oneLevelGategoryEdit.setBudget(oneLevelGategory.getBudget());
                oneLevelGategoryEdit.setModel(oneLevelGategory.getModel());
                oneLevelGategoryEdit.setCreateTime(oneLevelGategory.getCreateTime());
                oneLevelGategoryEdit.setCreateUserId(oneLevelGategory.getCreateUserId());
                oneLevelGategoryEdit.setIcon(oneLevelGategory.getIcon());
                oneLevelGategoryEdit.setId(oneLevelGategory.getId());
                oneLevelGategoryEdit.setOrder(oneLevelGategory.getOrder());
                oneLevelGategoryEdit.setIsDefault(oneLevelGategory.isDefault());
                oneLevelGategoryEdit.setStatus(oneLevelGategory.getStatus());
                oneLevelGategoryEdit.setTwoLevelCategories(oneLevelGategory.getTwoLevelCategories());
                oneLevelGategoryEdit.setValue(oneLevelGategory.getValue());
                mOneLevelGategoryEdits.add(oneLevelGategoryEdit);
            }
        }
    }

    /**
     * 初始化视图控件
     */
    private void initView() {

        mRight = (Button)findViewById(R.id.view_right_button);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener(this);
        mRight.setText(getStringResource(R.string.edit));

        mRecyclerView = (RecyclerView)findViewById(R.id.financial_one_level_list);
        mAdapter = new OneLevelEditAdapter(OneLevelEditActivity.this, mOneLevelGategoryEdits, this);
        mLayoutManager = new LinearLayoutManager(OneLevelEditActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(OneLevelEditActivity.this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        callback.setIsCanDrag(false);
        callback.setIsCanSwipe(false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button:
                if(edit){
                    mRight.setText(getStringResource(R.string.edit));
                }else{
                    mRight.setText(getStringResource(R.string.comlpete));
                }
                edit = !edit;
                if(!CommonUtil.isEmpty(mOneLevelGategoryEdits)){
                    for(OneLevelGategoryEdit oneLevelGategoryEdit: mOneLevelGategoryEdits){
                        oneLevelGategoryEdit.setEdit(edit);
                    }
                }
                mAdapter.notifyDataSetChanged();
                callback = new SimpleItemTouchHelperCallback(mAdapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                callback.setIsCanDrag(edit);
                callback.setIsCanSwipe(edit);
                break;
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        oneLevelCategoryDataBase.destroy();
        super.onDestroy();
    }
}
