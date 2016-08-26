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
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级分类的编辑界面的activity
 * Created by LeeDane on 2016/8/25.
 */
public class TwoLevelEditActivity extends BaseActivity {

    //默认图标数组
    private static final int[] mIconValues = { R.drawable.ic_category_edit, R.drawable.ic_trending_up_blue_a200_18dp, R.drawable.ic_list_white_18dp,
            R.drawable.ic_richpush_actionbar_back, R.drawable.ic_trending_down_pink_a200_18dp }; //定义数组
    private static final String[] mIconKeys = {"请选择", "分类", "收入", "列表", "向左", "支出"}; //定义数组

    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;
    private TwoLevelCategory mTwoLevelCategory;
    private int mTwoLevelCategoryId;
    private boolean edit; //是否可以编辑
    private EditText mName;
    private EditText mBudget;
    private EditText mSort;

    private RadioGroup mStatusGroup;

    private RadioGroup mDefaultGroup;

    private Spinner mOneLevel;
    private Spinner mSpinner;
    private ImageView mIcon;

    private TextView mSave;
    private TextView mDelete;

    private int status = 1;
    private int oneLeveId;
    private boolean isDefault = false;
    private int iconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if (!checkedIsLogin()) {
            Intent it = new Intent(this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.TwoLevelEditActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }

        twoLevelCategoryDataBase = new TwoLevelCategoryDataBase(this);
        mTwoLevelCategoryId = getIntent().getIntExtra("twoLevelCategoryId", 0);

        if(mTwoLevelCategoryId > 0){
            edit = true;
        }
        initData();

        if(edit && mTwoLevelCategory == null){
            ToastUtil.failure(TwoLevelEditActivity.this, "一级分类不存在");
            finish();
        }

        setContentView(R.layout.activity_financial_two_level_edit);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        setTitleViewText(getStringResource(R.string.two_level_edit));

        initView();

        if(mTwoLevelCategory != null){
            initEditView();
        }else{
            setTitleViewText("新增二级分类");
            mDelete.setText(getStringResource(R.string.reset));
        }
    }

    /**
     * 对编辑状态的状态进行初始化
     */
    private void initEditView(){
        mName.setText(mTwoLevelCategory.getValue());
        mBudget.setText(String.valueOf(mTwoLevelCategory.getBudget()));
        mSort.setText(String.valueOf(StringUtil.changeObjectToInt(mTwoLevelCategory.getOrder())));

        status = mTwoLevelCategory.getStatus();
        if(status == ConstantsUtil.STATUS_NORMAL){
            ((RadioButton)findViewById(R.id.two_level_edit_normal)).setChecked(true);
            ((RadioButton)findViewById(R.id.two_level_edit_disable)).setChecked(false);
        }else{
            ((RadioButton)findViewById(R.id.two_level_edit_normal)).setChecked(false);
            ((RadioButton)findViewById(R.id.two_level_edit_disable)).setChecked(true);
        }

        isDefault = mTwoLevelCategory.isDefault();
        if(isDefault){
            ((RadioButton)findViewById(R.id.two_level_edit_default)).setChecked(true);
            ((RadioButton)findViewById(R.id.two_level_edit_no_default)).setChecked(false);
        }else{
            ((RadioButton)findViewById(R.id.two_level_edit_default)).setChecked(false);
            ((RadioButton)findViewById(R.id.two_level_edit_no_default)).setChecked(true);
        }

        iconId = mTwoLevelCategory.getIcon();
        mSpinner.setSelection(getIconSelection(mTwoLevelCategory.getIcon()));
        if(mTwoLevelCategory.getIcon() > 0)
            mIcon.setImageResource(mTwoLevelCategory.getIcon());

        oneLeveId = mTwoLevelCategory.getOneLevelId();
        mOneLevel.setSelection(getOneLevelSelection(oneLeveId));
        mOneLevel.setEnabled(false);

        mDelete.setText(getStringResource(R.string.delete));
        setTitleViewText(mTwoLevelCategory.getValue() + "编辑");
    }

