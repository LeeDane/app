package com.leedane.cn.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * 部件的工具类
 * Created by LeeDane on 2016/12/11.
 */
public class WidgetUtil {

    private DateDailogCallBack dateDailogCallBack;

    public void setDateDailogCallBack(DateDailogCallBack dateDailogCallBack) {
        this.dateDailogCallBack = dateDailogCallBack;
    }

    public interface DateDailogCallBack{
        void callback(TextView textView, DatePicker dp, int year,int month, int dayOfMonth);
    }


    private TimeDailogCallBack timeDailogCallBack;

    public void setTimeDailogCallBack(TimeDailogCallBack timeDailogCallBack) {
        this.timeDailogCallBack = timeDailogCallBack;
    }

    public interface TimeDailogCallBack{
        void callback(TextView textView, TimePicker view, int hourOfDay, int minute);
    }

    /**
     * 显示日期选择控件
     * @param context
     * @param defaultYear
     * @param defalutMonth
     * @param defaultDay
     */
    public void showDateDialog(Context context, int defaultYear, int defalutMonth, int defaultDay, final TextView textView){

        if(defaultYear == 0 && defalutMonth == 0 && defaultDay == 0){
            Calendar calendar = Calendar.getInstance();
            defaultYear = calendar.get(Calendar.YEAR);
            defalutMonth = calendar.get(Calendar.MONTH);
            defaultDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker dp, int year,int month, int dayOfMonth) {
                if(dateDailogCallBack != null)
                    dateDailogCallBack.callback(textView, dp, year, month, dayOfMonth);
            }
        }, defaultYear, // 传入年份
                defalutMonth, // 传入月份
                defaultDay // 传入天数
        );
        dialog.show();
    }

    /**
     * 选择时间选择控件
     * @param context
     * @param hour
     * @param minute
     */
    public void showTimeDialog(Context context, int hour, int minute, final TextView textView){

        if(hour == 0 && minute == 0){
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(timeDailogCallBack != null)
                    timeDailogCallBack.callback(textView, view, hourOfDay, minute);
            }
        }, hour, // 传入小时
            minute, // 传入分钟
            true
        );
        dialog.show();
    }
}
