package com.leedane.cn.financial.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.List;

/**
 * 一级分类的编辑界面的activity
 * Created by LeeDane on 2016/8/25.
 */
public class OneLevelEditActivity extends BaseActivity {

    //默认图标数组
    private static final int[] mIconValues = { R.drawable.ic_category_edit, R.drawable.ic_trending_up_blue_a200_18dp, R.drawable.ic_list_white_18dp,
            R.drawable.ic_richpush_actionbar_back, R.drawable.ic_trending_down_pink_a200_18dp }; //定义数组
    private static final String[] mIconKeys = {"请选择", "分类", "收入", "列表", "向左", "支出"}; //定义数组

    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private OneLevelCategory mOneLevelCategory;
    private int mOneLevelCategoryId;
    private boolean edit; //是否可以编辑
    private EditText mName;
    private EditText mBudget;
    private EditText mSort;

    private RadioGroup mStatusGroup;
    private RadioGroup mModelGroup;

    private RadioGroup mDefaultGroup;

    private Spinner mSpinner;
    private ImageView mIcon;

    private TextView mSave;
    private TextView mDelete;

    private int status = 1;
    private int model;
    private boolean isDefault = false;
    private int iconId;

    private int clickPosition;//方便回传定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if (!checkedIsLogin()) {
            Intent it = new Intent(OneLevelEditActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.OneLevelEditActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(this);
        mOneLevelCategoryId = getIntent().getIntExtra("oneLevelCategoryId", 0);

        if(mOneLevelCategoryId > 0){
            edit = true;
        }
        initData();

        if(edit && mOneLevelCategory == null){
            ToastUtil.failure(OneLevelEditActivity.this, "一级分类不存在");
            finish();
        }

        setContentView(R.layout.activity_financial_one_level_edit);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        initView();
        if(mOneLevelCategory != null){
            initEditView();
        }else{
            setTitleViewText("新增一级分类");
            mDelete.setText(getStringResource(R.string.reset));
        }
    }


    /**
     * 对编辑状态的状态进行初始化
     */
    private void initEditView(){
        clickPosition = getIntent().getIntExtra("clickPosition", 0);//方便回传定位

        mName.setText(mOneLevelCategory.getValue());
        mBudget.setText(String.valueOf(mOneLevelCategory.getBudget()));
        mSort.setText(String.valueOf(StringUtil.changeObjectToInt(mOneLevelCategory.getOrder())));

        status = mOneLevelCategory.getStatus();
        if(status  == ConstantsUtil.STATUS_NORMAL){
            ((RadioButton)findViewById(R.id.one_level_edit_normal)).setChecked(true);
            ((RadioButton)findViewById(R.id.one_level_edit_disable)).setChecked(false);
        }else{
            ((RadioButton)findViewById(R.id.one_level_edit_normal)).setChecked(false);
            ((RadioButton)findViewById(R.id.one_level_edit_disable)).setChecked(true);
        }

        model = mOneLevelCategory.getModel();
        if(model  == IncomeOrSpendActivity.FINANCIAL_MODEL_INCOME){
            ((RadioButton)findViewById(R.id.one_level_edit_income)).setChecked(true);
            ((RadioButton)findViewById(R.id.one_level_edit_spend)).setChecked(false);
        }else{
            ((RadioButton)findViewById(R.id.one_level_edit_income)).setChecked(false);
            ((RadioButton)findViewById(R.id.one_level_edit_spend)).setChecked(true);
        }

        isDefault = mOneLevelCategory.isDefault();
        if(isDefault){
            ((RadioButton)findViewById(R.id.one_level_edit_default)).setChecked(true);
            ((RadioButton)findViewById(R.id.one_level_edit_no_default)).setChecked(false);
        }else{
            ((RadioButton)findViewById(R.id.one_level_edit_default)).setChecked(false);
            ((RadioButton)findViewById(R.id.one_level_edit_no_default)).setChecked(true);
        }

        iconId = mOneLevelCategory.getIcon();//设置默认值
        mSpinner.setSelection(getIconSelection(mOneLevelCategory.getIcon()));
        if(mOneLevelCategory.getIcon() > 0)
            mIcon.setImageResource(mOneLevelCategory.getIcon());

        mDelete.setText(getStringResource(R.string.delete));
        setTitleViewText(mOneLevelCategory.getValue() +"编辑");
    }

