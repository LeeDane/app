package com.leedane.cn.fragment.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.search.SearchHistoryAdapter;
import com.leedane.cn.bean.search.SearchHistoryBean;
import com.leedane.cn.database.SearchHistoryDataBase;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索历史列表的fragment类
 * Created by LeeDane on 2016/5/22.
 */
public class SearchHistoryFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = "SearchHistoryFragment";
    private Context mContext;
    private ListView mListView;
    private SearchHistoryAdapter mAdapter;
    private List<SearchHistoryBean> mSearchHistoryBeans = new ArrayList<>();

    //搜索历史项的点击事件
    public interface SearchHistorItemClickListener{
        void itemClick(SearchHistoryBean searchHistoryBean);
    }

    private SearchHistorItemClickListener searchHistorItemClickListener;

    public void setSearchHistorItemClickListener(SearchHistorItemClickListener searchHistorItemClickListener) {
        this.searchHistorItemClickListener = searchHistorItemClickListener;
    }

    private View mRootView;

    protected TextView mListViewFooter;
    protected View viewFooter;
    private SearchHistoryDataBase searchHistoryDataBase;

    public SearchHistoryFragment(){
    }

    public static final SearchHistoryFragment newInstance(Bundle bundle){
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_no_swipe_refresh_listview, container,
                    false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mContext == null)
            mContext = getActivity();

        searchHistoryDataBase = new SearchHistoryDataBase(mContext);
        mSearchHistoryBeans = searchHistoryDataBase.query(" order by create_time desc");

        this.mListView = (ListView) mRootView.findViewById(R.id.no_swipe_refresh_listview);
        mAdapter = new SearchHistoryAdapter(mContext, mSearchHistoryBeans);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.success(mContext, "点击item位置："+position);
                searchHistorItemClickListener.itemClick(mSearchHistoryBeans.get(position));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.menu_feedback);
                builder.setTitle("提示");
                builder.setMessage("清除该搜索历史?");
                builder.setPositiveButton("清除",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                showLoadingDialog("Clear", "try best to clear...");
                                SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(mContext);
                                searchHistoryDataBase.delete(mSearchHistoryBeans.get(position).getId());
                                searchHistoryDataBase.destroy();
                                mSearchHistoryBeans.remove(position);
                                List<SearchHistoryBean> temp = new ArrayList<SearchHistoryBean>();
                                temp.addAll(mSearchHistoryBeans);
                                mAdapter.refreshData(temp);
                                dismissLoadingDialog();
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                builder.show();
                return true;
            }
        });

        //listview下方的显示
        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setOnClickListener(SearchHistoryFragment.this);//添加点击事件
        mListViewFooter.setText(getResources().getString(R.string.clear_history));
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.listview_footer_reLoad:
                //ToastUtil.success(mContext, "点击清空全部历史");
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setCancelable(true);
                builder.setIcon(R.drawable.menu_feedback);
                builder.setTitle("提示");
                builder.setMessage("清空全部搜索历史?");
                builder.setPositiveButton("清空",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                showLoadingDialog("Clear", "try best to clear all...");
                                SearchHistoryDataBase searchHistoryDataBase = new SearchHistoryDataBase(mContext);
                                searchHistoryDataBase.deleteAll();
                                searchHistoryDataBase.destroy();
                                mAdapter.refreshData(new ArrayList<SearchHistoryBean>());
                                dismissLoadingDialog();
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                builder.show();
                break;
        }
    }

    /**
     * 弹出加载ProgressDiaLog
     */
    private ProgressDialog mProgressDialog;

    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     */
    protected void showLoadingDialog(String title, String main){
        showLoadingDialog(title, main, false);
    }
    /**
     * 显示加载Dialog
     * @param title  标题
     * @param main  内容
     * @param cancelable 是否可以取消
     */
    protected void showLoadingDialog(String title, String main, boolean cancelable){
        dismissLoadingDialog();
        mProgressDialog = ProgressDialog.show(getActivity(), title, main, true, cancelable);
    }
    /**
     * 隐藏加载Dialog
     */
    protected void dismissLoadingDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onDestroy() {
        searchHistoryDataBase.destroy();
        super.onDestroy();
    }
}
