package com.leedane.cn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.leedane.cn.app.R;
import com.leedane.cn.financial.activity.HomeActivity;
import com.leedane.cn.handler.AppVersionHandler;
import com.leedane.cn.handler.CommonHandler;
import com.leedane.cn.task.TaskListener;
import com.leedane.cn.util.ToastUtil;

import java.util.HashMap;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 带导航的activity
 * Created by LeeDane on 2015/10/6.
 */
public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TaskListener {

    protected Menu pMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        pMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_bar_search:
                Intent it_search = new Intent(NavigationActivity.this, SearchActivity.class);
                startActivity(it_search);
                return true;
            case R.id.action_bar_login:  //登录
                Intent it = new Intent();
                it.setClass(NavigationActivity.this, LoginActivity.class);
                startActivityForResult(it, MainActivity.LOGIN_REQUEST_CODE);
                /*Intent i = new Intent(NavigationActivity.this, LoginActivity.class);
                startActivity(i);*/
                return true;
            case R.id.action_bar_settings://设置
                Intent settings_in = new Intent(NavigationActivity.this, SettingActivity.class);
                startActivityForResult(settings_in, MainActivity.LOGIN_REQUEST_CODE);
                return true;
            case R.id.action_bar_out: //退出
                createLeaveAlertDialog();
                return true;
            case android.R.id.home:
                ToastUtil.success(this, "home", Toast.LENGTH_SHORT);
                finish();
                return true;
            case R.id.action_bar_version:
                //ToastUtil.success(this, "系统更新");
                AppVersionHandler.getNewestVersion(this, new HashMap<String, Object>());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 创建离开的警告提示框
     */
    public void createLeaveAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("离开本系统")
                .setMessage("残忍离开？")
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                })
                .setNegativeButton("点错了",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_find: //发现
               /* Intent it = new Intent(NavigationActivity.this, PersonalActivity.class);
                JSONObject userInfo = SharedPreferenceUtil.getUserInfo(getApplicationContext());
                int mUserId = 0;
                if(userInfo != null && userInfo.has("id"))
                    try{
                        mUserId = userInfo.getInt("id");
                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                it.putExtra("userId", mUserId);
                startActivity(it)*/;
                Intent it_find = new Intent(NavigationActivity.this, FindActivity.class);
                startActivity(it_find);
                break;
            case R.id.nav_gallery:  //图库
                Intent it_gallery = new Intent(NavigationActivity.this, GalleryActivity.class);
                startActivity(it_gallery);
                break;/*
            case R.id.nav_attention: //关注
                Intent it_attention = new Intent(NavigationActivity.this, AttentionActivity.class);
                startActivity(it_attention);
                break;*/
            case R.id.nav_message: //消息
                CommonHandler.startMyMessageActivity(NavigationActivity.this);
                break;
            case R.id.nav_file: //文件
                Intent it_file = new Intent(NavigationActivity.this, FileActivity.class);
                startActivity(it_file);
                break;
            case R.id.nav_financial: //我的记账
                Intent financial_in = new Intent(NavigationActivity.this, HomeActivity.class);
                startActivity(financial_in);
                break;
            case R.id.nav_share:  //分享
                OnekeyShare oks = new OnekeyShare();
               //关闭sso授权
                oks.disableSSOWhenAuthorize();

               // title标题：微信、QQ（新浪微博不需要标题）
                oks.setTitle("我是分享标题");  //最多30个字符

                // text是分享文本：所有平台都需要这个字段
                oks.setText("我是测试分享");  //最多40个字符

                // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
                //oks.setImagePath(Environment.getExternalStorageDirectory() + "/meinv.jpg");//确保SDcard下面存在此张图片

                //网络图片的url：所有平台
                oks.setImageUrl("http://7xnv8i.com1.z0.glb.clouddn.com/leedane_627560af-30af-4b14-91e0-c32d7e9991b6_20151125-122258-954-950_100x100.jpg");//网络图片rul

                // url：仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://sharesdk.cn");   //网友点进链接后，可以看到分享的详情

                // Url：仅在QQ空间使用
                oks.setTitleUrl("http://www.baidu.com");  //网友点进链接后，可以看到分享的详情

                // 启动分享GUI
                oks.show(this);
                break;
            case R.id.nav_help:  //帮助
                break;
            case R.id.nav_manage: //设置
                Intent setting_in = new Intent(NavigationActivity.this, SettingActivity.class);
                startActivityForResult(setting_in, MainActivity.LOGIN_REQUEST_CODE);
                break;
            case R.id.nav_loginout:  //退出登录
                break;
            default:
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void taskCanceled(com.leedane.cn.task.TaskType type) {

    }

    @Override
    public void taskFinished(com.leedane.cn.task.TaskType type, Object result) {

    }

    @Override
    public void taskStarted(com.leedane.cn.task.TaskType type) {

    }

}
