package com.leedane.cn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
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
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.leedane.cn.app.R;
import com.leedane.cn.application.BaseApplication;
import com.leedane.cn.baidumap.overlayutil.DrivingRouteOverlay;
import com.leedane.cn.baidumap.overlayutil.WalkingRouteOverlay;
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.DesUtils;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 附近activity
 * Created by LeeDane on 2016/12/27.
 */
public class NearbyActivity extends ActionBarBaseActivity implements
        OnGetGeoCoderResultListener, RadarSearchListener, BaiduMap.OnMapClickListener,
        BaiduMap.OnMarkerClickListener, OnGetRoutePlanResultListener {

    private  LatLng prePoint; //当前的定位信息

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private GeoCoder mSearch = null;
    private RadarSearchManager mManager = null;

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationClientOption.LocationMode mCurrentMode;
    private boolean isFirstLoc = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // SDK初始化
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_nearby;
    }

    @Override
    protected String getLabel() {
        return getStringResource(R.string.nearby);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nearby_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Message message = new Message();
        switch(item.getItemId()){
            case R.id.nearby_menu_refresh:
                message.what = FlagUtil.DO_NEARBY_SEARCH;
                dealHandler.sendMessageDelayed(message, 50);
                return true;
            case R.id.nearby_menu_list:
                ToastUtil.failure(NearbyActivity.this, "列表");
                return true;
            case R.id.nearby_menu_woman:
                ToastUtil.failure(NearbyActivity.this, "只看女生");
                return true;
            case R.id.nearby_menu_man:
                ToastUtil.failure(NearbyActivity.this, "只看男生");
                return true;
            case R.id.nearby_menu_all:
                ToastUtil.failure(NearbyActivity.this, "查看全部");
                return true;
            case R.id.nearby_menu_out: //清除位置退出
                showLoadingDialog("清除位置退出", "正在清除位置。", false);
                message.what = FlagUtil.NEARBY_CLEAR_LOCATION;
                dealHandler.sendMessage(message);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 发起附近人的请求
     */
    private void doNearBySearch(){
        RadarNearbySearchOption option = new RadarNearbySearchOption();
        option.centerPt(prePoint);    // 中心点
        option.pageCapacity(100);    // 每页包含的结果数
        option.pageNum(0);  // 当前需要查询的页码index，从0开始
        option.timeRange(DateUtil.stringToDate("2017-01-03 10:28:00"), new Date());
        option.radius(30000);    // 搜索半径
        //发起查询请求
        mManager.nearbyInfoRequest(option);
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

        mMapView.removeViewAt(1); // 去掉百度logo

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //设置点击事件的监听
        mBaiduMap.setOnMapClickListener(this);

        //设置maker的点击事件的监听
        mBaiduMap.setOnMarkerClickListener(this);

        //定义Maker坐标点
       /* LatLng point = new LatLng(39.963175, 116.400244);
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
        mBaiduMap.addOverlay(option1);*/

        //初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mManager = RadarSearchManager.getInstance();
        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);

        //一进来先定位
        getLocation();
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
        if(dealHandler != null)
            dealHandler.removeCallbacksAndMessages(null);

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

    private Handler dealHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FlagUtil.NEARBY_UPLOAD_LOCATION: //上传附近人的位置信息
                    //周边雷达设置用户身份标识，id为空默认是设备标识
                    mManager.setUserID("" + BaseApplication.getLoginUserId());
                    //上传位置
                    RadarUploadInfo info = new RadarUploadInfo();
                    try {
                        DesUtils desUtils = new DesUtils();
                        Map<String, Object> infoMap = new HashMap<>();
                        infoMap.put("account", BaseApplication.getLoginUserName());
                        infoMap.put("id", BaseApplication.getLoginUserId());
                        infoMap.put("sex", BaseApplication.getLoginUserSex());
                        String path = BaseApplication.getLoginUserPicPath();
                        String comments = desUtils.encrypt(new JSONObject(infoMap).toString()) + "leedaneapp" + (StringUtil.isNull(path) ? "" : path.substring(ConstantsUtil.QINIU_CLOUD_SERVER.length(), path.length()));
                        if (comments.length() > 255){
                            ToastUtil.failure(NearbyActivity.this, "由于您的头像路径过长，无法上传位置，请更换头像再进行此操作！");
                            return;
                        }
                        info.comments = comments;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    info.pt = prePoint;
                    mManager.uploadInfoRequest(info);
                    break;
                case FlagUtil.DO_NEARBY_SEARCH:
                    if(prePoint == null){
                        ToastUtil.failure(NearbyActivity.this, "无法获取您当前所在的位置！");
                        break;
                    }
                    doNearBySearch();
                    break;
                case FlagUtil.NEARBY_CLEAR_LOCATION:
                    gotoClearLocation();
                    break;
            }
        }
    };

    /**
     * 清除位置退出
     */
    private void gotoClearLocation(){
        if(mManager != null){
            mManager.clearUserInfo();
        }else{
            ToastUtil.failure(NearbyActivity.this, "地图管理对象已经失效，请重试！");
        }
    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //ToastUtil.success(NearbyActivity.this, "查询周边成功", Toast.LENGTH_LONG);
            try {
                DesUtils desUtils = new DesUtils();
                mBaiduMap.clear();
                for(RadarNearbyInfo info: radarNearbyResult.infoList){
                    String[] comments = info.comments.split("leedaneapp");
                    JSONObject object = new JSONObject(desUtils.decrypt(comments[0]));
                    int createUserId = object.getInt("id");
                    String account = object.getString("account");
                    String path = null;
                    if(comments.length > 1)
                        path = comments[1];

                    //构建Marker图标
                    BitmapDescriptor bitmap = null;
                    if(StringUtil.isNotNull(path)){
                        path = ConstantsUtil.QINIU_CLOUD_SERVER + path;
                        bitmap = BitmapDescriptorFactory.fromBitmap(ImageCacheManager.loadImage(path, 100, 100));
                    }else{
                        bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.no_pic);
                    }

                    Bundle extraInfo = new Bundle();
                    extraInfo.putInt("id", createUserId);
                    extraInfo.putString("account", account);
                    extraInfo.putString("path", path);
                    extraInfo.putInt("distance", info.distance);
                    extraInfo.putString("time", RelativeDateFormat.format(info.timeStamp));

                    //构建MarkerOption，用于在地图上添加Marker
                    MarkerOptions option = new MarkerOptions()
                            .position(info.pt) // //设置marker的位置
                            .icon(bitmap) //设置marker图标
                            .zIndex(9)  //设置marker所在层级
                            .extraInfo(extraInfo)
                            .title(account)
                            .draggable(true);  //设置手势拖拽

                    //在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
                }
                //获取成功，处理数据
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //获取失败
            ToastUtil.failure(NearbyActivity.this, "查询周边失败", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            //ToastUtil.success(NearbyActivity.this, "单次上传位置成功", Toast.LENGTH_SHORT);
        } else {
            //上传失败
            ToastUtil.success(NearbyActivity.this, "单次上传位置失败", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {
        dismissLoadingDialog();
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            ToastUtil.failure(NearbyActivity.this, "清除位置信息成功");
            finish();
        } else {
            ToastUtil.failure(NearbyActivity.this, "清除位置信息失败！");
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        mBaiduMap.hideInfoWindow();
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Bundle bundle = marker.getExtraInfo();
        //ToastUtil.success(NearbyActivity.this, "id："+bundle.getInt("id")+",账号是："+ bundle.getString("account"));
        showMakerDetail(marker);
        return true;
    }

    private InfoWindow mInfoWindow;
    /**
     * 展示maker详情
     * @param marker
     */
    private void showMakerDetail(final Marker marker) {  //显示气泡
        // 创建InfoWindow展示的view

        LatLng pt = marker.getPosition();
        Bundle bundle = marker.getExtraInfo();
        if(bundle != null && bundle.getInt("id") > 0){
            String path = bundle.getString("path");
            final String account = bundle.getString("account");
            final int createUserId = bundle.getInt("id");
            final int distance = bundle.getInt("distance");

            View view = LayoutInflater.from(this).inflate(R.layout.baidumap_maker_infowindow, null); //自定义气泡形状
            TextView accountTV = (TextView) view.findViewById(R.id.maker_account);
            TextView doTV = (TextView) view.findViewById(R.id.maker_do);
            TextView sendMessageTV = (TextView)view.findViewById(R.id.maker_send_message);
            TextView closeTV = (TextView) view.findViewById(R.id.maker_close);
            TextView routeTV = (TextView)view.findViewById(R.id.maker_route);
            ImageView imageIV = (ImageView)view.findViewById(R.id.maker_image);
            TextView distanceTV = (TextView)view.findViewById(R.id.maker_distance);
            TextView timeTV = (TextView)view.findViewById(R.id.maker_time);

            //显示距离
            distanceTV.setText(" 距离我：" +StringUtil.formatDistance(distance));

            //显示时间
            timeTV.setText(" 最近时间："+bundle.getString("time"));

            doTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startPersonalActivity(NearbyActivity.this, createUserId);
                    mBaiduMap.hideInfoWindow();
                }
            });

            sendMessageTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startChatDetailActivity(NearbyActivity.this, createUserId, account, 0);
                    mBaiduMap.hideInfoWindow();
                }
            });

            routeTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
                /*
                开车路线
                PlanNode from = PlanNode.withLocation(prePoint);
                PlanNode to = PlanNode.withLocation(marker.getPosition());
                routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(from).to(to));
                */
                    PlanNode from = PlanNode.withLocation(prePoint);
                    PlanNode to = PlanNode.withLocation(marker.getPosition());
                    routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(from).to(to));
                    routePlanSearch.setOnGetRoutePlanResultListener(NearbyActivity.this);
                }
            });
            closeTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                }
            });
            //pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
            accountTV.setText(account);

            if(StringUtil.isNotNull(path)) {
                imageIV.setImageBitmap(ImageCacheManager.loadImage(path, 100, 100));
            }
            // 定义用于显示该InfoWindow的坐标点
            // 创建InfoWindow的点击事件监听者
            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    mBaiduMap.hideInfoWindow();//影藏气泡

                }
            };
            // 创建InfoWindow
            mInfoWindow = new InfoWindow(view, pt, 0);
            mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        if (walkingRouteResult != null && walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            WalkingRouteOverlay walkingRouteOverlay = new WalkingRouteOverlay(mBaiduMap);
            walkingRouteOverlay.setData(walkingRouteResult.getRouteLines().get(0));
            mBaiduMap.addOverlays(walkingRouteOverlay.getOverlayOptions());

        }else{
            ToastUtil.success(NearbyActivity.this, "走路路线查询失败！");
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult != null && drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
            drivingRouteOverlay.setFocus(true);
            drivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0));
            mBaiduMap.addOverlays(drivingRouteOverlay.getOverlayOptions());

        }else{
            ToastUtil.success(NearbyActivity.this, "驾车路线查询失败！");
        }
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

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
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            if (longitude > 0 && latitude > 0) {
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
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll"); //设置坐标类型
        option.setScanSpan(5000); //设置发起定位请求的间隔时间为5000ms
        option.setPriority(LocationClientOption.GpsFirst); // 设置GPS优先
        option.disableCache(false);//禁止启用缓存定位
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.success(NearbyActivity.this, "抱歉，未能找到结果");
            return;
        }
        prePoint = result.getLocation();

        //上传定位的信息
        Message message = new Message();
        message.what = FlagUtil.NEARBY_UPLOAD_LOCATION;
        dealHandler.sendMessageDelayed(message, 50);

        //查询附近人
        Message message1 = new Message();
        message1.what = FlagUtil.DO_NEARBY_SEARCH;
        dealHandler.sendMessageDelayed(message1, 50);

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(prePoint));
        ToastUtil.success(NearbyActivity.this, result.getAddress());

        String province = result.getAddressDetail().province;
        String city = result.getAddressDetail().city;
        if (province != null && city != null) {

        }
    }

}
