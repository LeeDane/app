package com.leedane.cn.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leedane.cn.adapter.FindAdapter;
import com.leedane.cn.app.R;
import com.leedane.cn.bean.MenuBean;
import com.leedane.cn.bean.MySettingBean;
import com.leedane.cn.database.MySettingDataBase;
import com.leedane.cn.handler.AttentionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.util.ClassUtil;
import com.leedane.cn.util.MySettingConfigUtil;
import com.leedane.cn.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现activity
 * Created by LeeDane on 2016/4/14.
 */
public class FindActivity extends BaseActivity implements FindAdapter.OnRecyclerViewListener{

    private RecyclerView mRecyclerView;

    private FindAdapter mAdapter;

    private List<MenuBean> findBeans = new ArrayList<>();

    final private int REQUEST_CODE_ASK_CAMERA_PERMISSIONS = 123;//权限请求相机码
    final private int REQUEST_CODE_ASK_LOCATION_PERMISSIONS = 124;//权限请求定位码

    private MySettingDataBase mySettingDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setImmerseLayout(findViewById(R.id.baeselayout_navbar));
        setTitleViewText(R.string.find);
        backLayoutVisible();
        mySettingDataBase = new MySettingDataBase(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        //使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        mRecyclerView.setHasFixedSize(true);

        //如果你需要显示的是横向滚动的列表或者竖直滚动的列表，则使用这个LayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FindActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);

        findBeans.add(new MenuBean(R.drawable.menu_circle_of_friends, getStringResource(R.string.circle_of_friend)));
        findBeans.add(new MenuBean(R.drawable.menu_chat, getStringResource(R.string.chat)));
        findBeans.add(new MenuBean(R.drawable.menu_friends, getStringResource(R.string.my_friends)));
        //findBeans.add(new MenuBean(R.drawable.ic_search_blue_300_18dp, getStringResource(R.string.search)));
        findBeans.add(new MenuBean(R.drawable.qr_code, getStringResource(R.string.sao_yi_sao)));
        findBeans.add(new MenuBean(R.drawable.no_user, getStringResource(R.string.yao_yi_yao)));
        findBeans.add(new MenuBean(R.drawable.menu_security, getStringResource(R.string.nearby)));
        findBeans.add(new MenuBean(R.drawable.menu_address_list, getStringResource(R.string.address_list)));

        mAdapter = new FindAdapter(findBeans);
        mAdapter.setOnRecyclerViewListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        //ToastUtil.success(FindActivity.this, "点击："+findBeans.get(position).getTitle());
        String title = findBeans.get(position).getTitle();
        if(title.equalsIgnoreCase(getStringResource(R.string.circle_of_friend))){ //朋友圈
            Intent it_circle = new Intent(FindActivity.this, CircleOfFriendActivity.class);
            startActivity(it_circle);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.sao_yi_sao))) {//扫一扫
            //在执行扫描二维码之前检查是否具有打开照相机的权限
            try {
                if(ClassUtil.getInstance().classHasMethod(Context.class, "checkSelfPermission")) {
                    int hasWriteContactsPermission = 0;//权限检查
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        hasWriteContactsPermission = getBaseContext().checkSelfPermission(Manifest.permission.CAMERA);
                    }
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CODE_ASK_CAMERA_PERMISSIONS);
                        }
                        return;//没有权限，结束
                    } else {
                        //做自己的操作
                        CommonHandler.startMipcaActivityCapture(FindActivity.this);
                    }
                }else{
                    CommonHandler.startMipcaActivityCapture(FindActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.failure(FindActivity.this, "定位权限被拒绝，请到设置->应用管理中设置为允许！");
            }

        }else if(title.equalsIgnoreCase(getStringResource(R.string.search))) {//搜索
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, SearchActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.my_friends))){//我的朋友
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, FriendActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.address_list))){//通讯录
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, AddressListActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.chat))){//聊天
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, ChatActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.yao_yi_yao))){//摇一摇
            Intent intent = new Intent();
            intent.setClass(FindActivity.this, ShakeActivity.class);
            startActivity(intent);
        }else if(title.equalsIgnoreCase(getStringResource(R.string.nearby))){//附近人
            //在执行定位之前检查是否具有打开定位的权限
            try {
                if(MySettingConfigUtil.share_location){
                    if(ClassUtil.getInstance().classHasMethod(Context.class, "checkSelfPermission")){
                        int hasWriteContactsPermission = 0;//权限检查
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hasWriteContactsPermission = getBaseContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_LOCATION_PERMISSIONS);
                            }
                            return;//没有权限，结束
                        }else {
                            //做自己的操作
                            CommonHandler.startNearByActivity(FindActivity.this);
                        }
                    }else{
                        CommonHandler.startNearByActivity(FindActivity.this);
                    }
                }else{
                    showMustShareLocation();//非打开共享位置的用户
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.failure(FindActivity.this, "定位权限被拒绝，请到设置->应用管理中设置为允许！");
            }
           /* Intent intent = new Intent();
            intent.setClass(FindActivity.this, NearbyActivity.class);
            startActivity(intent);*/
        }
    }

    private void showMustShareLocation(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FindActivity.this);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.menu_feedback);
        builder.setTitle("提示");
        builder.setMessage("是否打开位置共享?");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MySettingBean mySettingBean = new MySettingBean();
                        mySettingBean.setId(14);
                        mySettingBean.setValue("1");
                        mySettingDataBase.update(mySettingBean);
                        //做自己的操作
                        CommonHandler.startNearByActivity(FindActivity.this);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case  REQUEST_CODE_ASK_LOCATION_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 用户允许权限 继续执行定位
                    CommonHandler.startNearByActivity(FindActivity.this);
                } else {
                    // Permission Denied 拒绝
                    ToastUtil.failure(FindActivity.this, "定位权限被拒绝，请到设置->应用管理中设置为允许！");
                }
                break;
            case REQUEST_CODE_ASK_CAMERA_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted 用户允许权限 继续执行（我这里执行的是二维码扫描，检查的是照相机权限）
                    CommonHandler.startMipcaActivityCapture(FindActivity.this);
                } else {
                    // Permission Denied 拒绝
                    ToastUtil.failure(FindActivity.this, "相机权限被拒绝，请到设置->应用管理中设置为允许！");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onItemLongClick(int position) {
        ToastUtil.success(FindActivity.this, "长按："+findBeans.get(position).getTitle());
        return false;
    }

    @Override
    protected void onDestroy() {
        if(mySettingDataBase != null)
            mySettingDataBase.destroy();
        super.onDestroy();
    }
}
