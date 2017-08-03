package com.leedane.cn.financial.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.bean.FinancialList;
import com.leedane.cn.financial.util.LogUtil;

/**
 * 记账基本的fragment
 * Created by LeeDane on 2016/7/19.
 */
@SuppressLint("ValidFragment")
public abstract class BaseFragment extends FinancialBaseFragment {

    private boolean isVisible = false;//当前Fragment是否可见
    private boolean isInitView = false;//是否与View建立起映射关系
    private boolean isFirstLoad = true;//是否是第一次加载数据

    private SparseArray<View> mViews;

    protected View mRootView;
    protected ToggleButton chartOrListButton;
    protected FinancialList financialList = new FinancialList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.m("   " + this.getClass().getSimpleName());
        mRootView = inflater.inflate(getContainerId(), container, false);
        mViews = new SparseArray<>();
        initView();
        isInitView = true;
        lazyLoadData();
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.m("   " + this.getClass().getSimpleName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.m("context" + "   " + this.getClass().getSimpleName());

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        LogUtil.m("isVisibleToUser " + isVisibleToUser + "   " + this.getClass().getSimpleName());
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();

        } else {
            isVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void lazyLoadData() {
        if (isFirstLoad) {
            LogUtil.m("第一次加载 " + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
        } else {
            LogUtil.m("不是第一次加载" + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
        }
        if (!isFirstLoad || !isVisible || !isInitView) {
            LogUtil.m("不加载" + "   " + this.getClass().getSimpleName());
            return;
        }

        LogUtil.m("完成数据第一次加载");
        initData();
        isFirstLoad = false;
    }

    /**
     * 让布局中的view与fragment中的变量建立起映射
     */
    private void initView(){
        chartOrListButton = (ToggleButton)mRootView.findViewById(R.id.chart_or_list_toggle_button);
        chartOrListButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chartOrListButton.setSelected(isChecked);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(getFragmentContainerId());
                if(fragment != null){
                    fragmentManager .beginTransaction().remove(fragment).commit();
                }
                Bundle bundle = new Bundle();
                bundle.putInt("model", getModel());
                if(isChecked){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getListDataFragment(bundle)).commit();
                }else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(getFragmentContainerId(), getChartDataFragment(bundle)).commit();
                }
            }
        });
        chartOrListButton.setSelected(true);
    }

    /**
     * 加载要显示的数据
     */
    protected abstract void initData();

    /**
     * fragment中可以通过这个方法直接找到需要的view，而不需要进行类型强转
     * @param viewId
     * @param <E>
     * @return
     */
    protected <E extends View> E findView(int viewId) {
        if (mRootView != null) {
            E view = (E) mViews.get(viewId);
            if (view == null) {
                view = (E) mRootView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return view;
        }
        return null;
    }
    /**
     * 大容器ID
     * @return
     */
    protected abstract int getContainerId();

    /**
     * fragment容器ID
     * @return
     */
    protected abstract int getFragmentContainerId();

    protected abstract String getFinancialListKey();

    /**
     * 列表展示的fragment
     * @param bundle
     * @return
     */
    protected abstract Fragment getListDataFragment(Bundle bundle);

    /**
     * 图标展示的fragment
     * @param bundle
     * @return
     */
    protected abstract Fragment getChartDataFragment(Bundle bundle);

    /**
     * 获取当前的模型类型（如：昨日、本周、本月、本年）
     * @return
     */
    protected abstract int getModel();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();

        //Bundle bundle = new Bundle();
        //bundle.putSerializable(getFinancialListKey(), financialList);
        //getActivity().getSupportFragmentManager().beginTransaction().add(getFragmentContainerId(), getListDataFragment(bundle)).commit();


    }

    @Override
    public void calculate(int model) {
        super.calculate(model);
        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(getFragmentContainerId());
        if(fragment != null){
            fragmentManager.beginTransaction().remove(fragment).commit();
        }*/
    }
}
