package com.leedane.cn.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.leedane.cn.adapter.LocationAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.bean.LocationBean;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置定位Activity
 * Created by leedane on 2016/6/15.
 */
public class LocationActivity extends BaseActivity {
    public static final String TAG = "LocationActivity";

    private EditText mSearchKey;
    private TextView mSearchBtn;
    private TextView mLocationCurrent;
    private ListView mListView;

    private double mLongitude; //经度
    private double mLatitude; //纬度

    private List<LocationBean> currentLocationBeans = new ArrayList<>();

    public LocationClient mLocationClient; //定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类

    private PoiSearch mPoiSearch = null;

    private List<LocationBean> mLocationBeans = new ArrayList<>();

    private LocationAdapter mAdapter;

    private boolean isLoading = false;

   // private  PoiS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //检查是否登录
        if(!checkedIsLogin()){
            Intent it = new Intent(LocationActivity.this, LoginActivity.class);
            //设置跳转的activity
            it.putExtra("returnClass", "com.leedane.cn.activity.LocationActivity");
            it.setData(getIntent().getData());
            startActivity(it);
            finish();
        }
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);

        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        //标题
        setTitleViewText(getStringResource(R.string.location));
        //显示整个顶部的导航栏
        backLayoutVisible();

        initView();

    }

    private void initView() {
        mSearchKey = (EditText)findViewById(R.id.location_search_key);
        mSearchBtn = (TextView)findViewById(R.id.location_search_btn);
        mLocationCurrent = (TextView)findViewById(R.id.location_current);
        mListView = (ListView)findViewById(R.id.location_listview);
        mAdapter = new LocationAdapter(LocationActivity.this, mLocationBeans);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new ListViewOnScrollListener());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_mood = new Intent();
                if(StringUtil.isNotNull(mLocationBeans.get(position).getName()))
                    intent_mood.putExtra("location", mLocationBeans.get(position).getName());
                else
                    intent_mood.putExtra("location", mLocationBeans.get(position).getAddrStr());

                intent_mood.putExtra("longitude", mLocationBeans.get(position).getLongitude());
                intent_mood.putExtra("latitude", mLocationBeans.get(position).getLatitude());

                setResult( MoodActivity.GET_LOCATION_CODE, intent_mood);
                finish();//关闭当前activity
            }
        });

        mSearchBtn.setOnClickListener(this);

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();//初始化
        mLocationClient.start();

        mPoiSearch = PoiSearch.newInstance();
    }

    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy()
    {
        mPoiSearch.destroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.location_search_btn:
                String location = mLocationCurrent.getText().toString();
                if(mLongitude == 0 || mLatitude == 0 || location.length() == getStringResource(R.string.location_current).length()){
                    ToastUtil.failure(this, "未定位到当前位置，无法进行搜索");
                    return;
                }else{
                   search();
                }
                break;
        }
    }

    /**
     * 执行搜索百度周边操作
     */
    private void search(){

        String searchKey = mSearchKey.getText().toString();
        if(StringUtil.isNull(searchKey)){
            if(currentLocationBeans == null){
                ToastUtil.failure(this, "请输入您要检索的信息");
            }else{
                List<LocationBean> temp = new ArrayList<>();
                mLocationBeans.clear();
                temp.addAll(currentLocationBeans);
                mAdapter.refreshData(temp);
            }
            return;
        }

        if(isLoading == true){
            ToastUtil.failure(this, "操作太频繁啦，歇一歇再来");
            return;
        }
        isLoading = true;
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                Log.i(TAG, "onGetPoiResult检索结果" +result.getTotalPageNum());
                isLoading = false;
                List < PoiInfo > poiInfos = result.getAllPoi();

                LocationBean locationBean;
                if(poiInfos != null && poiInfos.size()> 0){
                    List<LocationBean> temp = new ArrayList<>();
                    mLocationBeans.clear();
                    for(int i=0; i < poiInfos.size(); i++){
                        Log.i(TAG, "poi检索结果:"+poiInfos.get(i).name);
                        locationBean = new LocationBean();
                        locationBean.setAddrStr(poiInfos.get(i).address);
                        locationBean.setName(poiInfos.get(i).name);
                        locationBean.setLongitude(poiInfos.get(i).location.longitude);
                        locationBean.setLatitude(poiInfos.get(i).location.latitude);
                        temp.add(locationBean);
                    }
                    mAdapter.refreshData(temp);
                }else{
                    ToastUtil.failure(LocationActivity.this, "检索\""+mSearchKey.getText().toString() + "\"没有结果");
                    Log.i(TAG, "没有poi检索结果");
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
                Log.i(TAG, "onGetPoiDetailResult检索结果:"+ result.getCheckinNum());
            }
        };

        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        // 搜索
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(searchKey)
                .location(new LatLng(mLatitude, mLongitude))
                .pageCapacity(10).pageNum(10).radius(1000));//检索1公里的位置

                        /*PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
                        nearbySearchOption.location(new LatLng(latitude, longitude));
                        nearbySearchOption.keyword(editSearchKeyEt.getText().toString());
                        nearbySearchOption.radius(1000);// 检索半径，单位是米
                        nearbySearchOption.pageNum(page);
                        poiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求  */
                        /*mPoiSearch.searchInCity((new PoiCitySearchOption())
                                .city("北京")
                                .keyword("美食")
                                .pageNum(10));*/
                       /* mPoiSearch.searchInCity((new PoiCitySearchOption())
                                .city("广州")
                                .keyword("建设银行")
                                .pageNum(10));*/
    }
    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());//获得当前时间
            sb.append("\nlocType code : ");
            sb.append(location.getLocType());//获得erro code得知定位现状
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());//获得纬度
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());//获得经度
            sb.append("\nradius : ");
            sb.append(location.getRadius());

            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            if (location.getLocType() == BDLocation.TypeGpsLocation){//通过GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());//获得速度
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){//通过网络连接定位
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());//获得当前地址
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());//获得经营商？
            }

            sb.append("\ndistrict:"+location.getDistrict());
            sb.append("\ncity:"+location.getCity());
            sb.append("\ncountry:"+location.getCountry());
            sb.append("\nBuildingID:"+location.getBuildingID());
            sb.append("\nlocationDescribe:"+location.getLocationDescribe());
            sb.append("\nfloor:"+location.getFloor());
            sb.append("\nprovince:"+location.getProvince());
            sb.append("\nstreet:" + location.getStreet());
            sb.append("\ndirection:" + location.getDirection());
            List<Poi> list = location.getPoiList();
            LocationBean currentLocationBean = new LocationBean();
            currentLocationBean.setAddrStr(location.getAddrStr());
            currentLocationBean.setName(location.getBuildingName());
            currentLocationBean.setLongitude(location.getLongitude());
            currentLocationBean.setLatitude(location.getLatitude());
            currentLocationBeans.add(currentLocationBean);
            if(list != null && list.size() > 0){
                for(int i = 0; i < list.size(); i++){
                    sb.append("\npoiList"+i+":" + list.get(i).getName());
                    currentLocationBean = new LocationBean();
                    currentLocationBean.setName(list.get(i).getName());
                    currentLocationBean.setAddrStr(location.getAddrStr());
                    currentLocationBean.setLongitude(location.getLongitude());
                    currentLocationBean.setLatitude(location.getLatitude());
                    currentLocationBeans.add(currentLocationBean);
                }
            }


            List<LocationBean> temp = new ArrayList<>();
            temp.addAll(currentLocationBeans);
            mAdapter.refreshData(temp);

            //一搜索到信息就停止
            mLocationCurrent.setText(getStringResource(R.string.location_current) +location.getAddrStr());
            mLocationClient.stop();

            Log.i(TAG, "百度地图api定位返回的信息:" + sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    /**
     *
     * ListView向下滚动事件的监听
     */
    class ListViewOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            //滚动停止
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                //当倒数第三个数据出现的时候就开始加载
                if (view.getLastVisiblePosition() == view.getCount() -1) {
                    if(!isLoading){
                        search();
                    }
                }
            }
        }

        /**
         * 获取字符串资源
         * @param context
         * @param resourseId
         * @return
         */
        public String getStringResource1(Context context, int resourseId){
            if(context == null){
                return BaseApplication.newInstance().getResources().getString(resourseId);
            }else{
                return context.getResources().getString(resourseId);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }
}
