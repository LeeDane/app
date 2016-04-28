package com.leedane.cn.frament;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.PersonalListViewAdapter;
import com.leedane.cn.leedaneAPP.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心的frament类
 * Created by LeeDane on 2015/11/19.
 */
public class PersonalFragment extends Fragment{


    public static final String TAG = "PersonalFragment";
    private Context mContext;
    private ListView mListView;
    private TextView mTextViewLoading;
    private int mIndex;
    private View mRootView;

    public PersonalFragment(){}

    /**
     * 构建Fragment对象
     * @param index 当前fragment是第几个
     * @param context
     */
    public PersonalFragment(int index, Context context){
        this.mContext = context;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_personal_list, container,
                    false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView)mRootView.findViewById(R.id.personal_fragment_listview);
        mTextViewLoading = (TextView)getView().findViewById(R.id.personal_fragment_loading);
        mListView.setVisibility(View.VISIBLE);
        mTextViewLoading.setVisibility(View.GONE);

        List<String> datas = new ArrayList<>();
        datas.add("测试数据1");
        datas.add("测试数据2");
        datas.add("测试数据3");
        datas.add("测试数据4");
        datas.add("测试数据5");
        datas.add("测试数据6");
        datas.add("测试数据7");
        datas.add("测试数据8");
        datas.add("测试数据9");
        datas.add("测试数据10");
        datas.add("测试数据11");
        datas.add("测试数据12");
        datas.add("测试数据13");
        datas.add("测试数据14");
        datas.add("测试数据15");
        datas.add("测试数据16");
        datas.add("测试数据17");
        datas.add("测试数据18");
        datas.add("测试数据19");
        datas.add("测试数据20");
        datas.add("测试数据21");
        datas.add("测试数据22");
        datas.add("测试数据23");
        datas.add("测试数据24");

        mListView.setAdapter(new PersonalListViewAdapter(mContext, datas));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "点击啦"+position, Toast.LENGTH_SHORT).show();
            }
        });
        //setListViewHeightBasedOnChildren();
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****//*
    public void setListViewHeightBasedOnChildren() {
        PersonalListViewAdapter listAdapter = (PersonalListViewAdapter)mListView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, mListView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        Log.i(TAG, "listview高度是：" + totalHeight + (mListView.getDividerHeight() * (listAdapter.getCount() - 1)));
        params.height = 200;
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }*/
}
