package com.leedane.cn.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.Date;

/**
 * 附近activity
 * Created by LeeDane on 2016/12/27.
 */
public class NearbyActivity extends BaseActivity implements
        OnGetGeoCoderResultListener, RadarSearchListener{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private GeoCoder mSearch = null;
    RadarSearchManager mManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SDK初始化
        SDKInitializer.initialize(getApplicationContext());

        //当前视图
        setContentView(R.layout.activity_nearby);
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        //标题
        setTitleViewText(getStringResource(R.string.nearby));
        //显示整个顶部的导航栏
        backLayoutVisible();
        //创建地图对象
        init();

        final Button btn_location = (Button) findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //getLocation();
                //构造请求参数，其中centerPt是自己的位置坐标
                //定义Maker坐标点
                LatLng point1 = new LatLng(23.14729979288793, 113.34849463854097);
                //btn_location.setEnabled(false);
                RadarNearbySearchOption option = new RadarNearbySearchOption();
                option.centerPt(point1);    // 中心点
                option.pageCapacity(10);    // 每页包含的结果数
                option.pageNum(0);  // 当前需要查询的页码index，从0开始
                option.timeRange(DateUtil.stringToDate("2016-12-28 12:00:00"), new Date());
                option.radius(1000);    // 搜索半径
                //发起查询请求
                mManager.nearbyInfoRequest(option);
            }

        });
    }

    /**
     * 初始化方法
     */
    private void init() {
        //mMapView = (MapView) findViewById(R.id.bmapview);
        mMapView = new MapView(this, new BaiduMapOptions());
        mBaiduMap = mMapView.getMap();
        //卫星地图
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMaxAndMinZoomLevel(5, 15);

        /**添加一个对象*/
        RelativeLayout rlly_map = (RelativeLayout)findViewById(R.id.rlly_map);
        rlly_map.addView(mMapView);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.menu_circle_of_friends);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point) // //设置marker的位置
                .icon(bitmap); //设置marker图标
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        //定义Maker坐标点
        LatLng point1 = new LatLng(23.14729979288793, 113.34849463854097);
        //构建Marker图标
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory
                .fromResource(R.drawable.menu_friends);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions()
                .position(point1) // //设置marker的位置
                .icon(bitmap1) //设置marker图标
                .zIndex(9)  //设置marker所在层级
                .draggable(true);  //设置手势拖拽
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option1);

        //初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mManager = RadarSearchManager.getInstance();
        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);
        //周边雷达设置用户身份标识，id为空默认是设备标识
        mManager.setUserID("乐乐乐乐3");
        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = "用户备注信息";
        info.pt = point1;
        mManager.uploadInfoRequest(info);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMapView != null)
            mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMapView != null)
            mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mManager != null){
            mManager.removeNearbyInfoListener(this);
            mManager.destroy();
        }
        // 退出时销毁定位
        if(mLocClient != null)
            mLocClient.stop();
        // 关闭定位图层
        if(mBaiduMap != null)
            mBaiduMap.setMyLocationEnabled(false);

        if(mMapView != null)
            mMapView.onDestroy();
        mMapView = null;
        System.gc();
        super.onDestroy();
    }

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClientOption.LocationMode mCurrentMode;
    private boolean isFirstLoc = true;

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            Toast.makeText(NearbyActivity.this, "查询周边成功", Toast.LENGTH_LONG)
                    .show();
            //获取成功，处理数据
        } else {
            //获取失败
            Toast.makeText(NearbyActivity.this, "查询周边失败", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError error) {
        // TODO Auto-generated method stub
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            Toast.makeText(NearbyActivity.this, "单次上传位置成功", Toast.LENGTH_LONG)
                    .show();
        } else {
            //上传失败
            Toast.makeText(NearbyActivity.this, "单次上传位置失败", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            //此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }

            String addr = location.getAddrStr();
            if (addr != null) {
                Log.i("Test", addr);
            } else {
                Log.i("Test","error");
            }

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            if (longitude > 0 && latitude > 0) {
                Log.i("Test",String.format("纬度:%f 经度:%f", latitude,longitude));

                LatLng ptCenter = new LatLng(latitude,longitude);
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(ptCenter));
            }
            //停止定位
            mLocClient.stop();
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private void getLocation() {
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll"); //设置坐标类型
        option.setScanSpan(5000); //定位时间间隔
        mLocClient.setLocOption(option);

        mLocClient.start();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        // TODO Auto-generated method stub
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.success(NearbyActivity.this, "抱歉，未能找到结果");
            return;
        }
        mBaiduMap.clear();
//		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        ToastUtil.success(NearbyActivity.this, result.getAddress());

        String province = result.getAddressDetail().province;
        String city = result.getAddressDetail().city;
        if (province != null && city != null) {

        }
    }
}
