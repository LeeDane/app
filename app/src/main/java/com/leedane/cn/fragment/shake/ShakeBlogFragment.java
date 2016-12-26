package com.leedane.cn.fragment.shake;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.leedane.cn.adapter.search.SearchBlogAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.search.HttpResponseSearchBlogBean;
import com.leedane.cn.bean.search.SearchBlogBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.SearchHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 摇一摇博客的fragment类
 * Created by LeeDane on 2016/12/21.
 */
public class ShakeBlogFragment extends Fragment implements TaskListener{

    public static final String TAG = "ShakeBlogFragment";
    private Context mContext;
    private ListView mListView;
    private SearchBlogAdapter mAdapter;
    private List<SearchBlogBean> mSearchBlogBeans = new ArrayList<>();

    private View mRootView;

    protected TextView mListViewFooter;
    protected View viewFooter;

    private String searchKey;

    public ShakeBlogFragment(){
    }

    public static final ShakeBlogFragment newInstance(Bundle bundle){
        ShakeBlogFragment fragment = new ShakeBlogFragment();
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

        Bundle bundle = getArguments();
        if(bundle != null){
            this.searchKey = bundle.getString("searchKey");
        }

        if(StringUtil.isNull(searchKey)){
            ToastUtil.failure(mContext, "输入的搜索内容为空");
            return;
        }

        SearchHandler.getSearchBlogRequest(ShakeBlogFragment.this, searchKey);

        this.mListView = (ListView) mRootView.findViewById(R.id.no_swipe_refresh_listview);
        mAdapter = new SearchBlogAdapter(mContext, mSearchBlogBeans);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonHandler.startDetailActivity(mContext, "t_blog", mSearchBlogBeans.get(position).getId(), null);
            }
        });
        //listview下方的显示
        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setText(getResources().getString(R.string.search_blog_footer));
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void taskStarted(TaskType type) {

    }

    @Override
    public void taskFinished(TaskType type, Object result) {
        if(result instanceof Error){
            ToastUtil.failure(mContext, "网络连接失败，请稍后重试");
            return;
        }
        try{
            if(type == TaskType.LOAD_SEARCH_BLOG){
                HttpResponseSearchBlogBean httpResponseSearchBlogBean = BeanConvertUtil.strConvertToSearchBlogBeans(String.valueOf(result));
                if(httpResponseSearchBlogBean != null && httpResponseSearchBlogBean.isSuccess()){
                    List<SearchBlogBean> searchBlogBeans = httpResponseSearchBlogBean.getMessage();
                    if(searchBlogBeans != null && searchBlogBeans.size() > 0){
                        //临时list
                        List<SearchBlogBean> temList = new ArrayList<>();
                        mSearchBlogBeans.clear();
                        temList.addAll(searchBlogBeans);

                        if(mAdapter == null) {
                            mAdapter = new SearchBlogAdapter(mContext, mSearchBlogBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                    }else{
                            mSearchBlogBeans.clear();
                            mAdapter.refreshData(new ArrayList<SearchBlogBean>());
                    }
                }else{
                    JSONObject jsonObject = new JSONObject(String.valueOf(result));
                    if(jsonObject.has("isSuccess") && jsonObject.has("message")){
                        mListViewFooter.setText(jsonObject.getString("message"));
                    }else{
                        mListViewFooter.setText(getResources().getString(R.string.load_more_error));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void taskCanceled(TaskType type) {

    }
}