    /**
     * 获取一级分类的默认选项
     * @param oneLeveId
     * @return
     */
    private int getOneLevelSelection(int oneLeveId){
        int p = 0;
        if(oneLeveId == 0)
            return p;
        for(OneLevelCategory o: BaseApplication.oneLevelCategories ){
            if(o.getId() == oneLeveId){
                p = o.getId();
                break;
            }
        }
        return p;
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
        List<TwoLevelCategory> twoLevelCategories = BaseApplication.twoLevelCategories;
        if(!CommonUtil.isEmpty(twoLevelCategories)){
            for(TwoLevelCategory category: twoLevelCategories){
                if(category.getId() == mTwoLevelCategoryId){
                    mTwoLevelCategory = category;
                    break;
                }
            }
        }
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        Intent it = new Intent(this, TwoLevelOperationActivity.class);
        setResult(TwoLevelOperationActivity.TWO_LEVEL_CATEGORY_EDIT_CODE, it);

        mName = (EditText)findViewById(R.id.two_level_edit_name);
        mBudget = (EditText)findViewById(R.id.two_level_edit_budget);
        mSort = (EditText)findViewById(R.id.two_level_edit_sort);

        mStatusGroup = (RadioGroup)findViewById(R.id.two_level_edit_status_group);
        mStatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.two_level_edit_normal){
                    status = 1;
                }else
                    status = 0;
            }
        });

        mDefaultGroup = (RadioGroup)findViewById(R.id.two_level_edit_default_group);
        mDefaultGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.two_level_edit_default){
                    isDefault = true;
                }else
                    isDefault = false;
            }
        });

        mSpinner = (Spinner)findViewById(R.id.two_level_edit_spinner);
        mIcon = (ImageView)findViewById(R.id.two_level_edit_icon);
        //将可选内容与ArrayAdapter连接，
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mIconKeys);
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


        mOneLevel = (Spinner)findViewById(R.id.two_level_edit_one_level);
        ArrayAdapter oneLevelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getOneLevelList());
        mOneLevel.setAdapter(oneLevelAdapter);
        mOneLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oneLeveId = BaseApplication.oneLevelCategories.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                oneLeveId = 0;
            }
        });

        mSave = (TextView)findViewById(R.id.two_level_edit_save);
        mDelete = (TextView)findViewById(R.id.two_level_edit_delete);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    /**
     * 获取一级分类的列表
     * @return
     */
    private List<String> getOneLevelList(){
        List<String> oneLevelList = new ArrayList<>();
        if(!CommonUtil.isEmpty(BaseApplication.oneLevelCategories)){
            for(OneLevelCategory category: BaseApplication.oneLevelCategories){
                oneLevelList.add(category.getValue() + (category.isDefault()? "（默认)" : ""));
            }
        }
        return oneLevelList;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.two_level_edit_save: //保存
                try {
                    buildTwoLevelCategory();
                    Intent it = new Intent(this, TwoLevelOperationActivity.class);
                    twoLevelCategoryDataBase.save(mTwoLevelCategory);
                    if(edit){
                        ToastUtil.success(TwoLevelEditActivity.this, "编辑一级分类成功");
                        it.putExtra("type", "edit");
                    }else{
                        ToastUtil.success(TwoLevelEditActivity.this, "新增一级分类成功");
                        it.putExtra("type", "save");
                    }
                    setResult(TwoLevelOperationActivity.TWO_LEVEL_CATEGORY_EDIT_CODE, it);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(edit)
                        ToastUtil.success(TwoLevelEditActivity.this, "编辑一级分类失败");
                    else
                        ToastUtil.success(TwoLevelEditActivity.this, "新增一级分类失败");
                }
                break;
            case R.id.two_level_edit_delete: //删除或者重置

                if(edit){
                    AppUtil.vibrate(this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除一级分类《" + mTwoLevelCategory.getValue() +"》吗？这是不可逆行为，删掉将不能恢复！");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        twoLevelCategoryDataBase.delete(mTwoLevelCategoryId);
                                        Intent it = new Intent(TwoLevelEditActivity.this, TwoLevelOperationActivity.class);
                                        it.putExtra("type", "delete");
                                        setResult(TwoLevelOperationActivity.TWO_LEVEL_CATEGORY_EDIT_CODE, it);
                                        finish();
                                        ToastUtil.success(TwoLevelEditActivity.this, "删除一级分类《" + mTwoLevelCategory.getValue() + "》成功。");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(TwoLevelEditActivity.this, "删除一级分类《" + mTwoLevelCategory.getValue() + "》失败。");
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
                    ToastUtil.success(TwoLevelEditActivity.this, "重置");
                }
                break;
        }
    }

    //构建二级分类对象
    private void buildTwoLevelCategory(){

        if(mTwoLevelCategory == null)
            mTwoLevelCategory = new TwoLevelCategory();

        mTwoLevelCategory.setValue(mName.getText().toString());
        mTwoLevelCategory.setBudget(Float.parseFloat(mBudget.getText().toString()));
        mTwoLevelCategory.setOrder(StringUtil.changeObjectToInt(mSort.getText().toString()));

        mTwoLevelCategory.setStatus(status);

        mTwoLevelCategory.setOneLevelId(oneLeveId);

        mTwoLevelCategory.setIsDefault(isDefault);

        mTwoLevelCategory.setIcon(iconId);
    }

    @Override
    protected void onDestroy() {
        twoLevelCategoryDataBase.destroy();
        super.onDestroy();
    }
}