    /**
     * 获取图标的默认选项
     * @return
     */
    private int getIconSelection(int icon){
        int p = 0;
        if(icon == 0)
            return p;
        for(int k: mIconValues ){
            if(k == icon){
                p = k;
                break;
            }
        }
        return p;
    }
    /**
     * 初始化数据
     */
    private void initData() {
        List<OneLevelCategory> oneLevelCategories = BaseApplication.oneLevelCategories;
        if(!CommonUtil.isEmpty(oneLevelCategories)){
            for(OneLevelCategory category: oneLevelCategories){
                if(category.getId() == mOneLevelCategoryId){
                    mOneLevelCategory = category;
                    break;
                }
            }
        }
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        Intent it = new Intent(OneLevelEditActivity.this, OneLevelOperationActivity.class);
        setResult(OneLevelOperationActivity.ONE_LEVEL_CATEGORY_EDIT_CODE, it);

        mName = (EditText)findViewById(R.id.one_level_edit_name);
        mBudget = (EditText)findViewById(R.id.one_level_edit_budget);
        mSort = (EditText)findViewById(R.id.one_level_edit_sort);

        mStatusGroup = (RadioGroup)findViewById(R.id.one_level_edit_status_group);
        mStatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.one_level_edit_normal){
                    status = 1;
                }else
                    status = 0;
            }
        });

        mModelGroup = (RadioGroup)findViewById(R.id.one_level_edit_model_group);
        mModelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.one_level_edit_income){
                    model = 1;
                }else
                    model = 2;
            }
        });

        mDefaultGroup = (RadioGroup)findViewById(R.id.one_level_edit_default_group);
        mDefaultGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.one_level_edit_default){
                    isDefault = true;
                }else
                    isDefault = false;
            }
        });

        mSpinner = (Spinner)findViewById(R.id.one_level_edit_spinner);
        mIcon = (ImageView)findViewById(R.id.one_level_edit_icon);
        //将可选内容与ArrayAdapter连接，
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mIconKeys);
        //将adapter添加到m_Spinner中
        mSpinner.setAdapter(adapter);
        //到这里，就完成了下拉框的绑定数据，下拉框中已经有我们想要选择的值了。下面获取选择的值。
        //添加Spinner事件监听
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    iconId = 0;
                    mIcon.setVisibility(View.GONE);
                    return;
                }
                iconId = mIconValues[position];
                mIcon.setImageResource(iconId);
                mIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                iconId = 0;
                mIcon.setVisibility(View.GONE);
            }
        });

        mSave = (TextView)findViewById(R.id.one_level_edit_save);
        mDelete = (TextView)findViewById(R.id.one_level_edit_delete);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.one_level_edit_save: //保存
                try {
                    buildOneLevelCategory();
                    Intent it = new Intent(OneLevelEditActivity.this, OneLevelOperationActivity.class);
                    if(edit){
                        oneLevelCategoryDataBase.update(mOneLevelCategory);
                        ToastUtil.success(OneLevelEditActivity.this, "编辑一级分类成功");
                        it.putExtra("type", "edit");
                    }else{
                        oneLevelCategoryDataBase.insert(mOneLevelCategory);
                        ToastUtil.success(OneLevelEditActivity.this, "新增一级分类成功");
                        //新增的保存成功后把该条数据查出来
                        List<OneLevelCategory> oneLevelCategories =  oneLevelCategoryDataBase.query(" where value= '" +mOneLevelCategory.getValue()+ "' and order_ ="+mOneLevelCategory.getOrder()
                                    +" and model="+mOneLevelCategory.getModel());
                        it.putExtra("type", "save");
                        mOneLevelCategory = oneLevelCategories.get(0);
                    }
                    //缓存改变数据
                    BaseApplication.oneLevelCategories = oneLevelCategoryDataBase.query(" order by order_ ");

                    it.putExtra("oneLevelCategory", mOneLevelCategory);
                    it.putExtra("clickPosition", clickPosition);//方便回传定位
                    setResult(OneLevelOperationActivity.ONE_LEVEL_CATEGORY_EDIT_CODE, it);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(edit)
                        ToastUtil.success(OneLevelEditActivity.this, "编辑一级分类失败");
                    else
                        ToastUtil.success(OneLevelEditActivity.this, "新增一级分类失败");
                }
                break;
            case R.id.one_level_edit_delete: //删除或者重置

                if(edit){
                    AppUtil.vibrate(this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除一级分类《" + mOneLevelCategory.getValue() +"》吗？这是不可逆行为，删掉将不能恢复！");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        oneLevelCategoryDataBase.delete(mOneLevelCategoryId);
                                        Intent it = new Intent(OneLevelEditActivity.this, OneLevelOperationActivity.class);
                                        it.putExtra("type", "delete");
                                        it.putExtra("clickPosition", clickPosition);//方便回传定位
                                        setResult(OneLevelOperationActivity.ONE_LEVEL_CATEGORY_EDIT_CODE, it);
                                        finish();
                                        ToastUtil.success(OneLevelEditActivity.this, "删除一级分类《" + mOneLevelCategory.getValue() + "》成功。");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(OneLevelEditActivity.this, "删除一级分类《" + mOneLevelCategory.getValue() + "》失败。");
                                    }

                                }
                            });
                    builder.setNegativeButton("放弃",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            });
                    builder.show();
                }else{
                    ToastUtil.success(OneLevelEditActivity.this, "重置");
                }
                break;
        }
    }

    //构建一级分类对象
    private void buildOneLevelCategory(){

        if(mOneLevelCategory == null)
            mOneLevelCategory = new OneLevelCategory();

        mOneLevelCategory.setValue(mName.getText().toString());
        mOneLevelCategory.setBudget(Float.parseFloat(mBudget.getText().toString()));
        mOneLevelCategory.setOrder(StringUtil.changeObjectToInt(mSort.getText().toString()));

        mOneLevelCategory.setStatus(status);

        mOneLevelCategory.setModel(model);

        mOneLevelCategory.setIsDefault(isDefault);

        mOneLevelCategory.setIcon(iconId);
    }

    @Override
    protected void onDestroy() {
        oneLevelCategoryDataBase.destroy();
        super.onDestroy();
    }
}
