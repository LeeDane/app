package com.leedane.cn.financial.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialLocationBean;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.database.FinancialLocationDataBase;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.FileUtil;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.QiniuUploadManager;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.util.http.BeanUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 收入activity
 * Created by LeeDane on 2016/7/21.
 */
public class IncomeOrSpendActivity extends BaseActivity {
    //model是添加收入
    public static final int FINANCIAL_MODEL_INCOME = 1;
    //model是添加支出
    public static final int FINANCIAL_MODEL_SPEND = 2;

    //收入的颜色
    public static final int FINANCIAL_INCOME_COLOR = Color.rgb(129, 129, 247);

    //支出的颜色
    public static final int FINANCIAL_SPEND_COLOR = Color.rgb(255,0,0);

    //金钱
    private EditText mMoney;

    //选择日期时间
    private EditText mDate;

    //附加图片
    private ImageView mImg;

    //图片右侧的提示图标
    private BadgeView badge;

    //图片上传进度条
    private ProgressBar mProgressBar;

    //一级分类
    private TextView mOneLevel;

    //二级分类
    private TextView mTwoLevel;

    //备注
    private EditText mRemark;

    //图像的链接路径
    private String mPath;

    private String mLocalPath;

    private Spinner mLocation;
    private int selectLocationPosition = 0;

    // 模块，1:收入；2：支出
    private int mModel;

    public String token;

    private Calendar showTime;

    private TextView mSave;
    private TextView mDraft;

    private FinancialDataBase financialDataBase;
    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;
    private FinancialLocationDataBase financialLocationDataBase;
    private FinancialBean editFinancialBean;

    private List<FinancialLocationBean> mLocationBeans; //位置信息

    //是否编辑状态
    private boolean isEdit;

