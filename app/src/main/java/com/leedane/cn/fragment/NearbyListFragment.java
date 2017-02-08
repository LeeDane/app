package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.leedane.cn.activity.NearbyActivity;
import com.leedane.cn.adapter.CommentOrTransmitAdapter;
import com.leedane.cn.adapter.NearbyListAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.baidumap.overlayutil.DrivingRouteOverlay;
import com.leedane.cn.baidumap.overlayutil.WalkingRouteOverlay;
import com.leedane.cn.bean.NearbyBean;
import com.leedane.cn.customview.CircularImageView;
import com.leedane.cn.customview.MoodTextView;
import com.leedane.cn.customview.RecycleViewDivider;
import com.leedane.cn.financial.adapter.BaseRecyclerViewAdapter;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.handler.PraiseHandler;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DesUtils;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 附近人模块的列表展示的Fragment
 * Created by LeeDane on 2017/2/3.
 */
public class NearbyListFragment extends BaseRecyclerViewFragment implements BaseRecyclerViewAdapter.OnItemClickListener, BaseRecyclerViewAdapter.OnItemLongClickListener{

    public static final String TAG = "NearbyListFragment";

    private static final int pageCapacity = 20;
    private static final int radius = 30000;
    private int pageNum = 0;

    private View mRootView;
    private Context mContext;

    private RecyclerView mRecyclerView;
    private NearbyListAdapter mAdapter;//适配器

    private List<NearbyBean> mNearbyBeans = new ArrayList<>();

    public NearbyListFragment(){

    }

    public static final NearbyListFragment newInstance(Bundle bundle){
        NearbyListFragment fragment = new NearbyListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_recyclerview, container,
                    false);
        setHasOptionsMenu(true);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){

        }
        if(mContext == null)
            mContext = getActivity();

        initView();
    }

    @Override
    protected void sendFirstLoading() {
        ToastUtil.success(mContext, "sendFirstLoading");
        pageNum = 0;
        mNearbyBeans = new ArrayList<>();
        ((NearbyActivity) getActivity()).doNearBySearch(pageCapacity, pageNum, radius);
    }

    @Override
    protected void sendUpLoading() {
        ToastUtil.success(mContext, "sendUpLoading");
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void sendLowLoading() {
        ToastUtil.success(mContext, "sendLowLoading");
        ((NearbyActivity) getActivity()).doNearBySearch(pageCapacity, pageNum, radius);
    }

    @Override
    protected void sendLoadAgain(View view) {
        ToastUtil.success(mContext, "sendLoadAgain");
        ((NearbyActivity) getActivity()).doNearBySearch(pageCapacity, pageNum, radius);
    }

    /**
     * 展示附近人列表
     * @param radarNearbyResult
     */
    public void showNearbyList(RadarNearbyResult radarNearbyResult, RadarSearchError error){
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //ToastUtil.success(NearbyActivity.this, "查询周边成功", Toast.LENGTH_LONG);
            try {
                mSwipeLayout.setRefreshing(false);
                DesUtils desUtils = new DesUtils();
                NearbyBean nearbyBean;
                for(RadarNearbyInfo info: radarNearbyResult.infoList){
                    nearbyBean = new NearbyBean();
                    String[] comments = info.comments.split("leedaneapp");
                    JSONObject object = new JSONObject(desUtils.decrypt(comments[0]));
                    nearbyBean.setCreateUserId(object.getInt("id"));
                    nearbyBean.setAccount(object.getString("account"));
                    String path = null;
                    if(comments.length > 1)
                        path = comments[1];

                    nearbyBean.setUserPicPath(ConstantsUtil.QINIU_CLOUD_SERVER + path);
                    nearbyBean.setDistance(info.distance);
                    nearbyBean.setTime(info.timeStamp);
                    mNearbyBeans.add(nearbyBean);
                }
                mAdapter.notifyDataSetChanged();
                pageNum ++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //获取失败
            //ToastUtil.failure(mContext, "查询周边失败", Toast.LENGTH_LONG);
            mRecyclerViewFooter.setText(getStringResource(mContext, R.string.no_load_more));
        }
    }

    /**
     * 初始化方法
     */
    private void initView() {
        mSwipeLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        this.mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.id_recyclerview);
        mAdapter = new NearbyListAdapter(mContext, mNearbyBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
        mRecyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(mAdapter));

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.fragment_financial_main_footer, null);
        mAdapter.setFooterView(mFooterView);
        mRecyclerViewFooter = (TextView)mFooterView.findViewById(R.id.financial_footer);
        mRecyclerViewFooter.setText(getStringResource(mContext, R.string.loading));
        mRecyclerViewFooter.setOnClickListener(this);//添加点击事件

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.financial_footer:
                sendLoadAgain(v);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, Object data) {
        ToastUtil.success(mContext, "点击事件");
    }

    @Override
    public void onItemLongClick(int position) {
        ToastUtil.failure(mContext, "双击事件");
    }
}
