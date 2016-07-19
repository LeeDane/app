package com.leedane.cn.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
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
import com.leedane.cn.app.R;

import java.util.List;

/**
 * 测试
 * Created by leedane on 2016/5/17.
 */
public class TestActivity extends Activity {
    public static final String TAG = "TestActivity";

    private TextView textView;

    private MapView mMapView = null;

    private BaiduMap baiduMap;

    public LocationClient mLocationClient; //定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类

    private PoiSearch mPoiSearch = null;

   // private  PoiS;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_test);

        textView = (TextView)findViewById(R.id.id_textview);

        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        baiduMap =  mMapView.getMap();
        baiduMap.setTrafficEnabled(true);

        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.head);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        baiduMap.addOverlay(option);

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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy()
    {
        mPoiSearch.destroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                textView.setText(result.getTotalPageNum() +"");
                Log.i(TAG, "onGetPoiResult检索结果" +result.getTotalPageNum());
                List < PoiInfo > poiInfos = result.getAllPoi();
                if(poiInfos != null && poiInfos.size()> 0){
                    for(int i=0; i < poiInfos.size(); i++)
                        Log.i(TAG, "poi检索结果:"+poiInfos.get(i).name);
                }else{
                    Log.i(TAG, "没有poi检索结果");
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
                //获取Place详情页检索结果
                Log.i(TAG, "onGetPoiDetailResult检索结果:"+ result.getCheckinNum());
            }
        };

        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        // 搜索该坐标附近的餐厅
       /* mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword("餐厅")
                .location(new LatLng(23.147441, 113.348554))
                .pageCapacity(10).pageNum(10).radius(10000));*/

        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city("广州")
                .keyword("建设银行")
                .pageNum(10));
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
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
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
            if(list != null && list.size() > 0){
                for(int i = 0; i < list.size(); i++)
                    sb.append("\npoiList"+i+":" + list.get(i).getName());
            }
            textView.setText(location.getAddrStr());
            mLocationClient.stop();

            Log.i(TAG, "百度地图api定位返回的信息:" + sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }
}