    private OptionsPickerView pvCategoryOptions;//弹出分类选项
    private ArrayList<OneLevelCategory> oneLevelCategories; //一级分类
    private ArrayList<ArrayList<TwoLevelCategory>> twoLevelCategories; //二级分类
    private TimePickerView pvTime; //时间选择器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(IncomeOrSpendActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.financial.activity.IncomeOrSpendActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
            return;
        }
        setContentView(R.layout.activity_financial_income_or_spend);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        financialDataBase = new FinancialDataBase(IncomeOrSpendActivity.this);

        mImg = (ImageView)findViewById(R.id.financial_income_or_spend_img);
        mImg.setOnClickListener(IncomeOrSpendActivity.this);
        badge = new BadgeView(this, mImg);
        badge.setText("—");
        badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.take_photo));
                mPath = null;
                mProgressBar.setVisibility(View.GONE);
                badge.hide();
            }
        });

        initData();
        backLayoutVisible();
        findViewById(R.id.base_title_textview).setOnClickListener(this);
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(this);
        twoLevelCategoryDataBase = new TwoLevelCategoryDataBase(this);
        financialLocationDataBase = new FinancialLocationDataBase(this);

        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //判断是否是编辑状态
        if(getIntent().getIntExtra("local_id", 0) > 0){
            List<FinancialBean> fbeans = financialDataBase.query(" where local_id="+getIntent().getIntExtra("local_id", 0));
            if(CommonUtil.isEmpty(fbeans)){
                ToastUtil.failure(this, "该记账记录已经不存在！");
                finish();
            }
            editFinancialBean = fbeans.get(0);
            mModel = editFinancialBean.getModel();
            isEdit = true;
        }else{
            //默认是添加收入
            mModel = getIntent().getIntExtra("model", FINANCIAL_MODEL_SPEND);
        }

        if(mModel == FINANCIAL_MODEL_INCOME)
            setTitleViewText(getStringResource(R.string.financila_add_income));
        else
            setTitleViewText(getStringResource(R.string.financila_add_spend));

        //选项选择器
        pvCategoryOptions = new OptionsPickerView(this);
        resetCategorys();
        //设置选择的三级单位
        //pvCategoryOptions.setLabels("一级分类", "二级分类");
        pvCategoryOptions.setTitle("选择分类");
        pvCategoryOptions.setCyclic(false, false, true);
        pvCategoryOptions.setCancelable(true);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvCategoryOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mOneLevel.setText(oneLevelCategories.get(options1).getPickerViewText());
                mTwoLevel.setText(twoLevelCategories.get(options1).get(option2).getPickerViewText());
            }
        });

        if(getIntent().getType() != null){
            if(getIntent().getType().startsWith("image/")){
                List<String> images = AppUtil.getListPicPaths(IncomeOrSpendActivity.this);
                if(images.size() > 0){
                    if(images.size() > 1){
                        ToastUtil.success(IncomeOrSpendActivity.this, "抱歉，目前系统只接受一张图片，已自动为您选择一张图片展示。");
                    }
                    mLocalPath = images.get(0);
                    Bitmap bitmap = null;
                    if(StringUtil.isNotNull(mLocalPath)){
                        bitmap = BitmapUtil.getSmallBitmap(IncomeOrSpendActivity.this, mLocalPath, 150, 150);
                        mImg.setImageBitmap(bitmap);
                        CommonHandler.getQiniuTokenRequest(IncomeOrSpendActivity.this);
                        badge.show();
                    }else
                        ToastUtil.failure(IncomeOrSpendActivity.this, "获取不到图片路径");
                }
            }else if(getIntent().getType().startsWith("text/")){
                String v =  getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if(StringUtil.isNotNull(v)){
                    mRemark.setText(v);
                }
            }
        }
    }

    /**
     * 重置分类
     */
    private void resetCategorys(){
        oneLevelCategories = getOneLevelCategorys();
        twoLevelCategories = getTwoLevelCategorys(oneLevelCategories);
        pvCategoryOptions.setPicker(oneLevelCategories, twoLevelCategories, true);
        pvCategoryOptions.setSelectOptions(0, getTwoLevelCategorysDefaultIndex());
    }

    /**
     * 获取展示的一级分类
     * @return
     */
    private ArrayList<OneLevelCategory> getOneLevelCategorys(){
        List<OneLevelCategory> oneLevelGategories = BaseApplication.oneLevelCategories;
        ArrayList<OneLevelCategory> categories = new ArrayList<>();
        if(oneLevelGategories.size() > 0 ){
            for(OneLevelCategory oneLevelCategory : oneLevelGategories){
                if(oneLevelCategory.getModel() == mModel)
                    categories.add(oneLevelCategory);
            }
        }
        return categories;
    }

    /**
     * 获取展示的二级分类
     * @param oneLevelCategories
     * @return
     */
    private ArrayList<ArrayList<TwoLevelCategory>> getTwoLevelCategorys(ArrayList<OneLevelCategory> oneLevelCategories){
        ArrayList<ArrayList<TwoLevelCategory>> categories = new ArrayList<>();
        List<TwoLevelCategory> twoLevelCategories = BaseApplication.twoLevelCategories;
        for(OneLevelCategory oneLevelCategory: oneLevelCategories){
            ArrayList<TwoLevelCategory> twoLevelCategoryArrayList = new ArrayList<>();
            for(TwoLevelCategory twoLevelCategory: twoLevelCategories){
                if(twoLevelCategory.getOneLevelId() == oneLevelCategory.getId()){
                    twoLevelCategoryArrayList.add(twoLevelCategory);
                }
            }
            categories.add(twoLevelCategoryArrayList);
        }
        return categories;
    }

    /**
     * 获取二级分类第一个展示的默认值
     * @return
     */
    private int getTwoLevelCategorysDefaultIndex(){
        int index = 0;
        if(CommonUtil.isEmpty(twoLevelCategories.get(0)))
            return index;
        for(int i = 0; i < twoLevelCategories.get(0).size(); i++){
            if(twoLevelCategories.get(0).get(i).isDefault()){
                index = i;
                break;
            }
        }
        return index;
    }
    /**
     * 判断当前是否是可以编辑的状态
     * @return
     */
    private boolean isEdit(){
        return isEdit && editFinancialBean != null ? true: false;
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        mProgressBar = (ProgressBar)findViewById(R.id.financial_income_or_spend_img_progressbar);
        mMoney = (EditText)findViewById(R.id.financial_income_or_spend_money);
        mDate = (EditText)findViewById(R.id.financial_income_or_spend_date);
        mDate.setOnClickListener(IncomeOrSpendActivity.this);

        mOneLevel = (TextView)findViewById(R.id.financial_income_or_spend_one_level);
        mTwoLevel = (TextView)findViewById(R.id.financial_income_or_spend_two_level);
        mOneLevel.setOnClickListener(IncomeOrSpendActivity.this);
        mTwoLevel.setOnClickListener(IncomeOrSpendActivity.this);

        mSave = (TextView)findViewById(R.id.financial_income_or_spend_save);
        mDraft = (TextView)findViewById(R.id.financial_income_or_spend_draft);
        mSave.setOnClickListener(IncomeOrSpendActivity.this);
        mDraft.setOnClickListener(IncomeOrSpendActivity.this);

        mRemark = (EditText)findViewById(R.id.financial_income_or_spend_remark);

        //位置信息
        mLocation = (Spinner)findViewById(R.id.financial_income_or_spend_location);

        mLocationBeans = financialLocationDataBase.query(" where status = 1 order by id desc ");
        if(!CommonUtil.isEmpty(mLocationBeans)){
            List<String> locations = new ArrayList<>();
            locations.add("请选择记账的位置信息");
            for(FinancialLocationBean financialLocationBean: mLocationBeans){
                locations.add(financialLocationBean.getLocation());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locations);
            mLocation.setAdapter(arrayAdapter);
            mLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0)
                        selectLocationPosition = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            mLocation.setSelection(0, true);
        }
        if(isEdit()){
            Drawable drawable= getResources().getDrawable(R.drawable.ic_delete_forever_blue_a400_18dp);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mDraft.setCompoundDrawables(drawable, null, null, null);
            mDraft.setText(R.string.delete);
            showTime = Calendar.getInstance();
            String addTime = editFinancialBean.getAdditionTime();
            showTime.setTime(DateUtil.stringToDate(addTime));
            mDate.setText(addTime);
            mOneLevel.setText(editFinancialBean.getOneLevel());
            mTwoLevel.setText(editFinancialBean.getTwoLevel());
            mMoney.setText(String.valueOf(editFinancialBean.getMoney()));
            mRemark.setText(StringUtil.changeNotNull(editFinancialBean.getFinancialDesc()));
            String la = editFinancialBean.getLocation();
            if(StringUtil.isNotNull(la)){
                if(mLocationBeans.size() > 0){
                    mLocation.setSelection(getLocationPosition(la), true);
                }
            }
            if(StringUtil.isNotNull(editFinancialBean.getPath())){
                mPath = editFinancialBean.getPath();
                ImageCacheManager.loadImage(mPath, mImg, 150, 150);
            }

        }else{
            //以系统时间初始化date和time的展示
            Date date = new Date();
            showTime = Calendar.getInstance();
            showTime.setTime(date);
            mDate.setText(DateUtil.DateToString(date));
            mOneLevel.setText(getOneLevelDefaultValue());
            mTwoLevel.setText(getTwoLevelDefaultValue());
        }

        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.ALL);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        pvTime.setTime(showTime.getTime());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                mDate.setText(DateUtil.DateToString(date));
            }
        });

    }

    /**
     * 初始化一级分类值
     * @return
     */
    private String getOneLevelDefaultValue(){
        String value = "";
        if(CommonUtil.isEmpty(oneLevelCategories))
            return value;
        return oneLevelCategories.get(0).getValue();
    }

    /**
     * 初始化二级分类值
     * @return
     */
    private String getTwoLevelDefaultValue(){
        String value = "";
        if(CommonUtil.isEmpty(twoLevelCategories))
            return value;
        return twoLevelCategories.get(0).get(getTwoLevelCategorysDefaultIndex()).getValue();
    }

    /**
     * 保存操作
     */
    public void doSave(){
        String money = mMoney.getText().toString();
        if(StringUtil.isNull(money)){
            ToastUtil.failure(this, "请输入金额");
            return;
        }

        String onLevel = mOneLevel.getText().toString();
        if(StringUtil.isNull(onLevel)){
            ToastUtil.failure(this, "请先选择一级分类");
            return;
        }

        String twoLevel = mTwoLevel.getText().toString();
        if(StringUtil.isNull(twoLevel)){
            ToastUtil.failure(this, "请先选择二级分类");
            return;
        }

        String date = mDate.getText().toString();
        if(StringUtil.isNull(date)){
            ToastUtil.failure(this, "请先选择日期");
            return;
        }

        if(!isEdit()){
            editFinancialBean = new FinancialBean();
        }

        editFinancialBean.setSynchronous(false);
        editFinancialBean.setFinancialDesc(StringUtil.changeNotNull(mRemark.getText().toString()));
        editFinancialBean.setCreateUserId(BaseApplication.getLoginUserId());
        editFinancialBean.setCreateTime(DateUtil.DateToString(new Date()));
        if(StringUtil.isNotNull(mPath)){
            editFinancialBean.setPath(mPath);
            editFinancialBean.setHasImg(true);
        }
        editFinancialBean.setModel(mModel);
        editFinancialBean.setOneLevel(onLevel);
        editFinancialBean.setTwoLevel(twoLevel);
        editFinancialBean.setStatus(ConstantsUtil.STATUS_NORMAL);
        editFinancialBean.setMoney(Float.valueOf(money));
        editFinancialBean.setAdditionTime(date.trim());

        if(selectLocationPosition > 0)
            editFinancialBean.setLocation(getLocationText(selectLocationPosition - 1));

        try{
            if(isEdit()){
                financialDataBase.update(editFinancialBean);
            }else{
                //新增情况下先保存后查询，保证有local_id
                financialDataBase.save(editFinancialBean);
                editFinancialBean = financialDataBase.query(" order by local_id desc limit 1").get(0);
                isEdit = true;
            }
            Map<String, Object> data = new HashMap<>();
            BeanUtil.convertBeanToMap(editFinancialBean, data);
            FinancialHandler.save(IncomeOrSpendActivity.this, data);
            ToastUtil.success(IncomeOrSpendActivity.this, "数据已成功添加到本地!");
            Intent it = new Intent(this, HomeActivity.class);
            it.putExtra("hasUpdate", true);
            setResult(FlagUtil.IS_EDIT_OR_SAVE_FINANCIAL_CODE, it);
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.failure(IncomeOrSpendActivity.this, "保存失败!" + e.toString());
        }
        //后台重新计算记账数据
        FinancialHandler.calculateFinancialData(IncomeOrSpendActivity.this);
    }

    /**
     * 获取位置信息的选择的内容
     * @param position
     * @return
     */
    private String getLocationText(int position){
        if(CommonUtil.isEmpty(mLocationBeans))
            return null;
        return mLocationBeans.get(position).getLocation();
    }

    /**
     * 获取位置信息的选择的index
     * @param text
     * @return
     */
    private int getLocationPosition(String text){
        if(StringUtil.isNull(text) || CommonUtil.isEmpty(mLocationBeans))
            return 0;

        int i = 0;
        for(FinancialLocationBean bean: mLocationBeans){
            if(text.equals(bean.getLocation())){
                return i + 1;
            }
            i++;
        }
        return 0;
    }

    /**
     * 存为草稿操作
     */
    public void doDraft(){
        if(isEdit()){
            AppUtil.vibrate(IncomeOrSpendActivity.this, 50);//振动
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IncomeOrSpendActivity.this);
            builder.setCancelable(true);
            builder.setIcon(R.drawable.menu_feedback);
            builder.setTitle("提示");
            builder.setMessage("删除该记账记录?");
            builder.setPositiveButton("删除",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            //对于已经提交到服务器上的做标记删除操作
                            if(editFinancialBean.getId() > 0){
                                editFinancialBean.setStatus(ConstantsUtil.STATUS_DELETE);
                                financialDataBase.update(editFinancialBean);
                            }else{
                                //对还没有提交到服务器的，做直接删除操作
                                financialDataBase.delete(editFinancialBean.getLocalId());
                            }
                            ToastUtil.success(IncomeOrSpendActivity.this, "记录删除成功!");
                            Intent it = new Intent(IncomeOrSpendActivity.this, HomeActivity.class);
                            it.putExtra("hasUpdate", true);
                            setResult(FlagUtil.IS_EDIT_OR_SAVE_FINANCIAL_CODE, it);
                            finish();
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    });
            builder.show();
            return;
        }
        String money = mMoney.getText().toString();
        if(StringUtil.isNull(money)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请输入金额");
            return;
        }

        String onLevel = mOneLevel.getText().toString();
        if(StringUtil.isNull(onLevel)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择一级分类");
            return;
        }

        String twoLevel = mTwoLevel.getText().toString();
        if(StringUtil.isNull(twoLevel)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择二级分类");
            return;
        }

        String date = mDate.getText().toString();
        if(StringUtil.isNull(date)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择日期");
            return;
        }

        if(selectLocationPosition > 0)
            editFinancialBean.setLocation(getLocationText(selectLocationPosition - 1));

        editFinancialBean = new FinancialBean();
        editFinancialBean.setSynchronous(false);
        editFinancialBean.setFinancialDesc(StringUtil.changeNotNull(mRemark.getText().toString()));
        editFinancialBean.setCreateUserId(BaseApplication.getLoginUserId());
        editFinancialBean.setCreateTime(DateUtil.DateToString(new Date()));
        if(StringUtil.isNotNull(mPath)){
            editFinancialBean.setPath(mPath);
            editFinancialBean.setHasImg(true);
        }
        editFinancialBean.setModel(mModel);
        editFinancialBean.setOneLevel(onLevel);
        editFinancialBean.setTwoLevel(twoLevel);
        editFinancialBean.setStatus(ConstantsUtil.STATUS_DRAFT);
        editFinancialBean.setMoney(Float.valueOf(money));
        editFinancialBean.setAdditionTime(date.trim());
        try{
            financialDataBase.save(editFinancialBean);
            ToastUtil.success(IncomeOrSpendActivity.this, "草稿数据已成功添加到本地!" );
            finish();
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.failure(IncomeOrSpendActivity.this, "保存失败!" + e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(financialDataBase != null)
            financialDataBase.destroy();
        if(oneLevelCategoryDataBase != null)
            oneLevelCategoryDataBase.destroy();
        if(twoLevelCategoryDataBase != null)
            twoLevelCategoryDataBase.destroy();
        if(financialLocationDataBase != null)
            financialLocationDataBase.destroy();
        taskCanceled(TaskType.ADD_FINANCIAL);
        taskCanceled(TaskType.QINIU_TOKEN);

        if(pvCategoryOptions.isShowing()|| pvTime.isShowing()){
            pvCategoryOptions.dismiss();
            pvTime.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.financial_income_or_spend_img:  //选择图像
                showSelectItemMenuDialog();
                break;

            case  R.id.financial_income_or_spend_one_level: //选择一级分类
                closeInputMethod();
                pvCategoryOptions.show();
                break;
            case R.id.financial_income_or_spend_two_level: //选择二级分类
                closeInputMethod();
                pvCategoryOptions.show();
                break;
            case R.id.financial_income_or_spend_date: //选择日期
                closeInputMethod();
                pvTime.show();
                break;
            case R.id.base_title_textview:
                if(mModel == FINANCIAL_MODEL_INCOME){
                    mModel = FINANCIAL_MODEL_SPEND;
                    ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_spend));
                }else{
                    mModel = FINANCIAL_MODEL_INCOME;
                    ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_income));
                }
                resetCategorys();
                break;
            case R.id.financial_income_or_spend_save: //保存操作
                doSave();
                break;
            case R.id.financial_income_or_spend_draft: //草稿/删除操作
                doDraft();
                break;
        }
    }


    /**
     * 隐藏输入法
     */
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(mMoney.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }

    }

    private Dialog mDialog;
    /**
     * 显示弹出自定义view
     */
    public void showSelectItemMenuDialog(){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissSelectItemMenuDialog();

        mDialog = new Dialog(IncomeOrSpendActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(IncomeOrSpendActivity.this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        menus.add(getStringResource(R.string.select_gallery));
        menus.add(getStringResource(R.string.img_link));
        menus.add(getStringResource(R.string.image_detail));
        SimpleListAdapter adapter = new SimpleListAdapter(IncomeOrSpendActivity.this, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.simple_listview_item);
                //选择图库
                if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.select_gallery))){
                    //调用系统图库
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra("crop", true);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, FlagUtil.GET_SYSTEM_IMAGE_CODE);
                    //选择链接
                }else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.img_link))){

                    final EditText inputServer = new EditText(IncomeOrSpendActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(IncomeOrSpendActivity.this);
                    builder.setTitle("请输入网络图片(大小最好不要超过500k)").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String text = inputServer.getText().toString();
                            if (StringUtil.isNotNull(text)) {
                                mPath = text;
                                ImageCacheManager.loadImage(mPath, mImg, 150, 150);
                                badge.show();
                            }else{
                                ToastUtil.failure(IncomeOrSpendActivity.this, "请输入网络图片链接!");
                            }
                        }
                    });
                    builder.show();
                } else if(textView.getText().toString().equalsIgnoreCase(getStringResource(R.string.image_detail))){
                    if(StringUtil.isNotNull(mPath) && mPath.startsWith("http")){
                        CommonHandler.startImageDetailActivity(IncomeOrSpendActivity.this, mPath);
                    }else
                        ToastUtil.failure(IncomeOrSpendActivity.this, "请先上传图片");
                }
                dismissSelectItemMenuDialog();
            }
        });
        mDialog.setTitle("选择");
        mDialog.setCancelable(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissSelectItemMenuDialog();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(800,(menus.size() +1) * 90 +20);
        mDialog.setContentView(view);
        mDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissSelectItemMenuDialog(){
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            if(type == TaskType.ADD_FINANCIAL){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(IncomeOrSpendActivity.this, jsonObject);
                    //更新本地数据为已经同步状态
                    JSONObject object = jsonObject.getJSONObject("message");
                    List<FinancialBean> datas = financialDataBase.query(" where local_id =" + object.getInt("local_id"));
                    if(datas != null && datas.size() == 1){
                        FinancialBean financialBean = datas.get(0);
                        financialBean.setSynchronous(true);
                        financialBean.setId(object.getInt("id"));
                        financialDataBase.update(financialBean);
                    }
                    finish();
                }else{
                    ToastUtil.failure(IncomeOrSpendActivity.this, jsonObject);
                }
            }else if(type == TaskType.QINIU_TOKEN){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.optBoolean("isSuccess")){
                    ToastUtil.success(IncomeOrSpendActivity.this, jsonObject);
                    token = jsonObject.getString("message");
                    uploadImg();
                }else{
                    ToastUtil.failure(IncomeOrSpendActivity.this, jsonObject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadImg(){
        File data = new File(mLocalPath);
        String filename = BaseApplication.getLoginUserName() + "_app_upload_" + UUID.randomUUID().toString() +StringUtil.getFileName(mLocalPath);
        mProgressBar.setVisibility(View.VISIBLE);
        QiniuUploadManager.getInstance().getUploadManager().put(data, filename, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置。
                        //Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                        //ToastUtil.success(IncomeOrSpendActivity.this, "qiniu progress--->" + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        int i = (int) (percent * 100);
                        mProgressBar.setProgress(i);
                        if (i == 100) {
                            mProgressBar.setVisibility(View.GONE);
                            mPath = ConstantsUtil.QINIU_CLOUD_SERVER + key;
                        }
                        // ToastUtil.success(IncomeOrSpendActivity.this, "qiniu progress--->" + percent);
                        Log.i("qiniu progress", "i=" + i + "---->" + key + ": " + percent);
                    }
                }, null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode" + requestCode);
            if (requestCode == FlagUtil.GET_SYSTEM_IMAGE_CODE) {//图库返回
                mLocalPath = MediaUtil.getImageAbsolutePath(IncomeOrSpendActivity.this, data.getData());
                Bitmap bitmap = null;
                if(StringUtil.isNotNull(mLocalPath)){
                    bitmap = BitmapUtil.getSmallBitmap(IncomeOrSpendActivity.this, mLocalPath, 600, 800);
                    mImg.setImageBitmap(bitmap);
                    //将图片进行压缩
                    mLocalPath = FileUtil.getTempDir(getApplicationContext()) + File.separator + UUID.randomUUID().toString() + "_"+StringUtil.getFileName(mLocalPath);
                    BitmapUtil.bitmapToLocalPath(bitmap, mLocalPath);
                    CommonHandler.getQiniuTokenRequest(IncomeOrSpendActivity.this);
                    badge.show();
                }else
                    ToastUtil.failure(IncomeOrSpendActivity.this, "获取不到图片路径");
            }
        }
    }


}
