package com.leedane.cn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
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
import com.leedane.cn.financial.util.FlagUtil;
import com.leedane.cn.fragment.NearbyListFragment;
import com.leedane.cn.fragment.NearbyMapFragment;
import com.leedane.cn.util.CommonUtil;
import com.leedane.cn.util.ConstantsUtil;
import com.leedane.cn.util.DateUtil;
import com.leedane.cn.util.DesUtils;
import com.leedane.cn.util.StringUtil;
import com.leedane.cn.util.ToastUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附近activity
 * Created by LeeDane on 2016/12/27.
 */
public class NearbyActivity_old extends ActionBarBaseActivity implements
        OnGetGeoCoderResultListener, RadarSearchListener{

    private  LatLng prePoint; //当前的定位信息
    private GeoCoder mSearch = null;
    private RadarSearchManager mManager = null;

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationClientOption.LocationMode mCurrentMode;

    //标记当前是地图还是列表
    private boolean isMap = true;

    private TextView mCurrentLocationTV;

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
            /*case R.id.nearby_menu_list:
                isMap = !isMap;
                if(isMap){
                    item.setTitle(getStringResource(R.string.nearby_list));
                    clearAllFragment();
                    NearbyMapFragment mapFragment = NearbyMapFragment.newInstance(new Bundle());
                    getSupportFragmentManager().beginTransaction().add(R.id.nearby_container, mapFragment).commit();
                    if(prePoint != null)
                        mapFragment.showLocaton(prePoint);
                }else{
                    item.setTitle(getStringResource(R.string.nearby_map));
                    clearAllFragment();
                    NearbyListFragment listFragment = NearbyListFragment.newInstance(new Bundle());
                    getSupportFragmentManager().beginTransaction().add(R.id.nearby_container, listFragment).commit();
                }
                doNearBySearch(100, 0, 30000);
                return true;
            case R.id.nearby_menu_woman:
                ToastUtil.failure(NearbyActivity_old.this, "只看女生");
                return true;
            case R.id.nearby_menu_man:
                ToastUtil.failure(NearbyActivity_old.this, "只看男生");
                return true;
            case R.id.nearby_menu_all:
                ToastUtil.failure(NearbyActivity_old.this, "查看全部");
                return true;*/
            case R.id.nearby_menu_out: //清除位置退出
                showLoadingDialog("清除位置退出", "正在清除位置。", false);
                message.what = FlagUtil.NEARBY_CLEAR_LOCATION;
                dealHandler.sendMessage(message);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void clearAllFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(CommonUtil.isNotEmpty(fragments))
            for(Fragment fragment: fragments){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
    }

    /**
     * 执行周边搜索
     * @param pageCapacity  每页包含的数量，不能大于10， 大于10 使用10
     * @param pageNum
     * @param radius
     */
    public void doNearBySearch(int pageCapacity, int pageNum, int radius){

        if(prePoint == null){
            getLocation();
            return;
        }

        RadarNearbySearchOption option = new RadarNearbySearchOption();
        option.centerPt(prePoint);    // 中心点
        option.pageCapacity(pageCapacity > 10 ? 10: pageCapacity);    // 每页包含的结果数
        option.pageNum(pageNum);  // 当前需要查询的页码index，从0开始
        option.timeRange(DateUtil.stringToDate("2017-01-03 10:28:00"), new Date());
        option.radius(radius);    // 搜索半径
        //发起查询请求
        mManager.nearbyInfoRequest(option);
    }

    /**
     * 初始化方法
     */
    private void init() {

        mCurrentLocationTV = (TextView)findViewById(R.id.nearby_current_location);
        //初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mManager = RadarSearchManager.getInstance();
        //周边雷达设置监听
        mManager.addNearbyInfoListener(this);

        clearAllFragment();
        if(isMap){
            NearbyMapFragment mapFragment = NearbyMapFragment.newInstance(new Bundle());
            //getSupportFragmentManager().beginTransaction().add(R.id.nearby_container, mapFragment).commit();
        }else{
            NearbyListFragment listFragment = NearbyListFragment.newInstance(new Bundle());
            //getSupportFragmentManager().beginTransaction().add(R.id.nearby_container, listFragment).commit();
        }

        //一进来先定位
        getLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                            ToastUtil.failure(NearbyActivity_old.this, "由于您的头像路径过长，无法上传位置，请更换头像再进行此操作！");
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
                        ToastUtil.failure(NearbyActivity_old.this, "无法获取您当前所在的位置！");
                        break;
                    }else{
                        doNearBySearch(100, 0, 30000);
                    }
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
            ToastUtil.failure(NearbyActivity_old.this, "地图管理对象已经失效，请重试！");
        }
    }

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //ToastUtil.success(NearbyActivity.this, "查询周边成功", Toast.LENGTH_LONG);
            //判断是展示列表还是展示地图
            if(isMap){
                //NearbyMapFragment mapFragment = (NearbyMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearby_container);
                //mapFragment.showNearbyList(radarNearbyResult);
            }else{
                //NearbyListFragment listFragment = (NearbyListFragment) getSupportFragmentManager().findFragmentById(R.id.nearby_container);
                //listFragment.showNearbyList(radarNearbyResult);
            }
        } else {
            //获取失败
            ToastUtil.failure(NearbyActivity_old.this, "查询周边失败", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError error) {
        if (error == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            //ToastUtil.success(NearbyActivity.this, "单次上传位置成功", Toast.LENGTH_SHORT);
        } else {
            //上传失败
            ToastUtil.success(NearbyActivity_old.this, "单次上传位置失败", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {
        dismissLoadingDialog();
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            ToastUtil.failure(NearbyActivity_old.this, "清除位置信息成功");
            finish();
        } else {
            ToastUtil.failure(NearbyActivity_old.this, "清除位置信息失败！");
        }
    }

    @Override
    public void onRefresh() {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;

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
        if(mLocClient == null){
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
        }else{
            mLocClient.requestLocation();
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.success(NearbyActivity_old.this, "抱歉，未能找到结果");
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

        //
        mCurrentLocationTV.setText("当前位置是:" + result.getAddress());
        if(isMap){
            //NearbyMapFragment mapFragment = (NearbyMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearby_container);
            //mapFragment.showLocaton(prePoint);
        }

        String province = result.getAddressDetail().province;
        String city = result.getAddressDetail().city;
        if (province != null && city != null) {

        }
    }

    //加载附近人
    public void loadNearby(){

    }

}
