package com.leedane.cn.financial.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.AppUtil;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.DensityUtil;
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

    public static final int GET_SYSTEM_IMAGE_CODE = 112;

    //model是添加收入
    public static final int FINANCIAL_MODEL_INCOME = 1;
    //model是添加支出
    public static final int FINANCIAL_MODEL_SPEND = 2;

    //金钱
    private EditText mMoney;

    //选择日期
    private EditText mDate;

    //选择时间
    private EditText mTime;

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

    // 模块，1:收入；2：支出
    private int mModel;

    public String token;

    private Calendar showTime;

    private TextView mSave;
    private TextView mDraft;

    private FinancialDataBase financialDataBase;
    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;
    private FinancialBean editFinancialBean;

    //是否编辑状态
    private boolean isEdit;

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
        initData();
        backLayoutVisible();
        ((TextView)findViewById(R.id.base_title_textview)).setOnClickListener(IncomeOrSpendActivity.this);

        financialDataBase = new FinancialDataBase(IncomeOrSpendActivity.this);
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(IncomeOrSpendActivity.this);
        twoLevelCategoryDataBase = new TwoLevelCategoryDataBase(IncomeOrSpendActivity.this);

        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //判断是否是编辑状态
        if(getIntent().getSerializableExtra("financialBean") != null){
            editFinancialBean = (FinancialBean)getIntent().getSerializableExtra("financialBean");
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

        mProgressBar = (ProgressBar)findViewById(R.id.financial_income_or_spend_img_progressbar);

        mMoney = (EditText)findViewById(R.id.financial_income_or_spend_money);


        mDate = (EditText)findViewById(R.id.financial_income_or_spend_date);
        mTime = (EditText)findViewById(R.id.financial_income_or_spend_time);

        mDate.setOnClickListener(IncomeOrSpendActivity.this);
        mTime.setOnClickListener(IncomeOrSpendActivity.this);

        mOneLevel = (TextView)findViewById(R.id.financial_income_or_spend_one_level);
        mTwoLevel = (TextView)findViewById(R.id.financial_income_or_spend_two_level);
        mOneLevel.setOnClickListener(IncomeOrSpendActivity.this);
        mTwoLevel.setOnClickListener(IncomeOrSpendActivity.this);

        mSave = (TextView)findViewById(R.id.financial_income_or_spend_save);
        mDraft = (TextView)findViewById(R.id.financial_income_or_spend_draft);
        mSave.setOnClickListener(IncomeOrSpendActivity.this);
        mDraft.setOnClickListener(IncomeOrSpendActivity.this);

        mRemark = (EditText)findViewById(R.id.financial_income_or_spend_remark);

        if(isEdit()){
            mDraft.setText(R.string.delete);
            showTime = Calendar.getInstance();
            String addTime = editFinancialBean.getAdditionTime();
            showTime.setTime(DateUtil.stringToDate(addTime));
            mDate.setText(addTime.substring(0, 10));
            mTime.setText(addTime.substring(11, addTime.length()));
            mOneLevel.setText(editFinancialBean.getOneLevel());
            mTwoLevel.setText(editFinancialBean.getTwoLevel());
            mMoney.setText(String.valueOf(editFinancialBean.getMoney()));
            mRemark.setText(StringUtil.changeNotNull(editFinancialBean.getFinancialDesc()));

            if(StringUtil.isNotNull(editFinancialBean.getPath())){
                mPath = editFinancialBean.getPath();
                ImageCacheManager.loadImage(mPath, mImg, 150, 150);
            }

        }else{
            //以系统时间初始化date和time的展示
            Date date = new Date();
            showTime = Calendar.getInstance();
            showTime.setTime(date);
            mDate.setText(DateUtil.DateToString(date, "yyyy-MM-dd"));
            mTime.setText(DateUtil.DateToString(date, "HH:mm:ss"));

            List<OneLevelCategory> oneLevelGategories = oneLevelCategoryDataBase.query(" where is_default=1");
            if(oneLevelGategories != null && oneLevelGategories.size() ==1){
                mOneLevel.setText(oneLevelGategories.get(0).getValue());
            }

            List<TwoLevelCategory> twoLevelCategories = twoLevelCategoryDataBase.query(" where is_default=1");
            if(twoLevelCategories != null && twoLevelCategories.size() ==1) {
                mTwoLevel.setText(twoLevelCategories.get(0).getValue());
            }
        }
    }

    /**
     * 保存操作
     */
    public void doSave(){
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
        String time = mTime.getText().toString();
        if(StringUtil.isNull(time)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择日期");
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
        editFinancialBean.setAdditionTime(date + " " + time);
        try{
            if(isEdit()){
                financialDataBase.update(editFinancialBean);
            }else{
                financialDataBase.save(editFinancialBean);
            }
            Map<String, Object> data = new HashMap<>();
            BeanUtil.convertBeanToMap(editFinancialBean, data);
            FinancialHandler.save(IncomeOrSpendActivity.this, data);
            ToastUtil.success(IncomeOrSpendActivity.this, "数据已成功添加到本地!");
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.failure(IncomeOrSpendActivity.this, "保存失败!" + e.toString());
        }
        //后台重新计算记账数据
        FinancialHandler.calculateFinancialData(IncomeOrSpendActivity.this);
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
                            financialDataBase.delete(editFinancialBean.getLocalId());
                            ToastUtil.success(IncomeOrSpendActivity.this, "记录删除成功!");
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
        String time = mTime.getText().toString();
        if(StringUtil.isNull(time)){
            ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择日期");
            return;
        }

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
        editFinancialBean.setAdditionTime(date + " " + time);
        try{
            financialDataBase.save(editFinancialBean);

           /* Map<String, Object> data = new HashMap<>();
            BeanUtil.convertBeanToMap(financialBean, data);
            FinancialHandler.save(IncomeOrSpendActivity.this, data);*/
            ToastUtil.success(IncomeOrSpendActivity.this, "草稿数据已成功添加到本地!" );
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
        financialDataBase.destroy();
        oneLevelCategoryDataBase.destroy();
        twoLevelCategoryDataBase.destroy();
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
                showLevelListItemMenuDialog(1, 0);
                break;
            case R.id.financial_income_or_spend_two_level: //选择二级分类
                String oneLevel = mOneLevel.getText().toString();
                if(StringUtil.isNull(oneLevel)){
                    ToastUtil.failure(IncomeOrSpendActivity.this, "请先选择一级分类");
                    return;
                }
                showLevelListItemMenuDialog(2, TwoLevelCategoryDataBase.getIndexByOneLevelCategory(oneLevel));
                break;
            case R.id.financial_income_or_spend_date: //选择日期
                showDatePickerDialog();
                break;
            case R.id.financial_income_or_spend_time: //选择时间
                showTimePickerDialog();
                break;
            case R.id.base_title_textview:
                //编辑状态不让选择类型
                /*if(isEdit()){
                    ToastUtil.failure(IncomeOrSpendActivity.this, "编辑状态不能选择类型");
                    return;
                }*/
                //showPopwindow();
                if(mModel == FINANCIAL_MODEL_INCOME){
                    mModel = FINANCIAL_MODEL_SPEND;
                    ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_spend));
                }else{
                    mModel = FINANCIAL_MODEL_INCOME;
                    ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_income));
                }
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
     * 显示popupWindow
     */
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.financial_income_or_spend_popwin, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);


        // 实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
       // window.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(IncomeOrSpendActivity.this.findViewById(R.id.financial_income_or_spend_root),
                Gravity.TOP, 70, DensityUtil.dip2px(IncomeOrSpendActivity.this, 60));

        // 这里检验popWindow里的button是否可以点击
        Button first = (Button) view.findViewById(R.id.pop_btn_add_income);
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel = FINANCIAL_MODEL_INCOME;
                ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_income));
            }
        });

        Button second = (Button) view.findViewById(R.id.pop_btn_add_spend);
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel = FINANCIAL_MODEL_SPEND;
                ((TextView)findViewById(R.id.base_title_textview)).setText(getStringResource(R.string.financila_add_spend));
            }
        });

        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
            }
        });

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
                    startActivityForResult(intent, GET_SYSTEM_IMAGE_CODE);
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

    private Dialog mLevelDialog;

    /**
     * 显示弹出自定义view
     * @param type type == 1,表示一级分类， type == 2 表示二级分类
     * @param oneLevelId 当type == 2时， 一级菜单的ID
     */
    public void showLevelListItemMenuDialog(final int type, int oneLevelId){
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissLevelListItemMenuDialog();

        mLevelDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(this).inflate(R.layout.mood_list_menu, null);

        ListView listView = (ListView)view.findViewById(R.id.mood_list_menu_listview);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<String> menus = new ArrayList<>();

        if(type == 1){
            List<OneLevelCategory> oneLevelGategories = BaseApplication.oneLevelCategories;
            if(oneLevelGategories.size() > 0 ){
                for(OneLevelCategory oneLevelCategory : oneLevelGategories){
                    if(oneLevelCategory.getModel() == mModel)
                        menus.add(oneLevelCategory.getValue() + "   总预算("+ oneLevelCategory.getBudget() + ")" +(oneLevelCategory.isDefault() ? "   <font color='red'>默认</font>": ""));
                }
            }
        }else if(type == 2 && oneLevelId > 0 ){
            List<TwoLevelCategory> twoLevelCategories = BaseApplication.twoLevelCategories;
            if(!CommonUtil.isEmpty(twoLevelCategories)){
                for(TwoLevelCategory twoLevelCategory: twoLevelCategories){
                    if(twoLevelCategory.getOneLevelId() == oneLevelId){
                        menus.add(twoLevelCategory.getValue() + "   预算("+ twoLevelCategory.getBudget() + ") " +(twoLevelCategory.isDefault() ? "    <font color='red'>默认</font>": ""));
                        continue;
                    }
                }
            }
        }

        SimpleListAdapter adapter = new SimpleListAdapter(this.getApplicationContext(), menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView)view.findViewById(R.id.simple_listview_item)).getText().toString();
                if(StringUtil.isNotNull(text)){
                    int i = text.indexOf(" ");
                    if(i > 0 ){
                        text  = text.substring(0, i).trim();
                    }
                    if(type == 1){
                        mOneLevel.setText(text);
                        mTwoLevel.setText("");
                        showLevelListItemMenuDialog(2, TwoLevelCategoryDataBase.getIndexByOneLevelCategory(text));
                    }else{
                        mTwoLevel.setText(text);
                        dismissLevelListItemMenuDialog();
                    }
                }
            }
        });
        mLevelDialog.setTitle("操作");
        mLevelDialog.setCancelable(true);
        mLevelDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissLevelListItemMenuDialog();
            }
        });
        mLevelDialog.setContentView(view);
        mLevelDialog.show();
    }


    public void showDatePickerDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                String m = (month + 1) > 9 ? (month + 1) +"" : "0" +(month + 1);
                String d = dayOfMonth > 9 ? dayOfMonth +"": "0"+dayOfMonth;
                mDate.setText(year + "-" + m + "-" + d + " ");
                String date = mDate.getText().toString() + mTime.getText().toString();
                showTime.setTime(DateUtil.stringToDate(date));
                showTimePickerDialog();
            }
        }, showTime.get(Calendar.YEAR), // 传入年份
                showTime.get(Calendar.MONTH), // 传入月份
                showTime.get(Calendar.DAY_OF_MONTH) // 传入天数
        );
        dialog.show();
    }

    public void showTimePickerDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String h = hourOfDay > 9 ? hourOfDay +"" : "0" +hourOfDay;
                String m = minute > 9 ? minute +"" : "0" + minute;
                mTime.setText(h +":" +m +":"+"00");
                String date = mDate.getText().toString() + " " + mTime.getText().toString();
                showTime.setTime(DateUtil.stringToDate(date));
            }
        }, showTime.get(Calendar.HOUR_OF_DAY), // 传入小时
                showTime.get(Calendar.MINUTE), // 传入分钟
                true
        );
        dialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissLevelListItemMenuDialog(){
        if(mLevelDialog != null && mLevelDialog.isShowing())
            mLevelDialog.dismiss();
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);
        try{
            if(type == TaskType.ADD_FINANCIAL){
                dismissLoadingDialog();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
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
                if(jsonObject != null && jsonObject.has("isSuccess") && jsonObject.getBoolean("isSuccess") == true){
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
                            mPath = ConstantsUtil.QINIU_CLOUD_SERVER +  key;
                        }
                       // ToastUtil.success(IncomeOrSpendActivity.this, "qiniu progress--->" + percent);
                        Log.i("qiniu progress", "i="+i + "---->" +key + ": " + percent);
                    }
                }, null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            System.out.println("requestCode"+requestCode);
            if (requestCode == GET_SYSTEM_IMAGE_CODE) {//图库返回
                mLocalPath = MediaUtil.getImageAbsolutePath(IncomeOrSpendActivity.this, data.getData());
                Bitmap bitmap = null;
                if(StringUtil.isNotNull(mLocalPath)){
                    bitmap = BitmapUtil.getSmallBitmap(IncomeOrSpendActivity.this, mLocalPath, 150, 150);
                    mImg.setImageBitmap(bitmap);
                    CommonHandler.getQiniuTokenRequest(IncomeOrSpendActivity.this);
                    badge.show();
                }else
                    ToastUtil.failure(IncomeOrSpendActivity.this, "获取不到图片路径");
            }
        }
    }
}
