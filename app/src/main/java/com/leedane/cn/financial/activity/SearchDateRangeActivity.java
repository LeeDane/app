package com.leedane.cn.financial.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.MONDAY;

/**
 * 按照日历范围搜索ctivity
 * Created by LeeDane on 2018/1/17.
 */
public class SearchDateRangeActivity extends BaseActivity{
    private static final String TAG = "SearchDateRangeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_search_calendar);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.search_calendar_range);
        backLayoutVisible();

        MaterialCalendarView mcv = (MaterialCalendarView)findViewById(R.id.calendarView);
        mcv.setArrowColor(R.color.default_bg_color);

        //设置左边的指向按钮的图片
        //mcv.setLeftArrowMask(getBaseContext().getResources().getDrawable(R.drawable.app_bg));

        //设置右边的指向按钮的图片
        //mcv.setRightArrowMask(getBaseContext().getResources().getDrawable(R.drawable.app_bg));

        //mcv.showContextMenu()
        mcv.setSelectedDate(Calendar.getInstance());//当日选中
        mcv.state().edit()
                .setFirstDayOfWeek(MONDAY)
                .setMinimumDate(CalendarDay.from(2010, 4, 3))
                .setMaximumDate(Calendar.getInstance())
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Toast.makeText(SearchDateRangeActivity.this, date.getYear() +"年"+ date.getMonth() +"月"+ date.getDay() +"日", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SearchDateRangeActivity.this, SearchActivity.class);
                intent.putExtra("year", date.getYear());
                intent.putExtra("month", date.getMonth());
                intent.putExtra("day", date.getDay());
                setResult(FlagUtil.SYSTEM_RESULT_CODE, intent);
                finish();
                //startActivityForResult(intent, FlagUtil.CALENDAR_RANGE_CODE);
            }
        });
    }
}
