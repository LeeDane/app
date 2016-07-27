package com.leedane.cn.financial.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.activity.LoginActivity;
import com.leedane.cn.adapter.SimpleListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.OneLevelGategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.database.OneLevelCategoryDataBase;
import com.leedane.cn.financial.database.TwoLevelCategoryDataBase;
import com.leedane.cn.financial.handler.FinancialHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BitmapUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.MediaUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.util.http.BeanUtil;
import com.leedane.cn.volley.ImageCacheManager;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

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

    //金钱
    private EditText mMoney;

    //选择日期
    private EditText mDate;

    //选择时间
    private EditText mTime;

    //附加图片
    private ImageView mImg;

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

    private FinancialDataBase financialDataBase;
    private OneLevelCategoryDataBase oneLevelCategoryDataBase;
    private TwoLevelCategoryDataBase twoLevelCategoryDataBase;

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
        setTitleViewText(getStringResource(R.string.financial));
        backLayoutVisible();

        financialDataBase = new FinancialDataBase(IncomeOrSpendActivity.this);
        oneLevelCategoryDataBase = new OneLevelCategoryDataBase(IncomeOrSpendActivity.this);
        twoLevelCategoryDataBase = new TwoLevelCategoryDataBase(IncomeOrSpendActivity.this);
        mModel = getIntent().getIntExtra("model", 1);
        initView();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {

        mImg = (ImageView)findViewById(R.id.financial_income_or_spend_img);
        mImg.setOnClickListener(IncomeOrSpendActivity.this);

        mProgressBar = (ProgressBar)findViewById(R.id.financial_income_or_spend_img_progressbar);

        mMoney = (EditText)findViewById(R.id.financial_income_or_spend_money);


        mDate = (EditText)findViewById(R.id.financial_income_or_spend_date);
        mTime = (EditText)findViewById(R.id.financial_income_or_spend_time);
        //以系统时间初始化date和time的展示
        Date date = new Date();
        showTime = Calendar.getInstance();
        showTime.setTime(date);
        mDate.setText(DateUtil.DateToString(date, "yyyy-MM-dd"));
        mTime.setText(DateUtil.DateToString(date, "HH:mm:ss"));
        mDate.setOnClickListener(IncomeOrSpendActivity.this);
        mTime.setOnClickListener(IncomeOrSpendActivity.this);

        mOneLevel = (TextView)findViewById(R.id.financial_income_or_spend_one_level);
        mTwoLevel = (TextView)findViewById(R.id.financial_income_or_spend_two_level);
        mOneLevel.setOnClickListener(IncomeOrSpendActivity.this);
        mTwoLevel.setOnClickListener(IncomeOrSpendActivity.this);

        List<OneLevelGategory> oneLevelGategories = oneLevelCategoryDataBase.query(" where is_default=1");
        if(oneLevelGategories != null && oneLevelGategories.size() ==1){
            mOneLevel.setText(oneLevelGategories.get(0).getValue());
        }

        List<TwoLevelCategory> twoLevelCategories = twoLevelCategoryDataBase.query(" where is_default=1");
        if(twoLevelCategories != null && twoLevelCategories.size() ==1){
            mTwoLevel.setText(twoLevelCategories.get(0).getValue());
        }

        mRemark = (EditText)findViewById(R.id.financial_income_or_spend_remark);

    }

    /**
     * 保存操作
     * @param view
     */
    public void save(View view){
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

        FinancialBean financialBean = new FinancialBean();

        financialBean.setSynchronous(false);
        financialBean.setFinancialDesc(StringUtil.changeNotNull(mRemark.getText().toString()));
        financialBean.setCreateUserId(BaseApplication.getLoginUserId());
        financialBean.setCreateTime(DateUtil.DateToString(new Date()));
        if(StringUtil.isNotNull(mPath)){
            financialBean.setPath(mPath);
            financialBean.setHasImg(true);
        }
        financialBean.setModel(mModel);
        financialBean.setOneLevel(onLevel);
        financialBean.setTwoLevel(twoLevel);
        financialBean.setStatus(ConstantsUtil.STATUS_NORMAL);
        financialBean.setMoney(Float.valueOf(money));
        financialBean.setAdditionTime(date + " " + time);
        try{
            financialDataBase.save(financialBean);
            Map<String, Object> data = new HashMap<>();
            BeanUtil.convertBeanToMap(financialBean, data);
            FinancialHandler.save(IncomeOrSpendActivity.this, data);
            ToastUtil.success(IncomeOrSpendActivity.this, "数据已成功添加到本地!");
        }catch (Exception e){
            e.printStackTrace();
            ToastUtil.failure(IncomeOrSpendActivity.this, "保存失败!" + e.toString());
        }
    }

    /**
     * 存为草稿操作
     * @param view
     */
    public void draft(View view){
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

        FinancialBean financialBean = new FinancialBean();

        financialBean.setSynchronous(false);
        financialBean.setFinancialDesc(StringUtil.changeNotNull(mRemark.getText().toString()));
        financialBean.setCreateUserId(BaseApplication.getLoginUserId());
        financialBean.setCreateTime(DateUtil.DateToString(new Date()));
        if(StringUtil.isNotNull(mPath)){
            financialBean.setPath(mPath);
            financialBean.setHasImg(true);
        }
        financialBean.setModel(mModel);
        financialBean.setOneLevel(onLevel);
        financialBean.setTwoLevel(twoLevel);
        financialBean.setStatus(ConstantsUtil.STATUS_DRAFT);
        financialBean.setMoney(Float.valueOf(money));
        financialBean.setAdditionTime(date + " " + time);
        try{
            financialDataBase.save(financialBean);
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
            List<OneLevelGategory> oneLevelGategories = BaseApplication.oneLevelGategories;
            if(oneLevelGategories.size() > 0 ){
                for(OneLevelGategory oneLevelGategory: oneLevelGategories){
                    menus.add(oneLevelGategory.getValue() + "   总预算("+ oneLevelGategory.getBudget() + ")");
                }
            }
        }else if(type == 2 && oneLevelId > 0 ){
            List<TwoLevelCategory> twoLevelCategories = BaseApplication.twoLevelCategories;
            if(twoLevelCategories.size() > 0 ){
                for(TwoLevelCategory twoLevelCategory: twoLevelCategories){
                    if(twoLevelCategory.getOneLevelId() == oneLevelId){
                        menus.add(twoLevelCategory.getValue() + "   预算("+ twoLevelCategory.getBudget() + ")");
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
                mDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth + " ");
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
                mTime.setText(hourOfDay +":" +minute +":"+"00");
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
        Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .recorder(null)  // recorder分片上传时，已上传片记录器。默认null
                //.recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        UploadManager uploadManager = new UploadManager(config);

        File data = new File(mLocalPath);
        String filename = BaseApplication.getLoginUserName() + "_app_upload_" + UUID.randomUUID().toString() +StringUtil.getFileName(mLocalPath);
        mProgressBar.setVisibility(View.VISIBLE);
        uploadManager.put(data, filename, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置。
                        //Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, new UploadOptions(null, null, false, new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        int i = (int) (percent * 100);
                        mProgressBar.setProgress(i);
                        if(i == 100){
                            mProgressBar.setVisibility(View.GONE);
                            mPath = key;
                            ToastUtil.success(IncomeOrSpendActivity.this, "mPath--->"+mPath);
                        }
                        //Log.i("qiniu progress", "i="+i + "---->" +key + ": " + percent);
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
                }else
                    ToastUtil.failure(IncomeOrSpendActivity.this, "获取不到图片路径");
            }
        }
    }
}
