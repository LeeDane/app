package com.leedane.cn.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leedane.cn.adapter.CollectionAdapter;
import com.leedane.cn.adapter.search.SearchHistoryAdapter;
import com.leedane.cn.adapter.search.SearchUserAdapter;
import com.leedane.cn.bean.CollectionBean;
import com.leedane.cn.bean.HttpResponseCollectionBean;
import com.leedane.cn.bean.search.HttpResponseSearchUserBean;
import com.leedane.cn.bean.search.SearchUserBean;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.SearchHandler;
import com.leedane.cn.leedaneAPP.R;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.task.TaskType;
import com.leedane.cn.util.BeanConvertUtil;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索用户列表的fragment类
 * Created by LeeDane on 2016/5/22.
 */
public class SearchUserFragment extends Fragment implements TaskListener{

    public static final String TAG = "SearchUserFragment";
    private Context mContext;
    private ListView mListView;
    private SearchUserAdapter mAdapter;
    private List<SearchUserBean> mSearchUserBeans = new ArrayList<>();

    private View mRootView;

    protected TextView mListViewFooter;
    protected View viewFooter;

    private String searchKey;

    public SearchUserFragment(){
    }

    public static final SearchUserFragment newInstance(Bundle bundle){
        SearchUserFragment fragment = new SearchUserFragment();
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

        SearchHandler.getSearchUserRequest(SearchUserFragment.this, searchKey);

        this.mListView = (ListView) mRootView.findViewById(R.id.no_swipe_refresh_listview);
        mAdapter = new SearchUserAdapter(mContext, mSearchUserBeans);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonHandler.startPersonalActivity(mContext, mSearchUserBeans.get(position).getId());
            }
        });
        //listview下方的显示
        viewFooter = LayoutInflater.from(mContext).inflate(R.layout.listview_footer_item, null);
        mListView.addFooterView(viewFooter, null, false);
        mListViewFooter = (TextView)mRootView.findViewById(R.id.listview_footer_reLoad);
        mListViewFooter.setText(getResources().getString(R.string.search_user_footer));
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
            if(type == TaskType.LOAD_SEARCH_USER){
                HttpResponseSearchUserBean httpResponseSearchUserBean = BeanConvertUtil.strConvertToSearchUserBeans(String.valueOf(result));
                if(httpResponseSearchUserBean != null && httpResponseSearchUserBean.isSuccess()){
                    List<SearchUserBean> searchUserBeans = httpResponseSearchUserBean.getMessage();
                    if(searchUserBeans != null && searchUserBeans.size() > 0){
                        //临时list
                        List<SearchUserBean> temList = new ArrayList<>();
                        mSearchUserBeans.clear();
                        temList.addAll(searchUserBeans);

                        if(mAdapter == null) {
                            mAdapter = new SearchUserAdapter(mContext, mSearchUserBeans);
                            mListView.setAdapter(mAdapter);
                        }
                        mAdapter.refreshData(temList);
                    }else{
                            mSearchUserBeans.clear();
                            mAdapter.refreshData(new ArrayList<SearchUserBean>());
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
