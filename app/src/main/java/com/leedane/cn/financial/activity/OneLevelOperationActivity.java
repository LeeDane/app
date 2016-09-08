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
import com.leedane.cn.financial.adapter.OneLevelEditAdapter;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.OneLevelCategoryEdit;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 一级分类处理操作activity
 * Created by LeeDane on 2016/8/23.
 */
public class OneLevelOperationActivity extends BaseActivity implements OnStartDragListener,
            OneLevelEditAdapter.OnItemClickListener, OneLevelEditAdapter.OnItemLongClickListener {

    //一级分类编辑返回的code
    public static final int ONE_LEVEL_CATEGORY_EDIT_CODE = 57;

    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private RecyclerView mRecyclerView;
    private OneLevelEditAdapter mAdapter;
    private List<OneLevelCategoryEdit> mOneLevelGategoryEdits = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;
    private Button mRight;
    private boolean sort;
    private SimpleItemTouchHelperCallback callback;
    private Dialog mDialog;
    private TextView mShowTotalBudget;//展示总预算

    /**
     * 添加分类的imageview
     */
    private ImageView mRightImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(OneLevelOperationActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.OneLevelOperationActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_one_level_operation);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        setTitleViewText(getStringResource(R.string.one_level_opetation));
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(OneLevelOperationActivity.this);
        initData();
        initView();
    }

    private void initData(){
        mOneLevelGategoryEdits = convertToEditBean(BaseApplication.oneLevelCategories);
    }

    /**
     * 初始化视图控件
     */
    private void initView() {

        mRight = (Button)findViewById(R.id.view_right_button);
        mRight.setVisibility(View.VISIBLE);
        mRight.setOnClickListener(this);
        mRight.setText(getStringResource(R.string.sort));

        //显示标题栏的添加一级分类的图片按钮
        mRightImg = (ImageView)findViewById(R.id.view_right_img);
        mRightImg.setImageResource(R.drawable.ic_note_add_pink_200_18dp);
        mRightImg.setVisibility(View.VISIBLE);
        mRightImg.setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.financial_one_level_list);
        mAdapter = new OneLevelEditAdapter(OneLevelOperationActivity.this, mOneLevelGategoryEdits, this);
        mLayoutManager = new LinearLayoutManager(OneLevelOperationActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(OneLevelOperationActivity.this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mShowTotalBudget = (TextView)findViewById(R.id.show_total_budget);
        mShowTotalBudget.setText("当月支出总预算：" +String.valueOf(BaseApplication.getTotalBudget().floatValue()));

        callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.view_right_button:
                if(sort){
                    mRight.setText(getStringResource(R.string.sort));
                    //编辑完成，保存数据库
                    List<OneLevelCategory> oneLevelGategories = convertEditBeanToBean(mOneLevelGategoryEdits, true);
                    //将改变后的数据重新设置
                    for(OneLevelCategory oneLevelCategory: oneLevelGategories)
                        oneLevelCategoryDataBase.save(oneLevelCategory);
                    //将缓存的一级分类修改成新的数据
                    BaseApplication.oneLevelCategories = oneLevelGategories;
                }else{
                    mRight.setText(getStringResource(R.string.comlpete));
                }
                sort = !sort;
                if(!CommonUtil.isEmpty(mOneLevelGategoryEdits)){
                    for(OneLevelCategoryEdit oneLevelGategoryEdit: mOneLevelGategoryEdits){
                        oneLevelGategoryEdit.setEdit(sort);
                    }
                }
                callback.setIsCanDrag(sort);
                callback.setIsCanSwipe(sort);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.view_right_img://添加一级分类
                Intent it = new Intent(OneLevelOperationActivity.this, OneLevelEditActivity.class);
                startActivityForResult(it, ONE_LEVEL_CATEGORY_EDIT_CODE);
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

    /**
     * 显示弹出自定义view
     * @param index
     */
    public void showListItemMenuDialog(final int index){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissListItemMenuDialog();

        mDialog = new Dialog(OneLevelOperationActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(OneLevelOperationActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(R.string.view_two_level)); //查看二级分类
        menus.add(getStringResource(R.string.delete));//删除

        SimpleListAdapter adapter = new SimpleListAdapter(OneLevelOperationActivity.this, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //查看二级分类
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.view_two_level))){
                    Intent it = new Intent(OneLevelOperationActivity.this, TwoLevelOperationActivity.class);
                    it.putExtra("oneLevelCategoryId", mOneLevelGategoryEdits.get(index).getId());
                    startActivity(it);
                    //删除
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.delete))){
                    final String value = mOneLevelGategoryEdits.get(index).getValue();
                    AppUtil.vibrate(OneLevelOperationActivity.this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OneLevelOperationActivity.this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除一级分类《" + value +"》吗？这是不可逆行为，删掉将不能恢复！同时也会将其下面的二级分类进行删除。");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        //处理数据库数据
                                        oneLevelCategoryDataBase.delete(mOneLevelGategoryEdits.get(index).getId());
                                        //处理列表数据
                                        mAdapter.remove(index);
                                        //处理缓存数据
                                        BaseApplication.oneLevelCategories = convertEditBeanToBean(mOneLevelGategoryEdits, false);
                                        ToastUtil.success(OneLevelOperationActivity.this, "删除一级分类《" + value + "》成功。");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(OneLevelOperationActivity.this, "删除一级分类《" + value + "》失败。");
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
     * 将OneLevelGategory列表转化成OneLevelGategoryEdit列表
     * @param oneLevelGategories
     * @return
     */
    private List<OneLevelCategoryEdit> convertToEditBean(List<OneLevelCategory> oneLevelGategories){
        List<OneLevelCategoryEdit> oneLevelGategoryEdits = new ArrayList<>();
        if(!CommonUtil.isEmpty(oneLevelGategories)){
            for(OneLevelCategory oneLevelCategory : oneLevelGategories){
                oneLevelGategoryEdits.add(convertToEditBean(oneLevelCategory));
            }
        }
        return oneLevelGategoryEdits;
    }

    private OneLevelCategoryEdit convertToEditBean(OneLevelCategory oneLevelCategory){
        OneLevelCategoryEdit oneLevelGategoryEdit = new OneLevelCategoryEdit();
        oneLevelGategoryEdit.setBudget(oneLevelCategory.getBudget());
        oneLevelGategoryEdit.setModel(oneLevelCategory.getModel());
        oneLevelGategoryEdit.setCreateTime(oneLevelCategory.getCreateTime());
        oneLevelGategoryEdit.setCreateUserId(oneLevelCategory.getCreateUserId());
        oneLevelGategoryEdit.setIcon(oneLevelCategory.getIcon());
        oneLevelGategoryEdit.setId(oneLevelCategory.getId());
        oneLevelGategoryEdit.setOrder(oneLevelCategory.getOrder());
        oneLevelGategoryEdit.setIsDefault(oneLevelCategory.isDefault());
        oneLevelGategoryEdit.setStatus(oneLevelCategory.getStatus());
        oneLevelGategoryEdit.setTwoLevelCategories(oneLevelCategory.getTwoLevelCategories());
        oneLevelGategoryEdit.setValue(oneLevelCategory.getValue());
        return oneLevelGategoryEdit;
    }

    /**
     * 将OneLevelGategoryEdit列表转化成OneLevelGategory列表
     * @param oneLevelGategoryEdits
     * @param resetOrder 是否重置排序顺序
     * @return
     */
    private List<OneLevelCategory> convertEditBeanToBean(List<OneLevelCategoryEdit> oneLevelGategoryEdits, boolean resetOrder){
        List<OneLevelCategory> oneLevelGategories = new ArrayList<>();
        if(!CommonUtil.isEmpty(oneLevelGategoryEdits)){
            for(int i = 0; i < oneLevelGategoryEdits.size(); i++){
                oneLevelGategories.add(convertEditBeanToBean(oneLevelGategoryEdits.get(i), resetOrder, (i + 1)));
            }
        }
        return oneLevelGategories;
    }

    private OneLevelCategory convertEditBeanToBean(OneLevelCategoryEdit oneLevelCategoryEdit, boolean resetOrder, int order){
        OneLevelCategory oneLevelCategory = new OneLevelCategoryEdit();
        oneLevelCategory.setBudget(oneLevelCategoryEdit.getBudget());
        oneLevelCategory.setModel(oneLevelCategoryEdit.getModel());
        oneLevelCategory.setCreateTime(oneLevelCategoryEdit.getCreateTime());
        oneLevelCategory.setCreateUserId(oneLevelCategoryEdit.getCreateUserId());
        oneLevelCategory.setIcon(oneLevelCategoryEdit.getIcon());
        oneLevelCategory.setId(oneLevelCategoryEdit.getId());
        if(resetOrder)
            oneLevelCategory.setOrder(order);
        else
            oneLevelCategory.setOrder(oneLevelCategoryEdit.getOrder());
        oneLevelCategory.setIsDefault(oneLevelCategoryEdit.isDefault());
        oneLevelCategory.setStatus(oneLevelCategoryEdit.getStatus());
        oneLevelCategory.setTwoLevelCategories(oneLevelCategoryEdit.getTwoLevelCategories());
        oneLevelCategory.setValue(oneLevelCategoryEdit.getValue());
        return oneLevelCategory;
    }

    /**
     * 编辑一级分类
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Intent it = new Intent(OneLevelOperationActivity.this, OneLevelEditActivity.class);
        it.putExtra("oneLevelCategoryId", mOneLevelGategoryEdits.get(position).getId());
        it.putExtra("clickPosition", position);//方便回传定位
        startActivityForResult(it, ONE_LEVEL_CATEGORY_EDIT_CODE);
    }

    @Override
    public void onItemLongClick(int position) {
        showListItemMenuDialog(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case ONE_LEVEL_CATEGORY_EDIT_CODE:
                if(data == null)
                    return;

                String type = data.getStringExtra("type");
                if(StringUtil.isNull(type))
                    return;

                int clickPosition = data.getIntExtra("clickPosition", 0);
                if(type.equals("save")){
                    OneLevelCategory oneLevelCategory = (OneLevelCategory)data.getSerializableExtra("oneLevelCategory");
                    mAdapter.add(convertToEditBean(oneLevelCategory), clickPosition);
                }else if(type.equals("edit")){
                    OneLevelCategory oneLevelCategory = (OneLevelCategory)data.getSerializableExtra("oneLevelCategory");
                    mAdapter.refresh(convertToEditBean(oneLevelCategory), clickPosition);
                }else if(type.equals("delete")){
                    mAdapter.remove(clickPosition);
                }
                mShowTotalBudget.setText("当月支出总预算：" +String.valueOf(BaseApplication.getTotalBudget().floatValue()));
                ToastUtil.success(this, "activity返回resultCode=" + resultCode + ", requestCode=" + requestCode+",type="+type);
                break;
        }
    }
}
