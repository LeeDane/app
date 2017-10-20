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
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.financial.util.IconUtil;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 二级分类的编辑界面的activity
 * Created by LeeDane on 2016/8/25.
 */
public class TwoLevelEditActivity extends BaseActivity {

    //默认图标数组
    private static final List<String> mIconKeys; //定义数组

    static {
        Map<String, Integer> mapIcons = IconUtil.getInstance().mapIcons;
        mIconKeys = new ArrayList<>();
        mIconKeys.add("请选择");
        for(Map.Entry<String, Integer> entry: mapIcons.entrySet()){
            mIconKeys.add(entry.getKey());
        }
    }
    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;
    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
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
    private String iconName;
    private int clickPosition;//方便回传定位
    private List<OneLevelCategory> oneLevelCategories = new ArrayList<>();
    private List<String> oneLevelList = new ArrayList<>();

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
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(this);
        mTwoLevelCategoryId = getIntent().getIntExtra("twoLevelCategoryId", 0);

        initData();

        //编辑状态
        if(mTwoLevelCategoryId > 0){
            edit = true;
            oneLeveId = mTwoLevelCategory.getOneLevelId();
        }else{//新增状态
            oneLeveId = getIntent().getIntExtra("oneLevelId", 0);
        }

        if(edit && mTwoLevelCategory == null){
            ToastUtil.failure(TwoLevelEditActivity.this, "一级分类不存在");
            finish();
        }

        setContentView(R.layout.activity_financial_two_level_edit);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        backLayoutVisible();
        setTitleViewText(getStringResource(R.string.two_level_edit));

        getOneLevelList();
        initView();

