package com.leedane.cn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.leedane.cn.app.R;
import com.leedane.cn.baidumap.overlayutil.DrivingRouteOverlay;
import com.leedane.cn.baidumap.overlayutil.WalkingRouteOverlay;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DesUtils;
import com.leedane.cn.util.RelativeDateFormat;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;
import com.leedane.cn.volley.ImageCacheManager;

import org.json.JSONObject;

/**
 * 附近人模块的地图展示的Fragment
 * Created by LeeDane on 2017/2/3.
 */
public class NearbyMapFragment extends Fragment implements BaiduMap.OnMapClickListener,
        BaiduMap.OnMarkerClickListener, OnGetRoutePlanResultListener {

    public static final String TAG = "NearbyMapFragment";

    private View mRootView;
    private Context mContext;

    private LatLng prePoint; //当前的定位信息

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private GeoCoder mSearch = null;
    private RadarSearchManager mManager = null;

    private InfoWindow mInfoWindow;

    // 定位相关
    private LocationClient mLocClient;
    private LocationClientOption.LocationMode mCurrentMode;
    private boolean isFirstLoc = true;


    public NearbyMapFragment(){

    }

    public static final NearbyMapFragment newInstance(Bundle bundle){
        NearbyMapFragment fragment = new NearbyMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView == null)
            mRootView = inflater.inflate(R.layout.fragment_nearby_map, container,
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

        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        //mMapView = (MapView) findViewById(R.id.bmapview);
        mMapView = new MapView(mContext, new BaiduMapOptions());
        mBaiduMap = mMapView.getMap();
        //卫星地图
        //mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMaxAndMinZoomLevel(5, 15);

        /**添加一个对象*/
        RelativeLayout rlly_map = (RelativeLayout)mRootView.findViewById(R.id.rlly_map);
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
        //一进来先定位
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMapView != null)
            mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView != null)
            mMapView.onPause();
    }

    /**
     * 展示附近人列表
     * @param radarNearbyResult
     */
    public void showNearbyList(RadarNearbyResult radarNearbyResult){
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

            View view = LayoutInflater.from(mContext).inflate(R.layout.baidumap_maker_infowindow, null); //自定义气泡形状
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
                    CommonHandler.startPersonalActivity(mContext, createUserId);
                    mBaiduMap.hideInfoWindow();
                }
            });

            sendMessageTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHandler.startChatDetailActivity(mContext, createUserId, account, 0);
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
                    routePlanSearch.setOnGetRoutePlanResultListener(NearbyMapFragment.this);
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
            ToastUtil.success(mContext, "走路路线查询失败！");
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
            ToastUtil.success(mContext, "驾车路线查询失败！");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    /**
     * 展示当前的定位信息
     * @param location
     */
    public void showPreLocation(BDLocation location){

        if(mMapView == null)
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
    }

    public void showLocaton(LatLng point){
        prePoint = point;
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(prePoint));
    }

    @Override
    public void onDestroy() {
        // 关闭定位图层
        if(mBaiduMap != null)
            mBaiduMap.setMyLocationEnabled(false);

        if(mMapView != null)
            mMapView.onDestroy();
        mMapView = null;
        System.gc();
        super.onDestroy();
    }
}
