package com.leedane.cn.financial.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.bean.OneLevelCategory;
import com.leedane.cn.financial.bean.TwoLevelCategory;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.fragment.SearchChartDataFragment;
import com.leedane.cn.financial.fragment.SearchListFragment;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.fragment.search.SearchHistoryFragment;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.util.WidgetUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 搜索ctivity
 * Created by LeeDane on 2016/12/5.
 */
public class SearchActivity extends BaseActivity implements SearchHistoryFragment.SearchHistorItemClickListener,
            WidgetUtil.DateDailogCallBack{
    private static final String TAG = "SearchActivity";
    private EditText mSearchKey;
    private Button mSearchGo;
    private Button mSearchCondition;
    private String selectType = EnumUtil.SearchType.记账.name();
    private FinancialDataBase financialDataBase;
    private ToggleButton chartOrListButton;

    private FinancialList financialList;

    private Dialog mConditionDialog;
    private SearchCondition searchCondition = new SearchCondition();

    private WidgetUtil widgetUtil;
    /**
     * 搜索handler
     */
    private Handler searchAction = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.FINANCIAL_SEARCH_ACTION:
                    Bundle bundle = msg.getData();
                    SearchListFragment searchFragment = SearchListFragment.newInstance(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchFragment).commit();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchKey.getWindowToken(), 0);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_search);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.search);
        backLayoutVisible();

        ImageView searchByDateImageView =  ((ImageView)findViewById(R.id.view_right_img));
        searchByDateImageView.setVisibility(View.VISIBLE);
        searchByDateImageView.setImageDrawable(getDrawable(R.drawable.ic_date_range_white_18dp));
        searchByDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, SearchDateRangeActivity.class);
                startActivityForResult(intent, FlagUtil.CALENDAR_RANGE_CODE);
            }
        });
        initData();
        initView();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        Bundle bundle = new Bundle();
        bundle.putString("searchWhereSql", " where search_type = '" + selectType + "' order by datetime(create_time) desc");
        String[] searchTypes = new String[]{selectType};
        bundle.putStringArray("searchTypes", searchTypes);
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance(bundle);
        searchHistoryFragment.setSearchHistorItemClickListener(this);//注册监听器
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, searchHistoryFragment).commit();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        financialDataBase = new FinancialDataBase(SearchActivity.this);
        mSearchKey = (EditText)findViewById(R.id.search_key);
        mSearchGo = (Button)findViewById(R.id.do_search);
        mSearchGo.setOnClickListener(this);
        mSearchKey.addTextChangedListener(new TextWatcher() {

            private String beforeText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //为空，显示搜索列表
                if (s == null || s.length() == 0) {
                    financialList = null;
                    initData();
                } else {
                    //对删除的操作不做搜索处理
                    if (beforeText.length() < s.length())
                        goSearch();
                }
            }
        });
        mSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    goSearch();
                }
                return false;
            }
        });

        chartOrListButton = (ToggleButton)findViewById(R.id.chart_or_list_toggle_button);
        chartOrListButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chartOrListButton.setSelected(isChecked);
                if (financialList != null) {
                    FragmentManager fragmentManager = SearchActivity.this.getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.search_container);
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("financialSearchList", financialList);
                    if (isChecked) {
                        SearchActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.search_container, SearchListFragment.newInstance(bundle)).commit();
                    } else {
                        SearchActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.search_container, SearchChartDataFragment.newInstance(bundle)).commit();
                    }
                }
            }
        });
        chartOrListButton.setSelected(true);

        mSearchCondition = (Button)findViewById(R.id.show_condition);
        mSearchCondition.setOnClickListener(this);
    }

    /**
     * 执行搜索操作
     */
    public void goSearch(){
        String key = mSearchKey.getText().toString();
        if(StringUtil.isNotNull(key)){
            /*ToastUtil.failure(SearchActivity.this, "请先输入搜索内容");
            mSearchKey.setSelected(true);
            return;*/
            SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
            SearchHistoryBean searchHistoryBean = new SearchHistoryBean();
            searchHistoryBean.setCreateTime(DateUtil.DateToString(new Date()));
            searchHistoryBean.setSearchType(selectType);
            searchHistoryBean.setSearchKey(key);
            searchHistoryDataBase.insert(searchHistoryBean);
            searchHistoryDataBase.destroy();
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("financialSearchList", getFinancialList(key));
        Message message = new Message();
        message.setData(bundle);
        message.what = FlagUtil.FINANCIAL_SEARCH_ACTION;
        //55毫秒秒后进行
        searchAction.sendMessageDelayed(message, 55);

    }
    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        if(searchAction != null)
            searchAction.removeCallbacksAndMessages(null);
        if(financialDataBase != null)
            financialDataBase.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.do_search:
                goSearch();
                break;
            case R.id.show_condition:
                showSearchConditionDialog();
                break;
        }
    }

    private List<String> levelList = new ArrayList<>();
    private String searchLevelText;
    private View dialogRootView;
    /**
     * 展示检索条件的弹出框
     */
    private void showSearchConditionDialog() {
        //判断是否已经存在菜单，存在则把以前的记录取消
        dismissSearchConditionDialog();

        if(CommonUtil.isEmpty(levelList))
            loadLevelList();

        if(mConditionDialog == null){

            widgetUtil  = new WidgetUtil();
            widgetUtil.setDateDailogCallBack(this);
            mConditionDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);

            //if(dialogRootView == null)
            dialogRootView = LayoutInflater.from(this).inflate(R.layout.dialog_search_condition, null);

            final EditText searchStartTime = (EditText)dialogRootView.findViewById(R.id.search_start_time);
            final EditText searchEndTime = (EditText)dialogRootView.findViewById(R.id.search_end_time);
            final Spinner searchLevel = (Spinner)dialogRootView.findViewById(R.id.search_level);
            final TextView searchBtnReset = (TextView)dialogRootView.findViewById(R.id.search_btn_reset);
            final TextView searchBtnSave = (TextView)dialogRootView.findViewById(R.id.search_btn_save);

            searchStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = searchStartTime.getText().toString();
                    int year = 0, month = 0, day = 0;
                    if(StringUtil.isNotNull(text)){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(DateUtil.stringToDate(text, "yyyy-MM-dd"));
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                    }
                    widgetUtil.showDateDialog(SearchActivity.this, year , month, day, searchStartTime);
                }
            });

            searchEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = searchEndTime.getText().toString();
                    int year = 0, month = 0, day = 0;
                    if(StringUtil.isNotNull(text)){
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(DateUtil.stringToDate(text, "yyyy-MM-dd"));
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                    }
                    widgetUtil.showDateDialog(SearchActivity.this, year, month, day, searchEndTime);
                }
            });

            ArrayAdapter levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelList);
            searchLevel.setAdapter(levelAdapter);
            searchLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    searchLevelText = levelList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    searchLevelText = "请选择";
                }
            });

            searchBtnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchStartTime.setText("");
                    searchEndTime.setText("");
                    searchLevel.setSelection(0);
                }
            });

            searchBtnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(StringUtil.isNotNull(searchStartTime.getText().toString()))
                        searchCondition.start = searchStartTime.getText().toString();
                    if(StringUtil.isNotNull(searchEndTime.getText().toString()))
                        searchCondition.end = searchEndTime.getText().toString();
                    if(StringUtil.isNotNull(searchLevelText) && !"请选择".equals(searchLevelText))
                        searchCondition.level = searchLevelText;
                    dismissSearchConditionDialog();
                }
            });

            mConditionDialog.setTitle("高级检索条件");
            mConditionDialog.setCancelable(true);
            mConditionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dismissSearchConditionDialog();
                }
            });
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(850, ViewGroup.LayoutParams.WRAP_CONTENT);
            mConditionDialog.setContentView(dialogRootView, params);
        }
        mConditionDialog.show();
    }

    /**
     * 隐藏弹出自定义view
     */
    public void dismissSearchConditionDialog(){
        if(mConditionDialog != null && mConditionDialog.isShowing())
            mConditionDialog.dismiss();
    }

    /**
     * 获取分类的列表
     * @return
     */
    private void loadLevelList(){
        levelList.add("请选择");
        if(!CommonUtil.isEmpty(BaseApplication.twoLevelCategories)){
            int oneLevelId = 0;
            for(TwoLevelCategory category: BaseApplication.twoLevelCategories){
                if(category.getStatus() == ConstantsUtil.STATUS_NORMAL){
                    oneLevelId = category.getOneLevelId();
                    if(oneLevelId > 0){
                        levelList.add(getOneLevelText(oneLevelId) + ">>>" +category.getValue());
                    }
                }
            }
        }
    }

    /**
     * 根据一级分类ID获取一级分类名称
     * @param oneLevelId
     * @return
     */
    private String getOneLevelText(int oneLevelId) {
        if(!CommonUtil.isEmpty(BaseApplication.oneLevelCategories)){
            for(OneLevelCategory category: BaseApplication.oneLevelCategories){
                if(category.getId() == oneLevelId){
                    return category.getValue();
                }
            }
        }
        return "";
    }

    @Override
    public void itemClick(SearchHistoryBean searchHistoryBean) {

        String searchKey = searchHistoryBean.getSearchKey();
        if(StringUtil.isNull(searchKey)){
            ToastUtil.success(SearchActivity.this, "搜索关键字为空");
            return;
        }
        SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(SearchActivity.this);
        searchHistoryDataBase.insert(searchHistoryBean);
        searchHistoryDataBase.destroy();

        //设置搜索框的内容值
        mSearchKey.setText(searchKey);
        mSearchKey.setSelection(searchKey.length());
        mSearchKey.requestFocus();
    }

    private FinancialList getFinancialList(String searchKey){
        financialList = new FinancialList();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where status=");
        buffer.append(ConstantsUtil.STATUS_NORMAL);

        if(StringUtil.isNotNull(searchCondition.start))
            buffer.append(" and datetime(addition_time) >= datetime('" + searchCondition.start +"')");

        if(StringUtil.isNotNull(searchCondition.end))
            buffer.append(" and datetime(addition_time) <= datetime('" + searchCondition.end +"')");

        if(StringUtil.isNotNull(searchCondition.level)){
            String[] levels = searchCondition.level.split(">>>");
            buffer.append(" and one_level = '" + levels[0] +"'");
            buffer.append(" and two_level = '" + levels[1] +"'");
        }

        if(StringUtil.isNotNull(searchKey)){
            buffer.append(" and (one_level like'%");
            buffer.append(searchKey);
            buffer.append("%' or  two_level like'%");
            buffer.append(searchKey);
            buffer.append("%' or location like'%");
            buffer.append(searchKey);
            buffer.append("%' or financial_desc like'%");
            buffer.append(searchKey);
            buffer.append("%')");
        }
        buffer.append(" order by datetime(addition_time) desc");

        financialList.setFinancialBeans(financialDataBase.query(buffer.toString()));
        return financialList;
    }

    @Override
    public void callback(TextView textView, DatePicker dp, int year, int month, int dayOfMonth) {
        String m = (month + 1) > 9 ? (month + 1) +"" : "0" +(month + 1);
        String d = dayOfMonth > 9 ? dayOfMonth +"": "0"+dayOfMonth;
        textView.setText(year + "-" + m + "-" + d + " ");
        if(textView != null && textView.getId() == R.id.search_start_time){
            TextView endTimeTextview = (TextView)dialogRootView.findViewById(R.id.search_end_time);
            String text = endTimeTextview.getText().toString();
            int endYear = 0, endMonth = 0, endDay = 0;
            if(StringUtil.isNotNull(text)){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtil.stringToDate(text, "yyyy-MM-dd"));
                endYear = calendar.get(Calendar.YEAR);
                endMonth = calendar.get(Calendar.MONTH);
                endDay = calendar.get(Calendar.DAY_OF_MONTH);
            }
            widgetUtil.showDateDialog(SearchActivity.this, endYear ,endMonth, endDay, endTimeTextview);
        }

    }

    class SearchCondition{
        String start;
        String end;
        String level;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FlagUtil.CALENDAR_RANGE_CODE:
                if(FlagUtil.SYSTEM_RESULT_CODE == resultCode && data != null){
                    int year = data.getIntExtra("year", 0);
                    int month = data.getIntExtra("month", 0);
                    int day = data.getIntExtra("day", 0);
                    Calendar theDay = Calendar.getInstance();
                    theDay.set(year, month, day);
                    Date tDay = theDay.getTime();
                    theDay.add(Calendar.DAY_OF_MONTH, 1);
                    Date nDay = theDay.getTime();
                    searchCondition.start = DateUtil.DateToString(tDay, "yyyy-MM-dd");
                    searchCondition.end = DateUtil.DateToString(nDay, "yyyy-MM-dd");
                    goSearch();
                }
                break;
        }
    }
}