        if(mTwoLevelCategory != null){
            initEditView();
        }else{
            mOneLevel.setSelection(getOneLevelSelection(oneLeveId));
            setTitleViewText("新增二级分类");
            mDelete.setText(getStringResource(R.string.reset));
        }
    }

    /**
     * 对编辑状态的状态进行初始化
     */
    private void initEditView(){
        clickPosition = getIntent().getIntExtra("clickPosition", 0);
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

        iconName = mTwoLevelCategory.getIconName();
        mSpinner.setSelection(getIconSelection(mTwoLevelCategory.getIconName()));
        if(StringUtil.isNotNull(mTwoLevelCategory.getIconName()))
            mIcon.setImageResource(IconUtil.getInstance().getIcon(mTwoLevelCategory.getIconName()));

        mOneLevel.setSelection(getOneLevelSelection(oneLeveId));

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
        int k = 0;
        for(OneLevelCategory o: oneLevelCategories ){
            if(o.getId() == oneLeveId){
                p = k;
                break;
            }
            k++;
        }
        return p;
    }

    /**
     * 获取图标的默认选项
     * @return
     */
    private int getIconSelection(String icon){
        int p = 0;
        if(StringUtil.isNull(icon))
            return p;
        for(int i = 1 ; i < mIconKeys.size(); i++ ){
            if(mIconKeys.get(i).equals(icon)){
                p = i;
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
                if(category.getStatus() == ConstantsUtil.STATUS_NORMAL && category.getId() == mTwoLevelCategoryId){
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
        setResult(FlagUtil.TWO_LEVEL_CATEGORY_EDIT_CODE, it);

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
                isDefault = checkedId == R.id.two_level_edit_default;
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
                    iconName = "请选择";
                    mIcon.setVisibility(View.GONE);
                    return;
                }
                iconName = mIconKeys.get(position);
                mIcon.setImageResource(IconUtil.getInstance().getIcon(iconName));
                mIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                iconName = "请选择";
                mIcon.setVisibility(View.GONE);
            }
        });


        mOneLevel = (Spinner)findViewById(R.id.two_level_edit_one_level);
        ArrayAdapter oneLevelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, oneLevelList);
        mOneLevel.setAdapter(oneLevelAdapter);
        mOneLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oneLeveId = oneLevelCategories.get(position).getId();
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

        int model = TwoLevelCategoryDataBase.getModel(oneLeveId);
        if(!CommonUtil.isEmpty(BaseApplication.oneLevelCategories)){
            for(OneLevelCategory category: BaseApplication.oneLevelCategories){
                if(category.getStatus() == ConstantsUtil.STATUS_NORMAL && model == category.getModel()){
                    oneLevelCategories.add(category);
                    oneLevelList.add(category.getValue() + (category.isDefault()? "（默认)" : ""));
                }
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

                    //当前设置为默认的话，重置默认
                    if(mTwoLevelCategory.isDefault()){
                        int model = TwoLevelCategoryDataBase.getModel(oneLeveId);
                        //清空model下的一级分类默认
                        twoLevelCategoryDataBase.excuteSql("update " + OneLevelCategoryDataBase.ONE_LEVEL_CATEGORY_TABLE_NAME + " set is_default = 0 where model =" + model);
                        //设置当前的一级分类为默认
                        twoLevelCategoryDataBase.excuteSql("update " + OneLevelCategoryDataBase.ONE_LEVEL_CATEGORY_TABLE_NAME + " set is_default = 1 where id = " + oneLeveId);

                        //清空所有的二级分类的默认
                        twoLevelCategoryDataBase.resetAllNoDefault(model);
                    }

                    Intent it = new Intent(this, TwoLevelOperationActivity.class);
                    twoLevelCategoryDataBase.save(mTwoLevelCategory);
                    if(edit){
                        ToastUtil.success(TwoLevelEditActivity.this, "编辑二级分类成功");
                        it.putExtra("type", "edit");
                    }else{
                        ToastUtil.success(TwoLevelEditActivity.this, "新增二级分类成功");
                        //新增的保存成功后把该条数据查出来
                        List<TwoLevelCategory> twoLevelCategories =  twoLevelCategoryDataBase.query(" where value= '" +mTwoLevelCategory.getValue()+ "' and order_ ="+ mTwoLevelCategory.getOrder()
                                                                                    +" and one_level_id=" +mTwoLevelCategory.getOneLevelId());
                        mTwoLevelCategory = twoLevelCategories.get(0);
                        it.putExtra("type", "save");
                    }

                    refreshOneLevelCache();
                    //缓存改变数据
                    refreshTwoLevelCache();

                    it.putExtra("twoLevelCategory", mTwoLevelCategory);
                    it.putExtra("clickPosition", clickPosition);//方便回传定位
                    setResult(FlagUtil.TWO_LEVEL_CATEGORY_EDIT_CODE, it);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    if(edit)
                        ToastUtil.success(TwoLevelEditActivity.this, "编辑二级分类失败");
                    else
                        ToastUtil.success(TwoLevelEditActivity.this, "新增二级分类失败");
                }
                break;
            case R.id.two_level_edit_delete: //删除或者重置

                if(edit){
                    AppUtil.vibrate(this, 50);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.menu_feedback);
                    builder.setTitle("重要提示");
                    builder.setMessage("要删除二级分类《" + mTwoLevelCategory.getValue() +"》吗？这是不可逆行为，删掉将不能恢复！");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
                                        twoLevelCategoryDataBase.delete(mTwoLevelCategoryId);
                                        Intent it = new Intent(TwoLevelEditActivity.this, TwoLevelOperationActivity.class);
                                        it.putExtra("type", "delete");
                                        it.putExtra("clickPosition", clickPosition);//方便回传定位
                                        refreshTwoLevelCache();
                                        setResult(FlagUtil.TWO_LEVEL_CATEGORY_EDIT_CODE, it);
                                        finish();
                                        ToastUtil.success(TwoLevelEditActivity.this, "删除二级分类《" + mTwoLevelCategory.getValue() + "》成功。");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.success(TwoLevelEditActivity.this, "删除二级分类《" + mTwoLevelCategory.getValue() + "》失败。");
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

    /**
     * 重新获取一级分类的缓存
     */
    private void refreshOneLevelCache(){
        //缓存改变数据
        BaseApplication.oneLevelCategories = oneLevelCategoryDataBase.query(" order by order_ ");
    }

    /**
     * 重新获取二级分类的缓存
     */
    private void refreshTwoLevelCache(){
        //缓存改变数据
        BaseApplication.twoLevelCategories = twoLevelCategoryDataBase.query(" order by order_ ");
    }

    /**
     * 判断分类名称是否合法
     * @param value
     * @return
     */
    private boolean isExistsValue(String value){
        for(TwoLevelCategory twoLevelCategory: BaseApplication.twoLevelCategories){
            if(twoLevelCategory.getOneLevelId() == oneLeveId && value.equals(twoLevelCategory.getValue())){
                return true;
            }
        }
        return false;
    }

    //构建二级分类对象
    private void buildTwoLevelCategory(){

        String name = mName.getText().toString();
        if(StringUtil.isNull(name)){
            ToastUtil.failure(this, "请输入分类名称");
            return;
        }

        if(!edit)
            if(isExistsValue(name)){
                ToastUtil.failure(this, name +"已经被占用");
                return;
            }

        if(mTwoLevelCategory == null)
            mTwoLevelCategory = new TwoLevelCategory();

        mTwoLevelCategory.setValue(name);
        mTwoLevelCategory.setBudget(StringUtil.changeObjectToFloat(mBudget.getText().toString()));
        mTwoLevelCategory.setOrder(StringUtil.changeObjectToInt(mSort.getText().toString()));

        mTwoLevelCategory.setStatus(status);

        mTwoLevelCategory.setOneLevelId(oneLeveId);

        mTwoLevelCategory.setIsDefault(isDefault);
        if(!"请选择".equals(iconName))
            mTwoLevelCategory.setIconName(iconName);
    }

    @Override
    protected void onDestroy() {
        twoLevelCategoryDataBase.destroy();
        oneLevelCategoryDataBase.destroy();
        super.onDestroy();
    }
}
