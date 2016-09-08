package com.leedane.cn.financial.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.Helper.OnStartDragListener;
import com.leedane.cn.financial.Helper.SimpleItemTouchHelperCallback;
import com.leedane.cn.financial.adapter.TwoLevelEditAdapter;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategoryEdit;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 二级分类处理操作activity
 * Created by LeeDane on 2016/8/24.
 */
public class TwoLevelOperationActivity extends BaseActivity implements OnStartDragListener,
        TwoLevelEditAdapter.OnItemClickListener, TwoLevelEditAdapter.OnItemLongClickListener {

    //二级分类编辑返回的code
    public static final int TWO_LEVEL_CATEGORY_EDIT_CODE = 59;

    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;
    private RecyclerView mRecyclerView;
    private TwoLevelEditAdapter mAdapter;
    private List<TwoLevelCategoryEdit> mTwoLevelGategoryEdits = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private Button mRight;
    private boolean sort;
    private SimpleItemTouchHelperCallback callback;
    private int oneLevelId; //一级分类的ID
    private Dialog mDialog;
    private TextView mShowTotalBudget;//展示所在一级分类总预算
    private int model = IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME;

    /**
     * 添加分类的imageview
     */
    private ImageView mRightImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(TwoLevelOperationActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.TwoLevelOperationActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        oneLevelId = getIntent().getIntExtra("oneLevelCategoryId", 0);
        if(oneLevelId == 0){
            ToastUtil.failure(TwoLevelOperationActivity.this, "一级分类ID不存在");
            finish();
        }

        setContentView(R.layout.activity_financial_two_level_operation);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        setTitleViewText(getStringResource(R.string.two_level_opetation));
        twoLevelCategoryDataBase = new TwoLevelCategoryDataBase(TwoLevelOperationActivity.this);
        initData();
        initView();
    }

    /**
     * 初始化基本数据
     */
    private void initData(){
        List<TwoLevelCategory> twoLevelCategories = BaseApplication.twoLevelCategories;
        List<TwoLevelCategory> twoLevels = new ArrayList<>();
        if(!CommonUtil.isEmpty(twoLevelCategories)){
            for(TwoLevelCategory twoLevelCategory: twoLevelCategories){
                if(twoLevelCategory.getOneLevelId() == oneLevelId){
                    twoLevels.add(twoLevelCategory);
                }
            }
        }
        mTwoLevelGategoryEdits = convertToEditBean(twoLevels);
        model = TwoLevelCategoryDataBase.getModel(oneLevelId);

    }

    /**
     * 初始化视图控件
     */
    private void initView() {

        mRight = (Button)findViewById(R.id.view_right_button);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener(this);
        mRight.setText(getStringResource(R.string.sort));

        //显示标题栏的添加二级分类的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setImageResource(R.drawable.ic_note_add_pink_200_18dp);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.financial_two_level_list);
        mAdapter = new TwoLevelEditAdapter(TwoLevelOperationActivity.this, mTwoLevelGategoryEdits, this);
        mLayoutManager = new LinearLayoutManager(TwoLevelOperationActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(TwoLevelOperationActivity.this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        if(model == IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND){
            mShowTotalBudget = (TextView)findViewById(R.id.show_total_budget);
            mShowTotalBudget.setVisibility(View.VISIBLE);
            mShowTotalBudget.setText("当月该一级分类总预算：" + String.valueOf(getTotalBudget().floatValue()));
        }

        callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    /**
     * 获取一级分类的总预算
     * @return
     */
    private BigDecimal getTotalBudget(){
        BigDecimal total = new BigDecimal(0.0f);
        for(TwoLevelCategoryEdit twoLevelCategoryEdit: mTwoLevelGategoryEdits){
            total = total.add(new BigDecimal(twoLevelCategoryEdit.getBudget()));
        }
        return total;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button:
                if(sort){
                    mRight.setText(getStringResource(R.string.sort));
                    //编辑完成，保存数据库
                    List<TwoLevelCategory> twoLevelCategories = convertEditBeanToBean(mTwoLevelGategoryEdits, true);
                    for(TwoLevelCategory twoLevelCategory: twoLevelCategories)
                        //将改变后的数据重新设置
                        twoLevelCategoryDataBase.save(twoLevelCategory);
                    //将缓存的二级级分类修改成新的数据
                    BaseApplication.twoLevelCategories = twoLevelCategories;
                }else{
                    mRight.setText(getStringResource(R.string.comlpete));
                }
                sort = !sort;
                if(!CommonUtil.isEmpty(mTwoLevelGategoryEdits)){
                    for(TwoLevelCategoryEdit twoLevelCategoryEdit: mTwoLevelGategoryEdits){
                        twoLevelCategoryEdit.setEdit(sort);
                    }
                }

                callback.setIsCanDrag(sort);
                //callback.setIsCanSwipe(sort);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.view_right_img://添加二级分类
                Intent it = new Intent(this, TwoLevelEditActivity.class);
                it.putExtra("oneLevelId", oneLevelId);
                startActivityForResult(it, TWO_LEVEL_CATEGORY_EDIT_CODE);
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
        twoLevelCategoryDataBase.destroy();
        super.onDestroy();
    }

    /**
     * 显示弹出自定义view
     * @param index
     */
    public void showListItemMenuDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();

        mDialog = new Dialog(TwoLevelOperationActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(TwoLevelOperationActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(R.string.delete));//删除

        SimpleListAdapter adapter = new SimpleListAdapter(TwoLevelOperationActivity.this, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //删除
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.delete))){

                    final String value = mTwoLevelGategoryEdits.get(index).getValue();
                    AppUtil.vibrate(TwoLevelOperationActivity.this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TwoLevelOperationActivity.this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除二级分类《" + value +"》吗？这是不可逆行为，删掉将不能恢复！");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        //处理数据库数据
                                        twoLevelCategoryDataBase.delete(mTwoLevelGategoryEdits.get(index).getId());
                                        //处理列表数据
                                        mAdapter.remove(index);
                                        //处理缓存数据
                                        BaseApplication.twoLevelCategories = convertEditBeanToBean(mTwoLevelGategoryEdits, false);
                                        ToastUtil.success(TwoLevelOperationActivity.this, "删除二级分类《" + value + "》成功。");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(TwoLevelOperationActivity.this, "删除二级分类《" + value + "》失败。");
                                    }

                                }
                            });
                    builder.setNegativeButton("放弃",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                    builder.show();
                }

                dismissListItemMenuDialog();
            }
        });
        mDialog.setTitle("操作");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissListItemMenuDialog();
            }
        });
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissListItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    /**
     * 将TwoLevelCategory列表转化成TwoLevelCategoryEdit列表
     * @param twoLevelCategories
     * @return
     */
    private List<TwoLevelCategoryEdit> convertToEditBean(List<TwoLevelCategory> twoLevelCategories){
        List<TwoLevelCategoryEdit> twoLevelCategoryEdits = new ArrayList<>();
        if(!CommonUtil.isEmpty(twoLevelCategories)){
            for(TwoLevelCategory twoLevelCategory : twoLevelCategories){
                twoLevelCategoryEdits.add(convertToEditBean(twoLevelCategory));
            }
        }
        return twoLevelCategoryEdits;
    }

    private TwoLevelCategoryEdit convertToEditBean(TwoLevelCategory twoLevelCategory){
        TwoLevelCategoryEdit twoLevelCategoryEdit = new TwoLevelCategoryEdit();
        twoLevelCategoryEdit.setBudget(twoLevelCategory.getBudget());
        twoLevelCategoryEdit.setCreateTime(twoLevelCategory.getCreateTime());
        twoLevelCategoryEdit.setCreateUserId(twoLevelCategory.getCreateUserId());
        twoLevelCategoryEdit.setIcon(twoLevelCategory.getIcon());
        twoLevelCategoryEdit.setId(twoLevelCategory.getId());
        twoLevelCategoryEdit.setOrder(twoLevelCategory.getOrder());
        twoLevelCategoryEdit.setIsDefault(twoLevelCategory.isDefault());
        twoLevelCategoryEdit.setStatus(twoLevelCategory.getStatus());
        twoLevelCategoryEdit.setValue(twoLevelCategory.getValue());
        twoLevelCategoryEdit.setOneLevelId(twoLevelCategory.getOneLevelId());
        return twoLevelCategoryEdit;
    }

    /**
     *将TwoLevelCategoryEdit列表转化成TwoLevelCategory列表
     * @param twoLevelCategoryEdits
     * @param resetOrder 是否重置排序
     * @return
     */
    private List<TwoLevelCategory> convertEditBeanToBean(List<TwoLevelCategoryEdit> twoLevelCategoryEdits, boolean resetOrder){
        List<TwoLevelCategory> twoLevelCategories = new ArrayList<>();
        if(!CommonUtil.isEmpty(twoLevelCategoryEdits)){
            for(int i = 0; i < twoLevelCategoryEdits.size(); i++){
                twoLevelCategories.add(convertEditBeanToBean(twoLevelCategoryEdits.get(i), resetOrder, (i + 1)));
            }
        }
        return twoLevelCategories;
    }

    private TwoLevelCategory convertEditBeanToBean(TwoLevelCategoryEdit twoLevelCategoryEdit, boolean resetOrder, int order){
        TwoLevelCategory twoLevelCategory = new TwoLevelCategoryEdit();
        twoLevelCategory.setBudget(twoLevelCategoryEdit.getBudget());
        twoLevelCategory.setCreateTime(twoLevelCategoryEdit.getCreateTime());
        twoLevelCategory.setCreateUserId(twoLevelCategoryEdit.getCreateUserId());
        twoLevelCategory.setIcon(twoLevelCategoryEdit.getIcon());
        twoLevelCategory.setId(twoLevelCategoryEdit.getId());
        if(resetOrder)
            twoLevelCategory.setOrder(order);
        else
            twoLevelCategory.setOrder(twoLevelCategoryEdit.getOrder());
        twoLevelCategory.setIsDefault(twoLevelCategoryEdit.isDefault());
        twoLevelCategory.setStatus(twoLevelCategoryEdit.getStatus());
        twoLevelCategory.setValue(twoLevelCategoryEdit.getValue());
        twoLevelCategory.setOneLevelId(twoLevelCategoryEdit.getOneLevelId());
        return twoLevelCategory;
    }
    @Override
    public void onItemClick(int position) {
        Intent it = new Intent(TwoLevelOperationActivity.this, TwoLevelEditActivity.class);
        it.putExtra("twoLevelCategoryId", mTwoLevelGategoryEdits.get(position).getId());
        it.putExtra("clickPosition", position);//方便回传定位
        startActivityForResult(it, TWO_LEVEL_CATEGORY_EDIT_CODE);
    }

    @Override
    public void onItemLongClick(int position) {
        ToastUtil.success(this, "点击的position="+position);
        showListItemMenuDialog(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case TWO_LEVEL_CATEGORY_EDIT_CODE:
                if(data == null)
                    return;

                String type = data.getStringExtra("type");
                if(StringUtil.isNull(type))
                    return;

                int clickPosition = data.getIntExtra("clickPosition", 0);
                if(type.equals("save")){
                    TwoLevelCategory twoLevelCategory = (TwoLevelCategory)data.getSerializableExtra("twoLevelCategory");
                    mAdapter.add(convertToEditBean(twoLevelCategory), clickPosition);
                }else if(type.equals("edit")){
                    TwoLevelCategory twoLevelCategory = (TwoLevelCategory)data.getSerializableExtra("twoLevelCategory");
                    mAdapter.refresh(convertToEditBean(twoLevelCategory), clickPosition);
                }else if(type.equals("delete")){
                    mAdapter.remove(clickPosition);
                }
                initData();
                mAdapter.addDatas(mTwoLevelGategoryEdits);

                if(model == IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND){
                    mShowTotalBudget.setText("当月该一级分类总预算：" + String.valueOf(getTotalBudget().floatValue()));
                }
                ToastUtil.success(this, "activity返回resultCode=" + resultCode + ", requestCode=" + requestCode+",type="+type);
                break;
        }

    }
}
