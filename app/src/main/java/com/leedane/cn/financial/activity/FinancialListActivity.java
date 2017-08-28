package com.leedane.cn.financial.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.leedane.cn.activity.BaseActivity;
import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialBean;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.database.FinancialDataBase;
import com.leedane.cn.financial.fragment.SearchChartDataFragment;
import com.leedane.cn.financial.fragment.SearchListFragment;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.EnumUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.List;

/**
 * 记账列表ctivity(提供给柱状图点击的后查看详细列表)
 * Created by LeeDane on 2017/8/7.
 */
public class FinancialListActivity extends BaseActivity{
    private static final String TAG = "FinancialListActivity";
    private FinancialDataBase financialDataBase;
    private ToggleButton chartOrListButton;

    private FinancialList financialList = new FinancialList();
    private String startTime , endTime;
    private int model = IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_list);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.financial_list);
        backLayoutVisible();
        initView();
    }

    /**
     * 搜索handler
     */
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.FINANCIAL_FINANCIAL_LIST:
                    //执行查询操作
                    List<FinancialBean> financialBeans = financialDataBase.query(" where status= "+ ConstantsUtil.STATUS_NORMAL +" and datetime(addition_time) between datetime('" + startTime+ "') and datetime('"+ endTime +"') and model="+ model +" order by datetime(addition_time) desc");
                    financialList.setFinancialBeans(financialBeans);
                    FragmentManager fragmentManager = FinancialListActivity.this.getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.search_container);
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("financialSearchList", financialList);
                    FinancialListActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.search_container, SearchListFragment.newInstance(bundle)).commit();
                    break;
            }

        }
    };
    /**
     * 初始化控件
     */
    private void initView() {
        financialDataBase = new FinancialDataBase(FinancialListActivity.this);
        startTime = getIntent().getExtras().getString("startTime");
        endTime = getIntent().getExtras().getString("endTime");
        model = getIntent().getExtras().getInt("model", IncomeOrSpendActivity.FINANCIAL_MODEL_SPEND);
        //ToastUtil.success(getBaseContext(), "model="+model, Toast.LENGTH_LONG);
        Message message = new Message();
        message.what = FlagUtil.FINANCIAL_FINANCIAL_LIST;
        //55毫秒秒后进行
        handler.sendMessageDelayed(message, 55);

        chartOrListButton = (ToggleButton)findViewById(R.id.chart_or_list_toggle_button);
        chartOrListButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chartOrListButton.setSelected(isChecked);
                if (financialList != null) {
                    FragmentManager fragmentManager = FinancialListActivity.this.getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.search_container);
                    if (fragment != null) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("financialSearchList", financialList);
                    if (isChecked) {
                        FinancialListActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.search_container, SearchListFragment.newInstance(bundle)).commit();
                    } else {
                        FinancialListActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.search_container, SearchChartDataFragment.newInstance(bundle)).commit();
                    }
                }
            }
        });
        chartOrListButton.setSelected(true);
    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        super.taskFinished(type, result);

    }

    @Override
    protected void onDestroy() {
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
        if(financialDataBase != null)
            financialDataBase.destroy();
        super.onDestroy();
    }
}
